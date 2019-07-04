#!/bin/sh

f="$(mktemp || return 1)"
find src -name '*.java' >> "$f"
javac -d target "@$f"
rm "$f"
