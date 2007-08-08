#include<mpi.h>
#include <stdio.h>
#include "common.h"
#include "ProActiveMPI.h"

#define DEBUG_P3D 0

extern double timer_mpi_send, timer_mpi_recv, timer_proactive_send, timer_proactive_recv;
extern double timer_start;


void proactive_allreduce(int myid, int it, double var, double * reduce_tab, int cluster_id, int nb_cluster) {
			int cpt = 0;				int idx = 1;
			int error;
			double res;
			double test [2];
			double res_tab[2];
			reduce_tab[0] = 0;
			while (cpt < nb_cluster) {
				if (cpt == cluster_id) {
				// It's my turn to send data
					int target_cluster_rank = 0;
					
					while(target_cluster_rank < nb_cluster) {
						if(target_cluster_rank != cluster_id) {
							test[0] = var;
							test[1] = it; // We also send the iteration number for debug purposes
	      					error = ProActiveMPI_Send(&test, 2, MPI_DOUBLE, 0, (it + 90000), target_cluster_rank);
							if (error < 0){
								printf("[PA_REDUCE] !!! Error ProActiveMPI send from process %d, c_%d --> %d c_%d failed\n",myid, cluster_id, 0, target_cluster_rank);
							}		      										
						}
						target_cluster_rank++;
						
					}
				} else {
					error = ProActiveMPI_Recv(&res_tab, 2, MPI_DOUBLE, 0, (it + 90000), cpt);
					
					if (error < 0){
						printf("[PA_REDUCE] !!! Error ProActiveMPI receive from process %d, c_%d <-- %d c_%d failed\n",myid, cluster_id, 0, cpt);
					}
					
					if (((int)res_tab[1]) != it){
						printf("[PA_REDUCE] !!! Wrong iteration, %d, c_%d (cur_it) = %d : RECV  %d c_%d: (it_recv  %d) failed, error code = %d\n",myid, cluster_id, it, 0, cpt, (int)res_tab[1], error);
					}

					reduce_tab[idx] = res_tab[0];
					idx++;
				}
				cpt++;
			}
}


