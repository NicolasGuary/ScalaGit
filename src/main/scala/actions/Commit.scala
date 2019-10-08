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
      val (deeper, rest, path_max) = PathManager.getDeeperDirectory(l)
      val hash = Tree.createTree(deeper)
      if(PathManager.getParentPath(path_max).isEmpty) {
        if (hashFinal.isEmpty){
          println(s"hashfinal est-il vide? ${hashFinal.isEmpty} et hashfinal = {hashFinal.get.map(x=> x.getFileName())}")
          addTrees(rest, List(new Entry("tree" ,hash, path_max)))
        } else {
          println(s"hashfinal est-il vide? ${hashFinal.isEmpty} et hashfinal = ${hashFinal.map(x=> x.getFileName())}")
          addTrees(rest, new Entry("tree" ,hash, path_max) :: hashFinal)
        }
      } else {
        println(new Entry("tree", hash, PathManager.getParentPath(path_max).get) :: rest)
        addTrees(new Entry("tree", hash, PathManager.getParentPath(path_max).get) :: rest, hashFinal)
      }
    }
  }
}
