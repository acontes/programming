#!/bin/sh

echo
echo --- HelloWorld 2----------------------------------------------

  SCHEDULER_URL=$1


workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

$JAVACMD org.objectweb.proactive.examples.scheduler.HelloRet $SCHEDULER_URL

echo

