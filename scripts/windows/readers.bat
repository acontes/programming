@echo off
echo. 
echo --- Reader / Writer ---------------------------------------------

goto doit

:usage
echo. 
goto end


:doit
SETLOCAL
call init.bat
%JAVA_CMD%  org.objectweb.proactive.examples.readers.AppletReader
ENDLOCAL

:end
echo. 
echo -----------------------------------------------------------------
