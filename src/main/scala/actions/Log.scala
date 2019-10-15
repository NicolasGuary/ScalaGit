package actions

import java.io.File
import Console.{YELLOW, RESET}
import objects.{Commit, Tree}
import utils.IOManager

object Log {
  def log() = {
    val content = IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}logs${File.separator}${Branch.getCurrentBranch().name}"))
    val commits = content.split("\n").map(x => x.split("_")).map(x => new Commit(x(0), new Tree(List(),x(1)), x(2), x(3), x(4))).toList
    commitContentForLog(commits.reverse)
  }

  def commitContentForLog(commits: List[Commit]): Unit = {
    commits.map(x => println(s"${YELLOW}commit: ${x.id}${RESET}\ntree: ${x.master_tree.id}\nauthor: ${x.author}\nparent: ${x.parent_commit_id}\ntimestamp ${x.timestamp}\n"))
  }
}
