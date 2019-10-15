package actions

import java.io.File
import objects.Branch
import utils.IOManager
import Console.{GREEN, RESET}

object Branch {


  def branch(name: String): Unit = {
    val new_branch = s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}$name"
    println(s"created new branch $name")
    val current_commit = getCurrentCommitHash()
    IOManager.writeFile(new_branch, current_commit.getOrElse(""))
    //Generating the log file for the new branch
    IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}$name",
      IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${getCurrentBranch().name}")))
  }

  /**
   * Lists all the branches created, display the current branch and the latest commit hash for each branch
   */
  def branchAllVerbose(): Unit = {
    val branches = IOManager.getAllFilesFromCurrentDirectory(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads")
    branches.map(branch => if(branch.getName.equals(getCurrentBranch().name)){
      println(s"${GREEN}* ${branch.getName} ${IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${branch.getName}"))}${RESET}")
    } else {
      println(s"${branch.getName} ${IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${branch.getName}"))}")
    })
  }

  def getCurrentBranch(): Branch = {
    val heads = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}HEAD"))
    new Branch(new File(heads).getName)
  }

  //TODO - create a Commit object from the hash
  //TODO - create a method in commit that can retrieve the informations from the ID
  def getCurrentCommitHash(): Option[String] = {
    val branch_path = s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}heads${File.separator}${getCurrentBranch().name}"
    new File(branch_path).exists() match {
      case true =>  Some(IOManager.readFile(new File(branch_path)))
      case false => None
    }
  }

}
