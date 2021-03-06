package actions

import java.io.File

import objects.{Branch => OBranch}
import utils.IOManager

import Console.{GREEN, RESET}

object Branch {


  /**
   * Creates a new branch
   * @param name the branch name to create
   */
  def branch(name: String): Unit = {
    val new_branch = s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}$name"
    println(s"created new branch $name")
    val current_commit = OBranch.getCurrentCommitHash()
    IOManager.writeFile(new_branch, current_commit.getOrElse(""))
    //Generating the log file for the new branch
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}$name",
      IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${OBranch.getCurrentBranch().name}")))
  }

  /**
   * Lists all the branches created, display the current branch and the latest commit hash for each branch
   * Also displays all the tags created
   */
  def branchAllVerbose(): Unit = {
    val branches = IOManager.getAllFilesFromCurrentDirectory(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads")
    branches.map(branch => if(branch.getName.equals(OBranch.getCurrentBranch().name)){
      println(s"${GREEN}* ${branch.getName} ${IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${branch.getName}"))}${RESET}")
    } else {
      println(s"${branch.getName} ${IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${branch.getName}"))}")
    })
    Tag.listAllTags()
  }
}
