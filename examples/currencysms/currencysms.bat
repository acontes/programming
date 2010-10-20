@echo off
echo.
echo ------------------------------------------

goto doit

:usage
echo.
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..

call "..\init.bat"

%JAVA_CMD% org.objectweb.proactive.examples.components.sca.currencysms.Main
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
