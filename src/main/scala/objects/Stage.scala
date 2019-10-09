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
case class Stage(var entries: List[Entry] = List()) {

  def get_entries(): List[Entry] = {
    this.entries
  }

  def set_entries(entries: List[Entry]): Unit = {
    this.entries = entries
  }

  def addEntry(entry: Entry): List[Entry] = {
    entry +: this.get_entries()
  }

  //Return true if the stage already contains an entry with the same path and the same hash
  def alreadyStaged(entry: Entry): Boolean = {
    val stage = Stage.getStageAsEntries()
    stage.get_entries().contains(entry)
  }

  //Tells if the path is already in the stage
  def pathStaged(entry: Entry): Boolean = {
    val stage = Stage.getStageAsEntries()
    val res = stage.get_entries().filter(x => x.get_filepath().equals(entry.get_filepath()))
    res.nonEmpty
  }

  def updateEntry(new_entry: Entry): Stage = {
    val new_stage = Stage.getStageAsEntries()
    if (new_stage.pathStaged(new_entry)) {
      val non_duplicate_entries = new_stage.get_entries().filter(x => !new_stage.pathStaged(x))
      val filtered_stage = new Stage()
      filtered_stage.set_entries(non_duplicate_entries :+ new_entry)
      //filtered_stage.set_entries(filtered_stage.addEntry(new_entry))
      println("non duplicate")
      non_duplicate_entries.map(x => println(x.get_filepath()))
      println("filtered stage")
      filtered_stage.get_entries().map(x => println(x.get_filepath()))
      filtered_stage
    } else {
      new_stage
    }
  }
}


object Stage {

  def clear(): Unit = {
    IOManager.writeFile(s".sgit${File.separator}STAGE", "")
  }

  // returns a list of Entries
  // Only for the files that are at root of the project
  // Will be used to create the commit tree, because it needs to point to these blobs
  def retrieveStageRootBlobs(): List[Entry] = {
    val stage = Stage.getStageAsEntries()

    //Filter to keep only the root blobs
    stage.get_entries().filter(x => PathManager.isRootItem(x.get_filepath()))
  }

  //Returns the stage as a list of Entries
  def getStageAsEntries(): Stage = {
    val stage = new File(s".sgit${File.separator}STAGE")
    val res = new String(Files.readAllBytes(Paths.get(stage.getAbsolutePath)))
    val result = new Stage()
    if (!res.isEmpty){
      val stage_content = res.split("\n").map(x => x.split(" "))
      val paths = stage_content.map(x => x(0)).toList
      val hashs = stage_content.map(x => x(1)).toList
      val blob = List.fill(paths.size)("blob")
      val entries = (paths, hashs, blob).zipped.toList.map(x => new Entry(x._3, x._2, x._1))
      result.set_entries(entries)
    }
    result
  }
}

