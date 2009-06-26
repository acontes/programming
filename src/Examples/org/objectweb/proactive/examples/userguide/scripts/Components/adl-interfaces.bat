@echo off
echo.
echo --- User Guide: ADL Interfaces ------------------------------------------

goto doit

:usage
echo.
goto end


:doit
SETLOCAL ENABLEDELAYEDEXPANSION

call ..\init.bat

set JAVA_CMD=%JAVA_CMD% -Dfractal.provider="org.objectweb.proactive.core.component.Fractive"

%JAVA_CMD% org.objectweb.proactive.examples.userguide.components.adl.interfaces.Main %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------