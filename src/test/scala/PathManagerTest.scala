import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, Outcome}
import utils.PathManager
import objects.Entry

class PathManagerTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
  describe("If you try to get the deepest directory in testing") {
    it("you should get testing/b/c") {
      val a = new Entry("tree", "1c", "testing/a")
      val b = new Entry("tree", "1c", "testing/b")
      val c = new Entry("tree", "1c", "testing/b/c")

      val dirs = List(a,b,c)
      assert(PathManager.getDeepestDirectory(dirs)._1.equals(List(c)))
    }
  }

  describe("If you try to get the parent path for testing/b/c") {
    it("you should get testing/b") {
      val c ="testing/b/c"
      assert(PathManager.getParentPath(c).equals(Some("testing/b")))
    }
  }

  describe("If you try to get the parent path for b/c/file_in_c.txt") {
    it("you should get b/c") {
      val c ="b/c/file_in_c.txt"
      assert(PathManager.getParentPath(c).equals(Some("b/c")))
    }
  }

  describe("If you try to know if b/c/file_in_c.txt is at root of the project") {
    it("you should get false") {
      val c ="b/c/file_in_c.txt"
      assert(PathManager.isRootItem(c).equals(false))
    }
  }

  describe("If you try to know if root.txt is at root of the project") {
    it("you should get true") {
      val c ="root.txt"
      assert(PathManager.isRootItem(c).equals(true))
    }
  }


}
