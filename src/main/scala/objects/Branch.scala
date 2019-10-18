package objects

import java.io.File
import utils.IOManager

/**
 *
 * @param name
 */
case class Branch (name: String)

object Branch {
  def getCurrentBranch(): Branch = {
    val heads = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}HEAD"))
    new Branch(new File(heads).getName)
  }

  def getCurrentCommitHash(): Option[String] = {
    val branch_path = new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${getCurrentBranch().name}")
    val commit_hash = IOManager.readFile(branch_path)
    IOManager.readCommit(commit_hash)
  }

  def getCurrentCommit(): Option[Commit]= {
    val current_hash = getCurrentCommitHash()
    current_hash match {
      case Some(hash: String) => {
        IOManager.readCommit(hash) match {
          case Some(current_commit: String) => {
            val commit = current_commit.split("\n").map(x => x.split("_")).map(x => Commit(hash, new Tree(Tree.getTreeEntries(x(0)),x(0)), x(2), x(1), x(3)))
            Some(commit(0))
          }
          case None => None
        }
      }
      case None => None
    }
  }
}