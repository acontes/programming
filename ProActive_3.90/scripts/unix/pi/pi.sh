#!/bin/sh

workingDir=.

PROACTIVE=$workingDir/../../../.
. $PROACTIVE/scripts/unix/env.sh


$JAVACMD -Xmx256000000 -classpath $JAVA_HOME/lib/tools.jar:$PROACTIVE/compile/ant.jar:$PROACTIVE/compile/ant-launcher.jar:$PROACTIVE/lib/ws/xml-apis.jar:$PROACTIVE/lib/xercesImpl.jar org.apache.tools.ant.Main -buildfile $PROACTIVE/src/org/objectweb/proactive/examples/pi/scripts/build.xml "$@"


