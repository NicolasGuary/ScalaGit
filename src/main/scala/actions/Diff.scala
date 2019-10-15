package actions

import java.io.File
import objects.Stage
import utils.IOManager
import utils.diff.Differ

object Diff {
  /**
   * We check if the hash from each file from the Stage is different from the hash obtained from the file path in the Working Directory
   * If yes, we diff the file from the Stage with the current file in the Working Directory
   * Otherwise, we do nothing, because no difference
   */
  def diff(): Unit = {
    val stage = Stage.getStageAsEntries().entries
    val changed_entries = stage.filter(entry => !IOManager.getHashFromFile(new File(entry.filepath)).equals(entry.hash))
    changed_entries
      .map(entry =>
        Differ.displayDiff(
          IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}objects${File.separator}blobs${File.separator}${entry.hash}")).split("\n"),
            IOManager.readFile(new File(entry.filepath)).split("\n"), entry.filepath))
  }
}
