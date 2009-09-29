#!/bin/sh

echo
echo --- Hello World Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

HTTP_OPT=""
if [ $# -eq 0 ] ; then
	echo "Webservices will be deployed in the embedded Jetty instance on port 8080"
	HTTP_OPT="-Dproactive.http.port=8080"
else
	echo "Webservices will be deployed in an external servlet container at $@ "
fi

echo
echo

CXF_HOME=$PROACTIVE/src/Extensions/org/objectweb/proactive/extensions/webservices/cxf
CLASSPATH=$CLASSPATH:$CXF_HOME/lib/cxf-manifest.jar
JAVACMD=$JAVACMD" -Dproactive.http.port=8080"
JAVACMD=$JAVACMD" -Djava.rmi.server.RMIClassLoaderSpi=org.objectweb.proactive.core.classloading.protocols.ProActiveRMIClassLoader"

#$JAVACMD $HTTP_OPT org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld "$@"
$JAVACMD  org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld "$@"

echo
echo ------------------------------------------------------------
