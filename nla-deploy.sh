#!/bin/sh
dest="$1"
mvn package dependency:copy-dependencies
mv target/dependency $dest/lib
mv target/*.jar "$dest/lib"