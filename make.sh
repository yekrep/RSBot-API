#!/bin/sh

cd $(dirname "$0")

jh=/usr/libexec/java_home

bindir="$(if [ -z "$TMPDIR" ]; then echo /tmp; else echo "$TMPDIR"; fi)/$(basename `pwd`)"
if [ ! -d "$bindir" ]; then mkdir "$bindir"; fi

if [ -e "$jh" ]; then
	rt=$(/usr/libexec/java_home -v 1.6)
	rt="$rt/../Classes/classes.jar:$rt/../Classes/jce.jar"
else
	rt="$(readlink -f /usr/bin/javac | sed "s:/bin/javac::" | sed 's/\(java-\)[[:digit:]]/\'1'6/')/jre/lib/rt.jar"
fi

javac -target 1.6 -source 1.6 -bootclasspath "$rt" -d "$bindir" $(find src -name \*.java)
