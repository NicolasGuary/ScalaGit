package actions

import java.io.File
import objects.Blob
import objects.Tree

object Add {

  def add(path: String): Unit = {
    path.split(" ").map(x =>addChild(x, None))
  }

  def addChild(path: String, parent: Option[Tree]): Unit = {

    val file = new File(path)

    if(file.isDirectory){
      Tree.convertToTree(file, parent)
    } else if (file.isFile) {
      Blob.convertToBlob(file, parent)
    } else {
      println("Unknown type")
    }
  }

}


