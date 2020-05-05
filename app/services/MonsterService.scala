package services

import javax.inject.Singleton
import scalikejdbc._

@Singleton
class MonsterService {
  /*
  // initialize JDBC driver & connection pool
  Class.forName("org.postgresql.Driver")

  val url = "jdbc:postgresql://localhost:45432/sample_db"
  val user = "fujiwara"
  val pass = "fujiwara"
  ConnectionPool.singleton(url, user, pass)

  // ad-hoc session provider on the REPL
  //implicit val session = AutoSession
   */

  def getAll() = {
    val names = DB readOnly { implicit session =>
      sql"select name from m_monster".map(_.string("name")).list.apply()
    }
    names
  }

  def get(number: Option[String]) = {
    val numberVal = number.getOrElse("")
    val name = DB readOnly { implicit session =>
      sql"select name from m_monster where id = ${numberVal}".map(_.string("name")).list.apply()
    }
    name
  }

}
