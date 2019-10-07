package actions

import java.io.File

import utils.IOManager
import objects.Tree
import better.files.{File => BFile}

object Commit {

  def commit(): Unit = {
    val stage = retrieveStageStatus()
    val res = addTrees(stage, None)
  }


  def addTrees(l: List[(String, String, String)], hashFinal: Option[List[String]]): List[String] = {
    if(l.size == 0){
      hashFinal.get
    } else {
      val (deeper, rest, parent) = getDeeperDirectory(l)
      val hash = createTree(deeper)
      if(parent.isEmpty) {
        if (hashFinal.isEmpty){
          addTrees(rest, Some(List(hash)))
        } else {
          addTrees(rest, Some(hash :: hashFinal.get))
        }
      } else {
        addTrees((parent.get, hash, "tree") :: rest, hashFinal)
      }
    }
  }

  def createTree(deeper: List[(String, String, String)]): String = {
    val tree = new Tree()
    deeper.map(x => println(x))
    deeper.map(element => tree.set_items(tree.addElement(element._3, element._2, element._1)))
    val hash = tree.createTreeId(tree.get_items())
    tree.set_id(hash)
    tree.saveTreeFile(tree.get_id(), tree.get_items())
    tree.get_id()
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:
  //(src/main/scala/objects,a7dbb76b0406d104b116766a40f2e80a79f40a0349533017253d52ea750d9144)
  //(src/main/scala/utils,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0)
  def retrieveStageStatus(): List[(String,String, String)] = {
    //Retrieve useful data
    val stage = new File(s".sgit${File.separator}STAGE")
    val files = IOManager.readFile(stage)
    val base_dir = System.getProperty("user.dir")

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    val paths = stage_content.map(x => BFile(base_dir).relativize(BFile(x(0)).parent).toString).toList
    val hashs = stage_content.map(x =>x(1)).toList
    val blob = List.fill(paths.size)("blob")

    //Merging the result
    (paths,hashs,blob).zipped.toList
  }

  def getDeeperDirectory(l: List[(String, String, String)]): (List[(String,String, String)], List[(String,String, String)], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line._1.split("/").size >= max) {
      max = line._1.split("/").size
      pathForMax = line._1
    })

    val rest = l.filter(x => !(x._1.equals(pathForMax)))
    val deepest = l.filter(x => x._1.equals(pathForMax))

    val parentPath = getParentPath(pathForMax)
    (deepest, rest, parentPath)
  }

  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      val lastValue = pathSplit.last
      pathSplit.map(x => if(x != lastValue){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
      })
      Some(parentPath)
    }
  }
}
