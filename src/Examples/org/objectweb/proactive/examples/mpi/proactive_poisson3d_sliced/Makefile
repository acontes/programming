#-----Macros---------------------------------
INCLUDES = -I. -I /usr/include

# set up compiler and options
CXX=${HOME}/bin/mpicc
CXXFLAGS= -lm -O3 -g -Wall


all: prepare compile

prepare:
	echo "Getting ProActiveMPI.* TODO get .o instead "
	cp ../../../../../../../Core/org/objectweb/proactive/mpi/control/config/src/ProActiveMPI.h .
	cp ../../../../../../../Core/org/objectweb/proactive/mpi/control/config/src/ProActiveMPI.c .
	cp ../../../../../../../Core/org/objectweb/proactive/mpi/control/config/src/CommonInternalApi.h .
	cp ../../../../../../../Core/org/objectweb/proactive/mpi/control/config/src/CommonInternalApi.c .


#-----Suffix Rules---------------------------
# set up C suffixes and relationship between .c and .o files
compile: $(OBJ)
	$(CXX) $(CXXFLAGS) $(OBJ) -o proactive_poisson3D_Sliced

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
	rm proactive_poisson3D_Sliced
	rm -f *.o
	rm ProActiveMPI.* CommonInternalApi.*
