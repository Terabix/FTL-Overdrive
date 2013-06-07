@ECHO OFF
SETLOCAL
SETLOCAL ENABLEDELAYEDEXPANSION

CD /D "%~dp0"

SET APP_JAR=overdrive.jar

SET CLASSPATH=.
SET CLASSPATH=%CLASSPATH%;..\lib\jars\lwjgl.jar
SET CLASSPATH=%CLASSPATH%;..\lib\jars\xpp3-1.1.4c.jar
SET CLASSPATH=%CLASSPATH%;..\lib\jars\TWL.jar

SET PACKAGES=com.ftloverdrive
SET PACKAGES=%PACKAGES%,com.ftloverdrive.protocols.ftldat
SET PACKAGES=ECHO %PACKAGES:,=^^^&ECHO %


SET TASK_FAILED=0
CALL :clean

IF NOT EXIST build_jar MKDIR build_jar

SET COMPILED=0
CALL :compile
IF %TASK_FAILED%==1 GOTO die
SET COMPILED=1


CALL :build_jar
IF %TASK_FAILED%==1 GOTO die


:die
IF %TASK_FAILED%==1 (
  CALL :clean
  PAUSE 1>&2
) ELSE (
  CALL :clean
  IF %COMPILED%==1 PAUSE 1>&2
)
ENDLOCAL
EXIT /B



:clean
SET TASK_FAILED=0
ECHO.
ECHO Task: Clean

IF EXIST build_jar RMDIR /S /Q build_jar

GOTO :EOF



:compile
SET TASK_FAILED=0
ECHO.
ECHO Task: Compile

SET SOURCES=

FOR /F "delims=" %%i IN ('%PACKAGES%') DO (
  SET tmppath=%%i

  REM Replace periods with slashes
  SET tmppath=!tmppath:.=/!

  IF "!SOURCES!"=="" (
    SET SOURCES=!tmppath!/*.java
  ) ELSE (
    SET SOURCES=!SOURCES! !tmppath!/*.java
  )
)

PUSHD src\
javac.exe -d "..\build_jar" -classpath "%CLASSPATH%" %SOURCES% || SET TASK_FAILED=1
POPD

GOTO :EOF



:build_jar
SET TASK_FAILED=0
ECHO.
ECHO Task: Build Jar

IF EXIST "%APP_JAR%" DEL /Q "%APP_JAR%" >NUL 2>&1
IF EXIST "%APP_JAR%" (
  ECHO Error: Couldn't delete %APP_JAR%
  SET TASK_FAILED=1
  GOTO :EOF
)

PUSHD build_jar\
jar.exe cf "..\%APP_JAR%" * || SET TASK_FAILED=1
POPD
IF %TASK_FAILED%==1 GOTO :EOF

PUSHD resources\
jar.exe uf "..\%APP_JAR%" * || SET TASK_FAILED=1
POPD
IF %TASK_FAILED%==1 GOTO :EOF

GOTO :EOF
