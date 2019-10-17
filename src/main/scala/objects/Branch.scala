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
    val branch_path = s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${getCurrentBranch().name}"
    new File(branch_path).exists() match {
      case true =>  Some(IOManager.readFile(new File(branch_path)))
      case false => None
    }
  }

  def getCurrentCommit(): Option[Commit]= {
    val current_hash = getCurrentCommitHash()
    current_hash match {
      case Some(hash: String) => {
        val commit_content_string = IOManager.readCommit(hash)
        val commit = commit_content_string.split("\n").map(x => x.split("_")).map(x => Commit(hash, new Tree(Tree.getTreeEntries(x(0)),x(0)), x(2), x(1), x(3)))
        Some(commit(0))
      }
      case None => None
    }
  }
}