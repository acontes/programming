#!/bin/sh

echo
echo --- N-body with ProActive ---------------------------------

. $PROACTIVE_HOME/scripts/unix/env.sh

export XMLDESCRIPTOR=descriptors/ApplicationNBody.xml

$JAVACMD -DNB_WORKERS="$NB_WORKERS"  org.objectweb.proactive.examples.nbody.common.Start  $XMLDESCRIPTOR "$@"

echo
echo ---------------------------------------------------------
