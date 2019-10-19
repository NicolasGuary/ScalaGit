package actions

import java.io.File

import objects.Branch
import utils.IOManager

object Checkout {
  def checkout(branch_name: String) = {
    if(Branch.getAllExistingBranches().contains(new Branch(branch_name))){
      IOManager.writeFile(s"${IOManager.getRepoDirPath().get}${File.separator}HEAD", s"refs/heads/${branch_name}")
      println(s"Switched to $branch_name")
    } else println(s"Branch $branch_name doesn't exist.")

  }
}
