package utils

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

import scala.io.Source
import scala.annotation.tailrec


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

def readFile(file: File): String = {
  new String(Files.readAllBytes(Paths.get(file.getAbsolutePath)))
}

  def fileExists(path: String): Boolean ={
    Files.exists(Paths.get(path))
  }


  //Returns all the files found in the current directory described by path
  //Returns an empty list if no elements found
  //TODO - Maybe check if the path exists first
  def getAllFilesFromCurrentDirectory(path: String): List[File] = {
    val d = new File(path)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  //Returns all the directories found in the current directory described by path
  //Returns an empty list if no elements found
  //TODO - Maybe check if the path exists first
  def getAllDirectoriesFromCurrentDirectory(path: String): List[File] = {
    val d = new File(path)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isDirectory).toList
    } else {
      List[File]()
    }
  }

  //Returns everything at root of the path. Does not explore the folders (not recursive)
  //Returns an empty list if no elements found
  def getAllFromCurrentDirectory(path: String): List[File] = {
    val d = new File(path)
    if (d.exists && d.isDirectory) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }

  //Returns true if the path contains a directory and false otherwise
  def containsDirectory(path: File): Boolean= {
    path.exists && path.isDirectory
  }

// TODO - Method that explores all the folder from the path in argument. It should also omit .sgit folder so it doesn't add them to the repo.
  def exploreDirectory(d: File): Unit ={
    if(containsDirectory(d)){
      getAllFromCurrentDirectory(d.getAbsolutePath).map(file => if(file.isFile){
        println(file.getName)
      } else {
        exploreDirectory(file)
        println(file.getName)
      })
    }
  }

  def hash(s: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest((s).getBytes("UTF-8"))))
  }
}