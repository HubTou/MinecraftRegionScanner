VERSION=1.0

JC= javac
CLASSPATH= lib/commons-cli-1.4.jar:lib/nbt-4.0.jar
JFLAGS= -Xlint:unchecked -d bin -sourcepath src -cp $(CLASSPATH)
RM= rm -f

default: makedir srcdist bindist

makedir:
	@mkdir -p bin
	@mkdir -p jar
	@mkdir -p lib
	@mkdir -p src

srcdist: clean
	@zip -r RegionScanner-$(VERSION)-src.zip License README.md Makefile NotOfMiddleEarth.cfg bin jar lib src

bindist: jar
	@zip -r RegionScanner-$(VERSION).zip License README.md NotOfMiddleEarth.cfg RegionScanner-$(VERSION).jar RegionScanner

jar: bin/RegionScanner.class
	@$(RM) -r RegionScanner-$(VERSION).jar RegionScanner jar/*
	@( cd jar ; unzip -q ../lib/nbt-4.0.jar ; mv META-INF/MANIFEST.MF META-INF/MANIFEST.MF.nbt-4.0 )
	@( cd jar ; unzip -q ../lib/commons-cli-1.4.jar ; mv META-INF/MANIFEST.MF META-INF/MANIFEST.MF.commons-cli-1.4 )
	@( cd jar ; cp -R ../bin/* . ; echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF ; echo "Main-Class: org.tournier.RegionScanner.RegionScanner" >> META-INF/MANIFEST.MF ; echo "" >> META-INF/MANIFEST.MF )
	@( cd jar ; zip -qr ../RegionScanner-$(VERSION).jar * )
	@( echo "#!/bin/sh" > RegionScanner ; echo "java -jar RegionScanner-$(VERSION).jar \$$*" >> RegionScanner ; chmod a+x RegionScanner )

bin/RegionScanner.class:
	@$(JC) $(JFLAGS) src/org/tournier/RegionScanner/*.java

clean:
	@$(RM) -r bin/*
	@$(RM) -r jar/*

distclean: clean
	@$(RM) -r RegionScanner-$(VERSION).jar RegionScanner RegionScanner-$(VERSION)-src.zip RegionScanner-$(VERSION).zip

