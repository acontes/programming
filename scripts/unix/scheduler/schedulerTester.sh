#!/bin/sh

echo
echo --- SCHEDULER STRESS TEST ---------------------------------------------

echo shedulerTester [schedulerURL] [MaxSubmissionPeriod] [MaxNbJobs]

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

echo $JAVACMD

$JAVACMD org.objectweb.proactive.examples.scheduler.SchedulerTester $1 $2 $3

echo

