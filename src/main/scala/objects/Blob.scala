package objects

import java.io.File
import java.nio.file.Files
import java.security.MessageDigest

import utils.IOManager


object Blob {

  val md: MessageDigest = java.security.MessageDigest.getInstance("SHA-256")
  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  def convertToBlob(file: File): Unit = {
    //val hash = new sun.misc.BASE64Encoder().encode(md.digest(Files.readAllBytes(file.toPath)))
    val hash = getHashFromFile(file)
    IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${hash}",
      IOManager.readFile(file))
  }

  def getHashFromFile(file: File): String = {
    //val hash = new sun.misc.BASE64Encoder().encode(md.digest(Files.readAllBytes(file.toPath)))
    val hash = IOManager.hash(IOManager.readFile(file))
    hash
  }
}
