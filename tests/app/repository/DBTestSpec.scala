package app.repository

import com.typesafe.config.ConfigFactory
import org.scalatest._
import repository.MonsterRepository
import scalikejdbc._

trait TestDBSettings {

  def loadJDBCSettings() {
    // https://github.com/typesafehub/config
    val config = ConfigFactory.load()
    val url = config.getString("db.default.url")
    val user = config.getString("db.default.username")
    val password = config.getString("db.default.password")
    ConnectionPool.singleton(url, user, password)
  }

  loadJDBCSettings()
}

object DBTestSpec extends FlatSpec with Matchers with TestDBSettings {
  behavior of "MonsterRepository"

  it should "select record" in {
    val mr = new MonsterRepository
    val values = mr.get(Some("100"))
    values should not be(null)
  }
}
