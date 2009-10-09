#!/bin/sh

echo
echo --- GCM native execution of ProActive/MPI application: HelloWorld example -----------------------

export PROACTIVE_HOME="/user/emathias/desktop/home/SHORT_MPI_Extension"

workingDir=`dirname $0`
. ${workingDir}/../../env.sh


#########################
# MPI PROGRAM EXECUTION THROUGH GCM
#########################
echo "--- Starting hello World example" 

#workaround to guarantee same launching folder on mpifork, despite of strange NFS configs
#cd /tmp 

$JAVACMD -Dos=unix -Djava.library.path=$PROACTIVE/dist/lib/native -Ddiscogrid.runtime.type=AOB -Dproactive.home=$PROACTIVE_HOME -Djava.library.path=$LD_LIBRARY_PATH org.objectweb.proactive.extensions.nativecode.NativeStarter $PROACTIVE/examples/mpi/proactive_mpi/gcma.pa.xml  $PROACTIVE/examples/mpi/proactive_mpi/gcma.mpi1.xml $PROACTIVE/examples/mpi/proactive_mpi/gcma.mpi2.xml

#cd -



echo
echo ---------------------------------------------------------
