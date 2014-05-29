CC=javac
CFLAGS=-target 1.6 -source 1.6 -bootclasspath "`/usr/libexec/java_home -v 1.6`/../Classes/classes.jar:`/usr/libexec/java_home -v 1.6`/../Classes/jce.jar"

SRC=src
LIB=lib
RES=resources
BINDIR=sbin
DOCSDIR=docs

DOCSCFG=$(RES)/docs
SIGNCFG=$(RES)/signing
MANIFEST=$(RES)/Manifest.txt
NAME=RSBot
DIST=$(LIB)/$(NAME).jar

.PHONY: all bot bundle obfuscate codesign docs clean

all: clean docs codesign

bot:
	@if [ -d "$(BINDIR)" ]; then mkdir "$(BINDIR)"; fi
	@mkdir "$(BINDIR)"
	$(CC) $(CFLAGS) -d "$(BINDIR)" `find "$(SRC)" -name \*.java`

bundle: bot
	@if [ -e "$(DIST)" ]; then rm -f "$(DIST)"; fi
	jar cfm "$(DIST)" "$(MANIFEST)" -C "$(BINDIR)" .

obfuscate: bundle
	cd "$(LIB)"; java -jar allatori.jar allatori.xml

codesign: obfuscate
	jarsigner -tsa http://timestamp.digicert.com -keystore "$(SIGNCFG)/jks" -storepass "`cat $(SIGNCFG)/passwd`" "$(DIST)" "`cat $(SIGNCFG)/alias`"
	jarsigner -verify -certs "$(DIST)"

docs:
	@if [ -d "$(DOCSDIR)" ]; then rm -rf "$(DOCSDIR)"; fi
	`/usr/libexec/java_home -v 1.6`/bin/javadoc -d "$(DOCSDIR)" -version -author -windowtitle "RSBot API Documentation" -header "RSBot&trade; API" \
		-footer "`cat $(DOCSCFG)/footer.txt`" -bottom "`cat $(DOCSCFG)/bottom.txt`" -charset "utf-8" -docencoding "utf-8" \
		-classpath src -subpackages `cat $(DOCSCFG)/packages.txt` \
		-link http://docs.oracle.com/javase/6/docs/api/ java -link http://docs.oracle.com/javase/6/docs/api/ javax -exclude java:javax

clean:
	@rm -f "$(DIST)"
	@rm -fr "$(BINDIR)"
	@rm -fr "$(DOCSDIR)"
