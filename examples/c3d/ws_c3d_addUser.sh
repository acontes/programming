#!/bin/sh

echo
echo --- C3D ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

CLASSPATH=$CLASSPATH:$PROACTIVE_HOME/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf/lib/cxf-manifest.jar

export XMLDESCRIPTOR=$workingDir/GCMA_User.xml
$JAVACMD org.objectweb.proactive.examples.webservices.c3dWS.WSUser $XMLDESCRIPTOR "$@"



echo
echo ---------------------------------------------------------
