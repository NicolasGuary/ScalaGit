package objects

import java.io.File
import java.nio.file.{Files, Paths}
import utils.{IOManager, PathManager}

/**
 * This class is used to manage the content of the STAGE file
 * It can read, edit or clean this file when changes are made over time
 * @param entries
 */
case class Stage(var entries: List[Entry] = List())

object Stage {

  /**
   * clears the stage
   */
  def clear(): Unit = {
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}STAGE", "")
  }

  /**
   * Add entry to the stage
   * @param entry
   * @param stage
   * @return
   */
  def addEntry(entry: Entry, stage: Stage): Stage = {
    val newEntries = entry +: stage.entries
    val newStage = stage.copy(entries = newEntries)
    newStage
  }

  /**
   *
   * @param entry
   * @return true if the stage already contains an entry with the same path and the same hash
   */
  def alreadyStaged(entry: Entry): Boolean = {
    val stage = Stage.getStageAsEntries()
    stage.entries.contains(entry)
  }

  /**
   * Tells if the path is already in the stage
   * @param entry
   * @param current_stage
   * @return
   */
  def pathStaged(entry: Entry, current_stage: Stage): Boolean = {
    val res = current_stage.entries.filter(x => x.filepath.equals(entry.filepath))
    res.nonEmpty
  }

  /**
   * @param new_entry
   * @param current_stage
   * @return  a stage with the same entries, excepted the entries with the same path as new_entry that should be updated
   */
  def updateEntry(new_entry: Entry, current_stage: Stage): Stage = {
    val filtered_entries = current_stage.entries.filter(entry => !entry.filepath.equals(new_entry.filepath))
    val new_entries = filtered_entries :+ new_entry
    new Stage(new_entries)
  }

  /**
   * @return the stage as a list of Entries
   */
  def getStageAsEntries(): Stage = {
    val stage = new File(s"${IOManager.getRepoDirPath().get}${File.separator}STAGE")
    val res = new String(Files.readAllBytes(Paths.get(stage.getAbsolutePath)))
    if (!res.isEmpty){
      val stage_content = res.split("\n").map(x => x.split(" "))
      val paths = stage_content.map(x => x(0)).toList
      val hashs = stage_content.map(x => x(1)).toList
      val blob = List.fill(paths.size)("blob")
      val entries = (paths, hashs, blob).zipped.toList.map(x => Entry(x._3, x._2, x._1))
      val entries_stage = new Stage(entries = entries)
      val result = entries_stage.copy()
      result
    } else {
      new Stage()
    }
  }

  /**
   * Only for the files that are at root of the project
   * Will be used to create the commit tree, because it needs to point to these blobs
   * @return a list of CommitEntries
   */
  def retrieveStageRootBlobs(): List[Entry] = {
    val stage = Stage.getStageAsCommitEntries()
    //Filter to keep only the root blobs
    stage.filter(x => PathManager.isRootItem(x.filepath))
  }

  /**
   * This reads from the STAGE file and create a list with Entry used to generate the Trees during commit
   * @return the stage as a list of Entries
   */
  def getStageAsCommitEntries(): List[Entry] = {
    val stage = new File(s"${IOManager.getRepoDirPath().get}${File.separator}STAGE")
    val res = new String(Files.readAllBytes(Paths.get(stage.getAbsolutePath)))
    if (!res.isEmpty){
      val stage_content = res.split("\n").map(x => x.split(" "))
      val paths = stage_content.map(x => x(0)).toList
      val hashs = stage_content.map(x => x(1)).toList
      val blob = List.fill(paths.size)("blob")
      val entries = (paths, hashs, blob).zipped.toList.map(x => Entry(x._3, x._2, x._1, x._1))
      entries
    } else {
      List()
    }
  }
}

