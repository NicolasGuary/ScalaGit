# ScalaGit
## A Scala-based git-like code source manager

## Installation:
> This project uses sbt assembly & scala version 2.13.1

Clone the repository, and using `cd` move to the project directory, then you have two options.
> Using the shell script:

The first option is to run `source installation.sh` into the project directory. 
You can now use any `sgit` command you would like.
 (If the rights are not granted you should do `chmod u+wx sgit.sh` the write rights are used to write your current path into sgit.sh).

> Manually:

You will need to perform this set of commands to install sgit.
* `chmod u+x sgit.sh`
* `ln -s sgit.sh sgit`
* `sbt assembly`
* ``export PATH=$PATH:`pwd` ``
> Then to write the path of the JAR file into sgit.sh you will need to set the location:
* `` echo "java -jar `pwd`/target/scala-2.13/SGit-assembly-0.1.jar $"*"" > sgit.sh ``


## Commands:
This project is part of the functional programming course.
#### Create:
 * sgit init ✅
 
#### Local Changes:
* sgit status ✅
* sgit diff ✅
* sgit add <filename/filenames or . or regexp>  ✅
* git commit ✅

#### Commit History:
* sgit log (Show all commits starting with newest) ✅
* sgit log -p (Show changes overtime) ✅
* sgit log --stat (Show stats about changes overtime) ✅

#### Branches and Tags
* sgit branch <branch name> (Create a new branch) ✅
* sgit branch -av (List all existing branches and tags) ✅
* sgit checkout <branch or tag or commit hash> (⚠️only to switch branch)
* sgit tag <tag name> (or empty to list all tags) ✅

#### Merge & Rebase
* sgit merge <branch>
* sgit rebase <branch>
* sgit rebase -i <commit hash or banch name>
