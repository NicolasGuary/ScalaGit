package objects

import java.io.File
import utils.IOManager

/**
 * This class defines an object Tree that is saved on the .sgit/objects/tree folder
 * @param items a list of entries to write in the tree file, corresponding to its child Blob(s) and/or Tree(s)
 * @param id the hashed value of the content of the tree, used to identify the Tree
 */
case class Tree(var items: List[Entry] = List(), var id: String = "") {

  def saveTreeFile(id: String, items: List[Entry]): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}tree${File.separator}${id}" , treeContent(items))
  }

  def createTreeId(items: List[Entry]): String = {
    val content = treeContent(items)
    IOManager.hash(content)
  }

  def treeContent(items: List[Entry]): String = {
    var acc = ""
    items.map(x => acc = acc + x.content_type + " " + x.hash+ " "+ x.getFileName() + "\n")
    acc
  }
}

object Tree {
  def apply(): Tree = {
    new Tree
  }

  /**
   * @param hash the hash for the Tree
   * @return all the entries stored in the corresponding Tree
   */
  def getTreeEntries(hash: String): List[Entry] = {
    val tree_content = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}tree${File.separator}${hash}"))
    val tree = tree_content.split("\n").map(x => x.split(" "))
    val paths = tree.map(x => x(2)).toList
    val hashs = tree.map(x => x(1)).toList
    val type_entry = tree.map(x => x(0)).toList
    val entries = (paths, hashs, type_entry).zipped.toList.map(x => Entry(x._3, x._2, x._1))
    entries
  }

  /**
   * Creates a new tree with a list of entries and save it in .sgit/object/tree
   * @param entries
   * @return the new Tree made from the entries
   */
  def createTree(entries: List[Entry]): Tree = {
    val new_tree = new Tree(entries)
    val hash = new_tree.createTreeId(new_tree.items)
    val tree = new_tree.copy(id = hash)
    tree.saveTreeFile(tree.id, tree.items)
    tree
  }
}

