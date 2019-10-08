package actions

import java.io.File

import objects.Blob
import objects.Stage
import utils.IOManager

object Add {

  def add(path: String): Unit = {
    val paths = path.split(" ")
    val stage_paths = Stage.retrieveStagePaths()

    paths.map(path =>
      if(new File(path).exists() && !stage_paths.contains(path)) {doAdd(path)}
    else {
        println(s"Error - File ${path} doesn't exist or is already added.")
      })
  }

  def doAdd(path: String): Unit = {
    val file = new File(path)
    if (file.isDirectory){
      val allObjects = IOManager.exploreDirectory(file)
      println("Adding files...")
      allObjects.map(x => println(x))
      allObjects.map(item => if(item.isFile) {Blob.convertToBlob(item)})
    } else if (file.isFile){
      println(s"Adding file: ${path}")
      Blob.convertToBlob(file)
    }

  }

}


