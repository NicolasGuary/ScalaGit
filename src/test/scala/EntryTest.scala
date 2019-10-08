import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, Outcome}
import objects.Entry
class EntryTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  describe("If you try to get the filename of root.txt") {
    it("should return root.txt") {
      val entry = new Entry("blob", "1c", "root.txt")
      println(entry.getFileName())
      assert(entry.getFileName().equals("root.txt"))
    }
  }

  describe("If you try to get the filename of a/file_in_a.txt") {
    it("should return file_in_a.txt") {
      val entry = new Entry("blob", "1c", "file_in_a.txt")
      println(entry.getFileName())
      assert(entry.getFileName().equals("file_in_a.txt"))
    }
  }

  describe("If you try to get the file path for b/c/file_in_c.txt") {
    it("you should get b/c") {
      val entry = new Entry("blob", "1c", "testing/b/c/file_in_c.txt")
      println(entry.getFileDirectoryPath())
      println(s"est-ce un fichier ? ${new File(entry.get_filepath()).isFile}")
      assert(entry.getFileDirectoryPath().equals("testing/b/c"))
    }
  }

  describe("If you try to get the file path for b/c") {
    it("you should get the same unchanged path, so: b/c") {
      val entry = new Entry("tree", "1c", "b/c")
      println(entry.getFileDirectoryPath())
      assert(entry.getFileDirectoryPath().equals("b/c"))
    }
  }
}
