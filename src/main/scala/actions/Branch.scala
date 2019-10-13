package actions

import java.io.File

import objects.Branch
import utils.IOManager

object Branch {


  def branch(name: String) = {
    val new_branch = s".sgit${File.separator}refs${File.separator}heads${File.separator}$name"
    println(s"created new branch $name")
    val current_commit = getCurrentCommitHash()
    IOManager.writeFile(new_branch, current_commit)
    //Generating the log file for the new branch
    IOManager.writeFile(s".sgit${File.separator}refs${File.separator}logs${File.separator}$name", IOManager.readFile(new File(s".sgit${File.separator}refs${File.separator}logs${File.separator}${getCurrentBranch().name}")))
  }

  def getCurrentBranch(): Branch = {
    val heads = IOManager.readFile(new File(s".sgit${File.separator}HEAD"))
    new Branch(new File(heads).getName)
  }

  //TODO - create a Commit object from the hash
  //TODO - create a merhod in commit that can retrieve the informations from the ID
  def getCurrentCommitHash(): String = {
    val branch_path = s".sgit${File.separator}refs${File.separator}heads${File.separator}${getCurrentBranch().name}"
    IOManager.readFile(new File(branch_path))
  }

}
