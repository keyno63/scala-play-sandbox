package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import io.circe.generic.auto._
import sangria.marshalling.circe._
import sangria.parser.{QueryParser, SyntaxError}
import play.api.libs.circe.Circe
import sangria.schema._
import io.circe._
import io.circe.optics.JsonPath._
import io.circe.parser.{parse => cparse, _}
import sangria.ast.Document
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.slowlog.SlowLog

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

@Singleton
class GraphQlController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with Circe {
  /**
   * json parce の確認
   * @return
   */
  def xparse(): Action[Json] = Action(circe.json).async { request =>
    Future{
      cparse(request.body.toString) match {
        case Right(x) => Ok(x)
        case Left(x) => BadRequest(x.toString)
        case _ => BadRequest("")
      }
    }
  }

  /**
   * Graph Ql のエンドポイント
   * @return
   */
  def graphql(): Action[Json] = Action(circe.json).async { implicit request =>
    cparse(request.body.toString) match {
      case Right(json) => {
        /*
        val cursor: HCursor = json.hcursor
        val ret = cursor.downField("operationName").as[String]

        val operation = ret match {
          case Right(value) =>
            //Ok(s"Your new application is ready. $value")
            value
          //case _ => BadRequest("Parse Error, user")
          //case _ => ""
        }


        val vars = cursor.downField("variables").as[String] match {
          case Right(value) => value
         */
        val query = root.query.string.getOption(json)
        val operationName = root.operationName.string.getOption(json)
        val variablesStr: Option[String] = root.variables.string.getOption(json)

        query.map(QueryParser.parse(_)) match {
          case Some(Success(ast)) =>
            variablesStr.map(cparse) match {
              case Some(Left(error)) => Future(BadRequest(s"$error"))
              case Some(Right(json)) => executeGraphQL(ast, operationName, json, /*tracing.isDefined*/ false)
              case None => executeGraphQL(ast, operationName, root.variables.json.getOption(json) getOrElse Json.obj(), /*tracing.isDefined*/ false)
            }
          case Some(Failure(error)) => //complete(BadRequest, formatError(error))
            Future(BadRequest(s"$error"))
          case None =>
            Future(BadRequest(s"No query to execute"))
        }
      }
      case _ => {
        val b = request.body.toString
        Future(BadRequest(s"Parse Error JSON. body=[$b]"))
      }
    }

  }

  def formatError(error: Throwable): Json = error match {
    case syntaxError: SyntaxError =>
      Json.obj("errors" -> Json.arr(
        Json.obj(
          "message" -> Json.fromString(syntaxError.getMessage),
          "locations" -> Json.arr(Json.obj(
            "line" -> Json.fromBigInt(syntaxError.originalError.position.line),
            "column" -> Json.fromBigInt(syntaxError.originalError.position.column))))))
    case NonFatal(e) =>
      formatError(e.getMessage)
    case e =>
      throw e
  }

  def formatError(message: String): Json =
    Json.obj("errors" -> Json.arr(Json.obj("message" -> Json.fromString(message))))

  def executeGraphQL(query: Document, operationName: Option[String], variables: Json, tracing: Boolean) = {
      Executor.execute(
        SchemaDefinition.UserSchema, query, new UserRepo,
        variables = if (variables.isNull) Json.obj() else variables,
        operationName = operationName,
        middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil,
        deferredResolver = DeferredResolver.fetchers(SchemaDefinition.users)
      ).map(x => Ok(s"${x}"))
      .recover{
        case error: QueryAnalysisError => BadRequest(error.resolveError)
        case error: ErrorWithResolver => InternalServerError(error.resolveError)
      }
  }

}

trait UserBase {
  def id: Int
  def name: String
}
case class User(id: Int, name: String) extends UserBase
object Sample {
  val user: ObjectType[Unit, User] = sangria.macros.derive.deriveObjectType[Unit, User]()
}

/* user 情報. DB などのクエリが大変なのでコード上で書いておく */
class UserRepo {
  import UserRepo._

  def getUser(id: Int): Option[User] = users.find(c => c.id == id)

  def getUsers(limit: Int, offset: Int): List[User] = users.drop(offset).take(limit)
}

object UserRepo {
  val users = List(
    User(
      id = 1000,
      name = "taro"
    ),
    User(
      id = 2000,
      name = "jiro"
    )
  )
}

/* schema 定義. GraphQl のスキーマ定義. */
object SchemaDefinition {
  val ID: Argument[Int] = Argument("id", IntType, description = "id of the character")

  val LimitArg: Argument[Int] = Argument("limit", OptionInputType(IntType), defaultValue = 20)
  val OffsetArg: Argument[Int] = Argument("offset", OptionInputType(IntType), defaultValue = 0)

  val users: Fetcher[UserRepo, User, User, Int] = Fetcher.caching(
    (ctx: UserRepo, ids: Seq[Int]) =>
      Future.successful(ids.flatMap(id => ctx.getUser(id))))(HasId(_.id))

  val UserBase: InterfaceType[UserRepo, User] =
    InterfaceType(
      "UserBase",
      "Sample data, User.",
      () => fields[UserRepo, User](
        Field("id", IntType,
          Some("id"),
          resolve = _.value.id)
      )
    )
  /* 実際のオブジェクト */
  val User: ObjectType[UserRepo, User] =
    ObjectType(
      "User",
      "Sample data, User.",
      interfaces[UserRepo, User](UserBase),
      fields[UserRepo, User](
        Field(
          "id",
          IntType,
          Some("id"),
          resolve = _.value.id),
        Field(
          "name",
          StringType,
          Some("name"),
          resolve = _.value.name)
      )
    )

  val Query: ObjectType[UserRepo, Unit] = ObjectType(
    "Query", fields[UserRepo, Unit](
      Field("user", OptionType(User),
        arguments = ID :: Nil,
        resolve = (ctx) => ctx.ctx.getUser(ctx.arg(ID))),
      Field("users", ListType(User),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getUsers(ctx arg LimitArg, ctx arg OffsetArg)),
    ))
  // 実際の schema
  val UserSchema: Schema[UserRepo, Unit] = Schema(Query)
}

/*
curl -X POST localhost:9000/graphql \
  -H "Content-Type:application/json" \
  -d '{"query": "{user {name}}"}'
* */