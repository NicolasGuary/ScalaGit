package objects

import java.io.File

import better.files.{File => BFile}
import utils.{IOManager, PathManager}

/*
* This class is used to manage the content of the STAGE file
* It can read, edit or clean this file when changes are made over time
 */

object Stage {

  //Returns a list of Entries containing all the data stored in the STAGE.
  //The stage contains only paths for files so here we're gonna create blob Entries.
  def retrieveStageStatus(): (List[Entry]) = {
    //Retrieve useful data
    val stage = new File(s".sgit${File.separator}STAGE")
    val files = IOManager.readFile(stage)

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Get the data
    val paths = stage_content.map(x => x(0)).toList
    val hashs = stage_content.map(x =>x(1)).toList
    val blob = List.fill(paths.size)("blob")

    //Merging the result
    (paths,hashs,blob).zipped.toList.map(x => new Entry(x._3, x._2, x._1))
  }

  //Get all the paths saved on the STAGE
  def retrieveStagePaths(): List[String] = {
    val stage = new File(s".sgit${File.separator}STAGE")
    val files = IOManager.readFile(stage)
    val base_dir = System.getProperty("user.dir")
    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))
    stage_content.map(x => BFile(base_dir).relativize(BFile(x(0))).toString).toList
  }


  // returns a list of Entries
  // Only for the files that are at root of the project
  // Will be used to create the commit tree, because it needs to point to these blobs
  def retrieveStageRootBlobs(): List[Entry] = {
    val stage = new File(s".sgit${File.separator}STAGE")
    val files = IOManager.readFile(stage)

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Filter to keep only the root blobs
    val root_only = stage_content.filter(x => PathManager.isRootItem(x(0)))

    //Get data
    val paths = root_only.map(x => x(0)).toList
    val hashs = root_only.map(x =>x(1)).toList
    val blob = List.fill(hashs.size)("blob")

    //Merging the result
    (blob, hashs, paths).zipped.toList.map(x => new Entry(x._3, x._2, x._1))
  }
}

