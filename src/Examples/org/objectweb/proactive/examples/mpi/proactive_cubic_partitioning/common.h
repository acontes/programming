#ifndef POISSON_3D_COMMON_H_
#define POISSON_3D_COMMON_H_

#include "ProActiveMPI.h"

#define DEBUG_P3D 0
#define TIMED 1

#define ARRAY(tab,i,j,k, ny, nz) tab[((i)*((nz+3)*(ny+3)))+((j)*(nz+3))+k]

int is_local(int target_id, int cluster_id, int local_nb_process);

/**************************** Timers ****************************/

extern double timer_start;
extern double timer_exch, timer_allreduce;
extern double timer_mpi_recv, timer_mpi_send;
extern double timer_proactive_recv, timer_proactive_send;
extern double exch_timer_start;


#endif /*POISSON_3D_COMMON_H_*/
