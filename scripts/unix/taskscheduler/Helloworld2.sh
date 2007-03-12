#!/bin/sh

echo
echo --- HelloWorld2 ----------------------------------------------

if [ $# -lt 1 ]; then
    SCHEDULER_URL=//localhost/SchedulerNode
else
  SCHEDULER_URL=$1
fi

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

$JAVACMD org.objectweb.proactive.taskscheduler.HelloWorld2 $SCHEDULER_URL

echo

