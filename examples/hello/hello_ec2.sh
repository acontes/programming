#!/bin/sh

echo
echo --- Hello World example ---------------------------------------------

workingDir=`dirname $0`
. $workingDir/../env.sh
PROACTIVE=$workingDir/../..
echo "Starting EC2 deployment"

XMLDESCRIPTOR=${workingDir}/helloApplication.xml

GCMD=helloDeploymentEC2.xml

$JAVACMD  -Dgcmdfile=${GCMD} -Dos=unix org.objectweb.proactive.examples.hello.Hello $XMLDESCRIPTOR

echo
echo ------------------------------------------------------------
