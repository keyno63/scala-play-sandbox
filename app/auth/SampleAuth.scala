package auth

import domain.User

sealed trait SampleAuth {

}

sealed trait SampleAuth(val level: Int) {
  def checkAuthority(user: User): Boolean =
    level <= user.authLevel.value
}