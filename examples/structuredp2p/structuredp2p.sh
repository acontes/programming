#!/bin/sh

if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
	PROACTIVE=$workingDir/../../.
	CLASSPATH=.
fi

. ${workingDir}/../env.sh

usage() {
cat << EOF
Usage: $0 OPTIONS

This script run a structured peer-to-peer network on a machine or a grid in the desired mode for testing : 
	* Interactive : allows the user to choose the type of actions to perform (join, leave, search, list...)
	* Stress Test : launches randomly one of three basic operations (join, leave, search) on the network.

OPTIONS:
    -n     The number of peers to put on the network when it is created. Default is set to 10
    -i     Interactive mode 
    -s     Stress Test mode. It takes an argument which is the type of 
           operations to perform : 'j' for the join operation, 'l' for the leave
           operation, 's' for a lookup operation. 
    -d     Indicates which GCMD to use for the ProActive application deployment: 
           'localhost' or 'eons'. Default is set to 'localhost'.	
   
EXAMPLES:
    $0 -n 10 -i         Initializes a structured p2p network in interactive mode with 10 peers on.
    $0 -n 10 -st js     Initializes a structured p2p network in stress test mode with 10 peers on.
                        Operations that will be performed are only join and search.
EOF
}

NUMBER_OF_PEERS_TO_CREATE=10
MODE=
MODE_ARGS=
DESCRIPTOR_APP="$workingDir/GCMA.xml"
DESCRIPTOR_PEERS="../GCMD_Local.xml"
DESCRIPTOR_TRACKERS="../GCMD_Local.xml"

while getopts “n:is:d:” OPTION
do
     case $OPTION in
     	h)
        	usage
        	exit
        	;;
		n)
			NUMBER_OF_PEERS_TO_CREATE=$OPTARG
			;;
		i)
			MODE=i
			;;
		s)
			MODE=st
			MODE_ARGS=$OPTARG
			;;
		d)
			if [[ "$OPTARG" == "eons" ]]
			then
				DESCRIPTOR_PEERS="GCMD-Peers.xml"
				DESCRIPTOR_TRACKERS="GCMD-Trackers.xml"
			fi
            ;;
		?)
			usage
			exit
			;;
     esac
done

if [[ -z $MODE ]]
then
	usage
	exit
fi

if [[ "$MODE" == "i" ]]
then
	echo ---------- STRUCTURED P2P : Interactive Test  ----------
else 
	echo ---------- STRUCTURED P2P : Stress Test  ----------
fi
 
ARGS="$DESCRIPTOR_APP $NUMBER_OF_PEERS_TO_CREATE $MODE $MODE_ARGS"
MAIN_CLASS=org.objectweb.proactive.examples.structuredp2p.Launcher

$JAVACMD -Ddescriptor.peers=$DESCRIPTOR_PEERS -Ddescriptor.trackers=$DESCRIPTOR_TRACKERS $MAIN_CLASS $ARGS

echo
echo ---------------------------------------------------------
