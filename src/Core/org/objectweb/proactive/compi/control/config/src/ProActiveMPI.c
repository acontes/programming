/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */

#include "ProActiveMPI.h"



// if debug is on please define the path variable below
// in the MPI_Init function
#define BIGINT 100000
#define MAX_NOM 32



int INTERVAL = 5 ;

char * path; 

char *daemon_address, *jobmanager, *myhostname;

#define RECV_QUEUE_SIZE 20
msg_t * recv_queue [RECV_QUEUE_SIZE];
int recv_queue_order [RECV_QUEUE_SIZE];
int msg_recv_nb = 1;

int C2S_Q_ID, S2C_Q_ID;
int sem_set_id_mpi;            // semaphore set ID.
int myJob=-1;

int TAG_S_KEY;
int TAG_R_KEY;

void msg_stat(int msgid, struct msqid_ds * msg_info);
int openlog(char *path, int rank);

/*
int send_to_ipc_queue(int msg_type, void * buf, int count, MPI_Datatype datatype, 
					  int length, int dest, int tag, int idjob);
int send_splitted_buffer(int qid, void * buf, int count, MPI_Datatype datatype, int dest, int tag, int idjob);
*/
/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+-----+- MPI <-> MPI FUNCTIONS -+----+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/


int get_available_recv_queue_index() {
	int i = 0, free = -1;
	while((i < RECV_QUEUE_SIZE) && (free == -1)) {
		if (recv_queue_order[i] == -1) {
			recv_queue_order[i] = msg_recv_nb;
			free = i;
		}
		i++;
	}
	return free;
}

msg_t * check_already_received_msg(int count, ProActive_Datatype pa_datatype, int src, int tag, int idjob) {
	int i = 0;
 	msg_t * candidate = NULL;
 	int candidate_index = -1;
 	
	DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] check already received message invoked\n"));
	
	while (i < RECV_QUEUE_SIZE) {
	
		if(recv_queue[i] != NULL) {
			if ((recv_queue[i]->count == count) &&
				(recv_queue[i]->src == src) &&
				(recv_queue[i]->tag == tag) &&
				(recv_queue[i]->idjob == idjob) &&
				(recv_queue[i]->pa_datatype == pa_datatype)) {
					if (candidate != NULL) {
						// check which message was received first
						if (recv_queue_order[i] < candidate_index) {
							candidate = recv_queue[i];
							candidate_index = i;		
						}
					} else {
						candidate = recv_queue[i];
						candidate_index = i;
					}
				// We probably got the right message, but we must ensure fifo order.
			}
		}
		i++;
	}
	
	if (candidate != NULL) {
		// we found a message that match the requirements in the message queue.
		// we need to remove it
		recv_queue[candidate_index] = NULL;
		recv_queue_order[candidate_index] = -1;
		i = 0;
		while(i < RECV_QUEUE_SIZE) {
			if ((recv_queue_order[i] != -1) && (recv_queue_order[i] > recv_queue_order[candidate_index])) {
				recv_queue_order[i]--;
			}
			i++;
		}
		msg_recv_nb--;

		DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] QUEUE : got message at idx: %d lg: %d \n", candidate_index, msg_recv_nb));
	}
	
	return candidate;
}

/*
 * ProActiveMPI_Init
 */
