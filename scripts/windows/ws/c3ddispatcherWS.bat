@echo off
echo. 
echo --- C3D ---------------------------------------------

goto doit

:usage
echo. 
goto end


:doit
SETLOCAL
IF NOT DEFINED PROACTIVE set PROACTIVE=..\..\..\.

call %PROACTIVE%\scripts\windows\init.bat

set XMLDESCRIPTOR=..\..\..\descriptors\C3D_Dispatcher_Renderer.xml 
%JAVA_CMD%  org.objectweb.proactive.examples.webservices.c3dWS.C3DDispatcher %XMLDESCRIPTOR% %1 
ENDLOCAL

:end
echo. 
echo ---------------------------------------------------------
