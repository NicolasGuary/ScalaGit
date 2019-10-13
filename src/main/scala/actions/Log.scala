package actions

import java.io.File

import utils.IOManager

object Log {

  def log() = {
    val content = IOManager.readFile(new File(s".sgit${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}"))
    println(content)
  }
}
