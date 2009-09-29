@echo off
echo.
echo --- C3D Dispatcher WS ---------------------------------------------
echo --- (this example needs Tomcat Web Server installed and running) --------

goto doit

:usage
echo.
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..

call "..\init.bat"

set CLASSPATH=%CLASSPATH%;%PROACTIVE%\src\Extensions\org\objectweb\proactive\extensions\webservices\cxf\lib\cxf-manifest.jar
set JAVA_CMD=%JAVA_CMD% -Djava.rmi.server.RMIClassLoaderSpi=org.objectweb.proactive.core.classloading.protocols.ProActiveRMIClassLoader
set XMLDESCRIPTOR=GCMA_Renderer.xml
%JAVA_CMD% -Dproactive.http.port=8080 org.objectweb.proactive.examples.webservices.c3dWS.C3DDispatcher %XMLDESCRIPTOR% %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
