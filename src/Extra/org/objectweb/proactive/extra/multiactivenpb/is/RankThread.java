/*
	NAS PARALLEL BENCHMARKS -- MULTI-ACTIVE OBJECT IMPLEMENTATION
	
	This benchmark is based on the original multi-threaded NPB implementation.
	
!	Information on NPB 3.0, including the Technical Report NAS-02-008	  !
!    "Implementation of the NAS Parallel Benchmarks in Java",		  !
!    original specifications, source code, results and information	  !
!    on how to submit new results, is available at:			  !
!									  !
!	    http://www.nas.nasa.gov/Software/NPB/	
!

	Author: Zsolt Istvan (zsolt.istvan@gmx.net) 
 */
package org.objectweb.proactive.extra.multiactivenpb.is;

import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class RankThread extends ISBase{
  public int id;
  protected int local_hist[];  
  int start, end;	  
  int rstart, rend;
  
  public boolean done=true;
  public static int iteration=0;
  public int state;

  public RankThread(IS is, int Id,int s1, int e1, int s2, int e2){
    Init(is);
    master=is;
    id=Id;
    start=s1;
    end=e1;
    rstart=s2;
    rend=e2;
    local_hist = new int[MAX_KEY];
    state=0;
    setPriority(Thread.MAX_PRIORITY);
    setDaemon(true);
  }
  void Init(IS is){
    //initialize shared data
    num_threads=is.num_threads;
    MAX_KEY=is.MAX_KEY;

    key_array=is.key_array;
    test_index_array=is.test_index_array;
    master_hist=is.master_hist;
    partial_verify_vals=is.partial_verify_vals;
 }
 
  public void run(){
      for(;;){
        synchronized(this){
          while(done==true){
        	try{
        	  wait();
        	  synchronized(master){ master.notify();}
        	}catch(InterruptedException ie){}
          }
          switch(state){
          case 0:
              step1();
              state=1;
              break;
          case 1:
              step2();
              state=0;
              break;
          }
  	  synchronized(master){done=true;master.notify();}
        }
      }       
  }

  protected synchronized void step1(){
    key_array[iteration] = iteration;
    key_array[iteration+MAX_ITERATIONS] = MAX_KEY - iteration;
    for(int i=0; i<TEST_ARRAY_SIZE; i++ ){
       partial_verify_vals[i] = key_array[test_index_array[i]]; 
    }
    	    
    for(int i=0;i<MAX_KEY;i++) local_hist[i]=0;
    for(int i=start; i<=end; i++ ) local_hist[key_array[i]]++;  
    for(int i=0; i<MAX_KEY-1; i++ ) local_hist[i+1] += local_hist[i];  
  }
  
  public void step2(){
    //Parallel calculation of the master's histogram
    for(int i=rstart;i<=rend;i++){
      for(int j=0;j<num_threads;j++){
    	master_hist[i]+=rankthreads[j].local_hist[i];
      }
    }	    
  }
}