int ProActiveMPI_Init(int rank){
	int error;
	msg_t send_msg_buf, recv_msg_buf ;
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
	
	if (DEBUG){
		path = (char*) malloc(256);
		strcpy(path, "/home/sophia/emathias");
		if (openlog(path, myRank) < 0){
			printf("ERROR WHILE OPENING FILE PATH= %s \n", path); 
			perror("[ProActiveMPI_Init] openlog");  exit(1);}
		fprintf(mslog, "Initializing queues \n");
	}

	// get the mpi semaphore
	sem_set_id_mpi = semget(SEM_ID_MPI, 1, IPC_CREAT | S_IRUSR |
                 S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);
	if  (sem_set_id_mpi == -1){
		perror("[ProActiveMPI_Init] semget");
		exit(1);
	}
	
	struct semid_ds test;
	
	semctl(sem_set_id_mpi, 0, IPC_STAT, &test);
	
	DEBUG_PRINT(mslog, fprintf(mslog, "Block Semaphore  \n"));
	
	// first process lock the semaphore
	sem_lock(sem_set_id_mpi);
    // accessing exclusively the ClientToServer queue
	if ((C2S_Q_ID = msgget(C2S_KEY, ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI_Init] msgget 1 ");
		DEBUG_PRINT(mslog, fprintf(mslog, "Cannot open sending queue: %d   \n",C2S_Q_ID))

		return -1;
	}
	// the queue successfully opened
	else {
		// update TAG_KEY
		TAG_S_KEY=C2S_KEY;
		
		// check the pid of the last process which have accessed to the queue
		// if (pid <> 0) open this process is the second one
		msg_stat(C2S_Q_ID, &bufstat);
		if (bufstat.msg_lspid != 0){
			// acess the second message queue
			if ((C2S_Q_ID = msgget(C2S02_KEY,  ACCESS_PERM)) == -1) {
				perror("[ProActiveMPI_Init] msgget C2S_02");
				DEBUG_PRINT(mslog, fprintf(mslog, "Cannot open the second sending queue: %d   \n",C2S_Q_ID))
				return -1;
			}
			else{
				// update TAG_KEY
				TAG_S_KEY=C2S02_KEY;
				DEBUG_PRINT(mslog, fprintf(mslog, "Second Sending Queue %d successfully opened \n ",C2S_Q_ID))
			}
		}
DEBUG_PRINT(mslog, fprintf(mslog, "Sending Queue %d successfully opened \n ",C2S_Q_ID))
	}
	// accessing exclusively the ServerToClient queue
	if ((S2C_Q_ID = msgget(S2C_KEY,  ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI_Init] mssget S2C_01  ");
DEBUG_PRINT(mslog, fprintf(mslog, "Cannot open receiving queue: %d   \n",S2C_Q_ID))
		return -1;
	}
	else {
		TAG_R_KEY=S2C_KEY;
		// check the pid of the last process which have accessed to the queue
		// if (pid <> 0) open the second queue
		if (bufstat.msg_lspid != 0){
			if ((S2C_Q_ID = msgget(S2C02_KEY,  ACCESS_PERM)) == -1) {
				perror("[ProActiveMPI_Init] msgget S2C_02 ");
DEBUG_PRINT(mslog, fprintf(mslog, "Cannot open the second recving queue: %d   \n",S2C_Q_ID))
				return -1;
			}
			else{
				TAG_R_KEY=S2C02_KEY;
DEBUG_PRINT(mslog, fprintf(mslog, "Second Recving Queue %d successfully opened \n ",S2C_Q_ID))
			}
		}
DEBUG_PRINT(mslog, fprintf(mslog, "Receivind Queue %d successfully opened \n ",S2C_Q_ID))
	}
	// unlock the semaphore
	sem_unlock(sem_set_id_mpi);
DEBUG_PRINT(mslog, fprintf(mslog, "UnBlock Semaphore  \n"))
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
	DEBUG_PRINT(mslog, fprintf(mslog, "Sending to  %d \n ",C2S_Q_ID))
	error = msgsnd(C2S_Q_ID, &send_msg_buf,  get_payload_size(&send_msg_buf)/*pms+1*/, 0);
	DEBUG_PRINT(mslog, fprintf(mslog, "Sent %d bytes to queue %d successfully EINVAL %d error %d\n ", get_payload_size(&send_msg_buf), C2S_Q_ID, EINVAL, error))
	/*************************************************/
	/*************************************************/
	/*************************************************/
	
	if (error < 0) {
DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI_Init] !!! ERROR: msgsnd error\n"))
		perror("ERROR");
		return -1; }
	if (DEBUG){
		fprintf(mslog, "Waiting for job number in recv queue \n "); }
	// wait for the job number
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
//	error = msgrcv(S2C_Q_ID, &recv_msg_buf, pms+MSG_SIZE, TAG_R_KEY, 0);
	// if an error occured during receive call check if its an interrupted
	// System call and so retry to receive
	while (error < 0){
//		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI_Init] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI_Init] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
		}
		else{
			perror("ERROR");
			return -1; 
		}
	}
	// update the job field of this mpi process
	myJob = recv_msg_buf.idjob;

	if (DEBUG) {
 		fprintf(mslog, "[ProActiveMPI_Init] [END] Process\n");
  		fprintf(mslog, "[ProActiveMPI_Init] [END] myRank == %d \n", myRank);
  		fflush(mslog);
	}

	return 0;
}

/*
 * ProActiveMPI_Job
 */
int ProActiveMPI_Job(int * job){
	*job=myJob;
	return 0;
}




/*
 * ProActiveMPI_Send
 */
int ProActiveMPI_Send(void * buf, int count, MPI_Datatype datatype, int dest, int tag, int idjob)
{ 
	return send_to_ipc(C2S_Q_ID, MSG_SEND, TAG_S_KEY, buf, count, datatype, myRank, dest, tag, idjob);
}


/*
 * ProActiveMPI_Recv
 */
