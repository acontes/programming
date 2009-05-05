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
        echo "Usage: $0 NB_ROWS NB_COLS [ENTRY_POINT]"
        echo "  NB_ROWS : the number of rows for the grid"
        echo "  NB_COLS : the number of columns for the grid"
        echo "  ENTRY_POINT : the hostname of the computer which is used for entryPoint"
        exit 1
fi

echo
echo --- STRUCTURED P2P - Active Grid2D ---------------------

args="$1 $2 $default_descriptor ${3-$default_hostname}"

export CLASSPATH=../../lib/*:$CLASSPATH
export CLASSPATH=../../classes/Core:$CLASSPATH
export CLASSPATH=../../classes/Benchmarks:$CLASSPATH
export CLASSPATH=../../classes/Examples:$CLASSPATH
export CLASSPATH=../../classes/Extensions:$CLASSPATH
export CLASSPATH=../../classes/Extra:$CLASSPATH
export CLASSPATH=../../classes/Tests:$CLASSPATH
export CLASSPATH=../../classes/Utils:$CLASSPATH

$JAVACMD org.objectweb.proactive.extensions.structuredp2p.grid2D.Launcher $args

echo
echo ---------------------------------------------------------
