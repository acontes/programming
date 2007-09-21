#-----Macros---------------------------------
INCLUDES = -I. -I /usr/include

# set up compiler and options
PROACTIVE=../../../../../../../Core/org/objectweb/proactive/mpi/control/config/src
CXX=${MPI_HOME}/bin/mpich-1.2.7p1/bin/mpicc
CXXFLAGS= -lm -O3 -g -Wall

all: prepare compile

# TODO: get a .o instead
prepare:
	echo "Getting ProActiveMPI.* TODO get .o instead "
	cp ${PROACTIVE}/ProActiveMPI.h .
	cp ${PROACTIVE}/ProActiveMPI.c .
	cp ${PROACTIVE}/CommonInternalApi.h .
	cp ${PROACTIVE}/CommonInternalApi.c .


#-----Suffix Rules---------------------------
# set up C suffixes and relationship between .c and .o files
compile: $(OBJ)
	$(CXX) $(CXXFLAGS) $(OBJ) -o proactive_poisson3D_cubic

.SUFFIXES: .c

.c.o:
	$(CXX) $(CXXFLAGS) -c $<

.c :
	$(CXX) $(CXXFLAGS) $< -o $@

#-----File Dependencies----------------------

SRC = main-mpi.c matvec.c mkpart.c setrhs.c exchN1.c ProActiveMPI.c CommonInternalApi.c
OBJ = $(SRC:.o=.c)

#-----Other stuff----------------------------
#depend:
#	makedepend $(CXXFLAGS) -Y $(SRC)

clean:
	rm proactive_poisson3D_cubic
	rm -f *.o
	rm ProActiveMPI.* CommonInternalApi.*
