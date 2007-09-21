#include<mpi.h>
#include <stdio.h>
#include "common.h"

void proactive_allreduce(int myid, int it, double var, double * reduce_tab, int cluster_id, int nb_cluster) {
			int cpt = 0;				int idx = 1;
			int error;
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


void send_to_proactive(int my_cluster_rank, int my_worker_rank, int it,
		double * sndbuf, int lgsbuf, int target_cluster_rank,
		int target_mpi_rank, int tag) {
	int error;

	if (DEBUG_P3D) {
		printf("[DEBUG_P3D] [PA_SEND] c_%d w_%d --> c_%d w_%d\n",
				my_cluster_rank, my_worker_rank, target_mpi_rank,
				target_cluster_rank);
	}

	if (TIMED) {
		exch_timer_start = MPI_Wtime();
	}

	error = ProActiveMPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, target_mpi_rank, tag,
			target_cluster_rank);

	if (TIMED) {
		timer_proactive_send += (MPI_Wtime() - exch_timer_start);
	}

	if (error < 0) {
		printf("[PA_SEND] !!! Error ProActiveMPI  c_%d w_%d --> c_%d w_%d\n",
				my_cluster_rank, my_worker_rank, target_mpi_rank,
				target_cluster_rank);
	}
}

void recv_from_proactive(int my_cluster_rank, int my_worker_rank, int it,
		double * rcvbuf, int lgrbuf, int target_cluster_rank,
		int target_mpi_rank, int tag) {
	int error = 0;
	// Call is remote we use the proactive communication layer
	if (DEBUG_P3D) {
		printf("[DEBUG_P3D] [PA_RECV] c_%d w_%d <-- c_%d w_%d\n",
				my_cluster_rank, my_worker_rank, target_mpi_rank,
				target_cluster_rank);
	}

	if (TIMED) {
		exch_timer_start = MPI_Wtime();
	}

	error = ProActiveMPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, target_mpi_rank, (it
			+ 10000), target_cluster_rank);

	if (TIMED) {
		timer_proactive_recv += (MPI_Wtime() - exch_timer_start);
	}

	if (error < 0) {
		printf("[PA_RECV] !!! Error ProActiveMPI  c_%d w_%d <-- c_%d w_%d\n",
				my_cluster_rank, my_worker_rank, target_mpi_rank,
				target_cluster_rank);
	}
}

