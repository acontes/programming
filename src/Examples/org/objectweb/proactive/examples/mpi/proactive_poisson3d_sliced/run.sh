#      nlev = 1, nplev1 = 16, nplev2 = 0;
#      dims1[0] = 2; dims1[1] = 4; dims1[2] = 2;
#      dims2[0] = 1; dims2[1] = 1; dims2[2] = 1;
#      ntx = 20; nty = 40; ntz = 20;
#      maxit = 10000; 
#      mxnp1 = 16
#      epsf = 1.0e-06;

#mpiexec -n 16 ./poisson3D 1 16 0 2 4 2 1 1 1 20 40 20 10000 16


#mpiexec -n 4 /usr/bin/valgrind --tool=cachegrind ./poisson3D 1 4 0 1 4 1 1 1 1 20 40 20 10000 4


# REMEMBER tjat arraysizez is specified by a macro in common.h
# Current value is 100 !
mpiexec -n 4 ./poisson3D 100 100 100 1000

#mpiexec -gdb -n 4 ./poisson3D

#mpiexec -n 4 /usr/bin/valgrind --tool=memcheck ./poisson3D 1 4 0 1 4 1 1 1 1 20 40 20 10000 4


