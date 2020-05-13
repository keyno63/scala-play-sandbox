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
}