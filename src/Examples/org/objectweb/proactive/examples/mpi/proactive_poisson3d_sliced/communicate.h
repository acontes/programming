#ifndef COMMUNICATE_H_
#define COMMUNICATE_H_
#include <mpi.h>
			
void proactive_allreduce(int myid, int it, double var, double * reduce_tab, int cluster_id, int nb_cluster);
	
void exchn1(int it, int myid, int cluster_id, int local_nb_process, MPI_Comm * icomN1, int * ivoisN1, 
			int nxN1, int nyN1, int nzN1, 
			double * vecX,
			double * sndbuf, double * rcvbuf, int jobId);

void mkpart(int my_rank, int cluster_id, int total_nb_process,  int local_nb_process, int * neighbour_tab,
			int * global_x, int * global_y, int * global_z, int nx); 
     		
#endif /*COMMUNICATE_H_*/
