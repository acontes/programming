//---------------------------------------------------------------------
//     Probleme de Poisson 3D discretise par differences finies        
//     Resolution iterative par la methode de Jacobi
//---------------------------------------------------------------------


// GLOBAL PARAMETERS
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>

#include "common.h"
#include "communicate.h"
#include "compute.h"

/*************************** TIMERS *****************************/
double timer_start = 0.0;
double timer_exch = 0.0, timer_allreduce = 0.0;
double timer_mpi_recv = 0.0, timer_mpi_send = 0.0;
double timer_proactive_recv = 0.0, timer_proactive_send = 0.0;
double exch_timer_start = 0.0;

int main(int argc, char ** argv) {

	int worker_rank = 0, cluster_rank = 0;
	//      int corpc1 [3], corpc2 [3], dims1 [3], dims2 [3], 
//	int my_neighbours [2];

	//---- Nombres de points suivant Ox, Oy et Oz caracterisant le 
	//     maillage global (ie non-partitionne)
	//
	// Matrix size related variable
	int ntx, nty, ntz;

	// Clusters and workers geometry
	int dims_c_x = -2, dims_c_y = -2, dims_c_z = -2;
	int dims_w_x = -2, dims_w_y = -2, dims_w_z = -2;

	// Worker position in 3D coords
	int global_x = -1, global_y = -1, global_z = -1;

	/**************************** P3D variable ****************************/

	//---- hx, hy et hz sont les pas de discretisation suivant Ox, Oy et Oz 
	//     du maillage global (on suppose la discretisation uniforme)
	//
	double hx, hy, hz;

	//---- rhx2 = 1/hx**2, rhy2 = 1/hy**2 et rhz2 = 1/hz**2

	double rhx2, rhy2, rhz2;

	double dcoef, epsf, err, errglb, resglb, resnrm, res0 = 0.0, t0, t1;

	int it, maxit;

	double * frhs;
	double * solU;
	double * oldU;
	double * newU;
	double * rcvbuf;
	double * sndbuf;
	double * oldCpy;
	double * reduce_tab;
	//---------------------------------------------------------------------c
	//---- Variables locales 

	/**************************** MPI_INIT ****************************/
//	MPI_Comm icomG1;

	MPI_Init(&argc, &argv);
	//---- Communicateur par defaut      
	MPI_Comm icomm0 = MPI_COMM_WORLD;
	MPI_Comm_rank(icomm0, &worker_rank);
	//      MPI_Comm_size(icomm0, &local_nb_process);

	// Total matrix size 
	ntx = atoi(argv[1]);
	nty = atoi(argv[2]);
	ntz = atoi(argv[3]);

	// Number of clusters for each dimensions
	dims_c_x = atoi(argv[4]);
	dims_c_y = atoi(argv[5]);
	dims_c_z = atoi(argv[6]);

	// Number of workers for each dimensions inside each clusters
	dims_w_x = atoi(argv[7]);
	dims_w_y = atoi(argv[8]);
	dims_w_z = atoi(argv[9]);
	maxit = atoi(argv[10]);
//	cluster_rank = atoi(argv[11]);
	// TODO not necessary in the former code
	epsf = 1.0e-06;
	

	int error = ProActiveMPI_Init(worker_rank);
      
	if (error < 0){
		printf("[MPI] !!! Error ProActiveMPI init \n");
		MPI_Abort( MPI_COMM_WORLD, 1 );
	}
	
	ProActiveMPI_Job(&cluster_rank);

	int printer_rank = ((worker_rank == 0) && (cluster_rank == 0));

	// Clusters matrix size
	//      int ncx;
	//      int ncy;
	//      int ncz;

	/**************************** Parameters checking ****************************/
	if ((ntx % dims_c_x == 0) &&(nty % dims_c_y == 0)&&(ntz % dims_c_z == 0)) {
		printf("Cluster partitioning => OK\n");
	} else {
		printf("Cluster partitioning => KO nex %d, ney %d, nez %d \n", ntx
				% dims_c_x, nty % dims_c_y, ntz % dims_c_z);
		exit(-4);
	}

	if ((((ntx / dims_c_x) % dims_w_x) == 0) &&(((nty / dims_c_y) % dims_w_y)
			== 0)&&(((ntz / dims_c_z) % dims_w_z) == 0)) {
		printf("Worker partitioning => OK\n");
	} else {
		printf("Worker partitioning => KO mod => nex %d, ney %d, nez %d \n", ((ntx
				/ dims_c_x) % dims_w_x), ((nty / dims_c_y) % dims_w_y), ((ntz
				/ dims_c_z) % dims_w_z));
		exit(-4);
	}

	/**************************** Variable Initialisation ****************************/

	int total_nb_cluster = dims_c_x * dims_c_y * dims_c_z;
	int nb_worker_per_cluster = dims_w_x * dims_w_y * dims_w_z;
	int total_nb_worker = total_nb_cluster * nb_worker_per_cluster;

	
	// Elements matrix size
	int nex = ntx / (dims_c_x * dims_w_x);
	int ney = nty / (dims_c_y * dims_w_y);
	int nez = ntz / (dims_c_z * dims_w_z);
	
	// Cluster and Worker neighbouring, init to -2 to detect bugs
	//TODO replace by variable to enhance perf
	int cluster_neigh [] = { -2, -2, -2, -2, -2, -2 };
	int worker_neigh [] = { -2, -2, -2, -2, -2, -2 };

	if (cluster_rank < 0) {
		printf("ERROR cluster id must be > 0 \n");
		exit(-2);
	}

	// ASSERTION all ranks > 0
	// TODO check worker_rank is ok (previously setted at 1 instead of 0)
	if (worker_rank < 0) {
		printf("ERROR mpi process id must be > 0 \n");
		exit(-3);
	}
	

	//---- Quelques verifications
	//---- Coherence partitionnements
	/**************************** Print Informations ****************************/

	if (printer_rank) {
		printf("*****************************************************************\n");
		printf("Computing poisson3d %d %d %d %d\n", ntx, nty, ntz, maxit);
		printf("With %d workers dispatched on %d clusters\n", total_nb_worker,
				total_nb_cluster);
		printf("Each cluster having %d workers\n", nb_worker_per_cluster);
		printf("Clusters Geometry is %d %d %d\n", dims_c_x, dims_c_y, dims_c_z);
		printf("Workers Geometry inside cluster is %d %d %d\n", dims_w_x,
				dims_w_y, dims_w_z);
		printf("Matrix size on each worker %d %d %d\n", nex, ney, nez);
		printf("*****************************************************************\n");		
	}

	/****** Memory Instanciation ***************/
	frhs = (double *) calloc((nex+3)*(ney+3)*(nez+3), sizeof(double));
	if (frhs == NULL) {
		printf("Malloc error while creating frhs\n");
		exit(-1);
	}
	solU = (double *) calloc((nex+3)*(ney+3)*(nez+3), sizeof(double));
	if (solU == NULL) {
		printf("Malloc error while creating solU\n");
		exit(-1);
	}

	// Only used over [0,NYMAX+30]
	oldU = (double *) calloc((nex+3)*(ney+3)*(nez+3), sizeof(double));
	if (oldU == NULL) {
		printf("Malloc error while creating oldU\n");
		exit(-1);
	}
	newU = (double *) calloc((nex+3)*(ney+3)*(nez+3), sizeof(double));
	if (newU == NULL) {
		printf("Malloc error while creating newU\n");
		exit(-1);
	}

	int max1 = 0, max2 = 0;

	if (nex >= ney) {
		max1 = nex;
	} else {
		max1 = ney;
	}

	if (nez >= max1) {
		max2 = nez;
	} else {
		max1 = nex;
		max2 = ney;
	}

	rcvbuf = (double *) calloc((max1+3)*(max2+3), sizeof(double));
	sndbuf = (double *) calloc((max1+3)*(max2+3), sizeof(double));

	reduce_tab = calloc(total_nb_cluster, sizeof(double));
	/**************************** P3D starts here ****************************/

	//---- Definition des caracteristiques du maillage global 
	//---- Le domaine de calcul est le cube [0,1]x[0,1]x[0,1]

	//---- Pas de discretisation suivant Ox, Oy et Oz

	hx = 1.0/ (double) (ntx + 1);
	hy = 1.0/ (double) (nty + 1);
	hz = 1.0/ (double) (ntz + 1);

	rhx2 = 1.0/(hx*hx);
	rhy2 = 1.0/(hy*hy);
	rhz2 = 1.0/(hz*hz);

	dcoef = 2.0*(rhx2 + rhy2 + rhz2);

	if (printer_rank) {
		printf(" hx = %15.6e\n", hx);
		printf(" hy = %15.6e\n", hy);
		printf(" hz = %15.6e\n", hz);

		printf(" rhx2 = %15.6e\n", rhx2);
		printf(" rhy2 = %15.6e\n", rhy2);
		printf(" rhz2 = %15.6e\n", rhz2);

		printf(" dcoef = %15.6e\n", dcoef);
	}

	//---- Caracteristiques du partitionnement de niveau 1
	/**************************** Partitioning ****************************/
	/*void mkpart(int cluster_rank, int worker_rank, int dims_c_x, int dims_c_y,
			int dims_c_z, int dims_w_x, int dims_w_y, int dims_w_z,
			int * cluster_neigh, int * worker_neigh)*/
	
	mkpart(cluster_rank, worker_rank, dims_c_x, dims_c_y, dims_c_z, dims_w_x,
			dims_w_y, dims_w_z, cluster_neigh, worker_neigh);

	int global_rank = worker_rank + (cluster_rank) * total_nb_cluster;

	compute_global_coord_3d(global_rank, nex, ney, nez, dims_c_x * dims_w_x,
			dims_c_y * dims_w_y, dims_c_z * dims_w_z, &global_x, &global_y,
			&global_z);

//	void setrhs(int myid, double * vecF, double * solF, 
//					int nex, int ney, int nez, int global_x, int global_y, int global_z, 
//					double hx, double hy, double hz)
	
	
	//---- Initialisation du second membre et de la solution exacte
	setrhs(worker_rank, frhs, solU, nex, ney, nez, global_x, global_y, global_z,
			hx, hy, hz);
	int i, j, k;

	//---- Historique de convergence 
	it = 0;
	resnrm = 1.0;

	//---- Boucle principale: methode de ralaxation de Jacobi 
	MPI_Barrier(icomm0);

	//---- Calcul du temps CPU et du temps r�el de la boucle
	t0 = MPI_Wtime();
	FILE * output = NULL;

	if (printer_rank) {
		output = fopen("residu", "w");
	}

	if (printer_rank) {
		printf("[Start] Main Iteration\n");
	}

	/**************************** Main Loop ****************************/

	while ((it < maxit) && (resnrm > epsf)) {
		it += 1;

		//----    Echange des valeurs aux interfaces du partitionnement 
		//        de niveau 1

		if (TIMED) {
			timer_start = MPI_Wtime();
		}
		/*
			void exchn1(MPI_Comm * icomN1, int it, int cluster_rank, int worker_rank,
					int * cluster_neigh, int * worker_neigh, int nxN1, int nyN1, int nzN1,
					double * vecX, double * sndbuf, double * rcvbuf);
		*/
		
		exchn1(&icomm0, it, cluster_rank, worker_rank, cluster_neigh, worker_neigh, nex, ney, nez,
				oldU, sndbuf, rcvbuf);

		if (TIMED) {
			timer_exch += MPI_Wtime() - timer_start;
		}

		//----    newU = A*oldU

		matvec(oldU, newU, nex, ney, nez, rhx2, rhy2, rhz2);

		//----    Calcul du residu
		resnrm = 0.0;
		for (i = 1; i <= nex; i++) {
			for (j = 1; j <= ney; j++) {
				for (k = 1; k <= nez; k++) {
					resnrm += pow((ARRAY(frhs, i, j, k, ney, nez) - ARRAY(newU,
							i, j, k, ney, nez)), (double)2.0);
					//                resnrm = resnrm + pow((frhs[i,j,k] - newU[i,j,k]),(double) 2);
				}
			}
		}

		if (TIMED) {
			timer_start = MPI_Wtime();
		}
		// cluster local mpi all reduce
		MPI_Allreduce(&resnrm, &resglb, 1, MPI_DOUBLE, MPI_SUM, icomm0);
		
		// then synchronize with other proactive process
		if (worker_rank == 0) {
			// inter cluster all reduce between each cluster representant (worker 0)
			proactive_allreduce(worker_rank, it, resglb, reduce_tab, cluster_rank, total_nb_cluster);
		
         	for (i = 1; i < total_nb_cluster; i++) {
         		resglb += reduce_tab[i];
         	}
		}

		// broadcasting the inter cluster result to each worker
       	if (MPI_Bcast(&resglb, 1, MPI_DOUBLE, 0, icomm0) < 0) {
           	perror("[P3D] MPI_Bcast ERROR: ");
       	}
       	
       	if(TIMED) {
			timer_allreduce += (MPI_Wtime() - timer_start);
		}
       	
		resnrm = sqrt(resglb);

		//----    newU = D^{-1}*newU (sachant que A = L + U + D avec D matrice des termes diagonaux)
		for (i = 1; i <= nex; i++) {
			for (j = 1; j <= ney; j++) {
				for (k = 1; k <= nez; k++) {
					ARRAY(newU, i, j, k, ney, nez) = ARRAY(newU,i,j,k,ney,nez)/dcoef;
					//                  newU[i,j,k] = newU[i,j,k]/dcoef;
				}
			}
		}

		//----    newU = oldU - newU + D^{-1}*frhs
		for (i = 1; i <= nex; i++) {
			for (j = 1; j <= ney; j++) {
				for (k = 1; k <= nez; k++) {
					ARRAY(newU, i, j, k, ney, nez) = ARRAY(oldU,i,j,k,ney,nez) - ARRAY(newU,i,j,k,ney,nez) + ARRAY(frhs,i,j,k,ney,nez)/dcoef;
					//                  newU[i,j,k] = oldU[i,j,k] - newU[i,j,k] + frhs[i,j,k]/dcoef;
				}
			}
		}

		if (it == 1) {
			res0 = resnrm;
		}

		resnrm = resnrm/res0;

		if (printer_rank) {
			fprintf(output, "%d %15.6e\n", it, resnrm);
			if ((it % 100) == 1) {
				printf("it = %d\n", it);
			}
		}
		//----    Mise a jour de oldU pour iteration suivante

		oldCpy = oldU;
		oldU = newU;
		newU = oldCpy;

	} //---- Fin de la boucle principale

	if (printer_rank) {
		printf("[Stop] Main Iteration\n");
	}
	MPI_Barrier(icomm0);
	t1 = MPI_Wtime();

	if (worker_rank ==0) {
		fclose(output);
	}

	//---- Calcul de l'erreur 

	err = 0.0;

	double tmp;
	for (i = 1; i <= nex; i++) {
		for (j = 1; j <= ney; j++) {
			for (k = 1; k <= nez; k++) {
				tmp = ARRAY(oldU, i, j, k, ney, nez) - ARRAY(solU, i, j, k, ney,
						nez);
				err = (tmp > err) ? tmp : err;
			}
		}
	}

	MPI_Allreduce(&err, &errglb, 1, MPI_DOUBLE, MPI_MAX, icomm0);
	if (TIMED) {
		printf("TIMER (%d,%d) reduce: %10.2f exchn : %10.2f \n", cluster_rank, worker_rank,
				timer_allreduce, timer_exch);
		
		printf("TIMER (%d,%d) timer_proactive_send: %10.2f timer_proactive_recv : %10.2f \n", cluster_rank,
				worker_rank, timer_proactive_send, timer_proactive_recv);

		printf("TIMER (%d,%d) timer_mpi_send: %10.2f timer_mpi_recv : %10.2f \n", cluster_rank,
				worker_rank, timer_mpi_send, timer_mpi_recv);
	}

	if (printer_rank) {
		printf(" ================================================\n");
		printf(" Number of iterations (it) = %d\n", it);
		printf(" Residu normalise (resnrm) = %15.10e\n", resnrm);
		printf(" Erreur avec solution exacte (errglb) = %15.10e\n", errglb);
		printf(" ================================================\n");
		printf(" Computation duration (in sec) (t1-t0) = %10.2f\n", t1-t0);
		printf(" ================================================\n");
	}

	free(oldU);
	free(newU);
	free(solU);
	free(frhs);
	MPI_Finalize();

	return 0;
}

