package sample.scalascraper

import java.io.File
import java.util.StringJoiner
import scala.sys.process._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._

import scala.util.matching.Regex

object Sample extends App {

  get()

  def get() = {
    val browser = JsoupBrowser()
    val doc: Document = browser.parseFile("app/sample/scalascraper/resources/data2.txt")
    val items = doc >> elementList("tr")

//    for {
//      a <- items >> texts("td")
//    } println(s"td: $a")

    val data = items.map(_ >> texts("td")).filter(_.nonEmpty)
      .map {
        case List(id, n, types, specials, h, a, b, c, d, s, all) =>
          //      Pokemon(id.toInt, n, h.toInt, a.toInt, b.toInt, c.toInt, d.toInt, s.toInt, all.toInt)
          Pokemon2(
            id,
            n,
            convertType(types),
            convertSpecial(specials),
            h.toInt, a.toInt, b.toInt, c.toInt, d.toInt, s.toInt, all.toInt)
      }

    data.foreach(println(_))

    val f = new File("app/sample/scalascraper/resources/data2.csv")
    val maped = data.map(_.toString)
    for (element <- maped) {
      "echo %s".format(element) #>> f!
    }

  }

  def sample() {
    val browser = JsoupBrowser()
    //val doc = browser.parseFile("core/src/test/resources/example.html")
    val doc2: Document = browser.get("http://example.com")
    println(doc2)

    //val head = doc2 >> text("#header")
    //println(s"head: $head")

    val items = doc2 >> elementList("p")
    println(s"items: $items")
    items.map(_ >> text("p")).foreach(println(_))
  }

  def getYakkun() = {
    val browser = JsoupBrowser()
    //val doc: Document = browser.get("https://yakkun.com/sm/status_list.htm")

    val doc: Document = browser.parseFile("app/sample/scalascraper/resources/data.txt")
    val items = doc >> elementList("tr")

    val data = items.map(_ >> texts("td")).filter(_.nonEmpty)
      .map {
        case List(id, n, h, a, b, c, d, s, all) => Pokemon(id.toInt, n, h.toInt, a.toInt, b.toInt, c.toInt, d.toInt, s.toInt, all.toInt)
      }

    data.foreach(println(_))
  }

  def convertType(value: String) = {
    val ret = value split ' '
    ret match {
      case Array(a, b) =>
        //Array(Some(a), Some(b))
        Types(a, Some(b))
      case Array(a) =>
        //Array(Some(a), None)
        Types(a, None)
    }
  }

  def convertSpecial(value: String) = {
    val ret = value split ' '
    ret match {
      case Array(a, b, c) =>
        Characters(a, Some(b), Some(c.replace("(", "").replace(")", "")))
      case Array(a, b) => {
        if (b.startsWith("(")) {
          Characters(a, None, Some(b.replace("(", "").replace(")", "")))
        } else {
          Characters(a, Some(b), None)
        }
      }
      case Array(a) =>
        Characters(a, None, None)
    }
  }

}

case class Pokemon(
                    id: Int,
                    name: String,
                    h: Int,
                    a: Int,
                    b: Int,
                    c: Int,
                    d: Int,
                    s: Int,
                    all: Int
                  )

case class Pokemon2(
                    id: String,
                    name: String,
                    types: Types,
                    charactors: Characters,
                    h: Int,
                    a: Int,
                    b: Int,
                    c: Int,
                    d: Int,
                    s: Int,
                    all: Int
                  ){
  def toList = {
    List(id, name) ::: types.toList ::: charactors.toList :::
      //List(h.toString, a.toString, b.toString, c.toString, d.toString, s.toString, all.toString)
      List(h, a, b, c, d, s, all)
  }
  def toArray = {
    toList.toArray
  }

  override def toString() = {
    val sb = new StringJoiner(",")
    //toList.foreach(sb.add(_))
    toList.map {
      case "" => "null"
      case x: Int => x.toString
      case x: String => s""""$x""""
    }.foreach(sb.add(_))
    println(sb.toString)
    sb.toString
  }
}

case class Characters(type1: String, type2: Option[String], hidden: Option[String]) {
  def toList = {
    List(type1, type2.getOrElse(""), hidden.getOrElse(""))
  }
}

case class Types(type1: String, type2: Option[String])
{
  def toList = {
    List(type1, type2.getOrElse(""))
  }
}
