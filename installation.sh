#!/bin/bash
chmod u+x sgit.sh
ln -s sgit.sh sgit
export PATH=$PATH:`pwd`
sbt clean
sbt assembly
echo "java -jar `pwd`/target/scala-2.13/SGit-assembly-0.1.jar $"*"" > sgit.sh  