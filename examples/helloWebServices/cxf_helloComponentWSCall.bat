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
SET JAVA_CMD="%JAVA_CMD% -Dfractal.provider=org.objectweb.proactive.core.component.Fractive"
%JAVA_CMD% org.objectweb.proactive.examples.webservices.helloWorld.WSClientComponentCXF %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
