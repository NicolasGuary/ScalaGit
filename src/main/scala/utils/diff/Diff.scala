package utils.diff

import Console.{GREEN, RED, RESET}
//Implementation of Myer's diff algorithm
//Using the tutorial found at https://blog.jcoglan.com/2017/02/12/the-myers-diff-algorithm-part-1/

object Diff {

  /**
   * Displays all the modifications to do to file1 to get to file2
   * @param file1
   * @param file2
   *
   */
  def displayDiff(file1: Seq[String], file2: Seq[String]): Unit = {
    val deltas = diffFiles(file1, file2)
    deltas.map(delta => delta.diff match {
      case Operations.ADD => println(s"${GREEN}${delta.index} ${displayOperation(delta.diff)} ${delta.content}${RESET}")
      case Operations.REMOVE => println(s"${RED}${delta.index} ${displayOperation(delta.diff)} ${delta.content}${RESET}")
      case _ => println(s"${delta.index} ${displayOperation(delta.diff)} ${delta.content}")
    } )
  }

  /**
   *
   * @param operation
   * @return a String used to get a more readable output of the Diff.
   */
  def displayOperation(operation: Operations.Value): String = {
    operation match {
      case Operations.ADD => "+"
      case Operations.REMOVE => "-"
      case Operations.KEEP => ""
    }
  }

  /**
   *
   * @param file1
   * @param file2
   * @return A seq of the deltas between two files represented as Seq[String].
   *         A Delta is the modifications to apply line by line from text 1 to get to text 2.
   */
  def diffFiles (file1: Seq[String], file2: Seq[String]) : Seq[Delta] = {

    def aux(f1: Seq[String], f2: Seq[String], diffs: Seq[Delta], index: Int) : Seq[Delta] = {
      // Is both seq are empty, all differences have been computed (or nothing to compute)
      if (f1.isEmpty && f2.isEmpty) {
        return diffs
      }

      //If text1 is empty, we need to append all the lines from text2
      if (f1.isEmpty) {
        return aux(f1, f2.tail, diffs :+ Delta(Operations.ADD, index, f2.head), index + 1)
      }

      //If text2 is empty, we need to remove all the lines from text1
      if (f2.isEmpty) {
        return aux(f1.tail, f2, diffs :+ Delta(Operations.REMOVE, index, f1.head), index + 1)
      }

      //If the two lines are equal, we move to the next step (diagonal)
      if (f1.head.equals(f2.head)) {
        aux(f1.tail, f2.tail, diffs:+ Delta(Operations.KEEP, index, f1.head), index + 1)
      } else {
        //GREEDY ALGORITHM: If not equals, we try both solutions as we don't know the best one yet and we compare the results
        // We can either:
        //    - REMOVE file1 line or
        //    - ADD file2 line
        // I.E: Move to the right or down in the graph
        // We keep the best one (solution with less deltas to do)
        val delta_1 = aux(f1, f2.tail, diffs :+ Delta(Operations.ADD, index, f2.head), index + 1)
        val delta_2 = aux(f1.tail, f2, diffs :+ Delta(Operations.REMOVE, index, f1.head), index + 1)
        if (delta_1.length < delta_2.length) delta_1 else delta_2
      }
    }
    aux(file1, file2, Seq(), 0)
  }
}
