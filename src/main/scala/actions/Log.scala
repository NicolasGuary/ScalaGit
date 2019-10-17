package actions

import java.io.File

import Console.{RESET, YELLOW}
import objects.{Commit, Tree, Branch}
import utils.IOManager
import utils.diff.Differ

object Log {

  def log() = {
    val commits = getAllCommits()
    commitContentForLog(commits.reverse)
  }

  def logPatch() = {
    val commits = getAllCommits().reverse
    //Print the patch between the files from the Commit and his parent Commit
    commits.map(commit => printPatch(commit, commits))
  }

  /**
   * @param commit
   * @param commits
   * @return
   */
    //TODO - refactor with Option and pattern matching in Differ
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


  def logStat() = {
    val commits = getAllCommits().reverse
    commits.map(commit => printStat(commit, commits))
  }

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

  def getAllCommits(): List[Commit] = {
    val content = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}"))
    content.split("\n").map(x => x.split("_")).map(x => Commit(x(0), new Tree(Tree.getTreeEntries(x(1)),x(1)), x(2), x(3), x(4))).toList
  }

  def commitContentForLog(commits: List[Commit]): Unit = {
    commits.map(x => printCommitLog(x))
  }

  def printCommitLog(commit: Commit) = {
    println(s"${YELLOW}commit: ${commit.id}${RESET}\ntree: ${commit.master_tree.id}\nauthor: ${commit.author}\nparent: ${commit.parent_commit_id}\ntimestamp: ${commit.timestamp}\n")
  }
}
