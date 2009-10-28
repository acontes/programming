/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */

#include "ProActiveMPI.h"

#define RECV_QUEUE_SIZE 20

msg_t * static_recv_msg_buf;

msg_t * recv_queue [RECV_QUEUE_SIZE];
int recv_queue_order [RECV_QUEUE_SIZE];
int msg_recv_nb = 0;

int C2S_Q_ID, S2C_Q_ID;
int sem_set_id_mpi; // semaphore set ID.

/* Some informations about the mpi job */

int my_job_id=-1;
int nb_job=-1;

int TAG_S_KEY;
int TAG_R_KEY;

FILE * mslog;

void msg_stat(int msgid, struct msqid_ds * msg_info);

/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+-----+- MPI <-> MPI FUNCTIONS -+----+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/

int get_available_recv_queue_index() {
	int i = 0, free = -1;
	while ((i < RECV_QUEUE_SIZE) && (free == -1)) {
		if (recv_queue_order[i] == -1) {
			recv_queue_order[i] = msg_recv_nb;
			free = i;
		}
		i++;
	}
	return free;
}

msg_t * check_already_received_msg(int count, ProActive_Datatype pa_datatype,
		int src, int tag, int idjob) {
	int i = 0;
	msg_t * candidate = NULL;
	int candidate_index= RECV_QUEUE_SIZE;

	DEBUG_PRINT_NATIVE_SIDE(
			mslog,
			fprintf(
					mslog,
					"[ProActiveRecv] check already received message invoked, %d msg are in queue\n",
					msg_recv_nb));

	if (msg_recv_nb == RECV_QUEUE_SIZE) {
		DEBUG_PRINT_NATIVE_SIDE(
				mslog,
				fprintf(
						mslog,
						"[ProActiveRecv] ERROR msg queue is full\n"));		
	}
	
	while (i < RECV_QUEUE_SIZE) {
		if (recv_queue[i] != NULL) {
			DEBUG_PRINT_NATIVE_SIDE(
					mslog,
					fprintf(
							mslog,
							"[ProActiveRecv][check_already_received_msg] checking slot %d\n",
							i));
			DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
					"===> recv_queue[%d]->count %d == count %d\n", i,
					recv_queue[i]->count, count));
			DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
					"===> recv_queue[%d]->src %d == src %d\n", i,
					recv_queue[i]->src, src));
			DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
					"===> recv_queue[%d]->tag %d == tag %d\n", i,
					recv_queue[i]->tag, tag));
			DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
					"===> recv_queue[%d]->idjob %d == idjob %d\n", i,
					recv_queue[i]->idjob, idjob));
			DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
					"===> recv_queue[%d]->pa_datatype %d == pa_datatype %d\n", i,
					recv_queue[i]->pa_datatype, pa_datatype));

			/* MPI_recv does not necessarily match count but should be greater or equal */