int ProActiveMPI_Recv(void* buf, int count, MPI_Datatype datatype, int src, int tag, int idjob){

	msg_t * recv_msg_buf;
	ProActive_Datatype pa_datatype = type_conversion_MPI_to_proactive(datatype);
	int not_received = 1;
	int warning = 0;
	int error;
	while (not_received == 1) {
		
		recv_msg_buf = check_already_received_msg(count, pa_datatype, src, tag, idjob);
	
		// the message queue is empty we're waiting for a message coming from the queue.
		if (recv_msg_buf == NULL) {
			if ((recv_msg_buf = malloc(sizeof(msg_t))) == NULL) {
				if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! ERROR : MALLOC FAILED\n");
				}
				perror("[ProActiveMPI_Recv] !!! ERROR : MALLOC FAILED");
				return -1;
			}
			if ((error = recv_ipc_message(S2C_Q_ID, TAG_R_KEY, recv_msg_buf)) < 0) {
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! ERROR : IPC message queue reception error code: %d \n", error);
			}

				return -2;
			}
		}

		// A message has been received
		// We check if it's the one we're waiting for.
		if (recv_msg_buf->idjob != idjob){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! WARNING: BAD PARAMETER idjob. Storing to the message queue\n");
			}
			warning = 1;
		}
		else if ((src != MPI_ANY_SOURCE) && (recv_msg_buf->src != src)){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! WARNING: BAD PARAMETER src. Storing to the message queue\n");
			}
			warning = 1;
		}
		else if ((tag != MPI_ANY_TAG) && (recv_msg_buf->tag != tag)) {
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! WARNING: BAD PARAMETER tab. Storing to the message queue\n");
			}
			warning = 1;
		} 
		else if (recv_msg_buf->pa_datatype != type_conversion_MPI_to_proactive(datatype)){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI_Recv] !!! WARNING: BAD PARAMETER datatype. Storing to the message queue\n");
			}
			warning = 1;
		} 
		
		if (warning == 0) {
			// We got the right message
			not_received = 0;
		} else {
			// Recevied mesage is not the awaited one, we store it in the message queue
			int index = get_available_recv_queue_index();
		
			if (index == -1) {
				DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI_Recv] !!! ERROR: RECV MSG QUEUE IS FULL \n"))	
			} else {	
			// we store the message in the message queue
				recv_queue[index] = recv_msg_buf;
		
				DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI_Recv] WAIT FOR MSG from jobid:%d, rank:%d tagged as %d\n", idjob, src, tag));
				DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI_Recv] QUEUE store message at idx: %d lg: %d \n", index, msg_recv_nb));
				msg_recv_nb++;
			}
		}

		warning = 0;
	}
		//Received message is the one we're waiting for.	
		int length = debug_get_mpi_buffer_length(count, datatype, sizeof(char));

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI_Recv] !!! WRONG DATATYPE \n");}
			return -3;
		}
	// TODO see how we could integrate this copy into recv_message
		memcpy(buf, recv_msg_buf->data, length);
	
		free_msg_t(recv_msg_buf);	
	return 0;
	
}



/*
 * ProActiveMPI_IRecv
 */
int ProActiveMPI_IRecv(void* buf, int count, MPI_Datatype datatype, int src, int tag, int idjob, ProActiveMPI_Request *r){

	int error = -1;
	int length;
	msg_t recv_msg_buf;
	
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, IPC_NOWAIT);
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, IPC_NOWAIT);
		}
		// no message in the queue
		else if (errno == ENOMSG){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
			r->buf = buf; // keep buf address in structure to update it later
			(*r).flag = 0; // nothing recv yet
			// keep parameters for further recv
			(*r).count = count;
			(*r).pa_datatype =  type_conversion_MPI_to_proactive(datatype);
			(*r).src = src;
			(*r).tag = tag;
			(*r).idjob = idjob;
			return 0;
		}
		else{
			perror("[ProActiveMPI_IRecv]!!! ERROR");
			return -1; 
		}
	}
	
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
	else if ((src != MPI_ANY_SOURCE) && (recv_msg_buf.src != src)){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src %d\n", src);}
		return -1;
	}
	else if (recv_msg_buf.tag != tag) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype !=  type_conversion_MPI_to_proactive(datatype)){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {
		length = debug_get_mpi_buffer_length(count, datatype, sizeof(char));

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}
		
		memcpy(buf, recv_msg_buf.data, length);	
	}
	return 0;
}

/*
 * ProActiveMPI_Wait
 */
