package actions

import java.io.File

import objects.Stage
import utils.IOManager

object Diff {

  def diff(): Unit = {
    //Bkbakabka
    val stage = Stage.getStageAsEntries().entries
    //We allow to get the option here because we won't call actions if the .sgit repository is not initialized
    val working_directory = IOManager.exploreDirectoryAsEntries(new File(IOManager.getRepoDirPath().get).getParentFile)

    //We check if the hash from each file from the Stage is different from the hash obtained from the file in the Working Directory

    //If yes, we diff the file from the Stage with the file in the Working Directory

    //Otherwise, we do nothing, because no diff


  }
}
