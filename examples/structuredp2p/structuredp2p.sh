#!/bin/sh

if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
	PROACTIVE=$workingDir/../../.
	CLASSPATH=.
fi

. ${workingDir}/../env.sh

default_descriptor=${workingDir}/GCMA.xml
default_hostname=`hostname`

if [[ $# -lt  2 ]]	
then
        echo "Usage: $0 NB_PEERS TYPE"
        echo "  NB_PEERS : the number of peers to create on the network."
        echo "  TYPE : "
        echo "    I  : for an interactive launching."
        echo "    ST : for a stress test that you can stop by q-ENTER."
        exit 1
fi

echo
echo ---------- STRUCTURED P2P ----------

args="$default_descriptor $2 $3 ${1-$default_hostname}"

export CLASSPATH=../../lib/*:$CLASSPATH
export CLASSPATH=../../classes/Core:$CLASSPATH
export CLASSPATH=../../classes/Benchmarks:$CLASSPATH
export CLASSPATH=../../classes/Examples:$CLASSPATH
export CLASSPATH=../../classes/Extensions:$CLASSPATH
export CLASSPATH=../../classes/Extra:$CLASSPATH
export CLASSPATH=../../classes/Tests:$CLASSPATH
export CLASSPATH=../../classes/Utils:$CLASSPATH

$JAVACMD org.objectweb.proactive.examples.structuredp2p.can.Launcher $args

echo
echo ---------------------------------------------------------
