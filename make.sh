#!/bin/sh

f="$(mktemp)"
find src/main/java -name '*.java' > "$f"
d="$(mktemp -d)"

javac -nowarn -d "$d" "@$f"
rm "$f"
rm -r "$d"

