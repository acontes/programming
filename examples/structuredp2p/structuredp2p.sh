#!/bin/sh

if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
	PROACTIVE=$workingDir/../../.
	CLASSPATH=.
fi

. ${workingDir}/../env.sh

default_descriptor=${workingDir}/GCMA.xml

echo
echo --- STRUCTURED P2P - 2D Active Grid ---------------------

args="-descriptor $default_descriptor"

export CLASSPATH=$CLASSPATH:./../../classes/Core
export CLASSPATH=$CLASSPATH:./../../classes/Benchmarks
export CLASSPATH=$CLASSPATH:./../../classes/Exemples
export CLASSPATH=$CLASSPATH:./../../classes/Extensions
export CLASSPATH=$CLASSPATH:./../../classes/Extra
export CLASSPATH=$CLASSPATH:./../../classes/Tests
export CLASSPATH=$CLASSPATH:./../../classes/Utils

$JAVACMD org.objectweb.proactive.extensions.structuredp2p.grid2D.Main $args

echo
echo ---------------------------------------------------------