//			if ((recv_queue[i]->count <= count) && ((recv_queue[i]->src == src)
//					|| (src == MPI_ANY_SOURCE)) && (recv_queue[i]->tag == tag)
//					&& ((recv_queue[i]->idjob == idjob) || (idjob
//							== MPI_ANY_SOURCE)) && (recv_queue[i]->pa_datatype
//					== pa_datatype)) {
			if(is_awaited_message(idjob, src, tag, pa_datatype, count, recv_queue[i])) {
				if (candidate != NULL) {
					// check which message was received first
					if ((recv_queue_order[i] != -1 ) && (recv_queue_order[i]
							< candidate_index)) {
						DEBUG_PRINT_NATIVE_SIDE(
								mslog,
								fprintf(
										mslog,
										"[ProActiveRecv][check_already_received_msg] recv_queue_order[i] %d < candidate_index %d\n",
										recv_queue_order[i], candidate_index));
						candidate = recv_queue[i];
						candidate_index = i;
					}
				} else {
					candidate = recv_queue[i];
					candidate_index = i;
					DEBUG_PRINT_NATIVE_SIDE(
							mslog,
							fprintf(
									mslog,
									"[ProActiveRecv][check_already_received_msg] setting candidate_index to %d\n",
									candidate_index));
				}
				// We probably got the right message, but we must ensure fifo order.
			}
		} else {
			DEBUG_PRINT_NATIVE_SIDE(
					mslog,
					fprintf(
							mslog,
							"[ProActiveRecv][check_already_received_msg] slot %d => is empty\n",
							i));
		}
		i++;
	}

	if (candidate != NULL) {
		// we found a message that match the requirements in the message queue.
		// we need to remove it
		int candidate_msg_id = recv_queue_order[candidate_index];
		recv_queue[candidate_index] = NULL;
		recv_queue_order[candidate_index] = -1;
		i = 0;
		while (i < RECV_QUEUE_SIZE) {
			if ((recv_queue_order[i] != -1) && (recv_queue_order[i]
					> candidate_msg_id)) {
				recv_queue_order[i]--;
			}
			i++;
		}
		msg_recv_nb--;

		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog,
				"[ProActiveRecv] QUEUE : got message at idx: %d lg: %d \n",
				candidate_index, msg_recv_nb));
	} else {
		DEBUG_PRINT_NATIVE_SIDE(
				mslog,
				fprintf(
						mslog,
						"[ProActiveRecv][check_already_received_msg] exiting with (candidate == NULL) => %d\n",
						candidate==NULL));
	}
	return candidate;
}

/*
 * ProActiveMPI_Init
 */
