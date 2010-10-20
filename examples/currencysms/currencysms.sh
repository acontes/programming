#!/bin/sh

echo
echo --- currency sms example ---------------------------------------------
echo ---
echo --- The expected result is an exception
echo ---

workingDir=`dirname $0`
. ${workingDir}/../env.sh
#echo $PROACTIVE

#if [ $# -lt 1 ]; then
#    echo "
#       usage :
#         currencysms.sh <parameters>

#		optional parameters are :
#			- parser
#			- wrapper
#			- distributed (needs parser)
#    "
#    exit 1
#fi

$JAVACMD org.objectweb.proactive.examples.components.sca.currencysms.Main "$@"
echo
echo ---------------------------------------------------------
