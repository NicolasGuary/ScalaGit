package actions

import java.io.File
import java.util.{Calendar, Date}

import objects.Entry
import objects.Tree
import objects.Stage
import objects.Index
import utils.{IOManager, PathManager}

import scala.annotation.tailrec

/*
* This is the class that handles the commit
* It reads the STAGE and uses its content to build the corresponding Tree hierarchy.
 */

case class Commit(var id: String ="", var master_tree: Tree = new Tree(), var parent_commit_id: String ="", var author: String = "NicolasGuary", var timestamp: Date = new Date()) {

  def get_id(): String = {
    this.id
  }

  def set_id (id: String): Unit = {
    this.id = id
  }

  def get_master_tree(): Tree = {
    this.master_tree
  }

  def set_master_tree (master_tree: Tree): Unit = {
    this.master_tree = master_tree
  }

  def get_parent_commit_id(): String = {
    this.parent_commit_id
  }

  def set_parent_commit_id (parent_commit_id: String): Unit = {
    this.parent_commit_id = parent_commit_id
  }

  def get_author(): String = {
    this.author
  }

  def set_author (author: String): Unit = {
    this.author = author
  }

  def get_timestamp(): Date = {
    this.timestamp
  }

  def set_timestamp(timestamp: Date): Unit = {
    this.timestamp = timestamp
  }

  //Returns the content of the Commit that should get hashed to get the new Commit id
  def commitContent(): String = {
    s"tree ${this.master_tree.get_id()}\nauthor ${this.get_author()}\nparent ${this.get_parent_commit_id()}\ntimestamp ${this.get_timestamp()}\n"
  }

  def commitContentForLog(): String = {
    s"commit ${this.get_id()}\ntree ${this.master_tree.get_id()}\nauthor ${this.get_author()}\nparent ${this.get_parent_commit_id()}\ntimestamp ${this.get_timestamp()}\n"
  }

  def save(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}commit${File.separator}${this.get_id()}" , commitContent())
  }

  def set_current_commit(): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}", this.get_id())
  }

  def record_in_logs(): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}", this.commitContentForLog())
  }
}

//TODO - Blob in tree OK but for the Tree the name is the name of the current Entry and not the child tree contained.
object Commit {
  def apply(): Commit = {
    new Commit
  }

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

//TODO - master should be replaced by Branch.getCurrent()
  def generateCommit(master_tree: Tree): Commit = {
    val new_commit = new Commit()
    val parent_commit_id = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}"))
    val timestamp = Calendar.getInstance().getTime()
    new_commit.set_parent_commit_id(parent_commit_id)
    new_commit.set_timestamp(timestamp)
    new_commit.set_master_tree(master_tree)
    val id = IOManager.hash(new_commit.commitContent())
    new_commit.set_id(id)

    // save into objects/commit
    new_commit.save()
    //change the ref in refs/heads/master
    new_commit.set_current_commit()
    //record the commit in the logs
    new_commit.record_in_logs()
    new_commit
  }

  //Creates the commit tree from the lists for trees and blobs containing their entries
  //Returns the Tree generated for the commit tree
  def generateCommitTree(result: List[Entry], root_blobs: List[Entry]): Tree = {
    Tree.createTree(result ::: root_blobs)
  }

  //Browse all the paths received and builds the tree objects from it
  //Returns a list of the parent Entries, will be used to create the commit tree

  @tailrec
  def addTrees(l: List[Entry], hashFinal: List[Entry]): List[Entry] = {
    if(l.isEmpty){
      hashFinal
    } else {
      val (deepest, rest, path_max) = PathManager.getDeepestDirectory(l)
      val hash = Tree.createTree(deepest).get_id()
      if(PathManager.getParentPath(path_max).isEmpty) {
        if (hashFinal.isEmpty){
          addTrees(rest, List(new Entry("tree" ,hash, path_max)))
        } else {
          addTrees(rest, new Entry("tree" ,hash, path_max) :: hashFinal)
        }
      } else {
        addTrees(new Entry("tree", hash, PathManager.getParentPath(path_max).get) :: rest, hashFinal)
      }
    }
  }
}
