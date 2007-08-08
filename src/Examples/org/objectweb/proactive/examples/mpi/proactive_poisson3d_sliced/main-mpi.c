
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

#include "ProActiveMPI.h"

double timer_mpi_send, timer_mpi_recv, timer_proactive_send, timer_proactive_recv;
double timer_start;


int is_local(int target_id, int cluster_id, int local_nb_process) {
	return (target_id >= (cluster_id * local_nb_process)) && (target_id < ((cluster_id + 1) * local_nb_process));
}

void get_target_mpi_rank_and_cluster(int index, int local_nb_node, int * res_mpi_rank, int * res_cluster_id) {

	//TODO induce each cluster have the same amount of nodes
	int mpi_rank = (index % local_nb_node);
	int cluster_id = -1;
	while (index >= 0) {
		index -= local_nb_node;
		cluster_id++;
	}
	// as proactive start numbering jobId from 0
	*res_cluster_id = cluster_id;
	*res_mpi_rank = mpi_rank;
}

int main (int argc, char ** argv) {

      int myid = 0, local_nb_process = 0, total_nb_process = 0, cluster_id = 0, total_nb_cluster = 0, jobId = 0, error;
//      int corpc1 [3], corpc2 [3], dims1 [3], dims2 [3], 
      int my_neighbours [2];

//---- Nombres de points suivant Ox, Oy et Oz caracterisant le 
//     maillage global (ie non-partitionne)
//
      int ntx, nty, ntz;
      int nx = 0, ny = 0, nz = 0;
      int global_x = 0, global_y = 0, global_z = 0;
//	
//---- hx, hy et hz sont les pas de discretisation suivant Ox, Oy et Oz 
//     du maillage global (on suppose la discretisation uniforme)
//
      double hx, hy, hz;

//---- rhx2 = 1/hx**2, rhy2 = 1/hy**2 et rhz2 = 1/hz**2

      double rhx2, rhy2, rhz2;
      
//---------------------------------------------------------------------c
//---- Variables locales  
	  int it, maxit;
      double dcoef, epsf, err, errglb = 0.0, resglb, resnrm, res0 = 0.0, t0, t1;
      double * frhs;
      double * solU;
      double * oldU;
      double * newU;
      
      // timer
      double timer_allreduce = 0.0, timer_exch = 0.0;
      
      double * rcvbuf;
	  double * sndbuf;
	  double * oldCpy;
	  double * reduce_tab;
	  
	  timer_mpi_send = 0.0; timer_mpi_recv = 0.0; timer_proactive_send = 0.0; timer_proactive_recv = 0.0;
		timer_start = 0.0;

	  if(myid == 0) {
	  	printf("Starting poisson3d \n");
	  }
	  
      MPI_Init(&argc, &argv);
  	  //---- Communicateur par defaut      
      MPI_Comm icomm0 = MPI_COMM_WORLD;
      MPI_Comm_rank(icomm0, &myid);
      MPI_Comm_size(icomm0, &local_nb_process);


//---- Lecture de certains parametres globaux 
      ntx = atoi(argv[1]); nty = atoi(argv[2]); ntz = atoi(argv[3]);
      maxit = atoi(argv[4]);
      epsf = 1.0e-06;
      
      total_nb_process = atoi(argv[5]);
      total_nb_cluster = atoi(argv[6]);


//---- Quelques verifications
//---- Coherence partitionnements

	if ((ntx % total_nb_process) == 0) {
	 	nx = (ntx / total_nb_process);
	 	ny = nty;
	 	nz = ntz;
	 	
	  frhs = (double *) calloc((nx+3)*(ny+3)*(nz+3), sizeof(double));
      if (frhs == NULL) { printf("Malloc error while creating frhs\n"); exit(-1); }
      solU = (double *) calloc((nx+3)*(ny+3)*(nz+3), sizeof(double));
        if (solU == NULL) { printf("Malloc error while creating solU\n"); exit(-1); }
      oldU = (double *) calloc((nx+3)*(ny+3)*(nz+3), sizeof(double));
      if (oldU == NULL) { printf("Malloc error while creating oldU\n"); exit(-1); }
      newU = (double *) calloc((nx+3)*(ny+3)*(nz+3), sizeof(double));
    	if (newU == NULL) { printf("Malloc error while creating newU\n"); exit(-1); }
    	
	  rcvbuf = (double *) calloc((ny+3)*(nz+3), sizeof(double));
	  sndbuf = (double *) calloc((ny+3)*(nz+3), sizeof(double));


	} else {
		printf("can't matrix of size %d by %d number of processes \n", ntx, total_nb_process);
		exit(-1);
	}
	
	// ProActive initialisation	
	error = ProActiveMPI_Init(myid);
      
	if (error < 0){
		printf("[MPI] !!! Error ProActiveMPI init \n");
		MPI_Abort( MPI_COMM_WORLD, 1 );
	}
	
	ProActiveMPI_Job(&jobId);

	cluster_id = jobId;
	 
	reduce_tab = calloc(total_nb_cluster, sizeof(double));
      
	// End ProActive initialisation	
	
      if (myid == 0)  {
      	printf(" ntx = %d\n", ntx);
      	printf(" nty = %d\n", nty);
      	printf(" ntz = %d\n\n", ntz);
      	printf(" nx = %d\n", nx);
      	printf(" ny = %d\n", ny);
      	printf(" nz = %d\n", nz);
      }  

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

      if (myid == 0) {
      	printf(" hx = %15.6e\n", hx);
      	printf(" hy = %15.6e\n", hy);
      	printf(" hz = %15.6e\n", hz);
      	
      	printf(" rhx2 = %15.6e\n", rhx2);
      	printf(" rhy2 = %15.6e\n", rhy2);
      	printf(" rhz2 = %15.6e\n", rhz2);
      	
      	printf(" dcoef = %15.6e\n", dcoef);
      }
      
//---- Caracteristiques du partitionnement de niveau 1
      mkpart(myid, cluster_id, total_nb_process, local_nb_process, my_neighbours, &global_x, &global_y, &global_z, nx);

//---- Initialisation du second membre et de la solution exacte
     setrhs(myid, frhs, solU, nx, ny, nz, global_x, global_y, global_z, hx, hy, hz);
	 int i,j,k;

//---- Historique de convergence 
      it = 0;
      resnrm = 1.0;

//---- Boucle principale: methode de ralaxation de Jacobi 
      	if (MPI_Barrier(icomm0) < 0) {
          	perror("[P3D] MPI_Barrier ERROR: ");	
     	}

//---- Calcul du temps CPU et du temps r�el de la boucle
      t0 = MPI_Wtime();
	  FILE * output = NULL;

      if (myid == 0) {
       output = fopen("residu","w");
      }

//      if (myid == 0) {
      	printf("[Start] Main Iteration\n");
//      }



      while ((it < maxit) && (resnrm > epsf)) {
         it += 1;
		
//----    Echange des valeurs aux interfaces du partitionnement 
//        de niveau 1
		
		if(TIMED) {
			timer_start = MPI_Wtime();
		}
		
		exchn1(it, myid, cluster_id, local_nb_process,
			  &icomm0, my_neighbours, nx, ny, nz, oldU, sndbuf, rcvbuf, jobId);
			  
		if(TIMED) {
			timer_exch += (MPI_Wtime() - timer_start);
		}
		
		
//----    newU = A*oldU

        matvec(oldU, newU, nx, ny, nz, rhx2, rhy2, rhz2);

//----    Calcul du residu
         resnrm = 0.0;
         for (i = 1; i <= nx; i++) {
			for (j = 1; j <= ny; j++) {
				for (k = 1; k <= nz; k++) {
                  resnrm += pow((ARRAY(frhs,i,j,k,ny,nz) - ARRAY(newU,i,j,k,ny,nz)),(double)2.0);
//                resnrm = resnrm + pow((frhs[i,j,k] - newU[i,j,k]),(double) 2);
				}
			}
		}
	
		if(TIMED) {
			timer_start = MPI_Wtime();
		}
		
        if (MPI_Allreduce(&resnrm, &resglb, 1, MPI_DOUBLE, MPI_SUM, icomm0) < 0)  {
         	                      	perror("[P3D] MPI_Allreduce ERROR: ");	
     	}
		
		// then synchronize with other proactive process
		if (myid == 0) {
			proactive_allreduce(myid, it, resglb, reduce_tab, cluster_id, total_nb_cluster);
		
         	for (i = 1; i < total_nb_cluster; i++) {
         		resglb += reduce_tab[i];
         	}

		}

       	if (MPI_Bcast(&resglb, 1, MPI_DOUBLE, 0, icomm0) < 0) {
           	perror("[P3D] MPI_Bcast ERROR: ");
       	}
       	
       	if(TIMED) {
			timer_allreduce += (MPI_Wtime() - timer_start);
		}

         resnrm = sqrt(resglb);

//----    newU = D^{-1}*newU (sachant que A = L + U + D avec D matrice des termes diagonaux)
         for (i = 1; i <= nx; i++) {
			for (j = 1; j <= ny; j++) {
				for (k = 1; k <= nz; k++) {
                  ARRAY(newU,i,j,k,ny,nz) = ARRAY(newU,i,j,k,ny,nz)/dcoef;					
//                  newU[i,j,k] = newU[i,j,k]/dcoef;
				}
			}
		}

//----    newU = oldU - newU + D^{-1}*frhs
         for (i = 1; i <= nx; i++) {
			for (j = 1; j <= ny; j++) {
				for (k = 1; k <= nz; k++) {
                  ARRAY(newU,i,j,k,ny,nz) = ARRAY(oldU,i,j,k,ny,nz) - ARRAY(newU,i,j,k,ny,nz) + ARRAY(frhs,i,j,k,ny,nz)/dcoef;					
//                  newU[i,j,k] = oldU[i,j,k] - newU[i,j,k] + frhs[i,j,k]/dcoef;
				}
			}
		}

         if (it == 1) {
         	res0 = resnrm;
         }
         
         resnrm = resnrm/res0;
         
         if (myid == 0) {
         	if(cluster_id == 0) {
				fprintf(output,"%d %15.6e\n", it, resnrm);
         	}
			if ((it % 100) == 1) {
				printf("rid : %d, cid %d, it = %d\n", myid, cluster_id, it);
			}
         }
//----    Mise a jour de oldU pour iteration suivante

	oldCpy = oldU;
	oldU = newU;
	newU = oldCpy;

} //---- Fin de la boucle principale

      if (myid == 0) {
      	printf("[Stop] Main Iteration\n");
      }

    	if (MPI_Barrier(icomm0) < 0) {
          	perror("[P3D] MPI_Barrier ERROR: ");	
     	}
      
      t1 = MPI_Wtime();
      
      if (myid == 0) { 
      fclose(output);
      }
      
//---- Calcul de l'erreur 

      err = 0.0;

     	
      double tmp;
       for (i = 1; i <= nx; i++) {
			for (j = 1; j <= ny; j++) {
				for (k = 1; k <= nz; k++) {
					tmp  = ARRAY(oldU,i,j,k,ny,nz) - ARRAY(solU,i,j,k,ny,nz);
               		err = (tmp > err) ? tmp : err;
				}
			}
		}

    	if (MPI_Allreduce(&err, &errglb, 1, MPI_DOUBLE, MPI_MAX, icomm0) < 0) {
          	perror("[P3D] MPI_Allreduce ERROR: ");	
     	}
     
     
		if (TIMED) {
			printf("TIMER (%d,%d) reduce: %10.2f exchn : %10.2f -> MPI_S %10.2f MPI_R %10.2f PA_S %10.2f PA_R %10.2f \n", 
				myid, cluster_id, timer_allreduce, timer_exch, timer_mpi_send, timer_mpi_recv, timer_proactive_send, timer_proactive_recv);				
		}
		
      // then synchronize with other proactive process
      if (myid == 0) {
		proactive_allreduce(myid, it, errglb, reduce_tab, cluster_id, total_nb_cluster);	
		
		for (i = 1; i < total_nb_cluster; i++) {
	       	if (reduce_tab[i] > errglb) {
	       		errglb = reduce_tab[i];
	       	}
	    }
  
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
     free(rcvbuf);
     free(sndbuf);
 	ProActiveMPI_Finalize();
	MPI_Finalize();
      
      return 0;
}

