javah -jni org.objectweb.proactive.mpi.control.ProActiveMPIComm
 
mpicc -I/home/sophia/emathias/misc/mpich/include -L/home/sophia/emathias/misc/mpich/lib/ -I/home/sophia/emathias/Java/jdk1.6.0_01/include  -I/home/sophia/emathias/Java/jdk1.6.0_01/include/linux ProActiveMPIComm.c ./config/src/CommonInternalApi.c  -o  libProActiveMPIComm.so -shared -fPIC

cp  libProActiveMPIComm.so ../../../../../../../classes/Core/org/objectweb/proactive/mpi/control/

