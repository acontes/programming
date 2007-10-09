REM Script used to start the scilab tools
@echo off
echo. 
echo --- Scilab example ---------------------------------------------

:doit
<<<<<<< HEAD:scripts/windows/scilab/scilab.bat
SETLOCAL
=======
SETLOCAL ENABLEDELAYEDEXPANSION
>>>>>>> master:scripts/windows/scilab/scilab.bat
IF NOT DEFINED PROACTIVE set PROACTIVE=..\..\..

call "%PROACTIVE%\scripts\windows\init.bat"
call scilab_env.bat
%JAVA_CMD% org.objectweb.proactive.extensions.scilab.gui.SciFrame

ENDLOCAL

:end
pause
echo. 
echo ---------------------------------------------------------