int ProActiveMPI_Init(int rank) {
	int error;
	msg_t send_msg_buf, recv_msg_buf;
	//	int pms;
	struct msqid_ds bufstat;

	init_msg_t(&send_msg_buf);
	init_msg_t(&recv_msg_buf);

	// keep rank of this process
	myRank=rank;

	// init message queue
	int i = 0;
	while (i < RECV_QUEUE_SIZE) {
		recv_queue_order[i] = -1;
		i++;
	}
	
	if (DEBUG_NATIVE_SIDE) {
		char * path = (char *) malloc(MAX_NOM);
		path[0]='\0';
		strcpy(path, DEBUG_LOG_OUTPUT_DIR);
		if ((mslog = (open_debug_log(path, myRank, "mpi_log"))) == NULL) {
			printf("ERROR WHILE OPENING FILE PATH= %s \n", path);
			perror("[ProActiveMPI_Init] ERROR !!! Can't initialize logging files");
			exit(1);
		}
		fprintf(mslog, "Initializing queues \n");
	}
	
	if ((static_recv_msg_buf = (msg_t *) malloc(sizeof(msg_t))) == NULL) {
		if (DEBUG_NATIVE_SIDE) {
			fprintf(mslog, "[ProActiveMPI_Init] !!! ERROR : MALLOC FAILED\n");
		}
		perror("[ProActiveMPI_Init] !!! ERROR : MALLOC FAILED");
		return -1;
	}

	// get the mpi semaphore
	sem_set_id_mpi = semget(SEM_ID_MPI, 1, IPC_CREAT | S_IRUSR | S_IWUSR
			| S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);
	if (sem_set_id_mpi == -1) {
		perror("[ProActiveMPI_Init] semget");
		exit(1);
	}

	struct semid_ds test;

	semctl(sem_set_id_mpi, 0, IPC_STAT, &test);

	DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Block Semaphore  \n"));

	// first process lock the semaphore
	sem_lock(sem_set_id_mpi);
	// accessing exclusively the ClientToServer queue
	if ((C2S_Q_ID = msgget(C2S_KEY, ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI_Init] msgget 1 ");
		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Cannot open sending queue: %d   \n",C2S_Q_ID))

		return -1;
	} else {
		// the queue successfully opened
		// update TAG_KEY
		TAG_S_KEY=C2S_KEY;

		// check the pid of the last process which have accessed to the queue
		// if (pid <> 0) open this process is the second one
		msg_stat(C2S_Q_ID, &bufstat);
		if (bufstat.msg_lspid != 0) {
			// access the second message queue
			if ((C2S_Q_ID = msgget(C2S02_KEY, ACCESS_PERM)) == -1) {
				perror("[ProActiveMPI_Init] msgget C2S02");
				DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Cannot open the second sending queue: %d   \n",C2S_Q_ID))
				return -1;
			} else {
				// update TAG_KEY
				TAG_S_KEY=C2S02_KEY;
				DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Second Sending Queue %d successfully opened \n ",C2S_Q_ID))
			}
		}
		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Sending Queue %d successfully opened \n ",C2S_Q_ID))
	}

	// accessing exclusively the ServerToClient queue
	if ((S2C_Q_ID = msgget(S2C_KEY, ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI_Init] mssget S2C_01  ");
		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Cannot open receiving queue: %d   \n",S2C_Q_ID))
		return -1;
	} else {
		TAG_R_KEY=S2C_KEY;
		// check the pid of the last process which have accessed to the queue
		// if (pid <> 0) open the second queue
		if (bufstat.msg_lspid != 0) {
			if ((S2C_Q_ID = msgget(S2C02_KEY, ACCESS_PERM)) == -1) {
				perror("[ProActiveMPI_Init] msgget S2C_02 ");
				DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Cannot open the second recving queue: %d   \n",S2C_Q_ID))
				return -1;
			} else {
				TAG_R_KEY=S2C02_KEY;
				DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Second Recving Queue %d successfully opened \n ",S2C_Q_ID))
			}
		}
		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Receivind Queue %d successfully opened \n ",S2C_Q_ID))
	}
	// unlock the semaphore
	sem_unlock(sem_set_id_mpi);
	DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "UnBlock Semaphore  \n"))

	// Build the init message
	send_msg_buf.msg_type = MSG_INIT;
	send_msg_buf.TAG = TAG_S_KEY;
	send_msg_buf.src = rank;
	/*
	 #	msg->TAG = 0; 
	 # 	msg->msg_type = 0;
	 ?? 	msg->idjob = 0;
	 msg->count = 0;
	 # 	msg->src = 0;
	 ?? 	msg->dest = 0;
	 msg->pa_datatype = 0;
	 msg->data = NULL; 	
	 msg->tag = 0;
	 */
	DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Sending to  %d \n ",C2S_Q_ID))
	// callback the ProActive runtime with the init message
	error = msgsnd(C2S_Q_ID, &send_msg_buf, get_payload_size(&send_msg_buf)/*pms+1*/, 0);
	DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "Sent %d bytes to queue %d successfully EINVAL %d error %d\n ", get_payload_size(&send_msg_buf), C2S_Q_ID, EINVAL, error))

	if (error < 0) {
		DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "[ProActiveMPI_Init] !!! ERROR: msgsnd error\n"))
		perror("ERROR");
		return -1;
	}

	if (DEBUG_NATIVE_SIDE) {
		fprintf(mslog, "Waiting for job number in recv queue \n ");
	}

	// Waiting ack from the ProActive runtime, and retrieve the jobId
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf),
			TAG_R_KEY, 0);

	// if an error occured during receive call check if its an interrupted
	// System call and so retry to receive
	while (error < 0) {
		if (DEBUG_NATIVE_SIDE) {
			fprintf(
					mslog,
					"[ProActiveMPI_Init] !!! ERROR: msgrcv error ERRNO = %d, \n",
					errno);
		}
		if (errno == EINTR) {
			if (DEBUG_NATIVE_SIDE) {
				fprintf(mslog, "[ProActiveMPI_Init] !!! ERRNO = EINTR, \n");
			}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf,
					get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
		} else {
			perror("ERROR");
			return -1;
		}
	}

	// update the job field of this mpi process
	my_job_id = recv_msg_buf.idjob;
	// nb_job is setted in buf.src has a convenience
	nb_job = recv_msg_buf.src;

	if (DEBUG_NATIVE_SIDE) {
		fprintf(mslog, "[ProActiveMPI_Init] [END] Process\n");
		fprintf(mslog, "[ProActiveMPI_Init] [END] myRank == %d \n", myRank);
		fprintf(mslog, "[ProActiveMPI_Init] [END] nbProActiveJob == %d\n",
				nb_job);
		fflush(mslog);
	}

	return 0;
}

