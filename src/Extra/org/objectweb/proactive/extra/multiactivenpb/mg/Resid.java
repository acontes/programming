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
package org.objectweb.proactive.extra.multiactivenpb.mg;

import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class Resid extends MGBase{
  public int id;
  public boolean visr;
  public boolean done=true;
  
  public int n1, n2, n3;
  public int off;
  
  int state=0;
  int start,end,work;	      
  double u1[],u2[];

  public Resid(MG mg){
    Init(mg);
    u1=new double[nm+1];
    u2=new double[nm+1];
    setPriority(Thread.MAX_PRIORITY);
    setDaemon(true);
    master=mg;
  }
  void Init(MG mg){
    //initialize shared data
    num_threads=mg.num_threads;
    r=mg.r;
    v=mg.v;
    u=mg.u;
    a=mg.a;
    nm=mg.nm;
  }

  public void run(){
      for(;;){
        synchronized(this){
          while(done==true){
              try{
        	  wait();
              synchronized(master){master.notify();}
              }catch(InterruptedException ie){}
          }
          GetWork();
          step();
          synchronized(master){done=true; master.notify();}
        }
      }       
  }

    public void step(){
      int i3, i2, i1;
      if(work==0) return;
      double tmp[]=v;
      if(visr) tmp=r;
      for(i3=start;i3<=end;i3++)
         for(i2=1;i2<n2-1;i2++){
            for(i1=0;i1<n1;i1++){
               u1[i1] = u[off+i1+n1*(i2-1+n3*i3)] + u[off+i1+n1*(i2+1+n3*i3)]
                      + u[off+i1+n1*(i2+n3*(i3-1))] + u[off+i1+n1*(i2+n3*(i3+1))];
               u2[i1] = u[off+i1+n1*(i2-1+n3*(i3-1))] + u[off+i1+n1*(i2+1+n3*(i3-1))]
                      + u[off+i1+n1*(i2-1+n3*(i3+1))] + u[off+i1+n1*(i2+1+n3*(i3+1))];
            }
            for(i1=1;i1<n1-1;i1++){
               r[off+i1+n1*(i2+n3*i3)] = 
	       tmp[off+i1+n1*(i2+n3*i3)]
                           - a[0] * u[off+i1+n1*(i2+n3*i3)]
//c---------------------------------------------------------------------
//c  Assume a(1) = 0      (Enable 2 lines below if a(1) not= 0)
//c---------------------------------------------------------------------
//c    >                     - a[1] * ( u(i1-1,i2,i3) + u(i1+1,i2,i3)
//c    >                              + u1(i1) )
//c---------------------------------------------------------------------
                           - a[2] * ( u2[i1] + u1[i1-1] + u1[i1+1] )
                           - a[3] * ( u2[i1-1] + u2[i1+1] );
            }
         }
//c---------------------------------------------------------------------
//c     exchange boundary data
//c---------------------------------------------------------------------
      for(i3=start;i3<=end;i3++)
         for(i2=1;i2<n2-1;i2++){
            r[off+n1*(i2+n2*i3)] = r[off+n1-2+n1*(i2+n2*i3)];
            r[off+n1-1+n1*(i2+n2*i3)] = r[uoff+1+n1*(i2+n2*i3)];
         }

      for(i3=start;i3<=end;i3++)
         for(i1=0;i1<n1;i1++){
            r[off+i1+n1*n2*i3] = r[off+i1+n1*(n2-2+n2*i3)];
            r[off+i1+n1*(n2-1+n2*i3)] = r[off+i1+n1*(1+n2*i3)];
         }
    }
    
    private void GetWork(){
      int workpt=(wend-wstart)/num_threads;
      int remainder=wend-wstart-workpt*num_threads;
      if(workpt==0){
        if(id<=wend-wstart){
	  work=1;
	  start=end=wstart+id;
	}else{
	  work=0;
	}
      }else{
        if(id<remainder){
	  workpt++;
          start=wstart+workpt*id;
	  end=start+workpt-1;
	  work=workpt;
	}else{	
          start=wstart+remainder+workpt*id;
	  end=start+workpt-1;
	  work=workpt;
	}
      }
    }
}
