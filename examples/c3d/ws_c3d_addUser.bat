@echo off
echo.
echo --- C3D Add User WS ---------------------------------------------
echo --- (this example needs Tomcat Web Server installed and running) --------

goto doit

:usage
echo.
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..

call "..\init.bat"

set CLASSPATH="%CLASSPATH%:%PROACTIVE%/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf/lib/cxf-manifest.jar"

set XMLDESCRIPTOR=GCMA_User.xml
%JAVA_CMD% org.objectweb.proactive.examples.webservices.c3dWS.C3DUser %XMLDESCRIPTOR% %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