/*
 * ProActiveMPI_Job
 */
int ProActiveMPI_Job(int * job_, int * nb_job_) {
	*job_ = my_job_id;
	*nb_job_ = nb_job;
	return 0;
}

int ProActiveMPI_NF(void * buf, int count, MPI_Datatype datatype, int tag) {
	return send_to_ipc(C2S_Q_ID, MSG_NF, TAG_S_KEY, buf, count, datatype,
			myRank, myRank, tag, my_job_id);
}
/*
 * ProActiveMPI_Send
 */
int ProActiveMPI_Send(void * buf, int count, MPI_Datatype datatype, int dest,
		int tag, int idjob) {
	return send_to_ipc(C2S_Q_ID, MSG_SEND, TAG_S_KEY, buf, count, datatype,
			myRank, dest, tag, idjob);
}


int internal_ProActiveMPI_Recv(void* buf, int count, MPI_Datatype datatype,
		int src, int tag, int idjob, int no_wait, int * ret_code) {

	msg_t * recv_msg_buf = NULL;
	ProActive_Datatype pa_datatype = type_conversion_MPI_to_proactive(datatype);
	int from_already_received_msg = 0;
	int awaited = 0;
	int error;


	DEBUG_PRINT_NATIVE_SIDE(
			mslog,
			fprintf(
					mslog,
					"[ProActiveMPI_Recv] Try to recv from jobid:%d, rank:%d tagged as %d max_count:%d \n",
					idjob, src, tag, count));
	
	// we wait until we get the awaited message.
	do {
		if (msg_recv_nb > 0) {
			recv_msg_buf = check_already_received_msg(count, pa_datatype, src,
					tag, idjob);
		}

		// the message queue is empty we're waiting for a message coming from the queue.
		if (recv_msg_buf == NULL) {

			// we use the static buffer initialized at start-up
			recv_msg_buf = static_recv_msg_buf;
			//if error = empty queue 
			if ((error = recv_ipc_message(S2C_Q_ID, TAG_R_KEY, recv_msg_buf,
					no_wait, ret_code)) < 0) {
				return error;
			}
		} else {
			// got from the already received message queue.
			from_already_received_msg = 1;
		}

		awaited = is_awaited_message(idjob, src, tag, pa_datatype, count, recv_msg_buf);

		if (!awaited) {
			// Received message is not the awaited one, we store it in the message queue
			int index = get_available_recv_queue_index();

			if (index == -1) {
				DEBUG_PRINT_NATIVE_SIDE(mslog, fprintf(mslog, "[ProActiveMPI_Recv] !!! ERROR: RECV MSG QUEUE IS FULL \n"))
			} else {
				// we need to make a copy of recv_msg_buf to avoid erasure on next ProActive_Recv invocation	
				// we store the message in the message queue
				recv_queue[index] = copy_message(recv_msg_buf);

				if (recv_queue[index] != NULL) {
					DEBUG_PRINT_NATIVE_SIDE(
							mslog,
							fprintf(
									mslog,
									"[ProActiveMPI_Recv] QUEUE store message at idx: %d lg: %d \n",
									index, msg_recv_nb));
					msg_recv_nb++;
				}
			}
		}

	} while (awaited == 0);

	//Received message is the one we're waiting for.	
	int length = get_mpi_buffer_length(recv_msg_buf->count, datatype, sizeof(char));

	if (length < 0) {
		if (DEBUG_NATIVE_SIDE) {
			fprintf(mslog, "[ProActiveMPI_Recv] !!! WRONG DATATYPE \n");
		}
		return -3;
	}
	// TODO see how we could integrate this copy into recv_message
	memcpy(buf, recv_msg_buf->data, length);

	if (from_already_received_msg) {
		free_msg_t(recv_msg_buf);
	}

	return 0;
}

