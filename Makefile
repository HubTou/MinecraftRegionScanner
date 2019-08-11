VERSION=1.03

JC= javac
JFLAGS= -Xlint:unchecked -d bin -sourcepath src -cp $(CLASSPATH)
CLASSPATH= lib/commons-cli-1.4.jar:lib/nbt-4.1.jar
DL= fetch
DLFLAGS= 
#DL= curl
#DLFLAGS= -Os
RM= rm -f

default: makedir srcdist bindist

makedir:
	@mkdir -p bin
	@mkdir -p jar
	@mkdir -p lib/dist
	@mkdir -p src
	@mkdir -p test

srcdist: clean
	@zip -r RegionScanner-$(VERSION)-src.zip License README.md Makefile NotOfMiddleEarth.cfg bin jar lib src

bindist: jar
	@zip -r RegionScanner-$(VERSION).zip License README.md NotOfMiddleEarth.cfg RegionScanner-$(VERSION).jar RegionScanner test

jar: bin/RegionScanner.class
	@$(RM) -r RegionScanner-$(VERSION).jar RegionScanner jar/*
	@( cd jar ; unzip -q ../lib/nbt-4.1.jar ; mv META-INF/MANIFEST.MF META-INF/MANIFEST.MF.nbt-4.1 )
	@( cd jar ; unzip -q ../lib/commons-cli-1.4.jar ; mv META-INF/MANIFEST.MF META-INF/MANIFEST.MF.commons-cli-1.4 )
	@( cd jar ; cp -R ../bin/* . ; echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF ; echo "Main-Class: org.tournier.RegionScanner.RegionScanner" >> META-INF/MANIFEST.MF ; echo "" >> META-INF/MANIFEST.MF )
	@( cd jar ; zip -qr ../RegionScanner-$(VERSION).jar * )
	@( echo "#!/bin/sh" > RegionScanner ; echo "java -jar RegionScanner-$(VERSION).jar \$$*" >> RegionScanner ; chmod a+x RegionScanner )

bin/RegionScanner.class: libs
	@$(JC) $(JFLAGS) src/org/tournier/RegionScanner/*.java

libs: lib/commons-cli-1.4.jar lib/nbt-4.1.jar

lib/commons-cli-1.4.jar: lib/dist/commons-cli-1.4-bin.tar.gz
	@( cd lib ; tar xzf dist/commons-cli-1.4-bin.tar.gz ; mv commons-cli-1.4/commons-cli-1.4.jar . ; rm -rf commons-cli-1.4 )

lib/dist/commons-cli-1.4-bin.tar.gz:
	@( cd lib/dist ; $(DL) $(DLFLAGS) https://www-us.apache.org/dist//commons/cli/binaries/commons-cli-1.4-bin.tar.gz )

lib/nbt-4.1.jar: lib/dist/4.1.tar.gz
	@( cd lib ; tar xzf dist/4.1.tar.gz ; cd NBT-4.1 ; gradlew build ; mv build/libs/nbt-4.1.jar .. ; cd .. ; rm -rf NBT-4.1 )

lib/dist/4.1.tar.gz:
	@( cd lib/dist ; $(DL) $(DLFLAGS) https://github.com/Querz/NBT/archive/4.1.tar.gz )

clean:
	@$(RM) -r bin/*
	@$(RM) -r jar/*

distclean: clean
	@$(RM) -r RegionScanner-$(VERSION).jar RegionScanner RegionScanner-$(VERSION)-src.zip RegionScanner-$(VERSION).zip lib/commons-cli-1.4.jar lib/dist/commons-cli-1.4-bin.tar.gz lib/nbt-4.1.jar lib/dist/4.1.tar.gz

