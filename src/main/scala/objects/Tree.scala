package objects

import java.io.File

import utils.IOManager

object Tree {

  //Converts the current file into a Tree object if it's a folder.
  //It should point to any item it contains and indicate if it's a Tree or a Blob.
  // The id of the tree is the hash of all it's content after it is listed
  // The id of a subtree is the hash of all it's content

  def convertToTree(file: File){
    var treeEntries  = List[String]()
    if(file.isDirectory){
      val content = IOManager.getAllFromCurrentDirectory(file.getAbsolutePath)
      content.map(item => if(item.isFile){
        val hash = Blob.getHashFromFile(item)
        treeEntries ::= s"blob ${hash} ${item.getName}\n"
      } else {
        val hash = getHashFromTree(item)
        treeEntries ::= s"tree ${hash} ${item.getName}\n"
      })
    }
  }

  //generate the hash for a given tree based on its content from .sgit/objects/tree
  def getHashFromTree(tree: File): String = {

    val treeObject = findTree(tree)
    val hash = IOManager.hash(treeObject)
    hash
  }

  //Returns the content of the tree object stored in .sgit/objects/tree
  def findTree(tree: File): Option[String] = {
    val items = IOManager.getAllFilesFromCurrentDirectory(s".sgit${File.separator}objects${File.separator}tree")
    items.filter(_.getName.equals(getHashFromTree(tree)))
    if(items.isEmpty){
      println("Tree not found ! ")
      None
    } else {
      Some(IOManager.readFile(items(0)))
    }
  }
}
