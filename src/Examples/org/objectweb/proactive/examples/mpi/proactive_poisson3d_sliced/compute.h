#ifndef COMPUTE_H_
#define COMPUTE_H_

void matvec(double * vecX, double * vecY, int nx, int ny, int nz, double rhx2, double rhy2, double rhz2);

void setrhs(int myid, double * vecF, double * solF, 
				int nx, int ny, int nz, int n1x, int n1y, int n1z, 
				double hx, double hy, double hz);
     		
#endif /*COMPUTE_H_*/
