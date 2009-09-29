#!/bin/sh

echo
echo --- Undeploy Active Object Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

for i in `ls $PROACTIVE/lib/cxf`
do
   CLASSPATH=$CLASSPATH:$PROACTIVE/lib/cxf/$i
done

$JAVACMD org.objectweb.proactive.examples.webservices.helloWorld.Undeploy "$@"

echo
echo ------------------------------------------------------------
