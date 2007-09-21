#      nlev = 1, nplev1 = 16, nplev2 = 0;
#      dims1[0] = 2; dims1[1] = 4; dims1[2] = 2;
#      dims2[0] = 1; dims2[1] = 1; dims2[2] = 1;
#      ntx = 20; nty = 40; ntz = 20;
#      maxit = 10000; 
#      mxnp1 = 16
#      epsf = 1.0e-06;

#mpiexec -n 4 /usr/bin/valgrind --tool=cachegrind ./poisson3D 1 4 0 1 4 1 1 1 1 20 40 20 10000 4

#mpiexec -machinefile machineFile -n 4 /usr/bin/valgrind --tool=memcheck ./poisson3D 160 160 160 1000
#mpiexec -n 4 ./poisson3D 100 100 100 1000

#mpiexec -machinefile ~/mpiMachineFile_moult_cpu -n 4 ./poisson3D 100 100 100 2000

#mpiexec -n 4 /usr/bin/valgrind --tool=memcheck ./poisson3D 1 4 0 1 4 1 1 1 1 20 40 20 10000 4

x=1024
y=512
z=512
it=100
cluster_id=0

mpirun -machineFile $OAR_NODEFILE -np 64 ./poisson3D $x $y $z 1 1 1 4 4 4 $it $cluster_id
mpirun -machineFile $OAR_NODEFILE -np 32 ./poisson3D $x $y $z 1 1 1 4 4 2 $it $cluster_id
mpirun -machineFile $OAR_NODEFILE -np 16 ./poisson3D $x $y $z 1 1 1 4 2 2 $it $cluster_id
mpirun -machineFile $OAR_NODEFILE -np 8 ./poisson3D $x $y $z 1 1 1 2 2 2 $it $cluster_id
mpirun -machineFile $OAR_NODEFILE -np 4 ./poisson3D $x $y $z 1 1 1 2 2 1 $it $cluster_id
mpirun -machineFile $OAR_NODEFILE -np 2 ./poisson3D $x $y $z 1 1 1 2 1 1 $it $cluster_id

