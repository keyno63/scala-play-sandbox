package sample.typeclass

object Sample extends App {

  trait Convert[T] {
    def convert(value: T) : String
  }

  implicit val str: Convert[String] = new Convert[String] {
    override def convert(value: String): String = s"convert string ${value}"
  }

  implicit val int: Convert[Int] = new Convert[Int] {
    override def convert(value: Int): String = s"convert int ${value.toString}"
  }

  def convert[T](a: T)(implicit ip: Convert[T]) = println(ip.convert(a))

  // main
  convert(100)
  convert("Hello")

  implicit val int1: Convert[Int] = new Convert[Int] {
    override def convert(value: Int): String = s"convert int1 ${value.toString}"
  }
  convert(200)(int)
  convert(200)(int1)

}
