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
package org.objectweb.proactive.extra.multiactivenpb.sp;
import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class RHSAdder extends SPBase{
  public int id;
  public boolean done = true;

  //private data
  int lower_bound, upper_bound;
  
  public RHSAdder(SP sp,int low, int high){
    Init(sp);
    lower_bound=low;
    upper_bound=high;
    setPriority(Thread.MAX_PRIORITY);
    setDaemon(true);
    master=sp;
  }
  void Init(SP sp){
    //initialize shared data
    IMAX=sp.IMAX;
    JMAX=sp.JMAX; 
    KMAX=sp.KMAX; 
    problem_size=sp.problem_size; 
    nx2=sp.nx2;
    ny2=sp.ny2;
    nz2=sp.nz2;
    grid_points=sp.grid_points;
    niter_default=sp.niter_default;
    dt_default=sp.dt_default;    
    u=sp.u;
    rhs=sp.rhs;
    forcing=sp.forcing;
    isize1=sp.isize1;
    jsize1=sp.jsize1;
    ksize1=sp.ksize1;
    us=sp.us;
    vs=sp.vs;
    ws=sp.ws;
    qs=sp.qs;
    rho_i=sp.rho_i;
    speed=sp.speed;
    square=sp.square;
    jsize2=sp.jsize2;
    ksize2=sp.ksize2;
    ue=sp.ue;
    buf=sp.buf;
    jsize3=sp.jsize3;
    lhs=sp.lhs;
    lhsp=sp.lhsp;
    lhsm=sp.lhsm;
    jsize4=sp.jsize4;
    cv=sp.cv;
    rhon=sp.rhon;
    rhos=sp.rhos;
    rhoq=sp.rhoq;
    cuf=sp.cuf;
    q=sp.q;
    ce=sp.ce;
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
        step();
        synchronized(master){done = true;master.notify();}
      }
    }  
  }

  public void step(){  
    int i, j, k, m;
    for(k=lower_bound;k<=upper_bound;k++){
      for(j=1;j<=ny2;j++){
	for(i=1;i<=nx2;i++){
	  for(m=0;m<=4;m++){
	    u[m+i*isize1+j*jsize1+k*ksize1] = u[m+i*isize1+j*jsize1+k*ksize1] + 
	      rhs[m+i*isize1+j*jsize1+k*ksize1];
	  }
	}
      }
    }    
  }
}
