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

if [[ $# -eq  0 ]]
then
        echo "Usage: $0 [TYPE_LAUNCH]"
        echo "  NB_PEERS : number of peers to start"
        echo "  ENTRY_POINT : the hostname of the computer which is used for entryPoint"
        exit 1
fi

echo
echo ---------- STRUCTURED P2P ----------

args="$default_descriptor $2 ${1-$default_hostname}"

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
