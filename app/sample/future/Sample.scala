package sample.future

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._

object Sample extends App {

  val f1 = Future(1.to(10000).sum)
  val f2 = Future(1.to(1000).sum)

  val ret = for {
    a <- f1
    b <- f2
  } yield a + b

  ret.onComplete {
    case Success(x) => println(s"Future ret=[$x]")
    case Failure(_) =>
  }

  Await.result(ret, 0 nanos)
}
