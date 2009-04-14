#!/bin/sh

if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
	PROACTIVE=$workingDir/../../.
	CLASSPATH=.
fi

. ${workingDir}/../env.sh

default_descriptor=${workingDir}/GCMA.xml

if [[ $# -eq  0 ]]
then
        echo "Usage: $0 ROWS COLS"
        echo "  ROWS : the number of rows for the grid"
        echo "  COLS : the number of columns for the grid"
        exit 1
fi

echo
echo --- STRUCTURED P2P - 2D Active Grid ---------------------

args="-descriptor $default_descriptor $1 $2"



$JAVACMD org.objectweb.proactive.extensions.structuredp2p.grid2D.Launcher $args

echo
echo ---------------------------------------------------------
