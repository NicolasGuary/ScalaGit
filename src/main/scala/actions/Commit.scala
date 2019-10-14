package actions

import java.io.File
import java.util.{Calendar, Date}
import objects.Entry
import objects.Tree
import objects.Stage
import objects.Index
import utils.{IOManager, PathManager}
import scala.annotation.tailrec

/**
 * This is the class that handles the commit
 * @param id
 * @param master_tree
 * @param parent_commit_id
 * @param author
 * @param timestamp
 */
case class Commit(var id: String ="", var master_tree: Tree = new Tree(), var parent_commit_id: String ="", var author: String = "NicolasGuary", var timestamp: Date = new Date()) {

  //Returns the content of the Commit that should get hashed to get the new Commit id
  def commitContent(): String = {
    s"tree ${this.master_tree.get_id()}\nauthor ${this.author}\nparent ${this.parent_commit_id}\ntimestamp ${this.timestamp}\n"
  }

  def commitContentForLog(): String = {
    s"commit ${this.id}\ntree ${this.master_tree.get_id()}\nauthor ${this.author}\nparent ${this.parent_commit_id}\ntimestamp ${this.timestamp}\n"
  }

  def save(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}commit${File.separator}${this.id}" , commitContent())
  }

  def set_current_commit(): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}", this.id)
  }

  def record_in_logs(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}", this.commitContentForLog())
  }
}

object Commit {
  def apply(): Commit = {
    new Commit
  }

  /**
   * handles the sgit commit command.
   */
  def commit(): Unit = {
    if(Index.getIndexAsEntries().entries.nonEmpty){
      val root_blobs = Stage.retrieveStageRootBlobs()
      val stage = Stage.getStageAsEntries()
      val non_root = stage.entries.filter(x => !root_blobs.contains(x))
      val result = addTrees(non_root, List())
      val master_tree = generateCommitTree(result, root_blobs)
      generateCommit(master_tree)
      //Everything in the stage has been commit, we can now clear the index.
      Index.clear()
    } else {
      println("Warning - Nothing to commit \n  (use \"sgit add <file>...\" to stage files to be committed)")
    }

  }

  /**
   *
   * @param master_tree
   * @return a new Commit generated using the master_tree
   */
  def generateCommit(master_tree: Tree): Commit = {
    val parent_commit_id = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}"))
    val timestamp = Calendar.getInstance().getTime()
    val commit = new Commit(parent_commit_id = parent_commit_id, timestamp = timestamp, master_tree = master_tree)
    val id = IOManager.hash(commit.commitContent())
    val new_commit = commit.copy(id = id)
    // save into objects/commit
    new_commit.save()
    //change the ref in refs/heads/master
    new_commit.set_current_commit()
    //record the commit in the logs
    new_commit.record_in_logs()
    new_commit
  }

  /**
   * Creates the commit tree from the lists for trees and blobs containing their entries
   * @param result
   * @param root_blobs
   * @return the Tree generated for the commit tree
   */
  def generateCommitTree(result: List[Entry], root_blobs: List[Entry]): Tree = {
    Tree.createTree(result ::: root_blobs)
  }


  //TODO - for the Tree the name is the name of the current Entry and not the child tree contained.
  /**
   * Browse all the paths received and builds the tree objects from it
   * @param l
   * @param commitTree the entries that should be pointed by the commit tree
   * @return a list of the root entries, will be used to create the commit tree
   */
  @tailrec
  def addTrees(l: List[Entry], commitTree: List[Entry]): List[Entry] = {
    if(l.isEmpty){
      commitTree
    } else {
      val (deepest, rest, path_max) = PathManager.getDeepestDirectory(l)
      val hash = Tree.createTree(deepest).id
      if(PathManager.getParentPath(path_max).isEmpty) {
        if (commitTree.isEmpty){
          addTrees(rest, List(new Entry("tree" ,hash, path_max)))
        } else {
          addTrees(rest, new Entry("tree" ,hash, path_max) :: commitTree)
        }
      } else {
        addTrees(new Entry("tree", hash, PathManager.getParentPath(path_max).get) :: rest, commitTree)
      }
    }
  }
}
