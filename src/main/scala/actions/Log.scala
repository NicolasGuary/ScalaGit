package actions


import Console.{RESET, YELLOW}
import objects.Commit
import utils.IOManager
import utils.diff.Differ

object Log {

  /**
   * handles the log command
   */
  def log() = {
    getAllCommits() match {
      case Some(commits: List[Commit]) => commitContentForLog(commits.reverse)
      case None => println("No logs to display - You should commit first.")
    }
  }

  /**
   * handles the log patch command
   */
  def logPatch() = {
    getAllCommits() match {
      case Some(commits: List[Commit]) => commits.reverse.map(commit => printPatch(commit, commits))
      case None => println("No logs to display - You should commit first.")
    }
  }

  /**
   * handles the log stat command
   */
  def logStat() = {
    getAllCommits() match {
      case Some(commits: List[Commit]) => commits.reverse.map(commit => printStat(commit, commits))
      case None => println("No logs to display - You should commit first.")
    }
  }

  /**
   * @param commit
   * @param commits
   * @return
   */
  def printPatch(commit: Commit, commits: List[Commit]) = {
    //Print the log for the Commit
    printCommitLog(commit)

    //Print the diff between each commit
    val parent = commits.find(item => item.id.equals(commit.parent_commit_id))
    parent match {
      case Some(par_commit: Commit) => Differ.diffCommit(commit.master_tree.items, par_commit.master_tree.items)
      case None => Differ.diffCommit(commit.master_tree.items, List())
    }
  }

  /**
   * Prints the stat
   * @param commit
   * @param commits
   */
  def printStat(commit: Commit, commits: List[Commit]) = {
    //Print the log for the Commit
    printCommitLog(commit)
    //Print the stat for each commit
    val parent = commits.find(item => item.id.equals(commit.parent_commit_id))
    parent match {
      case Some(par_commit: Commit) => Differ.statCommit(commit.master_tree.items, Some(par_commit.master_tree.items))
      case None => Differ.statCommit(commit.master_tree.items, None)
    }
  }

  /**
   * @return all the commits from the current branch as a list of Commit
   */
  def getAllCommits(): Option[List[Commit]] = {
    IOManager.readLog()
  }

  /**
   * prints the content of a list of commits
   * @param commits
   */
  def commitContentForLog(commits: List[Commit]): Unit = {
    commits.map(x => printCommitLog(x))
  }

  /**
   * prints the log for a commit
   * @param commit
   */
  def printCommitLog(commit: Commit) = {
    println(s"${YELLOW}commit: ${commit.id}${RESET}\ntree: ${commit.master_tree.id}\nauthor: ${commit.author}\nparent: ${commit.parent_commit_id}\ntimestamp: ${commit.timestamp}\n")
  }
}
