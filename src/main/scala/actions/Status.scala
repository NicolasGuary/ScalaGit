package actions

import Console.{GREEN, RED, RESET}
import objects.{Entry, Index, Stage, Branch}
import utils.IOManager
import java.io.File
object Status {

  /**
   * displays the status
   */
  def status(): Unit = {
    val index = Index.getIndexAsEntries().entries
    val stage = Stage.getStageAsEntries().entries
    //We allow to get the option here because we won't call actions if the .sgit repository is not initialized
    val working_directory = IOManager.exploreDirectoryAsEntries(new File(IOManager.getRepoDirPath().get).getParentFile)

    println(s"On branch ${Branch.getCurrentBranch().name}")
    changesToCommit(index, stage)
    notStaged(working_directory, stage)
    untracked(working_directory, stage)
  }

  /**
   * Shows the changes to commit
   * @param index current INDEX
   * @param stage current STAGE
   */
  def changesToCommit(index: List[Entry], stage: List[Entry]): Unit = {
    println("Changes to be committed:")
    val hashes = stage.map(x => x.hash)
    val paths = stage.map(x => x.filepath)
    index
      .filter(entry => hashes.contains(entry.hash))
      .filter(x => paths.contains(x.filepath))
      .map(entry => println(s"   ${GREEN}${entry.filepath}${RESET}"))

  }

  /**
   * Displays changes not staged
   * @param working_directory
   * @param stage
   * @return
   */
  def notStaged(working_directory: List[Entry], stage: List[Entry]) = {
    println("Changes not staged for commit:\n  (use \"sgit add <file>...\" to update what will be committed)")
    val hashes = stage.map(x => x.hash)
    val paths = stage.map(x => x.filepath)
    working_directory
      .filter(entry => !hashes.contains(entry.hash))
      .filter(x => paths.contains(x.filepath))
      .map(entry => println(s"   ${RED}modified: ${entry.filepath}${RESET}"))
  }

  /**
   * Displays all the files not added in the STAGE
   * @param working_directory
   * @param stage
   * @return
   */
  def untracked(working_directory: List[Entry], stage: List[Entry]) = {
    println("Untracked files:\n  (use \"sgit add <file>...\" to include in what will be committed)")
    val paths = stage.map(x => x.filepath)
    working_directory
      .filter(entry => !paths.contains(entry.filepath))
      .map(entry => println(s"   ${RED}${entry.filepath}${RESET}"))
  }
}
