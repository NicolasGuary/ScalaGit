package objects

import java.io.File

import utils.IOManager


object Blob {

  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  def convertToBlob(file: File, tree: Option[Tree]): Option[Tree] = {
    val hash = getHashFromFile(file)
    IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
    tree match {
      case Some(tree) => {
        val newTree = tree.addElement("blob", getHashFromFile(file), file.getName)
        tree.set_items(newTree)
      }
      case _ => None
    }
    tree
  }

  def getHashFromFile(file: File): String = {
    IOManager.hash(IOManager.readFile(file))
  }
}
