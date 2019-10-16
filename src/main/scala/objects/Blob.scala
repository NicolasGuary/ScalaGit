package objects

import java.io.File

import utils.IOManager


object Blob {

  /**
   * Converts the file in parameters to a blob and stores it into the .sgit/objects/blobs folder
   * It also has the responsibility to update the STAGE and INDEX files.
   * @param file
   */
  def convertToBlob(file: File): Unit = {
    val hash = IOManager.getHashFromFile(file)
    val entry = new Entry("blob", hash, IOManager.relativize(file.getPath))
    val stage = Stage.getStageAsEntries()
    val index = Index.getIndexAsEntries()
    if(Stage.pathStaged(entry, stage)){
      if(Index.pathStaged(entry, index)){
        println(s"file ${entry.filepath} was already staged. Updating reference.")
      } else {
        println(s"${entry.filepath}")
      }
      IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}blobs${File.separator}${entry.hash}", IOManager.readFile(file))
      val updated_stage = Stage.updateEntry(entry, stage)
      val updated_index = Index.updateEntry(entry, index)
      Stage.clear()
      Index.clear()
      updated_stage.entries.map(x => IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}STAGE", x.filepath+ " "+x.hash+"\n"))
      updated_index.entries.map(x => IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}INDEX", x.filepath+ " "+x.hash+"\n"))
    } else {
      println(s"${entry.filepath}")
      IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}blobs${File.separator}${hash}", IOManager.readFile(file))
      IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}STAGE", IOManager.relativize(file.getPath)+ " "+hash+"\n")
      IOManager.overwriteFile( s"${IOManager.getRepoDirPath().get}${File.separator}INDEX", IOManager.relativize(file.getPath)+ " "+hash+"\n")
    }
  }


  /**
   * @param entries a list of entries, either blobs or trees
   * @return a list of Entry object refering to all the blobs pointed by the list of entries
   */
  def getAllBlob(entries: List[Entry]): List[Entry] = {
    def loop(entries: List[Entry], path: String): List[Entry] = {
      entries.flatMap(item =>
        if (item.content_type.equals("tree")) {
          loop(Tree.getTreeEntries(item.hash), s"$path${File.separator}${item.filepath}")
        }
        else {
          val current_path = s"${path}${File.separator}${item.filepath}"
          List(Entry(item.content_type, item.hash, if (current_path.startsWith(File.separator)) current_path.drop(1) else current_path))
        }
      )
    }
    loop(entries, "")
  }
}
