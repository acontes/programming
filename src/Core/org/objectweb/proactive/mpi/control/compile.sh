javah -jni org.objectweb.proactive.mpi.control.ProActiveMPIComm
 
mpicc -I${DISCOGRID_HOME}/misc/mpich/include -L${DISCOGRID_HOME}/misc/mpich/lib/ -I${DISCOGRID_HOME}/misc/jdk/include  -I${DISCOGRID_HOME}/misc/jdk/include/linux ProActiveMPIComm.c ./config/src/CommonInternalApi.c  -o  libProActiveMPIComm.so -shared -fPIC

cp  libProActiveMPIComm.so ../../../../../../../classes/Core/org/objectweb/proactive/mpi/control/

