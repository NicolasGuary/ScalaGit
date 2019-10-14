package actions

import java.io.File

import utils.IOManager

object Log {

  //TODO - change how logs are written (all in 1 line) and split at \n to create Commit objects. Then reverse the list.
  def log() = {
    val content = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}"))
    println(content)
  }
}
