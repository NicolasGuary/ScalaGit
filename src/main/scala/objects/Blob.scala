package objects

import java.io.File

import utils.IOManager


object Blob {

  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  //It also has the responsibility to update the STAGE file.
  def convertToBlob(file: File): Unit = {
    val hash = getHashFromFile(file)
    val entry = new Entry("blob", hash, file.getPath)

    var stage = Stage.getStageAsEntries()

    if(stage.pathStaged(entry)){
      println(s"file ${entry.get_filepath()} was already staged. Updating reference.")
      IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${entry.get_hash()}", IOManager.readFile(file))
      stage = stage.updateEntry(entry)
      Stage.clear()
      stage.get_entries().map(x => IOManager.overwriteFile( s".sgit${File.separator}STAGE", x.get_filepath()+ " "+x.get_hash()+"\n"))
    } else {
      IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
      IOManager.overwriteFile( s".sgit${File.separator}STAGE", file.getPath+ " "+hash+"\n")
    }
  }

  def getHashFromFile(file: File): String = {
    IOManager.hash(IOManager.readFile(file))
  }
}
