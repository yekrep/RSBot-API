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

l4jdir=lib/launch4j
if [ -d "$l4jdir" ]; then
	echo "Wrapping exe..."
	xml="$bindir-launch4j.xml"
	cat lib/launch4j.xml | sed "s|%L4J_BUILD%|$version|g" |\
		sed "s|%L4J_NAME%|$name|g" | sed "s|%L4J_JAR%|$dist|g" | sed "s|%L4J_FILENAME%|$name-$version.exe|g" |\
		sed "s|%L4J_ICON%|`pwd`/resources/icon.ico|g" | sed "s|%L4J_EXE%|`dirname "$dist"`/$name-$version.exe|g" |\
		sed "s|%L4J_VERS%|`echo $version | sed 's/\([0-9]\)/\1./g' | sed 's/\.$//'`|g" >$xml
	java -cp "$l4jdir/launch4j.jar" net.sf.launch4j.Main "$xml"
	rm "$xml"
else
	echo "Not wrapping"
fi

javastub=/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub
if [ -e "$javastub" ]; then
	echo "Bundling app..."
	bundledir="$bindir-app"
	if [ ! -d "$bundledir" ]; then mkdir "$bundledir"; fi
	appdir="$bundledir/$name.app/Contents"
	mkdir -p "$appdir"
	cp lib/Info.plist "$appdir/Info.plist"
	printf "APPL????" >"$appdir/PkgInfo" 
	mkdir -p "$appdir/MacOS"
	cp "$javastub" "$appdir/MacOS/JavaApplicationStub"
	mkdir -p "$appdir/Resources/Java"
	cp resources/icon.icns "$appdir/Resources/icon.icns"
	cp "$dist" "$appdir/Resources/Java/$name.jar"
	disttar="`dirname "$dist"`/$name-$version.tar"
	if [ -e "$disttar" ]; then rm "$disttar"; fi
	if [ "`which 7za`" ]; then
		(cd "$appdir/../../"; 7za a -ttar "$disttar" -r .)
	else
		tar cf "$disttar" -C "$appdir/../../" .
	fi
	rm -fr "$bundledir"
else
	echo "Not bundling"
fi

echo "Documentation..."
docscfg=resources/docs
javadoc="$(if [ -e "$jh" ]; then echo "`/usr/libexec/java_home -v 1.6`/bin/"; fi)javadoc"
$javadoc -quiet -d "docs" -version -author -windowtitle "`cat $docscfg/title.txt`" \
	-bottom "`cat $docscfg/bottom.txt`" -charset "utf-8" -docencoding "utf-8" \
	-classpath src -subpackages `cat $docscfg/packages.txt` -link http://docs.oracle.com/javase/6/docs/api/

