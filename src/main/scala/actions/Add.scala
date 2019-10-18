package actions

import java.io.File
import objects.Blob
import utils.IOManager
import better.files.{File => BFile}

object Add {

  def add(paths: Seq[String]): Unit = {
    println("Adding files...")
    paths.map(path =>
      if(new File(path).exists()) {
          doAdd(path)
      }
      else if (evaluateRegex(path).nonEmpty){
        evaluateRegex(path).map(item => doAdd(item.pathAsString))
      } else {
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

  /**
   *
   * @param regex
   * @return a list of files matching the regex passed in arguments
   */
  def evaluateRegex(regex: String) = {
    val repo = BFile(IOManager.getRepoDirPath().get)
    val results = repo.glob(regex).toList
    results
  }
}


