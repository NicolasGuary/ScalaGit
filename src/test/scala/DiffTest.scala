import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, Outcome}
import utils.diff.{Diff, Operations}

class DiffTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
  describe("If you try to get the diff ") {
    describe("of the same file") {
      it("you should get a Seq filled only with KEEP operations") {
        val text1 = Seq("a","a","a")
        val text2 = Seq("a","a","a")
        val deltas = Diff.diffFiles(text1, text2)
        assert(deltas.filter(delta => !delta.diff.equals(Operations.KEEP)).isEmpty)
      }
    }
    describe("and the files has 1 line that is different") {
      it("you should get a Seq with the right amount of Delta (1 ADD and 1 REMOVE so 2)") {
        val text1 = Seq("a","a","a")
        val text2 = Seq("a","b","a")
        val deltas = Diff.diffFiles(text1, text2)
        assert(deltas.filter(delta => delta.diff.equals(Operations.ADD) || delta.diff.equals(Operations.REMOVE)).length == 2)
      }
    }

    describe("and the files has 2 lines that are different") {
      it("you should get a Seq with the right amount of Delta (2 add and 2 remove so 4)") {
        println("DIFFING aaa and bba")
        val text1 = Seq("a","a","a")
        val text2 = Seq("b","b","a")
        val deltas = Diff.diffFiles(text1, text2)
        Diff.displayDiff(text1, text2)
        assert(deltas.filter(delta => delta.diff.equals(Operations.ADD) || delta.diff.equals(Operations.REMOVE)).length == 4)
      }
    }

    describe("and the second text is empty") {
      it("you should get a Seq asking to remove all the lines from text1") {
        val text1 = Seq("a","a","a")
        val text2 = Seq()
        assert(Diff.diffFiles(text1, text2).length.equals(text1.length))
      }
    }
  }
}