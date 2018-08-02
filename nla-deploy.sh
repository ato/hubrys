#!/bin/sh
dest="$1"
mvn package dependency:copy-dependencies
mv $SUBPROJECT/target/dependency $dest/lib
mv $SUBPROJECT/target/*.jar "$dest/lib"