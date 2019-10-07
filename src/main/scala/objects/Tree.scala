package objects

import java.io.File

import utils.IOManager

case class Tree(var items: List[(String, String, String)] = List(), var id: String = "") {

  def addElement(typeElem: String, id: String, filename: String): List[(String, String, String)] = {
    (typeElem, id, filename) :: this.get_items()
  }

  def get_items(): List[(String, String, String)] = {
    this.items
  }


  def set_items (items: List[(String, String, String)]): Unit = {
    this.items = items
  }

  def get_id(): String = {
    this.id
  }

  def set_id (id: String): Unit = {
    this.id = id
  }

  def saveTreeFile(id: String, items: List[(String, String, String)]): Unit = {
    IOManager.overwriteFile(s".sgit${File.separator}objects${File.separator}tree${File.separator}${id}" , treeContent(items))
  }

  def createTreeId(items: List[(String, String, String)]): String = {
    val content = treeContent(items)
    IOManager.hash(content)
  }

  def treeContent(items: List[(String, String, String)]): String = {
    var acc = ""
    items.map(x => acc = acc + x._1 + " " + x._2 +" "+ x._3 + "\n")
    acc
  }
}

object Tree {

  def apply(): Tree = {
    new Tree
  }

  //Converts the current file into a Tree object if it's a folder.
  //It should point to any item it contains and indicate if it's a Tree or a Blob.
  // The id of the tree is the hash of all it's content after it is listed
  // The id of a subtree is the hash of all it's content

  //def convertToTree(file: File, parent: Option[Tree]): Option[Tree] = {
  //}




}
