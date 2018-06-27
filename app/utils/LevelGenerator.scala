package utils;

class LevelGenerator(stageNumber: Int) {
  assert(stageNumber > 0)

  private val nbRows = (stageNumber/2 + 2).toInt
  private val nbCols = (stageNumber/2 + 2).toInt
  private val nbRedBonus = nbRows/3;
  private val nbYellowBonus = nbRows/3;
  private val nbWalls = 3*nbRows*nbCols/5+ (stageNumber%2)*nbRows*nbCols/8;

  // Define the cells' values possibilities
  private val EMPTY = 6
  private val WALL = 1
  private val LIFE = 2
  private val SHOW = 3
  private val ENTRY = 4
  private val EXIT = 5

  private val ENTRY_POINT = (nbRows / 2, 0)
  private val EXIT_POINT = (nbRows / 2, nbCols - 1)


  // Initialise the level array
  private var level = Array.fill(nbRows, nbCols)(EMPTY)


  // Calculate the minimum distance path between two points
  private def distanceMin(from: (Int, Int), to: (Int, Int)): Int =
    Math.abs(from._1 - to._1) + Math.abs(from._2 - to._2)

  // Check wether or not a point is in the level boundaries
  private def isIn(p: (Int, Int)): Boolean =
    (p._1 >= 0 && p._1 < level(0).length) &&
      (p._2 >= 0 && p._2 < level.length)

  // Check whether or not there is a valid path between two points
  private def pathExist(array: Array[Array[Int]], from: (Int, Int), to: (Int, Int)): Boolean = {
    //array foreach { row => row foreach print; println }
    //print(from + "=>")
    val VISITED = -1

    if (!isIn(from) || !isIn(to))
      false
    else if (from == to)
      true
    else {
      array(from._1)(from._2) match {
        case VISITED => false
        case WALL => false
        case EXIT => false
        case _ =>
          array(from._1)(from._2) = VISITED // mark as visited
          pathExist(array, (from._1 + 1, from._2), to) ||
            pathExist(array, (from._1 - 1, from._2), to) ||
            pathExist(array, (from._1, from._2 + 1), to) ||
            pathExist(array, (from._1, from._2 - 1), to)
      }
    }
  }

  // Generate a random Cell of the given type, it makes sure that
  // if the cell is a wall, there is still a path from entry to exit,
  // if the cell is a bonus, there is a path from the entry to the bonus
  private def generateCells(emptyCells: IndexedSeq[(Int, Int)], number: Int, cellType: Int): Unit = {
    if (emptyCells.length > 0) {
      val r = scala.util.Random
      if (number > 0) {
        val nextCells = if(emptyCells.length > (nbCols*nbRows*0.8).toInt) r.nextInt(emptyCells.length/2)+ emptyCells.length/4 else  r.nextInt(emptyCells.length)
        val elem = emptyCells(nextCells)
        val newEmptyCells = emptyCells.filter(x => x != elem)
        level(elem._1)(elem._2) = cellType
        if (pathExist(level.map(_.clone()), ENTRY_POINT, if (cellType == WALL) EXIT_POINT else (elem._1, elem._2))) {
          generateCells(newEmptyCells, number - 1, cellType)
        }
        else {
          level(elem._1)(elem._2) = EMPTY
          generateCells(newEmptyCells, number, cellType)
        }
      }
    }
  }

  // Return all the index of the empty cells of level
  def emptyCells = {
    var result = for {
      i <- 0 until level.length
      j <- 0 until level(0).length
    } yield (i, j)
    result.filter(x => level(x._1)(x._2) == EMPTY)
  }

  // Generate a level
  def generateLevel(): Array[Array[Int]] = {
    // Place the entry
    level(ENTRY_POINT._1)(ENTRY_POINT._2) = ENTRY

    // Place the exit
    level(EXIT_POINT._1)(EXIT_POINT._2) = EXIT

    // Place the Walls
    generateCells(emptyCells, nbWalls, WALL)

    // Place the Red Bonus
    generateCells(emptyCells, nbRedBonus, LIFE)

    // Place the Yellow Bonus
    generateCells(emptyCells, nbYellowBonus, SHOW)

    return level
  }
}
