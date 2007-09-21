#ifndef COMMUNICATE_H_
#define COMMUNICATE_H_
#include <mpi.h>

void proactive_allreduce(int myid, int it, double var, double * reduce_tab, int cluster_id, int nb_cluster);

void exchn1(MPI_Comm * icomN1, int it, int cluster_rank, int worker_rank,
		int * cluster_neigh, int * worker_neigh, int nxN1, int nyN1, int nzN1,
		double * vecX, double * sndbuf, double * rcvbuf);
	
void mkpart(int cluster_rank, int workerRank,
		int dims_c_x, int dims_c_y, int dims_c_z,
		int dims_w_x, int dims_w_y, int dims_w_z, int * cluster_neigh, int * worker_neigh);

void compute_global_coord_3d(
		int global_rank,
		int nwx, int nwy, int nwz,
		int dims_gw_x, int dims_gw_y, int dims_gw_z,
		int * global_x, int * global_y, int * global_z);

#endif /*COMMUNICATE_H_*/
