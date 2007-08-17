#!/bin/sh

echo
echo --- HelloWorld----------------------------------------------

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

CLASSPATH=../../../scheduler-plugins-src/org.objectweb.proactive.scheduler.plugin/bin/:$CLASSPATH

echo $JAVACMD

$JAVACMD org.objectweb.proactive.examples.scheduler.SimpleHelloWorld

echo