int ProActiveMPI_Wait(ProActiveMPI_Request *r){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
	
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	
	int idjob = (*r).idjob;
	int tag = (*r).tag;
	int count = (*r).count;
	int pa_datatype = (*r).pa_datatype;
	int src = (*r).src;
	
//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	if((error = recv_ipc_message(S2C_Q_ID, TAG_R_KEY, &recv_msg_buf)) < 0) {
	 return error;	
	}
	/*
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, pms+MSG_SIZE, TAG_R_KEY, 0);
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, pms+MSG_SIZE, TAG_R_KEY, 0);
		}
		// no message in the queue
		else{
			perror("[ProActiveMPI.c] ERROR");
			return -1; 
		}
	}
	*/
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
	else if ((src != MPI_ANY_SOURCE) && (recv_msg_buf.src != src)){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src %d\n", src);}
		return -1;
	}
	else if ((tag != MPI_ANY_TAG) && (recv_msg_buf.tag != tag)) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype != pa_datatype){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {
		length = get_proactive_buffer_length(count, pa_datatype);

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}
		
		memcpy(r->buf, recv_msg_buf.data, length);
	}
	return 0;
}

/*
 * ProActiveMPI_Test
 */
int ProActiveMPI_Test(ProActiveMPI_Request *r, int* flag){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
	
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	int idjob = (*r).idjob;
	int tag = (*r).tag;
	int count = (*r).count;
	int pa_datatype = (*r).pa_datatype;
	int src=(*r).src;
	
//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, IPC_NOWAIT);
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, IPC_NOWAIT);
		}
		// no message in the queue
		else if (errno == ENOMSG){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
			// mv buffer pointer
			*flag = 0; // not recv yet
			return 0;
		}
		else{
			perror("[ProActiveMPI.c] ERROR");
			return -1; 
		}
	}
	// Msg recved
	*flag = 1;
	
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
	else if ((src != MPI_ANY_SOURCE) && (recv_msg_buf.src != src)){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src %d\n", src);}
		return -1;
	}
	else if ((tag != MPI_ANY_TAG) && (recv_msg_buf.tag != tag)) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype != pa_datatype){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {
		length = get_proactive_buffer_length(count, pa_datatype);

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}
		
		memcpy(r->buf, recv_msg_buf.data, length);	
	}
	return 0;
}

/*
 * ProActiveMPI_AllSend
 */
int ProActiveMPI_AllSend( void * buf, int count, MPI_Datatype datatype, int tag, int idjob)
{ /*
	msg_t send_msg_buf;
	int error;
	int pms;
	int length;
	*/
	int error;
	if (DEBUG) {
		fprintf(mslog, "[ProActiveMPI.c] !!! ProActiveMPI_AllSend \n");}
		

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
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
		return -1;
	}

	memcpy(send_msg_buf.data, buf, length);
	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf, pms+length, 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR"); 
		return -1;  }
		*/
	error = send_to_ipc(C2S_Q_ID, MSG_ALLSEND, TAG_S_KEY, 
							 buf, count, datatype, myRank, 
							 /*dest*/ -1, tag, idjob);
		
	if (DEBUG) {fflush(mslog);}
	return error;
}




/*
 * ProActiveMPI_Barrier
 */
int ProActiveMPI_Barrier(int job){
	if (job == myJob) { 
		return MPI_Barrier(MPI_COMM_WORLD);}
	else
		return -1;
}

/*
 * ProActiveMPI_Finalize
 */
int ProActiveMPI_Finalize(){
	msg_t send_msg_buf;	
	int error;
	
	if (DEBUG_STMT) {
		init_msg_t(&send_msg_buf);
	}
	
	send_msg_buf.msg_type = MSG_FINALIZE;
	send_msg_buf.TAG = TAG_S_KEY;
	strcpy(send_msg_buf.data_backend, "");
	//TODO why payload + 1 ???
	error = msgsnd(C2S_Q_ID, &send_msg_buf, get_payload_size(&send_msg_buf)/*pms+1*/, 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR");
		return -1; }
	return 0;
}


/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+----- MPI -> PROACTIVE FUNCTIONS  -+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/

/*
 * ProActiveSend
 */
