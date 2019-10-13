package actions

import java.io.File

import utils.IOManager

object Checkout {

  def checkout(branch_name: String) = {
    IOManager.writeFile(s".sgit${File.separator}HEAD", s"refs/heads/${branch_name}")
    println(s"Switched to $branch_name")
  }
}