void exchn1(MPI_Comm * icomN1, int it, int cluster_rank, int worker_rank,
		int * cluster_neigh, int * worker_neigh, int nxN1, int nyN1, int nzN1,
		double * vecX, double * sndbuf, double * rcvbuf) {

	int ind, ix, iy, iz, lgrbuf, lgsbuf;
	MPI_Status status;

	//---- Processeur courant d'indices (i,j,k) dans la grille cartesienne 3D
	//---- Envoi au voisin (i-1,j,k)
	//---- Reception du voisin (i+1,j,k)

	if (worker_neigh[0] != -1) {
		for (iz = 1; iz <= nzN1; iz++) {
			for (iy = 1; iy <= nyN1; iy++) {
				ind = ((iz - 1)*nyN1 + iy) -1;
				sndbuf[ind] = ARRAY(vecX, 1, iy, iz, nyN1, nzN1);
			}
		}
		lgsbuf = nyN1*nzN1;

		if (cluster_neigh[0] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[0], 100, *icomN1);
			if (TIMED) {
				timer_mpi_send += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[0], worker_neigh[0], it + 10000);
		}

	}

	if (worker_neigh[1] != -1) {
		lgrbuf = nyN1*nzN1;

		if (cluster_neigh[1] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[1], 100, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[1], worker_neigh[1], it + 10000);
		}

		// Retrieving data from communication buffer.
		for (iz = 1; iz <= nzN1; iz++) {
			for (iy = 1; iy <= nyN1; iy++) {
				ind = ((iz - 1)*nyN1 + iy) -1;
				ARRAY(vecX, nxN1+1, iy, iz, nyN1, nzN1) = rcvbuf[ind];
			}
		}
	}

	//---- Envoi au voisin (i+1,j,k)
	//---- Reception du voisin (i-1,j,k)

	if (worker_neigh[1] != -1) {
		// Preparing data to send

		for (iz = 1; iz <= nzN1; iz++) {
			for (iy = 1; iy <= nyN1; iy++) {
				ind = ((iz - 1)*nyN1 + iy) - 1;
				sndbuf[ind] = ARRAY(vecX, nxN1, iy, iz, nyN1, nzN1);
			}
		}
		lgsbuf = nyN1*nzN1;

		//---- Envoi au voisin (i+1,j,k)
		if (cluster_neigh[1] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[1], 200, *icomN1);
			if (TIMED) {
				timer_mpi_send += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[1], worker_neigh[1], it + 10000);
		}
	}

	if (worker_neigh[0] != -1) {
		lgrbuf = nyN1*nzN1;

		//---- Reception du voisin (i-1,j,k)         	
		if (cluster_neigh[0] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[0], 200, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[0], worker_neigh[0], it + 10000);
		}

		for (iz = 1; iz <= nzN1; iz++) {
			for (iy = 1; iy <= nyN1; iy++) {
				ind = ((iz - 1)*nyN1 + iy) -1;
				ARRAY(vecX, 0, iy, iz, nyN1, nzN1) = rcvbuf[ind];
			}
		}
	}

	//---- Envoi au voisin (i,j-1,k)
	//---- Reception du voisin (i,j+1,k)
	if (worker_neigh[2] != -1) {
		for (iz = 1; iz <= nzN1; iz++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iz - 1)*nxN1 + ix) - 1;
				sndbuf[ind] = ARRAY(vecX, ix, 1, iz, nyN1, nzN1);
			}
		}
		lgsbuf = nxN1*nzN1;

		if (cluster_neigh[2] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[2], 300, *icomN1);
			if (TIMED) {
				timer_mpi_send += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[2], worker_neigh[2], it + 10000);
		}
	}

	if (worker_neigh[3] != -1) {
		lgrbuf = nxN1*nzN1;

		if (cluster_neigh[3] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[3], 300, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[3], worker_neigh[3], it + 10000);
		}

		// Retrieving data from communication buffer.
		for (iz = 1; iz <= nzN1; iz++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iz - 1)*nxN1 + ix) - 1;
				ARRAY(vecX, ix, nyN1+1, iz, nyN1, nzN1) = rcvbuf[ind];
			}
		}
	}

	//---- Envoi au voisin (i,j+1,k)
	//---- Reception du voisin (i,j-1,k)
	if (worker_neigh[3] != -1) {
		// Preparing data to send
		for (iz = 1; iz <= nzN1; iz++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iz - 1)*nxN1 + ix) - 1;
				sndbuf[ind] = ARRAY(vecX, ix, nyN1, iz, nyN1, nzN1);
			}
		}
		lgsbuf = nxN1*nzN1;
		//---- Envoi au voisin (i+1,j,k)
		if (cluster_neigh[3] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[3], 400, *icomN1);
			if (TIMED) {
				timer_mpi_send += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[3], worker_neigh[3], it + 10000);
		}
	}

	if (worker_neigh[2] != -1) {
		lgrbuf = nxN1*nzN1;
		if (cluster_neigh[2] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[2], 400, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[2], worker_neigh[2], it + 10000);
		}

		// Retrieving data from communication buffer.
		for (iz = 1; iz <= nzN1; iz++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iz - 1)*nxN1 + ix) - 1;
				ARRAY(vecX, ix, 0, iz, nyN1, nzN1) = rcvbuf[ind];
			}
		}
	}

	//---- Envoi au voisin (i,j,k-1)
	//---- Reception du voisin (i,j,k+1)
	if (worker_neigh[4] != -1) {
		// Preparing data to send
		for (iy = 1; iy <= nyN1; iy++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iy - 1)*nxN1 + ix) - 1;
				sndbuf[ind] = ARRAY(vecX, ix, iy, 1, nyN1, nzN1);
			}
		}
		lgsbuf = nxN1*nyN1;
		//---- Envoi au voisin (i,j,k-1)
		if (cluster_neigh[4] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[4], 500, *icomN1);
			if (TIMED) {
				timer_mpi_send += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[4], worker_neigh[4], it + 10000);
		}
	}

	if (worker_neigh[5] != -1) {
		lgrbuf = nxN1*nyN1;
		if (cluster_neigh[5] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[5], 500, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[5], worker_neigh[5], it + 10000);
		}

		// Retrieving data from communication buffer.
		for (iy = 1; iy <= nyN1; iy++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iy - 1)*nxN1 + ix) - 1;
				ARRAY(vecX, ix, iy, nzN1+1, nyN1, nzN1) = rcvbuf[ind];
			}
		}

	}

	//---- Envoi au voisin (i,j,k+1)
	//---- Reception du voisin (i,j,k-1)

	if (worker_neigh[5] != -1) {
		// Preparing data to send
		for (iy = 1; iy <= nyN1; iy++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iy - 1)*nxN1 + ix) - 1;
				sndbuf[ind] = ARRAY(vecX, ix, iy, nzN1, nyN1, nzN1);
			}
		}
		lgsbuf = nxN1*nyN1;

		//---- Envoi au voisin (i+1,j,k)
		if (cluster_neigh[5] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Send(sndbuf, lgsbuf, MPI_DOUBLE, worker_neigh[5], 600, *icomN1);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			send_to_proactive(cluster_rank, worker_rank, it, sndbuf, lgsbuf,
					cluster_neigh[5], worker_neigh[5], it + 10000);
		}
	}

	if (worker_neigh[4] != -1) {
		lgrbuf = nxN1*nyN1;

		if (cluster_neigh[4] == cluster_rank) {
			// Call is local we use the mpi communication layer
			if (TIMED) {
				exch_timer_start = MPI_Wtime();
			}
			MPI_Recv(rcvbuf, lgrbuf, MPI_DOUBLE, worker_neigh[4], 600, *icomN1,
					&status);
			if (TIMED) {
				timer_mpi_recv += (MPI_Wtime() - exch_timer_start);
			}
		} else {
			// Call is remote we use the proactive communication layer
			recv_from_proactive(cluster_rank, worker_rank, it, rcvbuf,
					lgrbuf, cluster_neigh[4], worker_neigh[4], it + 10000);
		}

		// Retrieving data from communication buffer.

		for (iy = 1; iy <= nyN1; iy++) {
			for (ix = 1; ix <= nxN1; ix++) {
				ind = ((iy - 1)*nxN1 + ix) - 1;
				ARRAY(vecX, ix, iy, 0, nyN1, nzN1) = rcvbuf[ind];
			}
		}
	}
}