int ProActiveSend(void* buf, int count, MPI_Datatype datatype, int dest, char* clazz, char* method, int idjob, ...){
	msg_t send_msg_buf;
	int error;
//	int pms;
	int length;
	char* next;
	int nb_args = 0;
	char * nb = (char *) malloc(sizeof(char)*2);
	char * parameters = (char *) malloc(50);
	va_list ptr;
	
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&send_msg_buf);
	} 
	
	if (DEBUG) {
	fprintf(mslog, "Test 0 %s %s\n", clazz, method); fflush(mslog);}
			
	strcpy(parameters, "");
	strcpy(send_msg_buf.method, "");

	// ptr initialization
	va_start(ptr, idjob);  
	next = va_arg(ptr, char*);
	while (next != NULL) {
		nb_args++;
		strcat(parameters, next);
		strcat(parameters, "\t");
		next = va_arg(ptr, char*);	
	}
	sprintf(nb, "%d", nb_args);
	send_msg_buf.msg_type = MSG_SEND_PROACTIVE;
	send_msg_buf.count = count;
	send_msg_buf.src = myRank ;
	send_msg_buf.dest = dest;
	send_msg_buf.pa_datatype =  type_conversion_MPI_to_proactive(datatype);
	send_msg_buf.TAG = TAG_S_KEY;
	send_msg_buf.idjob = idjob;
	strcpy(send_msg_buf.method,clazz);
	strcat(send_msg_buf.method,"\t");
	strcat(send_msg_buf.method,method);
	strcat(send_msg_buf.method,"\t");
	strcat(send_msg_buf.method,nb);
	strcat(send_msg_buf.method,"\t");
	strcat(send_msg_buf.method,parameters);
				if (DEBUG) {
	fprintf(mslog, "Test 4 %s \n", send_msg_buf.method);fflush(mslog);}
				
	length = debug_get_mpi_buffer_length(count, datatype, sizeof(char));

	if (length < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
		return -1;
	}
	
	memcpy(send_msg_buf.data, buf, length);
//	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf, get_payload_size(&send_msg_buf), 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR"); 
		return -1;  }
	if (DEBUG) {fflush(mslog);}
	
	return 0;
}


/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+----- PROACTIVE -> MPI  FUNCTIONS  -+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/

/*
 * ProActiveRecv
 */
int ProActiveRecv(void* buf, int count, MPI_Datatype datatype, int src, int tag, int idjob){
	msg_t  * recv_msg_buf = malloc(sizeof(msg_t));
	int error = 0;
//	int pms;
	int length;
	// getting datatype
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(recv_msg_buf);
	} 
	//TODO refactor not finished see ProActiveMPI_Recv
	ProActive_Datatype pa_datatype = type_conversion_MPI_to_proactive(datatype);
		
	DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI.c][ProActiveRecv] Entering %d", errno))	

	// first we check if we already receive the message
	recv_msg_buf = check_already_received_msg(count, pa_datatype, src, tag, idjob);
	if (recv_msg_buf == NULL) { 
		error = recv_ipc_message(S2C_Q_ID, PROACTIVE_KEY, recv_msg_buf);
	}
	
	if (error < 0){
		//TODO free
		return error;
	}
	
	// if an error occured during receive call check if its an interrupted
	// System call and so retry to receive
	/*
	while (error < 0){
//		strerror(errno);
		DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! ERROR: msgrcv error ERRNO = %d, \n", errno))
		
		if (errno == EINTR){
			DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! ERRNO = EINTR, \n"))
			error = msgrcv(S2C_Q_ID, recv_msg_buf, get_payload_size(recv_msg_buf), PROACTIVE_KEY, 0);
			DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! ERROR: msgrcv error ERRNO = %d, \n", errno))
		} else {
			DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! ERROR %d\n", errno))
			return -1; 
		}
	}*/
	
	DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! msgrcv succeeds \n"))

	if (recv_msg_buf->idjob != idjob){
		DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! BAD PARAMETER idjob, Queuing the message \n"))
		return -1;
	}
 	else if (recv_msg_buf->src != src){
 		if (DEBUG) {
 			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src \n");}
 			return -1;
 	}
	else if ((tag != MPI_ANY_TAG) && (recv_msg_buf->tag != tag)) {
			DEBUG_PRINT(mslog, 
				fprintf(mslog, "[ProActiveRecv] !!! ERROR: BAD PARAMETER tag \n"))
						
		int index = get_available_recv_queue_index();
		
		if (index == -1) {
			DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv] !!! ERROR: RECV MSG QUEUE IS FULL \n"))	
		} else {
		// we store the message in the message queue
			recv_queue[index] = recv_msg_buf;
			msg_recv_nb++;
		}
//		return -1;
	} 
	else if (recv_msg_buf->pa_datatype != pa_datatype){
			DEBUG_PRINT(mslog, 
				fprintf(mslog, "[ProActiveRecv] !!! ERROR: BAD PARAMETER datatype \n"))
		return -1;
	} else {
		length = debug_get_mpi_buffer_length(count, datatype, sizeof(char));

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveRecv] !!! BAD DATATYPE \n");}
			return -1;
		}

		memcpy(buf, recv_msg_buf->data, length);
		// we don't need the message buffer anymore
		free_msg_t(recv_msg_buf);
	}
	DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveRecv]Exiting %d", errno))
	return 0;
}

