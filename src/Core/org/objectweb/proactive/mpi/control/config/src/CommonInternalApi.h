#ifndef COMMON_INTERNAL_API_H_
#define COMMON_INTERNAL_API_H_

#include <mpi.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/sem.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include <pthread.h>
#include <sched.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <errno.h>
#include <stdarg.h>
#include <sys/stat.h>

#ifndef MPI_MAX_OBJECT_NAME
#define MPI_MAX_OBJECT_NAME 128
#endif

#define DEBUG_STMT 1

#define MAX_INT (1 << 30)

#define MSQ_SIZE	16384
#define MSG_DATA_SIZE 8020
//TODO: implement a routine to check message size does not outbound the maximum msg size MSGMAX
#define MET_SIZE 128
#define ACCESS_PERM 0666
#define MAX_NODE 256

/* used as KEY of message queue */
#define C2S_KEY 3026 
#define S2C_KEY	3020

/* used for a potentially second mpi process on same host */
#define C2S02_KEY 3032 
#define S2C02_KEY 3038

#define PROACTIVE_KEY 3044

#define SEM_ID_JAVA 250
#define SEM_ID_MPI  350

#define ANY_SRC -2
#define ANY_TAG -1

//TODO simplify the debugging process
/* Common Debug control */
#define VERBOSE_H_MSG 1
#define DEBUG_H 1

/* MPI Side Debug control */
#define DEBUG 1	// most verbose debug info
#define DEBUG_PRINT(f, statement) if (DEBUG) {statement; fflush(f);}

/* ProActive Side Debug control */
#define VERBOSE_MSG 1
#define VERBOSE_STAT 1


/* TAGs used in message's "msg_type" block */
#define MSG_SEND			2
#define MSG_RECV			4
#define MSG_INIT			6
#define MSG_ALLSEND       	8
#define MSG_FINALIZE		10
#define MSG_SEND_PROACTIVE	12
#define MSG_SEND_SPLIT_BEGIN 14 
#define MSG_SEND_SPLIT		16
#define MSG_SEND_SPLIT_END	18

union semun{
	int val;	
	struct semid_ds *buf;		
	unsigned short int *array;
	struct seminfo *__buf;	
};


// Data types
typedef int ProActive_Datatype;

typedef struct _msg {	// to be put into common header file "javampi.h"
  long int TAG; /* 8 */
  int msg_type, idjob, count, src, dest, tag; /* 4 * 6  = 24 */ 
  ProActive_Datatype pa_datatype; /* 4 */
  char * data; /* 1 */
  char method[MET_SIZE]; /* 128 */
  char data_backend[MSG_DATA_SIZE];
} msg_t ;

typedef struct _msg_split {	// to be put into common header file "javampi.h"
  long int TAG;
  int msg_type;  
  int data_length;
  char data[MSG_DATA_SIZE];
} splitted_msg_t ;

typedef struct _proactiveRequest {	// to be put into common header file
									// "javampi.h"
	  void *buf;
	  int flag; 
	  int idjob;
	  int count, src, pa_datatype, tag;
} ProActiveMPI_Request;


#define CONV_MPI_PROACTIVE_NULL ((ProActive_Datatype) 0)
#define CONV_MPI_PROACTIVE_CHAR ((ProActive_Datatype) 1)
#define CONV_MPI_PROACTIVE_UNSIGNED_CHAR ((ProActive_Datatype) 2)
#define CONV_MPI_PROACTIVE_BYTE ((ProActive_Datatype) 3)
#define CONV_MPI_PROACTIVE_SHORT ((ProActive_Datatype) 4)
#define CONV_MPI_PROACTIVE_UNSIGNED_SHORT ((ProActive_Datatype) 5)
#define CONV_MPI_PROACTIVE_INT ((ProActive_Datatype) 6)
#define CONV_MPI_PROACTIVE_UNSIGNED ((ProActive_Datatype) 7)
#define CONV_MPI_PROACTIVE_LONG ((ProActive_Datatype) 8)
#define CONV_MPI_PROACTIVE_UNSIGNED_LONG ((ProActive_Datatype) 9)
#define CONV_MPI_PROACTIVE_FLOAT ((ProActive_Datatype) 10)
#define CONV_MPI_PROACTIVE_DOUBLE ((ProActive_Datatype) 11)
#define CONV_MPI_PROACTIVE_LONG_DOUBLE ((ProActive_Datatype) 12)
#define CONV_MPI_PROACTIVE_LONG_LONG_INT ((ProActive_Datatype) 13)

