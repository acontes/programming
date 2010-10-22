@echo off
echo.
echo --- Currency SMS example ---------------------------------------------
echo ---
echo --- An Orange API Access Key is needed to run this example.
echo --- See http://api.orange.com/en/api/sms-api,1 for more informations about Orange SMS API.
echo ---

rem if "%1" == "help" goto usage

goto doit

:usage
echo.
echo currencysms.bat [Orange_API_Access_Key] [Destination_Number]
echo.
goto doit


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..
call "..\init.bat"
set JAVA_CMD=%JAVA_CMD% -Dsca.provider=org.objectweb.proactive.extensions.component.sca.SCAFractive
%JAVA_CMD%  org.objectweb.proactive.examples.components.sca.currencysms.Main %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
