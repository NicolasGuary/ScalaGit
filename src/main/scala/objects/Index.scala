package objects

import java.io.File
import java.nio.file.{Files, Paths}
import utils.IOManager

/*
* This class is used to manage the content of the INDEX file
* It can read, edit or clean this file when changes are made over time
* The INDEX is used to know the files added for the current commit. It is cleaned after each commit
 */
case class Index(var entries: List[Entry] = List())



object Index {

  def clear(): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}INDEX", "")
  }

  def addEntry(entry: Entry, index: Index): Index = {
    val newEntries = entry +: index.entries
    val newIndex = index.copy(entries = newEntries)
    newIndex
  }

  //Returns a stage with the same entries, excepted the entries with the same path as new_entry that should be updated
  def updateEntry(new_entry: Entry, current_stage: Index): Index = {
    val filtered_entries = current_stage.entries.filter(entry => !entry.get_filepath().equals(new_entry.get_filepath()))
    val new_entries = filtered_entries :+ new_entry
    new Index(new_entries)
  }

  //Tells if the path is already in the index
  def pathStaged(entry: Entry, current_index: Index): Boolean = {
    val res = current_index.entries.filter(x => x.get_filepath().equals(entry.get_filepath()))
    res.nonEmpty
  }

  //Returns the index as a list of Entries
  def getIndexAsEntries(): Index = {
    val index = new File(s"${IOManager.getRepoDirPath().get}${File.separator}INDEX")
    val res = new String(Files.readAllBytes(Paths.get(index.getAbsolutePath)))
    if (!res.isEmpty){
      val index_content = res.split("\n").map(x => x.split(" "))
      val paths = index_content.map(x => x(0)).toList
      val hashs = index_content.map(x => x(1)).toList
      val blob = List.fill(paths.size)("blob")
      val entries = (paths, hashs, blob).zipped.toList.map(x => new Entry(x._3, x._2, x._1))
      new Index(entries = entries)
    } else {
      new Index()
    }
  }
}