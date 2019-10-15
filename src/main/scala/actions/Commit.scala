package actions

import java.io.File
import java.util.{Calendar}
import objects.{Commit, CommitEntry, Index, Stage, Tree}
import utils.{IOManager, PathManager}
import scala.annotation.tailrec

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
      val stage = Stage.getStageAsCommitEntries()
      val non_root = stage.filter(x => !root_blobs.contains(x))
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
   * @param master_tree
   * @return a new Commit generated using the master_tree
   */
  def generateCommit(master_tree: Tree): Commit = {
    val parent_commit_id = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${Branch.getCurrentBranch().name}"))
    val timestamp = Calendar.getInstance().getTime.toString
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
  def generateCommitTree(result: List[CommitEntry], root_blobs: List[CommitEntry]): Tree = {
    Tree.createTree(result ::: root_blobs)
  }

  /**
   * Browse all the paths received and builds the tree objects from it
   * @param l
   * @param commitTree the entries that should be pointed by the commit tree
   * @return a list of the root entries, will be used to create the commit tree
   */
  @tailrec
  def addTrees(l: List[CommitEntry], commitTree: List[CommitEntry]): List[CommitEntry] = {
    if(l.isEmpty){
      commitTree
    } else {
      val (deepest, rest, path_max) = PathManager.getDeepestDirectory(l)
      val hash = Tree.createTree(deepest).id
      if(PathManager.getParentPath(path_max).isEmpty) {
        if (commitTree.isEmpty){
          addTrees(rest, List(CommitEntry("tree" ,hash, path_max)))
        } else {
          addTrees(rest, CommitEntry("tree" ,hash, path_max) :: commitTree)
        }
      } else {
        addTrees(CommitEntry("tree", hash, PathManager.getParentPath(path_max).get, path_max) :: rest, commitTree)
      }
    }
  }
}
