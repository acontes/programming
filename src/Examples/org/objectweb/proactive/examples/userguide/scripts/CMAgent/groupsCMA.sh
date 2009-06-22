#!/bin/sh

echo
echo --- Userguide CMAgent ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

export XMLDESCRIPTOR=$workingDir/../GCMDeployment/GCMA.xml
$JAVACMD org.objectweb.proactive.examples.userguide.cmagent.groups.Main $XMLDESCRIPTOR

echo
echo ------------------------------------------------------------
