package utils

import java.io.File

import objects.Entry

object PathManager {

  //Returns the parent path for the current path
  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      val lastValue = pathSplit.last
      pathSplit.map(x => if(x != lastValue){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
      })
      Some(parentPath)
    }
  }

  //Returns the longest path
  //If there are more than 1, it returns the first one found
  //returns:
  //  deepest: the list of the deepest Entries (if there multiple one with same value)
  //  rest: all the others Entries that are not in deepest (either same size but different or shorter)
  //  pathForMax: the maximum path found

  def getDeepestDirectory(l: List[Entry]): (List[Entry], List[Entry], String) = {
    var max = 0
    var pathForMax = ""
    l.map(line => if (line.getFileDirectoryPath().split("/").size >= max) {
      max = line.getFileDirectoryPath().split("/").size
      pathForMax = line.getFileDirectoryPath()
    })

    val rest = l.filter(x => !x.getFileDirectoryPath().equals(pathForMax))
    val deepest = l.filter(x => x.getFileDirectoryPath().equals(pathForMax))

    (deepest, rest, pathForMax)
  }

  def isRootItem(path: String): Boolean = {
    !path.contains(File.separator)
  }

  //Returns "tree" or "blob" regarding of the type of the hash
  def natureOfHash(hash: String): String = {
    if(new File(s"${IOManager.getRepoDirPath()}${File.separator}objects${File.separator}tree${File.separator}$hash").exists){
      "tree"
    } else if (new File(s"${IOManager.getRepoDirPath()}${File.separator}objects${File.separator}blobs${File.separator}$hash").exists){
      "blob"
    }else {
      "undefined"
    }
  }
}
