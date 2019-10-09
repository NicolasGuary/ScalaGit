package actions

import java.io.File
import objects.Blob
import utils.IOManager

object Add {

  def add(path: String): Unit = {
    val paths = path.split(" ")

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
    println("Adding files...")
    if (file.isDirectory){
      val allObjects = IOManager.exploreDirectory(file)
      allObjects.map(x => println(x))
      allObjects.map(item => if(item.isFile) {Blob.convertToBlob(item)})
    } else if (file.isFile){
        println(s"${path}")
        Blob.convertToBlob(file)
      }
  }
}


