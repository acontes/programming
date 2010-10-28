#!/bin/sh

echo
echo --- Currency SMS example ---------------------------------------------
echo ---
echo --- An Orange API Access Key is needed to run this example.
echo --- See http://api.orange.com/en/api/sms-api,1 for more informations about Orange SMS API.
echo ---

workingDir=`dirname $0`
. ${workingDir}/../env.sh

if [ $# -lt 2 ]; then
    echo "
       usage :
         currencysms.sh [Orange_API_Access_Key] [Destination_Number]
    "
    exit 1
fi

JAVACMD=$JAVACMD" -Dsca.provider=org.objectweb.proactive.extensions.sca.SCAFractive"

$JAVACMD org.objectweb.proactive.examples.components.sca.currencysms.Main "$@"


echo
echo ---------------------------------------------------------
