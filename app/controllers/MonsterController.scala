package controllers

import javax.inject._
import play.api.mvc._
import services.MonsterService

import scala.concurrent.{ExecutionContext, Future}

//@Singleton
class MonsterController @Inject() (cc: ControllerComponents, ms: MonsterService)
                                  (implicit ec: ExecutionContext)
extends AbstractController(cc) {

  def getAll: Action[AnyContent] = Action {
    val ret = ms.getAll()
    Ok(ret.toString())
  }

  def get(number: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    Future {
      val ret = ms.get(number)
      Ok(ret.toString())
    }
  }

}
