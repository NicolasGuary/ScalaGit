package objects

import java.io.File
import utils.IOManager

/*
* This class defines an object Tree that is saved on the .sgit/objects/tree folder
* items: a list of entries to write in the tree file, corresponding to its child Blob(s) and/or Tree(s)
* id: the hashed value of the content of the tree, used to identify the Tree
 */


case class Tree(var items: List[CommitEntry] = List(), var id: String = "") {

  def addElement(items: CommitEntry): List[CommitEntry] = {
    CommitEntry(items.content_type, items.hash, items.filepath, items.childpath) :: this.items
  }

  def set_items (items: List[CommitEntry]): Unit = {
    this.items = items
  }

  def set_id (id: String): Unit = {
    this.id = id
  }

  def saveTreeFile(id: String, items: List[CommitEntry]): Unit = {
    IOManager.overwriteFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}tree${File.separator}${id}" , treeContent(items))
  }

  def createTreeId(items: List[CommitEntry]): String = {
    val content = treeContent(items)
    IOManager.hash(content)
  }

  def treeContent(items: List[CommitEntry]): String = {
    var acc = ""
    items.map(x => acc = acc + x.content_type + " " + x.hash+ " "+ x.getFileName() + "\n")
    acc
  }
}

object Tree {

  def apply(): Tree = {
    new Tree
  }

  //Creates a new tree with a list of entries and save it in .sgit/object/tree
  def createTree(entries: List[CommitEntry]): Tree = {
    val tree = new Tree()
    entries.map(element => tree.set_items(tree.addElement(element)))
    val hash = tree.createTreeId(tree.items)
    tree.set_id(hash)
    tree.saveTreeFile(tree.id, tree.items)
    tree
  }
}

