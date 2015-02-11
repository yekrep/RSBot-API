#!/bin/sh

cd $(dirname "$0")
name=$(cat src/org/powerbot/Configuration.java | grep -o 'public\s.\+\sString\s\+NAME\s*=\s*".\+"' | awk '{print $NF}' | tr -d '"')
version=$(cat src/org/powerbot/Configuration.java | grep -o 'public\s.\+\sint\s\+VERSION\s*=\s*\d\+' | awk '{print $NF}')
dist="`pwd`/lib/$name-$version.jar"

jh=/usr/libexec/java_home

bindir="$(if [ -z "$TMPDIR" ]; then echo /tmp; else echo "$TMPDIR"; fi)/$name-$version-build"
if [ ! -d "$bindir" ]; then mkdir "$bindir"; fi

if [ -e "$jh" ]; then
	rt=$(/usr/libexec/java_home -v 1.6)
	rt="$rt/../Classes/classes.jar:$rt/../Classes/jce.jar"
else
	rt="$(readlink -f /usr/bin/javac | sed "s:/bin/javac::" | sed 's/\(java-\)[[:digit:]]/\'1'6/')/jre/lib/rt.jar"
fi

echo "Compiling..."
javac -target 1.6 -source 1.6 -bootclasspath "$rt" -d "$bindir" $(find src -name \*.java)
echo "Bundling..."
jar cfm "$dist" "resources/Manifest.txt" -C "$bindir" .
rm -fr "$bindir"

if [ -e "lib/allatori.jar" ]; then
	echo "Obfuscating..."
	xml="$bindir-allatori.xml"
	cat lib/allatori.xml | sed "s|%LOGFILE%|`pwd`/lib/releases/$version\.xml|g" | sed "s|%FILENAME%|$dist|g" >"$xml"
	java -cp lib/allatori.jar com.allatori.Obfuscate "$xml"
	rm "$xml"
else
	echo "Not obfuscating"
fi

signdir=resources/signing
if [ -e "$signdir/passwd" ]; then
	echo "Signing..."
	jarsigner -tsa http://timestamp.digicert.com -keystore "$signdir/jks" -storepass "`cat $signdir/passwd`" "$dist" "`cat $signdir/alias`"
	jarsigner -verify -certs "$dist"
else
	echo "Not signing"
fi

echo "Documentation..."
docscfg=resources/docs
javadoc="$(if [ -e "$jh" ]; then echo "`/usr/libexec/java_home -v 1.6`/bin/"; fi)javadoc"
$javadoc -quiet -d "docs" -version -author -windowtitle "`cat $docscfg/title.txt`" \
	-bottom "`cat $docscfg/bottom.txt`" -charset "utf-8" -docencoding "utf-8" \
	-classpath src -subpackages `cat $docscfg/packages.txt` -link http://docs.oracle.com/javase/6/docs/api/

