# ScalaGit
## A Scala-based git-like code source manager


This project is part of the functional programming course.
#### Create:
 * sgit init ✅
#### Local Changes:
* sgit status ✅
* sgit diff ✅
* sgit add <filename/filenames or . or regexp>  ✅ (⚠️ regexp to handle)
* git commit ✅ (⚠️ Folder names in Trees to correct)

#### Commit History:
* sgit log (Show all commits starting with newest) ✅
* sgit log -p (Show changes overtime) ⏳
* sgit log --stat (Show stats about changes overtime) ⏳


#### Branches and Tags
* sgit branch <branch name> (Create a new branch) ✅
* sgit branch -av (List all existing branches and tags) ✅
* sgit checkout <branch or tag or commit hash> ✅ (⚠️ tag or commit hash to do, W-D to re-build)
* sgit tag <tag name>


#### Merge & Rebase
* sgit merge <branch>
* sgit rebase <branch>
* sgit rebase -i <commit hash or banch name>
