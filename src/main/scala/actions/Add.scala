package actions

import objects.Blob
import objects.Tree
import utils.IOManager

object Add {

  def add(path: String): Unit = {

    println(s"Adding all files from ${path}")
    println("Here are all the files from the current path:")

    val elements = IOManager.getAllFilesFromCurrentDirectory(path)
    elements.map(x => println(x))
    elements.map(x => Blob.convertToBlob(x))

    val dirs = IOManager.getAllDirectoriesFromCurrentDirectory(path)
    dirs.map(x => println(x))
    dirs.map(x => Tree.convertToTree(x))

  }

}

