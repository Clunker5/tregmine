The Tregmine Plugin
========

This is the source manager for the Tregmine plugin.
Index
-----

The main plugin is in the src directory. In addition, there are a few utilities
and tools in this repository:

 * delegate_gen - Generates a class from an interface that can be extended.
 * eventdebug - Development tool for when Bukkit events get triggered.
 * gamemagic - Stuff specific to the main tregmine world
 * world_importer - Tool for transitioning from the old tregmine database
 format.
 * fix_broken - Simple tool for scanning mc region files and discarding broken chunks
 * mojang_nbt - Mojangs lib for parsing nbt files
 * zone_exporter - Tool for exporting tregmine zones as single player levels
 * zone_mapper - Tool for generating maps from tregmine zones

Building and installing
========
First, build Tregmine. You can do this by running
```
gradle build
```
in the root directory.

Next, take the jar with the name 'tregmine-withDependencies.jar' - It is vital you take this file and not the generic 'tregmine.jar' -- This way, your jar file has all of the necessary libraries. Put the jar in your plugins folder.

Take the provided .sql file in the repo (the name may change which is why only the extension is provided) and import it to the desired database.

Run your server and let Tregmine do first-run operations. Then stop the server and configure the generated config.yml

You're all set! (To make yourself an admin, run `promote <USERNAME> SENIOR_ADMIN` from the console)
Current Coders
-------
 * Eric Rabil <ericjrabil@gmail.com> - Current maintainer

Special Thanks To
------------
 * Ein Andersson - Original author
 * Emil Hernvall
 * Josh Morgan
 * Joe Notaro
 * James Sherlock
 * Robby Catron
