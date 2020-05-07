package domain

case class User(
                 id: Int,
                 name: String,
                 email: String,
                 authLevel: Int,
                 password: String,
                 createAt: Int) {
  // TODO: あとで時刻にする
}

object User {

}
