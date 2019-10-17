package actions

import java.io.File

import objects.Branch
import utils.IOManager

object Tag {

  def tag(tagName: String): Unit = {
    val new_tag = s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}tags${File.separator}$tagName"
    val current_commit = Branch.getCurrentCommitHash()
    current_commit match {
      case None => println("fatal: Failed to resolve 'HEAD' as a valid ref. \nYou should try to do a commit first")
      case commit => {
        IOManager.writeFile(new_tag, commit.get)
        println(s"created new tag $tagName on commit $current_commit")
      }
    }
  }

  def listAllTags(): Unit = {
    val tags = IOManager.getAllFilesFromCurrentDirectory(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}tags")
    tags.map(tag => println(s"${tag.getName} on commit: ${IOManager.readFile(new File(s"${IOManager.getRepoDirPath().get}${File.separator}refs${File.separator}tags${File.separator}${tag.getName}"))}"))
  }
}
