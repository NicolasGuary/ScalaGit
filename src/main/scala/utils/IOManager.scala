package utils

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}


// This is an utility class for writing and managing files and directories
object IOManager {

  //Create a file with the name passed in parameters
  //Returns true if it worked, false otherwise
  def createFile(name: String): Boolean ={
    new File(name).createNewFile()
  }
  //Create all the files with the name passed in parameters
  //Returns true if it worked, false otherwise
  def createFiles(names: List[String]): List[Boolean] = {
    names.map(name => createFile(name))
  }

  //Create a directory with the name passed in parameters
  //Returns true if it worked, false otherwise
  def createDirectory(name: String): Boolean = {
    new File(name).mkdirs()
  }

  //Create all the directories with the names passed in parameters
  //Returns true if it worked for the given directory, false otherwise
  def createDirectories(names: List[String]): List[Boolean] = {
    names.map(name => createDirectory(name))
  }

  //Write the content of s into filename
  def writeFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }

  def fileExists(path: String): Boolean ={
    Files.exists(Paths.get(path))
  }
}