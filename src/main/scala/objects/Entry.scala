package objects

import java.io.File

import better.files.{File => BFile}

/**
 * This class defines the entity that is saved into the Blob and Tree files objects.
 * It records the necessary information about an Entry but also the childpath from where this Entry is from in the addTrees() tailrec algo from Commit
 * @param content_type either "blob" or "tree"
 * @param hash the hashed value corresponding to this record
 * @param filepath the path to this file
 * @param childpath the path from where this entry is from, used to get the correct directory name in trees
 */
case class Entry(var content_type: String = "", var hash: String = "", var filepath: String= "", var childpath: String= ""){
  val base_dir = System.getProperty("user.dir")

  /**
   * @return the name of the childpath or the name of the filepath if no childpath (ie. the root folder)
   */
  def getFileName(): String = {
    if(this.childpath.isEmpty){
      BFile(this.filepath).name
    } else {
      BFile(this.childpath).name
    }
  }

  /**
   * @return the path without the file name if it's a file or the unchanged path is it's a directory
   */
  def getFileDirectoryPath(): String = {
    if(new File(this.filepath).isFile) {
      BFile(base_dir).relativize(BFile(this.filepath).parent).toString
    } else {
      this.filepath
    }
  }

  /**
   *
   * @return the filepath attribute relativized to the base directory
   */
  def getFileRelativizedPath(): String = {
      BFile(base_dir).relativize(BFile(this.filepath)).toString
  }
}


