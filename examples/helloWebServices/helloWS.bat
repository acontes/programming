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

set JAVA_CMD=%JAVA_CMD% -Djava.rmi.server.RMIClassLoaderSpi=org.objectweb.proactive.core.classloading.protocols.ProActiveRMIClassLoader
set CLASSPATH=%CLASSPATH%;%PROACTIVE%\src\Extensions\org\objectweb\proactive\extensions\webservices\cxf\lib\cxf-manifest.jar

%JAVA_CMD% -Dproactive.http.port=8080 org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
