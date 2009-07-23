#!/bin/sh

if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
	PROACTIVE=$workingDir/../../.
	CLASSPATH=.
fi

. ${workingDir}/../env.sh

#export CLASSPATH=../../:$CLASSPATH
#export CLASSPATH=../../lib/*:$CLASSPATH
#export CLASSPATH=../../classes/Core:$CLASSPATH
#export CLASSPATH=../../classes/Benchmarks:$CLASSPATH
#export CLASSPATH=../../classes/Examples:$CLASSPATH
#export CLASSPATH=../../classes/Extensions:$CLASSPATH
#export CLASSPATH=../../classes/Extra:$CLASSPATH
#export CLASSPATH=../../classes/Tests:$CLASSPATH
#export CLASSPATH=../../classes/Utils:$CLASSPATH

default_descriptor=../GCMD_Local.xml
default_hostname=`hostname`
default_st_operation=jls
descriptor_app="$workingDir/GCMA.xml"
descriptor_peer=$default_descriptor
descriptor_tracker=$default_descriptor

if [[ $# -lt  2 ]]	
then
        echo "Usage: $0 NB_PEERS TYPE [DEPLOYMENT]"
        echo "  NB_PEERS : the number of peers to create on the network."
        echo "  TYPE : "
        echo "    I  : for an interactive launching."
        echo "    ST : for a stress test that you can stop by q-ENTER."
        echo "  DEPLOYMENT : local or eons, default it's set on local."
        exit 1
fi

if [[ $# -gt 2 && "$3" == "eons" ]]
then 
	default_st_operation=$4
	descriptor_peer=GCMD-Peers.xml
	descriptor_tracker=GCMD-Trackers.xml
fi	

if [[ $# -eq 3 && $2 == "ST" ]]
then
	default_st_operation=$3
fi

if [[ $# -eq 4 && $2 == "ST" ]]
then
	default_st_operation=$4
fi

echo
if [[ "$2" == "I" ]]
then
	echo ---------- STRUCTURED P2P : Interactive Test  ----------
else 
	echo ---------- STRUCTURED P2P : Stress Test  ----------
fi
 
args="$descriptor_app $default_hostname $1 $2 $default_st_operation"
$JAVACMD -Ddescriptor.peer=$descriptor_peer -Ddescriptor.tracker=$descriptor_tracker org.objectweb.proactive.examples.structuredp2p.can.Launcher $args

echo
echo ---------------------------------------------------------
