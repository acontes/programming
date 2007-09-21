#include <stdio.h>
#include "common.h"

void setrhs(int myid, double * vecF, double * solF, 
				int nx, int ny, int nz, int global_x, int global_y, int global_z, 
				double hx, double hy, double hz) {
//---------------------------------------------------------------------c
//     Initialisation du second membre et de la solution exacte
//---------------------------------------------------------------------c
      
// solF(nxmax,nymax,nzmax), 
// vecF(nxmax,nymax,nzmax)

//      INTEGER corpc1(3), corpc2(3)
  	int ig, jg, kg;
  	double xx, yy, zz;
	int i,j,k;
   	
	for (i = 1; i <= nx; i++) {
		for (j = 1; j <= ny; j++) {
			for (k = 1; k <= nz; k++) {
 			   ig = i + global_x;
               jg = j + global_y;
               kg = k + global_z;
//               if (myid == 0) {printf("(%d,%d,%d) -> (%d,%d,%d)\n",i,j,k,ig,jg,kg);}
	       /*
				if(myid == 1) {
				printf ("1 %d, %d, %d\n", ig, jg, kg);	
				}      
				               
				if(myid == 2) {
				printf ("2 %d, %d, %d\n", ig, jg, kg);	
				}               
				if(myid == 3) {
				printf ("3 %d, %d, %d\n", ig, jg, kg);	
				}      
				if (myid == 4) {
				printf ("4 %d, %d, %d\n", ig, jg, kg);	
				}      */
               xx = ((double)ig)*hx;
               yy = ((double)jg)*hy;
               zz = ((double)kg)*hz;

               ARRAY(vecF,i,j,k,ny,nz) =-2.0*((xx*(xx - 1.0) + yy*(yy - 1.0))* zz*(zz - 1.0) + xx*(xx - 1.0)*yy*(yy - 1.0));
               ARRAY(solF,i,j,k,ny,nz) = xx*yy*zz*(xx - 1.0)*(yy - 1.0)*(zz - 1.0);
	  		}
		}
	}
}
