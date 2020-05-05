package services

import javax.inject.{Inject, Singleton}
import repository.MonsterRepository

@Singleton
class MonsterService @Inject() (mr: MonsterRepository) {

  def getAll() = {
    mr.getAll()
  }

  def get(number: Option[String]) = {
    mr.get(number)
  }
}