/*
 * ProActiveWait
 */
int ProActiveWait(ProActiveMPI_Request *r){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	int idjob = (*r).idjob;
	int tag = (*r).tag;
	int count = (*r).count;
	int pa_datatype = (*r).pa_datatype;
	
//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, 0);
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, 0);
		}
		// no message in the queue
		else{
			perror("[ProActiveMPI.c] ERROR");
			return -1; 
		}
	}
	
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
// else if (recv_msg_buf.src != src){
// if (DEBUG) {
// fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src \n");}
// return -1;
// }
	else if ((tag != MPI_ANY_TAG) && (recv_msg_buf.tag != tag)) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype != pa_datatype){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {		
		length = get_proactive_buffer_length(count, pa_datatype);

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}

		memcpy(r->buf, recv_msg_buf.data, length);	
	}
	return 0;	
}

/*
 * ProActiveTest
 */
int ProActiveTest(ProActiveMPI_Request *r, int* flag){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
	if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	int idjob = (*r).idjob;
	int tag = (*r).tag;
	int count = (*r).count;
	int pa_datatype = (*r).pa_datatype;
	
//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, IPC_NOWAIT);
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, IPC_NOWAIT);
		}
		// no message in the queue
		else if (errno == ENOMSG){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
			// mv buffer pointer
			*flag = 0; // not recv yet
			return 0;
		}
		else{
			perror("[ProActiveMPI.c] ERROR");
			return -1; 
		}
	}
	// Msg recved
	*flag = 1;
	
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
// else if (recv_msg_buf.src != src){
// if (DEBUG) {
// fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src \n");}
// return -1;
// }
	else if ((tag != MPI_ANY_TAG) && (recv_msg_buf.tag != tag)) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype != pa_datatype){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {
		length = get_proactive_buffer_length(count, pa_datatype);

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}

		memcpy(r->buf, recv_msg_buf.data, length);	
	}
	return 0;
}
	
/*
 * ProActiveIRecv
 */
int ProActiveIRecv(void* buf, int count, MPI_Datatype datatype, int src, int tag, int idjob, ProActiveMPI_Request *r){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, IPC_NOWAIT);
	
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), PROACTIVE_KEY, IPC_NOWAIT);
		}
		// no message in the queue
		else if (errno == ENOMSG){
			if (DEBUG) {
				fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
			r->buf = buf; // keep buf address in structure to update it later
			(*r).flag = 0; // nothing recv yet
			// keep parameters for further recv
			(*r).count = count;
			(*r).pa_datatype = type_conversion_MPI_to_proactive(datatype);
			(*r).src = src;
			(*r).tag = tag;
			(*r).idjob = idjob;
			return 0;
		}
		else{
			perror("[ProActiveIRecv]!!! ERROR");
			return -1; 
		}
	}
	
	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != idjob){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER idjob \n");}
		return -1;
	}
// else if (recv_msg_buf.src != src){
// if (DEBUG) {
// fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER src \n");}
// return -1;
// }
	else if (recv_msg_buf.tag != tag) {
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER tag \n");}
		return -1;
	} 
	else if (recv_msg_buf.pa_datatype != type_conversion_MPI_to_proactive(datatype)){
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: BAD PARAMETER datatype \n");}
		return -1;
	} else {
		length = debug_get_mpi_buffer_length(count, datatype, sizeof(char));

		if (length < 0) {
			if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! BAD DATATYPE \n");}
			return -1;
		}

		memcpy(buf, recv_msg_buf.data, length);
	}
	
	return 0;
	
}

// /////////////////////////////////////////////////////////
// ///////////////// F77 IMPLEMENTATION /////////////////////
// //////////////////////////////////////////////////////////


