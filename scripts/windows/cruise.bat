@echo off
echo. 
echo --- CruiseControl ----------------------------------------

goto doit

:usage
echo. 
goto end


:doit
SETLOCAL
call init.bat
%JAVA_CMD% org.objectweb.proactive.examples.cruisecontrol.CruiseControlApplet
ENDLOCAL

:end
echo. 
echo ---------------------------------------------------------