void exchn1(int it, int myid, int cluster_id, int local_nb_process, MPI_Comm * icomN1, int * ivoisN1, 
			int nxN1, int nyN1, int nzN1, 
			double * vecX,
			double * sndbuf, double * rcvbuf, int jobId) {
		
      int ind, iy, iz, lgrbuf, lgsbuf, error;
		MPI_Status status;


//---- Processeur courant d'indices (i,j,k) dans la grille cartesienne 3D
//---- Envoi au voisin (i-1,j,k)
//---- Reception du voisin (i+1,j,k)

      if (ivoisN1[0] != MPI_PROC_NULL) {
      	// Preparing data to send.
		for(iz = 1; iz <= nzN1; iz++) {
			for(iy = 1; iy <= nyN1; iy++) {
               ind = ((iz - 1)*nyN1 + iy) -1;
               sndbuf[ind] = ARRAY(vecX,1,iy,iz, nyN1, nzN1);
			}
		}
         lgsbuf = nyN1*nzN1;   
	               	
      	if (is_local(ivoisN1[0], cluster_id, local_nb_process) > 0) {
      		// Call is local we use the mpi communication layer
      		int mpi_rank = MPI_PROC_NULL;
      		int local_cluster = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[0], local_nb_process, &mpi_rank, &local_cluster);
      		if (DEBUG_P3D){
     			printf("[MPI] Sending from: myid %d mycluster_id %d ivoisN1[0] %d nxN1 %d, lgsbuf %d target_mpi_rank %d tag %d\n",
  		      			myid, cluster_id, ivoisN1[0], nxN1, lgsbuf, mpi_rank,  (it + 10000));
      		}
 
       		if (TIMED) {
      			timer_start = MPI_Wtime();	
      		}
      		
		      MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, mpi_rank, /*100*/ (it + 10000),
                      *icomN1);
                      
             if (TIMED) {
      			timer_mpi_send += (MPI_Wtime() - timer_start);      			
      		}
                      
      	} else {
  			// Call is remote we use the proactive communication layer
      		int target_mpi_rank = 0;
      		int target_cluster_rank = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[0], local_nb_process, &target_mpi_rank, &target_cluster_rank);
	      		if (DEBUG_P3D){
      			printf("[DEBUG_P3D] send from myid %d mycluster_id %d --> ivoisN1[0] %d, target_mpi_rank %d, idjob %d \n", 
      			myid, cluster_id, ivoisN1[0], target_mpi_rank, target_cluster_rank);
      		      		}
      		
      		if (TIMED) {
      			timer_start = MPI_Wtime();	
      		}
      		
			error = ProActiveMPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, target_mpi_rank, (it + 10000), target_cluster_rank);

      		if (TIMED) {
      			timer_proactive_send += (MPI_Wtime() - timer_start);      			
      		}
      					
			if (error < 0){
				printf("[PA_SEND] !!! Error ProActiveMPI send from process %d to %d failed\n", myid, ivoisN1[0]);
			}
  		}
      }
      
      

      if (ivoisN1[1] != MPI_PROC_NULL) {
         lgrbuf = nyN1*nzN1;

      	if (is_local(ivoisN1[1], cluster_id, local_nb_process) > 0) {
      		// Call is local we use the mpi communication layer
      		int mpi_rank = MPI_PROC_NULL;
      		int local_cluster = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[1], local_nb_process, &mpi_rank, &local_cluster);
      		      		if (DEBUG_P3D){
     			printf("[MPI] Receiving from: myid %d mycluster_id %d ivoisN1[1] %d nxN1 %d, lgsbuf %d target_mpi_rank %d tag %d\n",
  		      			myid, cluster_id, ivoisN1[1], nxN1, lgrbuf, mpi_rank, (it + 10000));
      		      		}      	
			if (TIMED) {
      			timer_start = MPI_Wtime();	
      		}     			
		      MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, mpi_rank, /*100*/ (it + 10000),
                      *icomN1,&status);
    		      	
      		if (TIMED) {
      			timer_mpi_recv += (MPI_Wtime() - timer_start);      			
      		}	                         

      	} else {
  			// Call is remote we use the proactive communication layer
      		int target_mpi_rank = 0;
      		int target_cluster_rank = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[1], local_nb_process, &target_mpi_rank, &target_cluster_rank);
      		if (DEBUG_P3D){
      			printf("[DEBUG_P3D] Receiving from: myid %d mycluster_id %d <-- ivoisN1[1] %d target_mpi_rank %d idjob %d\n",
  		      			myid, cluster_id, ivoisN1[1], target_mpi_rank, target_cluster_rank);
      		}
      		       		if (TIMED) {
      			timer_start = MPI_Wtime();	
      		}
      		error = ProActiveMPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, target_mpi_rank, (it + 10000), target_cluster_rank);
      		
      		      		if (TIMED) {
      			timer_proactive_recv += (MPI_Wtime() - timer_start);      			
      		}	   
      		
			if (error < 0){
					printf("[PA_RECV] !!! Error ProActiveMPI receive from process %d, c_%d <-- %d c_%d failed\n",myid, cluster_id, ivoisN1[1], target_cluster_rank);
			}
      	}         
         
      	// Retrieving data from communication buffer.
		for(iz = 1; iz <= nzN1; iz++) {
			for(iy = 1; iy <= nyN1; iy++) {
               ind = ((iz - 1)*nyN1 + iy) -1;
               ARRAY(vecX,nxN1+1,iy,iz,nyN1,nzN1) = rcvbuf[ind];
			}
		}
  	 }

  

      
