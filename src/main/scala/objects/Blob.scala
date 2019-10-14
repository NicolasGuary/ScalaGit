package objects

import java.io.File

import utils.IOManager


object Blob {

  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  //It also has the responsibility to update the STAGE file.
  def convertToBlob(file: File): Unit = {
    val hash = IOManager.getHashFromFile(file)
    val entry = new Entry("blob", hash, IOManager.relativize(file.getPath))
    val stage = Stage.getStageAsEntries()
    val index = Index.getIndexAsEntries()
    if(Stage.pathStaged(entry, stage)){
      if(Index.pathStaged(entry, index)){
        println(s"file ${entry.get_filepath()} was already staged. Updating reference.")
      } else {
        println(s"${entry.get_filepath()}")
      }
      IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}blobs${File.separator}${entry.get_hash()}", IOManager.readFile(file))
      val updated_stage = Stage.updateEntry(entry, stage)
      val updated_index = Index.updateEntry(entry, index)
      Stage.clear()
      Index.clear()
      updated_stage.entries.map(x => IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}STAGE", x.get_filepath()+ " "+x.get_hash()+"\n"))
      updated_index.entries.map(x => IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}INDEX", x.get_filepath()+ " "+x.get_hash()+"\n"))
    } else {
      println(s"${entry.get_filepath()}")
      IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
      IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}STAGE", IOManager.relativize(file.getPath)+ " "+hash+"\n")
      IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}INDEX", IOManager.relativize(file.getPath)+ " "+hash+"\n")
    }

  }
}
