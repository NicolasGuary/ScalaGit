import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, Outcome}
import java.io.File

import actions.Init

class InitTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  describe("If you try to initialize a new sgit repository") {
    describe("and you don't have one initialized yet") {
      it("should create a brand new .sgit directory.") {
        Init.init()
        assert(new File(".sgit/STAGE").exists())
        assert(new File(".sgit/HEAD").exists())
        assert(new File(".sgit/refs/tags").exists())
        assert(new File(".sgit/refs/heads").exists())
        assert(new File(".sgit/objects/tree").exists())
        assert(new File(".sgit/objects/blobs").exists())
        assert(new File(".sgit/objects/commit").exists())
      }
    }

    describe("and you already have a sgit directory initialized") {
      it("should print an error message saying you already have a .sgit directory") {
        // TODO - find how to test a println
         // assert(Init.init() == "An sgit repository has already been initialized here.")
      }
    }
  }

}
