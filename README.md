# About Minecraft Region Scanner

A Minecraft 1.7.10 (only) command line utility to analyze and edit region files.

This utility fully supports the Lord of the Rings mod (LOTRmod) v34.3.

# Compilation

Just type "make" in a Unix-like computer.

You'll need a Java Compiler (I used Java 1.8) and a zip package.

# Installation / Configuration / Usage

Please check the tool's wiki at https://lotr-minecraft-mod-exiles.fandom.com/wiki/Minecraft_Region_Scanner.

# Versions and changelog

1.00 2019-07-11

    Initial public release. Supports v34.3 of the Lord of the Rings mod (LOTRmod)

# Caveats

Developed with Java 1.8 (recompile if your server is running under a lower version) and tested only on Minecraft 1.7.10 / Forge 10.13.4.1614.

# Limits & Known bugs

See the tool's wiki.

# Further development plans

All the main functionalities are implemented.

There's a list of possible unimplemented functionalities on the tool's wiki.

However I have no plan to port this to newer Minecraft versions as I will only use it for the LOTR mod.

# License

This open source software is distributed under a BSD license (see the "License" file for details).

# Credits

The Java Archive (jar) file used for binary distribution bundles 2 external libraries:

- Querz NBT 4.0 (see https://github.com/Querz/NBT)

- Apache Commons CLI 1.4 (see https://commons.apache.org/proper/commons-cli/)

The source code includes a snippet of code from lxknvlk (https://stackoverflow.com/users/3060279/lxknvlk) found at https://stackoverflow.com/questions/16273318/transliteration-from-cyrillic-to-latin-icu4j-java to transliterate cyrillic to latin.

# Author

Hubert Tournier

11 July 2019
