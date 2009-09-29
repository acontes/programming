#!/bin/sh

echo
echo --- Undeploy Component Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

CXF_HOME=$PROACTIVE/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf
CLASSPATH=$CLASSPATH:$CXF_HOME/lib/cxf-manifest.jar
$JAVACMD org.objectweb.proactive.examples.webservices.helloWorld.UndeployComponent "$@"

echo
echo ------------------------------------------------------------
