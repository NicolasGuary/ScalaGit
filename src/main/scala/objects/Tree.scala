package objects

import java.io.File
import utils.IOManager

/*
* This class defines an object Tree that is saved on the .sgit/objects/tree folder
* items: a list of entries to write in the tree file, corresponding to its child Blob(s) and/or Tree(s)
* id: the hashed value of the content of the tree, used to identify the Tree
 */


case class Tree(var items: List[Entry] = List(), var id: String = "") {

  def addElement(items: Entry): List[Entry] = {
    new Entry(items.get_content_type(), items.get_hash(), items.get_filepath()) :: this.get_items()
  }

  def get_items(): List[Entry] = {
    this.items
  }

  def set_items (items: List[Entry]): Unit = {
    this.items = items
  }

  def get_id(): String = {
    this.id
  }

  def set_id (id: String): Unit = {
    this.id = id
  }

  def saveTreeFile(id: String, items: List[Entry]): Unit = {
    IOManager.overwriteFile(s".sgit${File.separator}objects${File.separator}tree${File.separator}${id}" , treeContent(items))
  }

  def createTreeId(items: List[Entry]): String = {
    val content = treeContent(items)
    IOManager.hash(content)
  }

  def treeContent(items: List[Entry]): String = {
    var acc = ""
    items.map(x => acc = acc + x.get_content_type() + " " + x.get_hash()+ " "+ x.getFileName() + "\n")
    acc
  }
}

object Tree {

  def apply(): Tree = {
    new Tree
  }

  //Creates a new tree with a list of elements for the deepest path and save it in .sgit/object/tree
  def createTree(deeper: List[Entry]): String = {
    val tree = new Tree()
    deeper.map(element => tree.set_items(tree.addElement(element)))
    val hash = tree.createTreeId(tree.get_items())
    tree.set_id(hash)
    tree.saveTreeFile(tree.get_id(), tree.get_items())
    tree.get_id()
  }
}
