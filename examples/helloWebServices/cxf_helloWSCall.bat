@echo off
echo.
echo --- Hello World Web Service ---------------------------------------------

goto doit

:usage
echo.
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..

call "..\init.bat"

set CLASSPATH="%CLASSPATH%:%PROACTIVE%/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf/lib/cxf-manifest.jar"
%JAVA_CMD% org.objectweb.proactive.examples.webservices.helloWorld.WSClientCXF %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
