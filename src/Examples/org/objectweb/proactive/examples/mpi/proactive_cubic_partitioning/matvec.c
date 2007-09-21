#include "common.h"

void matvec(double * vecX, double * vecY, int nx, int ny, int nz, double rhx2, double rhy2, double rhz2) {
//---------------------------------------------------------------------
//     Calcul du produit matrice/vecteur vecY = A*vecX                 
//     La matrice A n'est pas stockee et le produit matrice/vecteur    
//     est deroule                                                     
//---------------------------------------------------------------------

//---- Parametres d'appel 

// double * vecX(0:nxmax+1,0:nymax+1,0:nzmax+1) 
// double * vecY(0:nxmax+1,0:nymax+1,0:nzmax+1)
      
//---------------------------------------------------------------------
//---- Variables locales 
      double dcoef = 2.0*(rhx2 + rhy2 + rhz2);

//---- On suppose ici que les valeurs de vecX aux points dans les bandes 
//     interface (i=0 et i=nx+1), (j=0 et j=ny+1) et (k=0 et k=nz+1) 
//     ont ete mis a jour par echange de valeurs entre sous-domaines 
//     voisins (sauf pour les frontieres physiques du domaine de calcul)
	int i,j,k;
for (i = 1; i <= nx; i++) {
	for (j = 1; j <= ny; j++) {
		for (k = 1; k <= nz; k++) {
              ARRAY(vecY,i,j,k,ny,nz) =
              	 	dcoef*ARRAY(vecX,i,j,k,ny,nz)
              	 - rhx2*(ARRAY(vecX,i-1,j,k,ny,nz) + ARRAY(vecX,i+1,j,k,ny,nz))
              	 - rhy2*(ARRAY(vecX,i,j-1,k,ny,nz) + ARRAY(vecX,i,j+1,k,ny,nz))
              	 - rhz2*(ARRAY(vecX,i,j,k-1,ny,nz) + ARRAY(vecX,i,j,k+1,ny,nz));
  		}
	}
}
}

