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

#if [[ $# -eq  0 ]]
#then
#        echo "Usage: $0 [ENTRY_POINT] [NB_PEERS]"
#        echo "  ENTRY_POINT : the hostname of the computer which is used for entryPoint"
#        echo "  NB_PEERS : number of peers to start"
#        exit 1
#fi

echo
echo --- STRUCTURED P2P - Active Grid2D ---------------------

args="$default_descriptor ${1-$default_hostname} $2"

export CLASSPATH=../../lib/*:$CLASSPATH
export CLASSPATH=../../classes/Core:$CLASSPATH
export CLASSPATH=../../classes/Benchmarks:$CLASSPATH
export CLASSPATH=../../classes/Examples:$CLASSPATH
export CLASSPATH=../../classes/Extensions:$CLASSPATH
export CLASSPATH=../../classes/Extra:$CLASSPATH
export CLASSPATH=../../classes/Tests:$CLASSPATH
export CLASSPATH=../../classes/Utils:$CLASSPATH

$JAVACMD org.objectweb.proactive.extensions.structuredp2p.examples.canoverlay.Launcher $args

echo
echo ---------------------------------------------------------