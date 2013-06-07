FTL-Overdrive-1
===============

Engine rewrite for FTL: Faster Than Light with a focus on moddability.

This project uses [Themable Widget Library](http://l33tlabs.org).

Requires: Java 1.6+

-

Several batch files are included:

 1. \_overdrive_build.bat
    - Compiles Overdrive's source and creates overdrive.jar, including everything in the "./resources/" dir.
 2. \_overdrive\_run.bat
    - Runs overdrive.jar.
 3. \_themer\_run.bat
    - Runs the TWL Theme Editor.
 4. \_themer\_ftldat\_run.bat
    - Runs the TWL Theme Editor with support for FTL dats. See below.

-

For reference it is recommended, though not required, that you manually extract FTL's resources somewhere.

-

A special protocol has been added so TWL Theme URLs can reference images inside FTL dats.

    ftldat://resource.dat/img/main_menus/main_base2.png

If you want to use the Theme Editor, compile overdrive.jar first. It contains a launcher that will set up that protocol handler, then start the editor. Go to File-Open Project, and choose "./resources/theme/FTL-OD.xml".

At runtime "./theme/" will be created by extracting a copy from overdrive.jar (if the dir doesn't already exist). If you edit the contents of *that* folder, you can press F5 in-game to reload the theme without restarting.

-

To update the theme ("./resources/theme/" or "./theme/" ?) while debugging:

 1. Edit the theme.
 2. Right click on the project in Eclipse and select Refresh
 3. Press F5 in-game.
