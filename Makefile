CC=javac
CFLAGS=-g:none

SRC=src
LIB=lib
RES=resources
BINDIR=sbin
DOCSDIR=docs

DOCSCFG=$(RES)/docs
SIGNCFG=$(RES)/signing
IMGDIR=$(RES)/images
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
	jar cfm "$(DIST)" "$(MANIFEST)" -C "$(BINDIR)" . "license.txt" "acknowledgements.txt"  "$(IMGDIR)"/*.png

obfuscate: bundle
	cd "$(LIB)"; java -jar allatori.jar allatori.xml

codesign: obfuscate
	jarsigner -tsa http://timestamp.digicert.com -keystore "$(SIGNCFG)/jks" -storepass "`cat $(SIGNCFG)/passwd`" "$(DIST)" "1cc18f4c-900a-11e2-be97-c66279deb983"
	jarsigner -verify -certs "$(DIST)"

docs:
	@if [ -d "$(DOCSDIR)" ]; then rm -rf "$(DOCSDIR)"; fi
	javadoc -d "$(DOCSDIR)" -version -author -windowtitle "RSBot API Documentation" -header "RSBot&trade; API" -footer "`cat $(DOCSCFG)/footer.txt`" -bottom "`cat $(DOCSCFG)/bottom.txt`" -charset "utf-8" -docencoding "utf-8" -classpath src -subpackages `cat $(DOCSCFG)/packages.txt`

clean:
	@rm -f "$(DIST)"
	@rm -fr "$(BINDIR)"
	@rm -fr "$(DOCSDIR)"
