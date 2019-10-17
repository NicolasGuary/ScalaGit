package actions

import Console.{GREEN, RED, RESET}
import objects.{Entry, Index, Stage, Branch}
import utils.IOManager
import java.io.File
object Status {

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


  def changesToCommit(index: List[Entry], stage: List[Entry]): Unit = {
    //TODO - new file not detected because when added the file goes straight into the stage
    //In the index, when adding, check if the file is also in the stage. If yes it's modified, otherwise it's new.
    println("Changes to be committed:")
    index
      .filter(entry => !stage.contains(entry))
      .map(entry => println(s"   ${GREEN}new file: ${entry.getFileName()}${RESET}"))
    index
      .filter(entry => stage.contains(entry))
      .map(entry => println(s"    ${GREEN}modified: ${entry.getFileName()}${RESET}"))
  }

  def notStaged(working_directory: List[Entry], stage: List[Entry]) = {
    println("Changes not staged for commit:\n  (use \"sgit add <file>...\" to update what will be committed)")
    val hashes = stage.map(x => x.hash)
    val paths = stage.map(x => x.filepath)
    working_directory
      .filter(entry => paths.contains(entry.getFileName()) && !hashes.contains(entry.hash))
      .map(entry => println(s"   ${RED}modified: ${entry.getFileName()}${RESET}"))
  }

  def untracked(working_directory: List[Entry], stage: List[Entry]) = {
    println("Untracked files:\n  (use \"sgit add <file>...\" to include in what will be committed)")
    working_directory
      .filter(entry => !stage.contains(entry))
      .map(entry => println(s"   ${RED}${entry.filepath}${RESET}"))
  }
}
