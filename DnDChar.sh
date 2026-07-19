#!/bin/bash

# When run from the file manager as a default app,
# the data file clicked on is passed with its full path.
# When run from the command line,
# the data file does not have its full path.
# Thus, when run from the file manager, set the first parameter
# to "fullPath" so this script knows not to prefix the path.
# Also, the -file parameter is optional,
# so run properly when there is no file given.

if [ "$1" == "fullPath" ]
then
	dir=""
	shift
else
	# save the current dir
	dir="`pwd`/"
fi

# go to the program dir, so it can access config files
cd ~/Fish/DnD/chars/bin

# if a data file was given, prefix the path (if any) to it
if [ -z "$1" ]
then
	fil=""
else
	fil="-file ${dir}${1}"
fi

java -jar DnD.jar -font +2 $fil
