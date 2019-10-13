package actions

object Status {

  def status(): Unit ={
    println(s"On branch ${Branch.getCurrentBranch().name}")

    println("Changes to be committed:")
    println("Changes not staged for commit:\n  (use \"sgit add <file>...\" to update what will be committed)")
    println("Untracked files:\n  (use \"sgit add <file>...\" to include in what will be committed)")




  }
}