void proactivempi_init_(int * rank, int* ierr){
	msg_t send_msg_buf, recv_msg_buf ;
	int error;
//	int pms;
	char path[256];
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
		init_msg_t(&send_msg_buf);
	} 
	
	strcpy(path,"");
	strcpy(path,"/tmp");
	myRank = *rank;
	if (DEBUG){  
		if (openlog(path, *rank) < 0){ printf("[ProActiveMPI.c] ERROR WHILE OPENING FILE PATH= %s \n", path); perror("ERROR");  exit(1);}
		fprintf(mslog, "[ProActiveMPI.c] Initializing queues \n");
	}
	
	if ((C2S_Q_ID = msgget(C2S_KEY,  ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI.c] ERROR ");
		if (DEBUG){
			fprintf(mslog, "[ProActiveMPI.c] Cannot open sending queue: %d   \n",C2S_Q_ID);
		}
		*ierr=-1;
		return;
	}
	else {
		if (DEBUG){ 
			fprintf(mslog, "[ProActiveMPI.c] Sending Queue %d successfully opened \n ",C2S_Q_ID); }
	}
	if ((S2C_Q_ID = msgget(S2C_KEY,  ACCESS_PERM)) == -1) {
		perror("[ProActiveMPI.c] ERROR ");
		if (DEBUG){
			fprintf(mslog, "[ProActiveMPI.c] Cannot open receiving queue: %d   \n",S2C_Q_ID);
		}
		*ierr=-1;
		return;
	}
	else {
		if (DEBUG){
			fprintf(mslog, "[ProActiveMPI.c] Receivind Queue %d successfully opened \n ",S2C_Q_ID); }
	}
	send_msg_buf.msg_type = MSG_INIT;
	send_msg_buf.TAG = TAG_S_KEY;
	send_msg_buf.src = *rank;
// strcpy(send_msg_buf.data, "");
//	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf,  get_payload_size(&send_msg_buf)+1, 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR");
		*ierr=-1;
		return; }
	
	if (DEBUG){
		fprintf(mslog, "[ProActiveMPI.c] Waiting for job number in recv queue \n "); }
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
	// if an error occured during receive call check if its an interrupted
	// System call and so retry to receive
	while (error < 0){
		strerror(errno);
		if (DEBUG) {
			fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgrcv error ERRNO = %d, \n", errno);}
		
		if (errno == EINTR){
			if (DEBUG) { fprintf(mslog, "[ProActiveMPI.c] !!! ERRNO = EINTR, \n");}
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
		}
		else{
			perror("[ProActiveMPI.c] ERROR");
			*ierr=-1;
			return; 
		}
	}
	
	myJob = recv_msg_buf.idjob;
	
	if (DEBUG) {fflush(mslog);}
	*ierr=0;
}

void proactivempi_send_(void * buf, int* cnt, MPI_Datatype* datatype, int* dest, int* tag, int* idjob, int* ierr)
{ 
	msg_t send_msg_buf;
	int error;
//	int pms;
	int length = 0;
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&send_msg_buf);
	} 
	
	int count = *cnt;
	send_msg_buf.msg_type = MSG_SEND;
	send_msg_buf.count = *cnt;
	send_msg_buf.src = myRank ;
	send_msg_buf.dest = *dest;
	send_msg_buf.pa_datatype = type_conversion_MPI_to_proactive(*datatype);
	send_msg_buf.tag = *tag;
	send_msg_buf.TAG = TAG_S_KEY;
	send_msg_buf.idjob = *idjob;
	
	length = debug_get_mpi_buffer_length(count, *datatype, sizeof(char));
	if (length < 0) {
	 	*ierr=-1;
	}
	
	memcpy(send_msg_buf.data, buf, length);
//	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf,  get_payload_size(&send_msg_buf), 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR"); 
		*ierr= -1;
		return;}
	if (DEBUG) {fflush(mslog);}
	*ierr=0;
}



void proactivempi_recv_(void* buf, int* cnt, MPI_Datatype* datatype, int* src, int* tag, int* idjob, int* ierr){
	msg_t recv_msg_buf;
	int error = -1;
//	int pms;
	int length;
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&recv_msg_buf);
	} 
	
	int count = *cnt;

	DEBUG_PRINT(mslog, fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] Entering %d", errno))

//	pms= sizeof(msg_t) - sizeof(recv_msg_buf.TAG) - sizeof(recv_msg_buf.data);
	
	error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
	// if an error occured during receive call check if its an interrupted
	// System call and so retry to receive
	while (error < 0){
		strerror(errno);
		DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERROR: msgrcv error ERRNO = %d, \n", errno))
		
		if (errno == EINTR){
			DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERRNO = EINTR, \n"))
			error = msgrcv(S2C_Q_ID, &recv_msg_buf, get_payload_size(&recv_msg_buf), TAG_R_KEY, 0);
		}
		else{
			perror("[ProActiveMPI.c][proactivempi_recv_] ERROR");
			*ierr=-1; 
			return; 
		}
	}
	