//---- Envoi au voisin (i+1,j,k)
//---- Reception du voisin (i-1,j,k)

      if (ivoisN1[1] != MPI_PROC_NULL) {
      	// Preparing data to send
      	
		for(iz = 1; iz <= nzN1; iz++) {
			for(iy = 1; iy <= nyN1; iy++) {
               ind = ((iz - 1)*nyN1 + iy) - 1;
               sndbuf[ind] = ARRAY(vecX,nxN1,iy,iz,nyN1,nzN1);
			}
		}
         lgsbuf = nyN1*nzN1;

		//---- Envoi au voisin (i+1,j,k)
   		if (is_local(ivoisN1[1], cluster_id, local_nb_process) > 0) {
      		// Call is local we use the mpi communication layer
      		int mpi_rank = MPI_PROC_NULL;
      		int local_cluster = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[1], local_nb_process, &mpi_rank, &local_cluster);
      		      		if (DEBUG_P3D){
	     			printf("[MPI] Sending from: myid %d mycluster_id %d --> ivoisN1[1] %d target_mpi_rank %d tag %d\n",
	myid, cluster_id, ivoisN1[1], mpi_rank,(it + 20000));
      		      		}
   			if (TIMED) {
      			timer_start = MPI_Wtime();      			
      		}
   			      MPI_Send(sndbuf, lgsbuf, 
                       MPI_DOUBLE, mpi_rank, (it + 20000)/*200*/,
                       *icomN1);                       
            if (TIMED) {
      			timer_mpi_send += (MPI_Wtime() - timer_start);      			
      		}
   		} else {
			// Call is remote we use the proactive communication layer		
      		int target_mpi_rank = 0;
      		int target_cluster_rank = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[1], local_nb_process, &target_mpi_rank, &target_cluster_rank);
      		      		if (DEBUG_P3D){
      		         			printf("[DEBUG_P3D] send from myid %d mycluster_id %d --> ivoisN1[0] %d, target_mpi_rank %d, idjob %d \n", 
      			myid, cluster_id, ivoisN1[1], target_mpi_rank, target_cluster_rank);
      		      		}
			
			   			if (TIMED) {
      			timer_start = MPI_Wtime();      			
      		}
			error = ProActiveMPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, target_mpi_rank, (it + 20000), target_cluster_rank);
			
			
      		if (TIMED) {
      			timer_proactive_send += (MPI_Wtime() - timer_start);      			
      		}
      		
			if (error < 0){
				printf("[PA_SEND] !!! Error ProActiveMPI send from process %d to %d \n", myid, ivoisN1[1]);
      		}
   		}
      }


		if (ivoisN1[0] != MPI_PROC_NULL) {
         	lgrbuf = nyN1*nzN1;
         	
			//---- Reception du voisin (i-1,j,k)         	
	   		if (is_local(ivoisN1[0], cluster_id, local_nb_process) > 0) {
	      		// Call is local we use the mpi communication layer
      		int mpi_rank = MPI_PROC_NULL;
      		int local_cluster = 0;
      		get_target_mpi_rank_and_cluster(ivoisN1[0], local_nb_process, &mpi_rank, &local_cluster);
      		      		if (DEBUG_P3D){
 			printf("[MPI] Receiving from: myid %d mycluster_id %d ivoisN1[0] %d nxN1 %d, lgsbuf %d target_mpi_rank %d tag %d\n",
  		      			myid, cluster_id, ivoisN1[0], nxN1, lgrbuf, mpi_rank, (it + 20000));
      		      		}
	      	if (TIMED) {
      			timer_start = MPI_Wtime();      			
      		}
      		      		
	   			      MPI_Recv(rcvbuf, lgrbuf, 
	                       MPI_DOUBLE, mpi_rank, (it + 20000) /*200*/,
	                       *icomN1, &status);
      		if (TIMED) {
      			timer_mpi_recv += (MPI_Wtime() - timer_start);      			
      		}	                       
	   		} else {
				// Call is remote we use the proactive communication layer
	      		int target_mpi_rank = 0;
	      		int target_cluster_rank = 0;
	      		get_target_mpi_rank_and_cluster(ivoisN1[0], local_nb_process, &target_mpi_rank, &target_cluster_rank);
	      		      		if (DEBUG_P3D){
      			printf("[DEBUG_P3D] Receiving from: myid %d mycluster_id %d <-- ivoisN1[0] %d target_mpi_rank %d idjob %d\n",
  		      			myid, cluster_id, ivoisN1[0], target_mpi_rank, target_cluster_rank);
	      		      		}
	      	if (TIMED) {
      			timer_start = MPI_Wtime();      			
      		}
	      		error = ProActiveMPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, target_mpi_rank, (it + 20000), target_cluster_rank);
      		if (TIMED) {
      			timer_proactive_recv += (MPI_Wtime() - timer_start);      			
      		}	   
				if (error < 0){
					printf("[PA_RECV] !!! Error ProActiveMPI receive from process %d, c_%d <-- %d c_%d failed\n",myid, cluster_id, ivoisN1[0], target_cluster_rank);
				}
	   		}
	   		
			for(iz = 1; iz <= nzN1; iz++) {
				for(iy = 1; iy <= nyN1; iy++) {
	               ind = ((iz - 1)*nyN1 + iy) -1;
	               ARRAY(vecX,0,iy,iz,nyN1,nzN1) = rcvbuf[ind];
				}
			}
		}
}
