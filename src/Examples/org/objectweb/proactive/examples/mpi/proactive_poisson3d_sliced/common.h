#ifndef POISSON_3D_COMMON_H_
#define POISSON_3D_COMMON_H_

#define ARRAY(tab,i,j,k, ny, nz) tab[((i)*((nz+3)*(ny+3)))+((j)*(nz+3))+k]

#define TIMED 1

int is_local(int target_id, int cluster_id, int local_nb_process);

void get_target_mpi_rank_and_cluster(int index, int nx, int * res_mpi_rank, int * res_cluster_id);


#endif /*POISSON_3D_COMMON_H_*/

