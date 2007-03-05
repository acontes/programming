#!/bin/sh

echo
echo --- Flowshop ---------------------------------------------



workingDir=`dirname $0`
. $workingDir/env.sh

$JAVACMD -Dflowshopparser.taillard=false org.objectweb.proactive.examples.bnb.flowshop.Main $@

echo
echo ------------------------------------------------------------
