package objects

import java.io.File
import java.nio.file.{Files, Paths}

import better.files.{File => BFile}
import objects.Blob.getHashFromFile
import utils.{IOManager, PathManager}

/*
* This class is used to manage the content of the STAGE file
* It can read, edit or clean this file when changes are made over time
 */
case class Stage(var entries: List[Entry] = List())

object Stage {

  def clear(): Unit = {
    IOManager.writeFile(s".sgit${File.separator}STAGE", "")
  }

  def addEntry(entry: Entry, stage: Stage): Stage = {
    val newEntries = entry +: stage.entries
    val newStage = stage.copy(entries = newEntries)
    newStage
  }

  //Return true if the stage already contains an entry with the same path and the same hash
  def alreadyStaged(entry: Entry): Boolean = {
    val stage = Stage.getStageAsEntries()
    stage.entries.contains(entry)
  }

  //Tells if the path is already in the stage
  def pathStaged(entry: Entry, current_stage: Stage): Boolean = {
    val res = current_stage.entries.filter(x => x.get_filepath().equals(entry.get_filepath()))
    res.nonEmpty
  }

  //Returns a stage with the same entries, excepted the entries with the same path as new_entry that should be updated
  def updateEntry(new_entry: Entry, current_stage: Stage): Stage = {
    val filtered_entries = current_stage.entries.filter(entry => !entry.get_filepath().equals(new_entry.get_filepath()))
    val new_entries = filtered_entries :+ new_entry
    new Stage(new_entries)
  }

  // returns a list of Entries
  // Only for the files that are at root of the project
  // Will be used to create the commit tree, because it needs to point to these blobs
  def retrieveStageRootBlobs(): List[Entry] = {
    val stage = Stage.getStageAsEntries()

    //Filter to keep only the root blobs
    stage.entries.filter(x => PathManager.isRootItem(x.get_filepath()))
  }

  //Returns the stage as a list of Entries
  def getStageAsEntries(): Stage = {
    val stage = new File(s".sgit${File.separator}STAGE")
    val res = new String(Files.readAllBytes(Paths.get(stage.getAbsolutePath)))
    if (!res.isEmpty){
      val stage_content = res.split("\n").map(x => x.split(" "))
      val paths = stage_content.map(x => x(0)).toList
      val hashs = stage_content.map(x => x(1)).toList
      val blob = List.fill(paths.size)("blob")
      val entries = (paths, hashs, blob).zipped.toList.map(x => new Entry(x._3, x._2, x._1))
      val entries_stage = new Stage(entries = entries)
      val result = entries_stage.copy()
      result
    } else {
      new Stage()
    }
  }
}