int ProActiveMPI_IRecv(void* buf, int count, MPI_Datatype datatype, int src,
		int tag, int idjob, ProActiveMPI_Request *r) {

	r->buf = buf; // keep buf address in structure to update it later
	r->count = count;
	r->datatype = datatype;
	r->src = src;
	r->tag = tag;
	r->idjob = idjob;
	r->finished = 0;
	r->op_type = 1; /*recv*/;

	return 0;
}

int ProActiveMPI_Recv(void* buf, int count, MPI_Datatype datatype, int src,
		int tag, int idjob) {
	int error = 0;
	int ret_code = 0;
	// we block until we get the expected message
	error = internal_ProActiveMPI_Recv(buf, count, datatype, src, tag, idjob,
			0/*blocking call*/, &ret_code);
	return error;
}

int ProActiveMPI_Test(ProActiveMPI_Request *r) {
	int error = 0;
	int ret_code = 0;
	// we try to get the message only one time
	error = internal_ProActiveMPI_Recv(r->buf, r->count, r->datatype, r->src,
			r->tag, r->idjob, 1/*non-blocking call*/, &ret_code);

	if (!error) {
		// we received the awaited message
		r->finished = 1;
	} else {
		// we got an error however if error has been triggered because queue is empty
		// we can ignore it as it is the purpose of the ProActiveMPI_Test function to
		// test that
		if (ret_code == ENOMSG) {
			// finished is not set as we didn't receive the message.
			return 0;
		}
	}
	return error;
}

int ProActiveMPI_Wait(ProActiveMPI_Request *r) {
	int error = ProActiveMPI_Recv(r->buf, r->count, r->datatype, r->src,
			r->tag, r->idjob);
	if (!error) {
		r->finished = 1;
	}
	return error;
}

/*
 * ProActiveMPI_AllSend
 */
int ProActiveMPI_AllSend(void * buf, int count, MPI_Datatype datatype, int tag,
		int idjob) { /*
 msg_t send_msg_buf;
 int error;
 int pms;
 int length;
 */
	int error;
	if (DEBUG_NATIVE_SIDE) {
		fprintf(mslog, "[ProActiveMPI.c] !!! ProActiveMPI_AllSend \n");
	}

	/*		
	 send_msg_buf.msg_type = MSG_ALLSEND;
	 send_msg_buf.count = count;
	 send_msg_buf.src = myRank ;
	 send_msg_buf.dest = -1;
	 send_msg_buf.pa_datatype =  ;
	 send_msg_buf.tag = tag;
	 send_msg_buf.TAG = TAG_S_KEY;
	 send_msg_buf.idjob = idjob;
	 
	 length = get_mpi_buffer_length(count, datatype, sizeof(char));

	 if (length < 0) {
	 if (DEBUG_NATIVE_SIDE) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
	 return -1;
	 }

	 memcpy(send_msg_buf.data, buf, length);
	 pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	 error = msgsnd(C2S_Q_ID, &send_msg_buf, pms+length, 0);
	 if (error < 0) {
	 if (DEBUG_NATIVE_SIDE) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
	 perror("[ProActiveMPI.c] ERROR"); 
	 return -1;  }
	 */
	error = send_to_ipc(C2S_Q_ID, MSG_ALLSEND, TAG_S_KEY, buf, count, datatype,
			myRank,
			/*dest*/-1, tag, idjob);

	if (DEBUG_NATIVE_SIDE) {
		fflush(mslog);
	}
	return error;
}

