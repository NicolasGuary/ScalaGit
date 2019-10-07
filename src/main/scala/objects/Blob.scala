package objects

import java.io.File

import utils.IOManager


object Blob {

  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  def convertToBlob(file: File): Unit = {
    val hash = getHashFromFile(file)
    IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
    IOManager.overwriteFile( s".sgit${File.separator}STAGE", file.getPath+ " "+hash+"\n")
  }

  def getHashFromFile(file: File): String = {
    IOManager.hash(IOManager.readFile(file))
  }
}
