package objects

import java.io.File
import utils.IOManager
import objects.Branch

/**
 * This is the class that handles the commit
 *
 * @param id commit hash
 * @param master_tree commit tree
 * @param parent_commit_id hash of the parent commit
 * @param author constant. The name of the comitter
 * @param timestamp date & time of the commit creation
 */
case class Commit(var id: String ="", var master_tree: Tree = new Tree(), var parent_commit_id: String ="", var author: String = "NicolasGuary", var timestamp: String="") {

  /***
   * @return the content of the Commit that should get hashed to get the new Commit id
   */
  def commitContent(): String = {
    s"${this.master_tree.id}_${this.author}_${this.parent_commit_id}_${this.timestamp}\n"
  }

  /**
   *
   * @return the content of the Commit that should get saved in the logs
   */
  def commitContentLogs(): String = {
    s"${this.id}_${this.master_tree.id}_${this.parent_commit_id}_${this.author}_${this.timestamp}\n"
  }

  /**
   * write the commit into the .sgit directory
   */
  def save(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}commit${File.separator}${this.id}" , commitContent())
  }

  /**
   * write the commit into the branch
   */
  def write_commit(): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}", this.id)
  }

  /**
   * write the commit into the logs
   */
  def record_in_logs(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}", this.commitContentLogs())
  }
}
