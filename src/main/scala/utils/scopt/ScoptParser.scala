package utils.scopt

import java.io.File

import actions.{Add, Commit, Init}
import scopt.OParser

// Config stores arguments for the commands inputed
// command stores the action asked by the user
// all the other attributes are arguments to a command
case class Config(
                   command: String = "",
                   patch: Boolean = false,
                   verbose: Boolean = false,
                   debug: Boolean = false,
                   stat: Boolean = false,
                   showAllBranches: Boolean = false,
                   branchName: String = "",
                   files: Seq[File] = Seq(),
                 )

object ScoptParser extends App {
  val builder = OParser.builder[Config]

  val parser = {
    import builder._

    //Adding all the commands that sgit should be able to parse
    OParser.sequence(

      //Declaring sgit
      programName("sgit"),
      head("sgit", "1.0.0"),
      help("help")
        .text("This is a list of the features provided by sgit"),

      //INIT command
      cmd("init")
        .action((_, c) => c.copy(command = "init"))
        .text("Initialize an empty sgit repository at the current path."),

      //ADD command
      cmd("add")
        .action((_, c) => c.copy(command = "add"))
        .text("Stages the files passed in arguments of the command.")
        .children(
          arg[File]("<file>...")
            .unbounded()
            .optional()
            .action((x, c) => c.copy(files = c.files :+ x))
            .text("Files to add to the stage")
        ),

      //STATUS command
      cmd("status")
        .action((_, c) => c.copy(command = "status"))
        .text("Show the working tree status"),

      //COMMIT command
      cmd("commit")
        .action((_, c) => c.copy(command = "commit"))
        .text("Commit all the staged files"),

      //DIFF command
      cmd("diff")
        .action((_, c) => c.copy(command = "diff"))
        .text("Show changes between commits, commit and working tree, etc"),

      //LOG command
      //Options: -p and -stat
      cmd("log")
        .action((_, c) => c.copy(command = "log"))
        .text(" Show commit logs (chronological order)")
        .children(
          opt[Unit]('p', "patch")
            .text("show the logs with the diff of each commited file ")
            .action((_, c) => c.copy(patch = true)),
          opt[Unit]("stat")
            .text("show the status of insertion and deletion of each commited file")
            .action((_, c) => c.copy(stat = true))
        ),

      //BRANCH command
      //Options: -p and -stat
      //Args: sgit branch <name>
      cmd("branch")
        .action((_, c) => c.copy(command = "branch"))
        .text("Creates a new branch")
        .children(
          arg[String]("name")
            .required()
            .action((x, c) => c.copy(branchName = x))
            .text("Name of the new branch"),
          opt[Unit]('a', "all")
            .action((_, c) => c.copy(showAllBranches = true))
            .text("Shows all branches"),
          opt[Unit]('v', "verbose")
            .action((_, c) => c.copy(verbose = true))
            .text("Adds the hash and commit subject  for each branch"),
          //Checking if the arguments received correspond to a valid action
          checkConfig(
            c =>
              if (c.showAllBranches && c.branchName != "")
                failure("You cannot use 'all' option with a branch name")
              else if (c.verbose && c.branchName != "")
                failure("You cannot use 'verbose' option with a branch name")
              else success
          )
        )
    )
  }

  // OParser.parse returns Option[Config] => we need to check if we received Some(config) or None
  // Then we need to call the method corresponding to the arguments received
  // TODO - check if there is a better way to handle the commands
  // TODO - Create a method to parse the args received from the config.
  // TODO - Check if the .sgit directory exists before doing any command other that init !
  OParser.parse(parser, args, Config()) match {
    case Some(config) => {
      config.command match {
        case "init" => {
          Init.init()
        }
        case "add" => {
          Add.add(args.drop(1).mkString(" "))
        }
        case "commit" => {
          Commit.commit()
        }
        case _ => {
          //Check if .sgit exists, if yes do the command, else throw an error because it's not an sgit repo
          println(s"config reçue =  ${config} avec ${args}")
        }
      }
    }
    case _ =>
      println("Your argument did not match with an existing one. Run sgit --help for more informations.")
  }
}
