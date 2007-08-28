#!/bin/sh


if [ $# -lt 1 ]
then
		  echo "Usage: ./cleanup.sh <nodefile>"
		  exit -1
fi


for i in `cat $1`
do
	    echo "cleaning IPC queues in $i"
		ssh $i killall java
	        ssh $i killall rmiregistry

	  	for j in `ssh $i ipcs -s | grep -v Mess | grep -v key | grep -v Sem | grep -v Sha | sed '/^$/d' | cut -d" " -f2`
		do
			  echo "Freeing shared_mem $j in $i"
			  ssh $i ipcrm -s $j 2>/dev/null
		done

		for j in `ssh $i ipcs -q | grep -v Mess | grep -v key | grep -v Sem | grep -v Sha | sed '/^$/d' | cut -d" " -f2`
	        do
        	   echo "Deleting message $j in $i"
	           ssh $i ipcrm -q $j 2>/dev/null
	        done

		for j in `ssh $i ipcs -m | grep -v Mess | grep -v key | grep -v Sem | grep -v Sha | sed '/^$/d' | cut -d" " -f2`
	        do
          	   echo "Deleting semaphore $j in $i"
	           ssh $i ipcrm -s $j 2>/dev/null
		done

		#for j in `ssh $i ps -aux 2>/dev/null | grep emathias | grep -v oar | cut -d" " -f6  | sort | uniq`
		#do
	  	  #echo "Killing pid $j in $i"
		  		#done

		echo
		echo

done






