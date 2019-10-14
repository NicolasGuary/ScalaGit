package utils

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest
import scala.annotation.tailrec
import better.files.{File => BFile}
import objects.Entry

// TODO - clean unused methods that were implemented first because I thought they would be useful...

// This is an utility class for writing and managing files and directories
object IOManager {

  var ignore: List[String]= List(".sgit", ".git", ".DS_Store", "project", ".idea", "target", "streams", "inputFileStamps")

  /**
   * Create a file with the name passed in parameters
   * @param name
   * @return true if it worked, false otherwise
   */
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

  //Add the content of s into filename
  def overwriteFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file, true))
    bw.write(s)
    bw.close()
  }

def readFile(file: File): String = {
  new String(Files.readAllBytes(Paths.get(file.getAbsolutePath)))
}

  def removeFile(file: File): Unit = {
    Files.delete(Paths.get(file.getAbsolutePath))
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

// Method that explores all the folder from the path in argument.
// It should also omit .sgit and .git folders so it doesn't add them to the repo.
// This method only retrieves the files
  def exploreDirectory(path: File): List[File] = {
      val allFiles = path.listFiles().toList
      allFiles.flatMap(item =>
          if (!ignore.contains(item.getName)) {
            if (item.isDirectory) {
              exploreDirectory(item)
            }
            else {
              List(item)
            }
          } else {
            List[File]()
          }
        )
  }

  // Method that explores all the folder from the path in argument.
  // It should also omit .sgit and .git folders so it doesn't add them to the repo.
  // This method only retrieves the files
  def exploreDirectoryAsEntries(path: File): List[Entry] = {
    val basedir = System.getProperty("user.dir")
    val allFiles = path.listFiles().toList
    allFiles.flatMap(item =>
      if (!ignore.contains(item.getName)) {
        if (item.isDirectory) {
          exploreDirectoryAsEntries(item)
        }
        else {
          List(new Entry("blob", getHashFromFile(item), BFile(basedir).relativize(BFile(item.getPath)).toString))
        }
      } else {
        List[Entry]()
      }
    )
  }

  /**
   * @param: String, the path, initialized at . (current path)
   * @return the path as String for the nearest .sgit repository or None if no .sgit repository is initialized from the current path to root.
   */
  @tailrec
  def getRepoDirPath(path: String = "."): Option[String] = {
    val currentCanonicalPath = new File(path).getCanonicalFile()
    val repository_dir = ".sgit"
    if (containsDirectory(new File(s"${currentCanonicalPath}${File.separator}${repository_dir}")))
      Some(s"${currentCanonicalPath}${File.separator}${repository_dir}")
    else {
      if (currentCanonicalPath.getParentFile() == null) None
      else getRepoDirPath(currentCanonicalPath.getParentFile().getCanonicalPath)
    }
  }

  /**
   *
   * @param s
   * @return the hashed value for the String s
   */
  def hash(s: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"))))
  }

  def getHashFromFile(file: File): String = {
    hash(readFile(file))
  }

  /**
   *
   * @param path
   * @return this path but relativized to the root of the sgit repository
   */
  def relativize(path: String): String = {
    val repo_dir = getRepoDirPath().get
    BFile(repo_dir).parent.relativize(BFile(path)).toString
  }
}