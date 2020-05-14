package auth

import domain.User

sealed abstract class SampleAuth(val level: Int) {
  def checkAuthority(user: User): Boolean =
    level <= user.authLevel.level
}

object SampleAuth {
  case object Disabled extends SampleAuth(-1)
  case object Administrator extends SampleAuth(0)
  case object NormalUser extends SampleAuth(1)

  val values = Seq(Disabled, Administrator, NormalUser)
  def find(value: Int): Option[SampleAuth] = values.find(_.level == value)

}