/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+----- TODO MOVE TO IMPL -+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/

/*
int INTERVAL = 5 ;

char * path; 

char *daemon_address, *jobmanager, *myhostname;

#define RECV_QUEUE_SIZE 10
msg_t * recv_queue [RECV_QUEUE_SIZE];
int recv_queue_order [RECV_QUEUE_SIZE];
int msg_recv_nb = 1;

int C2S_Q_ID, S2C_Q_ID;
int sem_set_id_mpi;            // semaphore set ID.
int myJob=-1;
int myRank=-1;

int TAG_S_KEY;
int TAG_R_KEY;
FILE* mslog;
*/

extern FILE* mslog;
extern int myRank;

/*---+----+-----+----+----+-----+----+----+-----+----+-*/
/*---+----+----- UTILS FUNCTIONS  -+-----+- */
/*---+----+-----+----+----+-----+----+----+-----+----+-*/
void print_msg_t(FILE * f, msg_t * msg);
void print_splitted_msg_t(FILE * f, splitted_msg_t * msg);
void init_msg_t(msg_t * msg);
void init_splitted_msg_t(splitted_msg_t * msg);
int get_payload_size(msg_t * msg);
int get_data_payload_size (msg_t * msg);
int get_payload_size_splitted_msg(splitted_msg_t * msg);
int get_data_payload_size_splitted_msg(splitted_msg_t * msg);

//int get_mpi_buffer_length(int count, MPI_Datatype datatype, int byte_size);
int get_proactive_buffer_length(int count, ProActive_Datatype datatype);
//int same_MPI_Datatype(MPI_Datatype datatype1, MPI_Datatype datatype2, char * name1, char * name2);
int same_MPI_Datatype(MPI_Datatype datatype1, MPI_Datatype datatype2);
ProActive_Datatype type_conversion_MPI_to_proactive (MPI_Datatype datatype);
MPI_Datatype type_conversion_proactive_to_MPI (ProActive_Datatype datatype);
int debug_get_mpi_buffer_length(int count, MPI_Datatype datatype, int byte_size);

int is_splittable_msg(msg_t * recv_msg_buf);
void free_msg_t(msg_t * recv_msg_buf);
void free_msg_t_data_buffer(msg_t * recv_msg_buf);

int populate_splitted_message(splitted_msg_t * send_msg_buf, int msg_type, long int TAG, void * buf, int length);
int populate_msg(msg_t * send_msg_buf, int msg_type, long int TAG, void * buf, int count, MPI_Datatype datatype, int src, int dest, int tag, int idjob);
int send_to_ipc(int qid, int msg_type, long int TAG, void * buf, int count, MPI_Datatype datatype, int src, int dest, int tag, int idjob);
int send_ipc_message(int qid, msg_t * send_msg_buf);
int send_splitted_message(int qid, msg_t * send_msg_buf);
int send_raw_msg_to_ipc_queue(int id, void * send_msg_buf, int size);
int recv_ipc_message(int qid, long int tag, msg_t * recv_msg);
int recv_raw_msg_from_ipc_queue (int qid, long msg_type, void * recv_msg_buf,  int size);

// SEMAPHORE
void sem_unlock(int sem_set_id);
void sem_lock(int sem_set_id);
#endif /* COMMON_INTERNAL_API_H_ */
