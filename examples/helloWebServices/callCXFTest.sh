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

for i in `ls $PROACTIVE/lib/cxf`
do
   CLASSPATH=$CLASSPATH:$PROACTIVE/lib/cxf/$i
done

#$JAVACMD $HTTP_OPT org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld "$@"
$JAVACMD -Dproactive.http.port=8080 org.objectweb.proactive.examples.webservices.helloWorld.CallCXFTest "$@"

echo
echo ------------------------------------------------------------
