#!/bin/sh

echo
echo --- MPI deployment example ---------------------------------------------

echo " --- RUNNING AO-BASED 3D SLICED POISSON IN 8 MACHINES  ---"

#MPICC=$(which mpicc 2> /dev/null || echo "mpicc Not Found!" >&2 )
#[[ -z $MPICC ]] && exit 1
#workingDir=`dirname $0`
#. $workingDir/env.sh
PROACTIVE=../../../
EXAMPLES=$PROACTIVE/src/Examples/org/objectweb/proactive/examples/mpi

#XMLDESCRIPTOR=$PROACTIVE/descriptors/MPI-descriptor-wrapping-poisson3d-Local.xml
XMLDESCRIPTOR=$PROACTIVE/descriptors/examples/mpi/MPI-descriptor-poisson-AO.xml

$JAVACMD -Dproactive.old.parser=true -Dlog4j.configuration=file:$PROACTIVE/scripts/proactive-log4j -Dproactive.rmi.port=6099 -Djava.library.path=/home/sophia/emathias/ProActiveMPI/src/Core/org/objectweb/proactive/mpi/control/ org.objectweb.proactive.examples.mpi.proactive_poisson3d_sliced.Poisson3dSlicedWrapping  $XMLDESCRIPTOR 


echo
echo ------------------------------------------------------------
