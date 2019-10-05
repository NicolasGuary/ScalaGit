#!/bin/bash
cd ~/IdeaProjects/SGit/
sbt --error 'set showSuccess := false' "run $*"