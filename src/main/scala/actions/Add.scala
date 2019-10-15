package actions

import java.io.File
import objects.Blob
import utils.IOManager

object Add {

  def add(paths: Seq[String]): Unit = {
    println("Adding files...")
    paths.map(path =>
      if(new File(path).exists()) {
          doAdd(path)
      }
      else {
          println(s"Error - File ${path} doesn't exist")
      })
  }

  def doAdd(path: String): Unit = {
    val file = new File(path)
    if (file.isDirectory){
      val allObjects = IOManager.exploreDirectory(file)
      allObjects.map(item => if(item.isFile) {Blob.convertToBlob(item)})
    } else if (file.isFile){
        Blob.convertToBlob(file)
      }
  }
}


