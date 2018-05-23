package services

import models.User
import utils.LevelGenerator

class GameService {
  def createNewStage(u:User) = {
    val generator = new LevelGenerator(30, 30, 2, 2, 500)
    generator.generateLevel() foreach { row => row foreach print; println }
    println()
  }

  def winGame(u:User) = {

  }

  def looseGame(u:User) = {

  }
}
