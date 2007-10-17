#!/bin/sh

echo
echo --- Scheduler----------------------------------------------


  SCHEDULER_URL=$2
	RM=$1

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

CLASSPATH=$workingDir/../../scheduler-plugins-src/org.objectweb.proactive.scheduler.plugin/bin/:$CLASSPATH

echo $JAVACMD

$JAVACMD -Xmx512m -Xms512m -agentlib:yjpagent org.objectweb.proactive.extra.scheduler.examples.LocalSchedulerExample $RM $SCHEDULER_URL

echo

