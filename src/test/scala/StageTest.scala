import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, Outcome}
import objects.{Entry, Stage}
class StageTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  describe("If you try to get the STAGE ") {
    describe("and the STAGE is empty") {
      it("you should get an empty stage") {
        assert(Stage.getStageAsEntries().equals(Stage(List())))
      }
    }
    describe("and the STAGE contains one element") {
      it("you should get a stage with 1 entry") {
        val stage = new Stage()
        val a = new Entry("tree", "1c", "testing/file_in_a.txt")
        val newStage = stage.copy(List(a))
        assert(newStage.equals(Stage(List(a))))
      }
    }
  }

  describe("If you try to add a new entry to an empty STAGE ") {
    it("you should get one entry in the stage") {
      val stage = new Stage()
      val a = new Entry("tree", "1c", "testing/file_in_a.txt")
      val newStage = stage.copy(List(a))
      assert(newStage.entries.length.equals(1))
    }
  }
}