/*
 * ProActiveMPI_Barrier
 */
int ProActiveMPI_Barrier(int job) {
	if (job == my_job_id) {
		return MPI_Barrier(MPI_COMM_WORLD);
	} else
		return -1;
}

/*
 * ProActiveMPI_Finalize
 */
int ProActiveMPI_Finalize() {
	msg_t send_msg_buf;
	int error;

	if (DEBUG_STMT) {
		init_msg_t(&send_msg_buf);
	}

	send_msg_buf.msg_type = MSG_FINALIZE;
	send_msg_buf.TAG = TAG_S_KEY;
//	send_msg_buf.idjob = ;
//	send_msg_buf.src = ;
//	send_msg_buf.dest = ;
	send_msg_buf.count = 0;
	send_msg_buf.pa_datatype = CONV_MPI_PROACTIVE_NULL;
//	send_msg_buf.tag = ;

	strcpy(send_msg_buf.data_backend, "");
	//TODO why payload + 1 ???
	error = msgsnd(C2S_Q_ID, &send_msg_buf,
			get_payload_size(&send_msg_buf)/*pms+1*/, 0);
	if (error < 0) {
		if (DEBUG_NATIVE_SIDE) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");
		}
		perror("[ProActiveMPI.c] ERROR");
		return -1;
	}
	return 0;
}

// /////////////////////////////////////////////////////////
// ///////////////// F77 IMPLEMENTATION /////////////////////
// //////////////////////////////////////////////////////////


void proactivempi_init_(int * rank, int* ierr) {
	*ierr = ProActiveMPI_Init(*rank);
}

void proactivempi_send_(void * buf, int* cnt, MPI_Datatype* datatype,
		int* dest, int* tag, int* idjob, int* ierr) {
	*ierr = ProActiveMPI_Send(buf, *cnt, *datatype, *dest, *tag, *idjob);
}

void proactivempi_recv_(void* buf, int* cnt, MPI_Datatype* datatype, int* src,
		int* tag, int* idjob, int* ierr) {
	*ierr = ProActiveMPI_Recv(buf, *cnt, *datatype, *src, *tag, *idjob);
}

/* NON BLOCKING COMMUNICATION - HOW TO HANDLE REQUEST WITH PROACTIVEMPI_FORTRAN ? 
 * 
 * Define a structure:
 * 
 * struct _request{
 * 		ProActiveMPI_Request  myRequest;
 * 		struct request * next;
 * } request;
 * 
 * In fortran the request handler is an INTEGER.
 * 
 * For a PROACTIVEMPI_IRECV go through the linked list and create a new ProActiveMPI_Request
 * at the index define in the fortran subroutine that is the INTEGER request.
 * 
 *  Unlike in C we cannot use directly some pointers in Fortran, but an INTEGER.
 *  So a linked list has to be defined in order to store a corresponding structure.
 * 
 */

void proactivempi_allsend_(void * buf, int* cnt, MPI_Datatype* datatype,
		int* tag, int* idjob, int*ierr) {
	*ierr = ProActiveMPI_AllSend(buf, *cnt, *datatype, *tag, *idjob);
}

void proactivempi_job_(int * job, int * nb_process, int* ierr) {
	*ierr = ProActiveMPI_Job(job, nb_process);
}

void proactivempi_barrier_(int* job, int* ierr) {
	*ierr = ProActiveMPI_Barrier(*job);
}

void proactivempi_finalize_(int* ierr) {
	*ierr = ProActiveMPI_Finalize();
}

void msg_stat(int msgid, struct msqid_ds * msg_info) {
	int reval;
	reval=msgctl(msgid, IPC_STAT, msg_info);
	if (reval==-1) {
		printf("[ProActiveMPI.c] get msg info error\n");
		return;
	}
}