//	if (DEBUG) {fflush(mslog);}
	// filter
	if (recv_msg_buf.idjob != *idjob){

			DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERROR: BAD PARAMETER idjob \n"))
		*ierr=-1; 
		return; 
	}
	else if (recv_msg_buf.src != *src){
			DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERROR: BAD PARAMETER src \n"))
		*ierr=-1; 
		return; 
	}
	else if (recv_msg_buf.tag != *tag) {
			DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERROR: BAD PARAMETER tag \n"))
		*ierr=-1; 
		return; 
	} 
	else if (recv_msg_buf.pa_datatype != type_conversion_MPI_to_proactive(*datatype)){
			DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] !!! ERROR: BAD PARAMETER datatype \n"))
		*ierr=-1; 
		return; 
	} 
	else{
		length = debug_get_mpi_buffer_length(count, *datatype, sizeof(char));
		
		if (length < 0) {
			*ierr=-1;
			return; 
		}
		
		memcpy(buf, recv_msg_buf.data, length);
	}
	
	*ierr=0;
	DEBUG_PRINT(mslog,fprintf(mslog, "[ProActiveMPI.c][proactivempi_recv_] Exiting %d", errno)) 
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


void proActivempi_barrier_(int* job, int* ierr){
	if (*job == myJob) { 
		*ierr = MPI_Barrier(MPI_COMM_WORLD);}
	else{
		*ierr=-1;
		return;
	}
}

void proactivempi_finalize_(int* ierr){
	msg_t send_msg_buf;
	int error;
	int pms;
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&send_msg_buf);
	} 
	
	send_msg_buf.msg_type = MSG_FINALIZE;
	send_msg_buf.TAG = TAG_S_KEY;
	strcpy(send_msg_buf.data, "");
	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf,  get_payload_size(&send_msg_buf)/*pms+1*/, 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR");
		*ierr= -1; return; }
	if (DEBUG) {fflush(mslog);}
	*ierr=0;
	return;
}


void proactivempi_job_(int * job, int* ierr){
	*job=myJob;
	*ierr=0;
}

void proactivempi_allsend_(void * buf, int* cnt, MPI_Datatype* datatype, int* tag, int* idjob, int*ierr)
{ 
	msg_t send_msg_buf;
	int error;
//	int pms;
	int length;
		if (DEBUG_STMT) {
		// clear buffers in debug mode to avoid valgrind warnings
		init_msg_t(&send_msg_buf);
	} 
	
	int count = *cnt;
	if (DEBUG) {
		fprintf(mslog, "[ProActiveMPI.c] !!! ProActiveMPI_AllSend \n");}
	send_msg_buf.msg_type = MSG_ALLSEND;
	send_msg_buf.count = *cnt;
	send_msg_buf.src = myRank ;
	send_msg_buf.dest = -1;
	send_msg_buf.pa_datatype = type_conversion_MPI_to_proactive(*datatype);
	send_msg_buf.tag = *tag;
	send_msg_buf.TAG = TAG_S_KEY;
	send_msg_buf.idjob = *idjob;
	
	length = debug_get_mpi_buffer_length(count, *datatype, sizeof(char));
		
	if (length < 0) {
		*ierr=-1;
		return; 
	}
		
	memcpy(send_msg_buf.data, buf, length);
//	pms= sizeof(msg_t) - sizeof(send_msg_buf.TAG) - sizeof(send_msg_buf.data);
	error = msgsnd(C2S_Q_ID, &send_msg_buf,  get_payload_size(&send_msg_buf), 0);
	if (error < 0) {
		if (DEBUG) {fprintf(mslog, "[ProActiveMPI.c] !!! ERROR: msgsnd error\n");}
		perror("[ProActiveMPI.c] ERROR"); 
		*ierr = -1;
		return;  }
	if (DEBUG) {fflush(mslog);}
	*ierr = 0;
	return;
}

void msg_stat(int msgid, struct msqid_ds * msg_info)
{
	int reval;
	reval=msgctl(msgid,IPC_STAT,msg_info);
	if(reval==-1)
	{
		printf( "[ProActiveMPI.c] get msg info error\n");
		return;
	}
}

int openlog(char *path, int rank){
	char hostname[MAX_NOM];
	char nombre[2];
	int err = 0;
	sprintf(nombre, "%d", rank);
	gethostname(hostname, MAX_NOM);
	strcat(path, "/log");
	umask(000);
	err = mkdir(path, S_IRWXU | S_IRWXG | S_IRWXO);

	if ((err >  0) || (errno == EEXIST)) {
		//TODO possible bug as EEXIST indicate that it could be a file
		strcat(path, "/mpi_log");
		strcat(path, "_");
		strcat(path, hostname);
		strcat(path, "_");
		strcat(path, nombre);
		mslog = fopen(path, "w");

		if(mslog==NULL) { 
			err= -1;
		} else {
		 err = 0;
		}
	}
	
	return err;
}

