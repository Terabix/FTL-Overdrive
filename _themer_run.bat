@ECHO OFF
SETLOCAL

CD /D "%~dp0"

SET CLASSPATH=.
SET CLASSPATH=%CLASSPATH%;lib\jars\xpp3-1.1.4c.jar
SET CLASSPATH=%CLASSPATH%;lib\jars\lwjgl.jar
SET CLASSPATH=%CLASSPATH%;lib\jars\TWL.jar
SET CLASSPATH=%CLASSPATH%;themer\TWLThemeEditor.jar
SET CLASSPATH=%CLASSPATH%;themer\TWLEffects.jar
SET CLASSPATH=%CLASSPATH%;themer\jna.jar
SET CLASSPATH=%CLASSPATH%;themer\asm3.jar
SET CLASSPATH=%CLASSPATH%;themer\asm-tree-3.1.jar
SET CLASSPATH=%CLASSPATH%;themer\asm-analysis-3.1.jar
SET CLASSPATH=%CLASSPATH%;themer\JavaFreeType.jar

java.exe -Djava.library.path="lib\natives" -cp "%CLASSPATH%" de.matthiasmann.twlthemeeditor.Main

PAUSE
ENDLOCAL
EXIT /B
