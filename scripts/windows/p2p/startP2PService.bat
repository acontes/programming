@echo off
echo.
echo --- StartP2PService -------------------------------------

goto doit

:usage
echo. 
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION enabledelayedexpansion
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..\..

call "%PROACTIVE%\scripts\windows\init.bat"


%JAVA_CMD% org.objectweb.proactive.extra.p2p.service.StartP2PService %*

echo.

echo ---------------------------------------------------------