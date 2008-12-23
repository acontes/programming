#!/bin/sh
# ----------------------------------------------------------------------------
#
# This variable should be set to the directory where is installed PROACTIVE
#

CLASSPATH=.

# User envrionment variable
if [ ! -z "$PROACTIVE_HOME" ] ; then
	PROACTIVE=$PROACTIVE_HOME
fi


# Internal ProActive scripts can override $PROACTIVE
if [ -z "$PROACTIVE" ]
then
	workingDir=`dirname $0`
       PROACTIVE=$(cd $workingDir/../.././ || (echo "Broken PROACTIVE installation" ; exit 1) && echo $PWD)
fi


# ----------------------------------------------------------------------------


JAVA_HOME=${JAVA_HOME-NULL};
if [ "$JAVA_HOME" = "NULL" ]
then
echo
echo "The enviroment variable JAVA_HOME must be set the current jdk distribution"
echo "installed on your computer."
echo "Use "
echo "    export JAVA_HOME=<the directory where is the JDK>"
exit 127
fi

# ----
# Set up the classpath using classes dir or jar files
#

if [ -d $PROACTIVE/classes ]
then
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/Core
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/Extensions
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/Extra
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/Examples
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/Benchmarks
    CLASSPATH=$CLASSPATH:$PROACTIVE/classes/EC2
    for i in $PROACTIVE/lib/*.jar ; do
      CLASSPATH=$CLASSPATH:$i
    done
    for i in $PROACTIVE/lib/amazon-ec2/third-party/*/*.jar ; do
      CLASSPATH=$CLASSPATH:$i
    done
else
    CLASSPATH=$CLASSPATH:$PROACTIVE/dist/lib/ProActive.jar
    CLASSPATH=$CLASSPATH:$PROACTIVE/dist/lib/ProActive_examples.jar
fi

#echo "CLASSPATH"=$CLASSPATH
export CLASSPATH

#    -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8066 \

JAVACMD=$JAVA_HOME/bin/java"\
	-Djava.security.manager \
	-Djava.security.policy=$PROACTIVE/examples/proactive.java.policy \
	-Djava.endorsed.dirs=${PROACTIVE}/lib/amazon-ec2/third-party/jaxb-ri-2.1 \
	-Dlog4j.configuration=file:${PROACTIVE}/examples/proactive-log4j \
	-Dec2.accessKeyId=0F326N2S93BJQBH1VDR2 \
    -Dec2.secretAccessKey=OuwVaTRRbf1cZG71pPq3QrnhqxhVn6Cv5A1uhthz \
	-Dproactive.communication.protocol=http \
	-Dproactive.http.port=8080 \
	-Dproactive.home=$PROACTIVE \
	-Dos=unix"

export PROACTIVE
export JAVACMD
