package objects

import java.io.File
import better.files.{File => BFile}

/**
 * This class defines the entity that is saved into the Blob and Tree files objects.
 * It records the necessary information about an Entry in those files
 * @param content_type either "blob" or "tree"
 * @param hash the hashed value corresponding to this record
 * @param filepath the path to this file
 */
case class Entry (var content_type: String = "", var hash: String = "", var filepath: String= ""){
  val base_dir = System.getProperty("user.dir")

  def get_hash(): String = {
    this.hash
  }

  def set_hash (hash: String): Unit = {
    this.hash = hash
  }

  def get_content_type(): String = {
    this.content_type
  }

  def set_content_type (content_type: String): Unit = {
    this.content_type = content_type
  }

  def get_filepath(): String = {
    this.filepath
  }

  def set_filepath (filepath: String): Unit = {
    this.filepath = filepath
  }

  def getFileName(): String = {
    BFile(this.filepath).name
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

  def getFileRelativizedPath(): String = {
      BFile(base_dir).relativize(BFile(this.filepath)).toString
  }
}

object Entry {
  def apply(): Entry = {
    new Entry
  }
}
