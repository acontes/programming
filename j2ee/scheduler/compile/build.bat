@echo off

if NOT DEFINED JAVA_HOME goto javahome
if NOT DEFINED PROACTIVE_HOME goto pahome
if "%JAVA_HOME%" == "" goto javahome
if "%1" == "" goto projecthelp


:build
SETLOCAL
set CLASSPATH=compile\lib\ant-launcher.jar;%CLASSPATH%
echo %CLASSPATH%
set compileDir=%PROACTIVE_HOME%\compile
cd ..
"%JAVA_HOME%\bin\java" "-Dant.home=compile" "-Dant.library.dir=%compileDir%\lib"  -Xmx512000000 org.apache.tools.ant.launch.Launcher -buildfile compile\build.xml %1 %2 %3 %4 %5 %WHEN_NO_ARGS%
ENDLOCAL
goto end


:projecthelp
set WHEN_NO_ARGS="-projecthelp"
goto build

:pahome
echo.
echo PROACTIVE_HOME environment variable is not set. Will try to guess it
set PROACTIVE_HOME="..\..\..\"
echo "I guess your PROACTIVE_HOME is %PROACTIVE_HOME%. If not, try to set it manually!"
goto build

:javahome
echo.
echo The enviroment variable JAVA_HOME must be set to the current jdk 
echo distribution installed on your computer.
echo Use 
echo    set JAVA_HOME=<the directory where is the JDK>
goto end


:end
set WHEN_NO_ARGS=
