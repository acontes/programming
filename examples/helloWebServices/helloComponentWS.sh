#!/bin/sh

echo
echo --- Hello World Component Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

JAVACMD=$JAVACMD" -Dproactive.http.port=8080 -Dfractal.provider=org.objectweb.proactive.core.component.Fractive"
JAVACMD=$JAVACMD" -Djava.rmi.server.RMIClassLoaderSpi=org.objectweb.proactive.core.classloading.protocols.ProActiveRMIClassLoader"

$JAVACMD org.objectweb.proactive.examples.webservices.helloWorld.HelloWorldComponent "$@"

echo
echo ------------------------------------------------------------
