#include<mpi.h>
#include <stdio.h>

void Coord_1D_to_3D(int global_rank, int dims_gw_x, int dims_gw_y,
		int dims_gw_z, int * coord_x, int * coord_y, int * coord_z) {
	int tmp_coord = 0;
	int i;
	int j;
	int k;

	for (i=0; i < dims_gw_x; i++) {
		for (j=0; j < dims_gw_y; j++) {
			for (k=0; k < dims_gw_z; k++) {
				if (global_rank == tmp_coord) {
					*coord_x = i;
					*coord_y = j;
					*coord_z = k;
					return;
				}
				tmp_coord++;
			}
		}
	}
}

int Coord_3D_to_1D_rank(int dims_gw_x, int dims_gw_y, int dims_gw_z,
		int coord_x, int coord_y, int coord_z) {
	int coord_1d = 0;
	int x, y, z;

	for (x=0; x < dims_gw_x; x++) {
		for (y=0; y < dims_gw_y; y++) {
			for (z=0; z < dims_gw_z; z++) {
				if ((coord_x == x) &&(coord_y == y)&&(coord_z == z)) {
					return coord_1d;
				}
				coord_1d++;
			}
		}
	}

	return -1;
}

void compute_global_coord_3d(int global_rank, int nex, int ney, int nez,
		int dims_gw_x, int dims_gw_y, int dims_gw_z, int * global_x,
		int * global_y, int * global_z) {

	Coord_1D_to_3D(global_rank, dims_gw_x, dims_gw_y, dims_gw_z, global_x,
			global_y, global_z);

	*global_x = *global_x * nex;
	*global_y = *global_y * ney;
	*global_z = *global_z * nez;
}

/**************************************************************************/

int isOnRightBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {
	return ((rank % dims_w_x) == (dims_w_x - 1));
}

int isOnLeftBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {

	return ((rank % dims_w_x) == 0);
}

int isOnUpperBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {
	int z = 0;
	int xy = dims_w_x;
	while (z < dims_w_z) {
		if ((rank >= (xy - dims_w_x)) && (rank < xy)) {
			return 1;
		}
		xy = xy + (dims_w_x * dims_w_y);
		z++;
	}
	return 0;
}

int isOnDownBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {
	int z = 0;
	int xy = (dims_w_x * dims_w_y);
	while (z < dims_w_z) {
		if ((rank >= (xy - dims_w_x)) && (rank < xy)) {
			return 1;
		}
		xy += (dims_w_x * dims_w_y);;
		z++;
	}
	return 0;
}

int isOnFrontBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {
	int xy = (dims_w_x * dims_w_y);
	if ((rank >= 0) && (rank < xy)) {
		return 1;
	}
	return 0;
}

int isOnRearBorder(int rank, int dims_w_x, int dims_w_y, int dims_w_z) {
	int xyz_min = (dims_w_x * dims_w_y * (dims_w_z - 1));
	int xyz_max = (dims_w_x * dims_w_y * dims_w_z);
	if ((rank >= xyz_min) && (rank < xyz_max)) {
		return 1;
	}
	return 0;
}

void mkpart(int cluster_rank, int worker_rank, int dims_c_x, int dims_c_y,
		int dims_c_z, int dims_w_x, int dims_w_y, int dims_w_z,
		int * cluster_neigh, int * worker_neigh) {

	// Number of clusters for each dimensions
	// int dims_c_x, dims_c_y, dims_c_z;

	// Number of workers for each dimensions inside each clusters
	// int dims_w_x, dims_w_y, dims_w_z;

	int right_c, left_c, up_c, down_c, front_c, rear_c;
	int right_w, left_w, up_w, down_w, front_w, rear_w;

	// right neighbour  x+1, y, z 
	if (isOnRightBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnRightBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			right_c = -1;
			right_w = -1;
		} else {
			right_c = cluster_rank + 1;
			right_w = worker_rank - (dims_w_x - 1);
		}
	} else {
		right_c = cluster_rank;
		right_w = worker_rank + 1;
	}
	// left neighbour  x-1, y, z 
	if (isOnLeftBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnLeftBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			left_c = -1;
			left_w = -1;
		} else {
			left_c = cluster_rank - 1;
			left_w = worker_rank + (dims_w_x - 1);
		}
	} else {
		left_c = cluster_rank;
		left_w = worker_rank - 1;
	}

	// up neighbour  x, y-1, z 
	if (isOnUpperBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnUpperBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			up_c = -1;
			up_w = -1;
		} else {
			up_c = cluster_rank - dims_w_x;
			up_w = (dims_w_x * dims_w_y) - (dims_w_x - worker_rank);
		}
	} else {
		up_c = cluster_rank;
		up_w = worker_rank - dims_w_x;
	}

	// down neighbour  x, y+1, z 
	if (isOnDownBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnDownBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			down_c = -1;
			down_w = -1;
		} else {
			down_c = cluster_rank + dims_c_x;
			down_w = dims_w_x - ((dims_w_x * dims_w_y) - worker_rank);
		}
	} else {
		down_c = cluster_rank;
		down_w = worker_rank + dims_w_x;
	}

	// front neighbour  x, y, z-1  
	if (isOnFrontBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnFrontBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			front_c = -1;
			front_w = -1;
		} else {
			front_c = cluster_rank - (dims_c_x * dims_c_y);
			front_w = (dims_w_x * dims_w_y * (dims_w_z - 1)) + worker_rank;
		}
	} else {
		front_c = cluster_rank;
		front_w = worker_rank - (dims_w_x * dims_w_y);
	}

	// rear neighbour  x, y, z+1  
	if (isOnRearBorder(worker_rank, dims_w_x, dims_w_y, dims_w_z) > 0) {
		if (isOnRearBorder(cluster_rank, dims_c_x, dims_c_y, dims_c_z) > 0) {
			rear_c = -1;
			rear_w = -1;
		} else {
			rear_c = cluster_rank + (dims_c_x * dims_c_y);
			rear_w = worker_rank - (dims_w_x * dims_w_y * (dims_w_z - 1));
		}
	} else {
		rear_c = cluster_rank;
		rear_w = worker_rank + (dims_w_x * dims_w_y);
	}

	cluster_neigh[0] = left_c;
	cluster_neigh[1] = right_c;
	cluster_neigh[2] = up_c;
	cluster_neigh[3] = down_c;
	cluster_neigh[4] = front_c;
	cluster_neigh[5] = rear_c;

	worker_neigh[0] = left_w;
	worker_neigh[1] = right_w;
	worker_neigh[2] = up_w;
	worker_neigh[3] = down_w;
	worker_neigh[4] = front_w;
	worker_neigh[5] = rear_w;
}

/**************************************************************************/

