#include<mpi.h>
#include <stdio.h>

void mkpart(int my_rank, int cluster_id, int total_nb_process, int local_nb_process, int * neighbour_tab,
			int * global_x, int * global_y, int * global_z, int nx) {
			
	// range of cluster_id is [1 ... nbCluster]
	// As each cluster start mpi processes from [0 ... nbProcess[ we map
	// mpi processes to the matrix partition

	// assert my_rank > 0 && cluster_id > 0
	/*
	int binf = (cluster_id * local_nb_process);
	int bsup = ((cluster_id + 1) * local_nb_process) - 1;
	*/
	int global_id = my_rank + (cluster_id * local_nb_process);
	/*
	if (global_id == binf) {
		neighbour_tab[0] = MPI_PROC_NULL;
	} else {
		neighbour_tab[0] = global_id - 1;		
	}
	
	 if (global_id == bsup) {
		neighbour_tab[1] = MPI_PROC_NULL;
	} else {
		neighbour_tab[1] = global_id + 1;		
	} 

	if (global_id == 0) {	
		*global_x = 0;
	} else {
		*global_x = (global_id * nx);
	}*/
	
	
	if (global_id == 0) {
		neighbour_tab[0] = MPI_PROC_NULL;
		*global_x = 0;		
	} else {
		neighbour_tab[0] = global_id - 1;
		*global_x = (global_id * nx);			
	}
			
	if (global_id == (total_nb_process - 1)) {
		neighbour_tab[1] = MPI_PROC_NULL;
	} else {
		neighbour_tab[1] = global_id + 1;
	}

	printf("MKPART my_rank %d, cluster_id %d, total_nb_process %d, vois[0] %d, vois[1] %d, global_id %d\n",
	my_rank, cluster_id, total_nb_process, neighbour_tab[0], neighbour_tab[1], global_id);
	*global_y = 0;
	*global_z = 0;
	
//	printf(" rank = %d, x = %d, y = %d, z = %d\n", my_rank, *global_x, *global_y, *global_z);
}
