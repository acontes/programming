#!/bin/sh

echo
echo --- Communicator ----------------------------------------------

workingDir=..
PROACTIVE=$workingDir/../..
CLASSPATH=.
. $workingDir/env.sh

$JAVACMD org.objectweb.proactive.extensions.scheduler.examples.AdminCommunicator $@


echo
