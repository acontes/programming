@echo off
echo.
echo --- StartP2PService -------------------------------------

goto doit

:usage
echo. 
goto end


:doit
SETLOCAL enabledelayedexpansion
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..\..

call "%PROACTIVE%\scripts\windows\init.bat"


%JAVA_CMD% org.objectweb.proactive.p2p.service.StartP2PService %*

echo.

echo ---------------------------------------------------------