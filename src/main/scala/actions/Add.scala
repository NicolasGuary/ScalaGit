package actions

import java.io.File

import objects.Blob
import utils.IOManager

object Add {
//TODO - test if path exsits

  def add(path: String): Unit = {
    val file = new File(path)
    if (file.isDirectory){
      val allObjects = IOManager.exploreDirectory(file)
      println("Adding files...")
      allObjects.map(x => println(x))
      allObjects.map(item => if(item.isFile) {Blob.convertToBlob(item)})
    } else if (file.isFile){
      Blob.convertToBlob(file)
    }

  }

}


