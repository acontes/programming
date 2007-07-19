#!/bin/sh

echo
echo --- Scheduler----------------------------------------------


  SCHEDULER_URL=$2
	RM=$1

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

CLASSPATH=../../../scheduler-plugins-src/org.objectweb.proactive.scheduler.plugin/bin/:$CLASSPATH

echo $JAVACMD

$JAVACMD org.objectweb.proactive.examples.scheduler.LocalSchedulerExample $RM $SCHEDULER_URL

echo

