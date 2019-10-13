package actions

import java.io.File

import utils.IOManager

object Init{

  //Initialize a new .sgit directory at the current location
  //Skips the initialization if a .sgit directory already exists at the current location

  def init(): Unit = {

    val listOfDirectories: List[String] = List(
      s".sgit${File.separator}objects${File.separator}blobs",
      s".sgit${File.separator}objects${File.separator}tree",
      s".sgit${File.separator}objects${File.separator}commit",
      s".sgit${File.separator}refs${File.separator}heads",
      s".sgit${File.separator}refs${File.separator}tags",
      s".sgit${File.separator}refs${File.separator}logs"
    )

    val listOfFiles: List[String] = List(
      s".sgit${File.separator}HEAD",
      s".sgit${File.separator}STAGE",
      s".sgit${File.separator}INDEX",
      s".sgit${File.separator}refs${File.separator}logs${File.separator}master",
      s".sgit${File.separator}refs${File.separator}heads${File.separator}master"
    )

      if(IOManager.fileExists(".sgit")){
        println("An sgit repository has already been initialized here.")
      } else {
        IOManager.createDirectories(listOfDirectories)
        IOManager.createFiles(listOfFiles)
        IOManager.writeFile(listOfFiles(0), "refs/heads/master")
        println("A new sgit repository has been initialized with success.")
      }
  }
}
