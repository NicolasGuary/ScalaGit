package objects

import java.io.File

import utils.IOManager


object Blob {

  //Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
  //It also has the responsibility to update the STAGE file.
  def convertToBlob(file: File): Unit = {
    val hash = IOManager.getHashFromFile(file)
    val entry = new Entry("blob", hash, file.getPath)

    val stage = Stage.getStageAsEntries()
    val index = Index.getIndexAsEntries()
    if(Stage.pathStaged(entry, stage)){
      println(s"file ${entry.get_filepath()} was already staged. Updating reference.")
      IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${entry.get_hash()}", IOManager.readFile(file))
      val updated_stage = Stage.updateEntry(entry, stage)
      val updated_index = Index.updateEntry(entry, index)
      Stage.clear()
      Index.clear()
      updated_stage.entries.map(x => IOManager.overwriteFile( s".sgit${File.separator}STAGE", x.get_filepath()+ " "+x.get_hash()+"\n"))
      updated_index.entries.map(x => IOManager.overwriteFile( s".sgit${File.separator}INDEX", x.get_filepath()+ " "+x.get_hash()+"\n"))
    } else {
      println(s"${entry.get_filepath()}")
      IOManager.writeFile(s".sgit${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
      IOManager.overwriteFile( s".sgit${File.separator}STAGE", file.getPath+ " "+hash+"\n")
      IOManager.overwriteFile( s".sgit${File.separator}INDEX", file.getPath+ " "+hash+"\n")
    }

  }
}
