@echo off
echo. 
echo --- Results retriever -------------------------------------

SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..\..
call "%PROACTIVE%\scripts\windows\init.bat"

%JAVA_CMD% org.objectweb.proactive.extensions.scheduler.examples.GetJobResult %1 %2 %3

:end
echo. 
echo ---------------------------------------------------------