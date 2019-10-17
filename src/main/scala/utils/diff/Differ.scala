package utils.diff

import objects.{Blob, Entry}
import utils.IOManager
import Console.{BLUE, GREEN, RED, RESET}

/**
 * Implementation of Myer's diff algorithm
 * Using the tutorial found at https://blog.jcoglan.com/2017/02/12/the-myers-diff-algorithm-part-1/
 */
object Differ {

  /**
   * Displays all the modifications to do to file1 to get to file2
   * @param file1
   * @param file2
   * @param filepath
   */
  def displayDiff(file1: Seq[String], file2: Seq[String], filepath: String): Unit = {
    println(s"${BLUE}Diff: ${filepath}${RESET}")
    val deltas = diffFiles(file1, file2)
    deltas.map(delta => delta.diff match {
      case Operations.ADD => println(s"    ${GREEN}${displayOperation(delta.diff)} ${delta.content}${RESET}")
      case Operations.REMOVE => println(s"    ${RED}${displayOperation(delta.diff)} ${delta.content}${RESET}")
      case Operations.KEEP => println(s"    ${displayOperation(delta.diff)} ${delta.content}")
    })
  }

  /**
   * Displays the diff between blobs from two commits
   * @param commit the first commit, as a list of Entry
   * @param old_commit the parent commit, as a list of Entry
   */
  def diffCommit(commit: List[Entry], old_commit: List[Entry]) = {
    val commit_blobs = Blob.getAllBlob(commit)
    val old_commit_blobs = Blob.getAllBlob(old_commit)
    if(old_commit_blobs.nonEmpty){
      val all_commits = commit_blobs.zip(old_commit_blobs)
      val commits = all_commits.filter(item => !item._1.hash.equals(item._2.hash))
      commits
        .map(tuple =>
          displayDiff(IOManager.readBlob(tuple._2.hash).split("\n"),
            IOManager.readBlob(tuple._1.hash).split("\n"), tuple._2.filepath))
    } else {
      commit_blobs
        .map(blob =>
          displayDiff(Seq(), IOManager.readBlob(blob.hash).split("\n"), blob.filepath))
    }
  }


  /**
   * Displays the stats between blobs from two commits
   *
   * @param commit the first commit, as a list of Entry
   * @param old_commit the parent commit, as an Option for a list of Entry (first commit doesn't have parent)
   */
  def statCommit(commit: List[Entry], old_commit: Option[List[Entry]]) = {
    val commit_blobs = Blob.getAllBlob(commit)
    val max_path_size = commit_blobs.maxBy(_.filepath.length).filepath.length

    old_commit match {
      case Some(old: List[Entry]) => {
        val old_commit_blobs = Blob.getAllBlob(old)
        val all_commits = commit_blobs.zip(old_commit_blobs)
        val commits = all_commits.filter(item => !item._1.hash.equals(item._2.hash))
        val total = commits
          .map(tuple =>
            displayStat(IOManager.readBlob(tuple._2.hash).split("\n"),
              IOManager.readBlob(tuple._1.hash).split("\n"), tuple._2.filepath, max_path_size))
        val additions = total.map(list => list.map(item => item._1)).map(_.sum).sum
        val deletions = total.map(list => list.map(item => item._2)).map(_.sum).sum
        printTotalStatistics(total.length, additions, deletions)
      }
      case None => {
       val total = commit_blobs
          .map(blob =>
            displayStat(Seq(), IOManager.readBlob(blob.hash).split("\n"), blob.filepath, max_path_size))
        val additions = total.map(list => list.map(item => item._1)).map(_.sum).sum
        val deletions = total.map(list => list.map(item => item._2)).map(_.sum).sum
        printTotalStatistics(total.length, additions, deletions)
      }
    }
  }

  def printTotalStatistics(total: Int, additions: Int, deletions: Int): Unit = {
    println(s"$total file changed, $additions insertions(+), $deletions deletions(-)")
  }

  /**
   * Show the stats between for two files (counts additions, deletions and prints the total)
   * @param file1 original file
   * @param file2 updated file
   * @param filepath path for the files
   */
  def displayStat(file1: Seq[String], file2: Seq[String], filepath: String, maxlength: Int): List[(Int, Int)] = {
    val deltas = diffFiles(file1, file2)
    val added = deltas.count(_.diff.equals(Operations.ADD))
    val deleted = deltas.count(_.diff.equals(Operations.REMOVE))
    println(s"$filepath ${" "*(maxlength - filepath.length)}  | ${added + deleted} ${GREEN}${"+"*added}${RESET} ${RED}${"-"*deleted}${RESET}")
    List((added, deleted))
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
