#!/bin/sh

cd `dirname "$0"`
log=$(ls *.xml 2>/dev/null | sort -r -n | head -n 1)
java -cp ../allatori.jar com.allatori.StackTrace "$log" input.txt output.txt
cat output.txt
