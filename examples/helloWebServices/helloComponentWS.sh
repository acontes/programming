#!/bin/sh

echo
echo --- Hello World Component Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

CXF_HOME=$PROACTIVE/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf
CLASSPATH=$CLASSPATH:$CXF_HOME/lib/cxf-manifest.jar
JAVACMD=$JAVACMD" -Dproactive.http.port=8080 -Dfractal.provider=org.objectweb.proactive.core.component.Fractive"

$JAVACMD org.objectweb.proactive.examples.webservices.helloWorld.HelloWorldComponent "$@"

echo
echo ------------------------------------------------------------
