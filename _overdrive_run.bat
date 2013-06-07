@ECHO OFF
SETLOCAL

CD /D "%~dp0"

SET JAVA_CMD=D:\Apps\j2sdk1.6.0_13\bin\java.exe

SET APP_JAR=overdrive.jar

SET CLASSPATH=.
SET CLASSPATH=%CLASSPATH%;%APP_JAR%
SET CLASSPATH=%CLASSPATH%;lib\jars\lwjgl.jar
SET CLASSPATH=%CLASSPATH%;lib\jars\xpp3-1.1.4c.jar
SET CLASSPATH=%CLASSPATH%;lib\jars\TWL.jar

"%JAVA_CMD%" -Djava.library.path="lib\natives" -cp "%CLASSPATH%" com.ftloverdrive.OverDrive

PAUSE
ENDLOCAL
EXIT /B
