package actions

import objects.Entry
import objects.Tree
import objects.Stage
import utils.PathManager

import scala.annotation.tailrec

/*
* This is the class that handles the commit
* It reads the STAGE and uses its content to build the corresponding Tree hierarchy.
 */

//TODO - Blob in tree OK but for the Tree the name is the name of the current Entry and not the child tree contained.

object Commit {

  def commit(): Unit = {

    val root_blobs = Stage.retrieveStageRootBlobs()
    val stage = Stage.retrieveStageStatus()
    val non_root = stage.filter(x => !root_blobs.contains(x))
    val result = addTrees(non_root, List())

    //generateCommitTree(result, root_blobs)


  }

  //Creates the commit tree from the lists for trees and blobs containing their entries
  //Returns the entry generated for the commit tree
  def generateCommitTree(result: List[Entry], root_blob: List[Entry]): List[Entry] = {
    val tree = new Tree()
    //trees.map(element => tree.set_items(tree.addElement(element)))
    val hash = tree.createTreeId(tree.get_items())
    tree.set_id(hash)
    tree.saveTreeFile(tree.get_id(), tree.get_items())
    tree.get_id()
    tree.get_items()
  }

  //Browse all the paths received and builds the tree objects from it
  //Returns a list of the parent Entries, will be used to create the commit tree

  @tailrec
  def addTrees(l: List[Entry], hashFinal: List[Entry]): List[Entry] = {
    if(l.isEmpty){
      hashFinal
    } else {
      val (deepest, rest, path_max) = PathManager.getDeepestDirectory(l)
      val hash = Tree.createTree(deepest)
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
