#!/bin/sh

echo
echo --- Security intent example ---------------------------------------------
echo ---
echo --- This example shows how to do data transfer encryption with SCA intent feature
echo ---

workingDir=`dirname $0`
. ${workingDir}/../env.sh

if [ $# -lt 0 ]; then
    echo "
       usage :
         securityIntents.sh
    "
    exit 1
fi

JAVACMD=$JAVACMD" -Dsca.provider=org.objectweb.proactive.extensions.sca.SCAFractive"

$JAVACMD org.objectweb.proactive.examples.components.sca.securityintent.Main


echo
echo ---------------------------------------------------------
