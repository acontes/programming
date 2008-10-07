#! /bin/sh

TMP=/tmp

# /mnt/scratch is a tmpfs mount point for faster builds on schubby
if [ -w "/mnt/scratch" ] ; then
	TMP=/mnt/scratch
fi

PROACTIVE_DIR=$1
VERSION=$2
JAVA_HOME=$3

TMP_DIR=""

echo " [i] PROACTIVE_DIR: $PROACTIVE_DIR"
echo " [i] VERSION:       $VERSION"
echo " [i] JAVA_HOME:     $JAVA_HOME"
echo " [i] TMP:           $TMP"
function warn_and_exit {
	echo "$1" 1>&2
	exit 1
}

function warn_print_usage_and_exit {
	echo "$1" 1>&2
	echo "" 1>&2
	echo "Usage: $0 PROACTIVE_DIR VERSION JAVA_HOME" 1>&2
	exit 1
}


if [ -z "$PROACTIVE_DIR" ] ; then
	warn_print_usage_and_exit "PROACTIVE_DIR is not defined"
fi

if [ -z "$VERSION" ] ; then
	warn_print_usage_and_exit "VERSION is not defined"
fi

if [ -z "$JAVA_HOME" ] ; then
	warn_print_usage_and_exit "JAVA_HOME is not defined"
fi
export JAVA_HOME=${JAVA_HOME}


TMP_DIR="${TMP}/ProActive-${VERSION}"
output=$(mkdir ${TMP_DIR} 2>&1)
if [ "$?" -ne 0 ] ; then
	if [ -e ${TMP_DIR} ] ; then
		echo " [w] ${TMP_DIR} already exists. Delete it !"
		rm -Rf ${TMP_DIR}
		mkdir ${TMP_DIR}
		if [ "$?" -ne 0 ] ; then
			warn_and_exit "Cannot create ${TMP_DIR}: $output"
		fi
	else
		warn_and_exit "Cannot create ${TMP_DIR}"
	fi
fi

cp -Rf ${PROACTIVE_DIR} ${TMP_DIR}

cd ${TMP_DIR} || warn_and_exit "Cannot move in ${TMP_DIR}"
if [ "$(find src/ -name "*.java" | xargs grep serialVersionUID | wc -l)" -le 50 ] ; then
	if [ -z "${RELAX}" ] ; then
		warn_and_exit " [E] serialVersionUID are NOT defined"
	fi
fi


cd compile || warn_and_exit "Cannot move in compile"
./build clean
./build -Dversion="${VERSION}" deploy.all
./build -Dversion="${VERSION}" manualHtml manualPdf

cd ${TMP_DIR} || warn_and_exit "Cannot move in ${TMP_DIR}"
echo " [i] Clean"

# Subversion
find . -type d -a -name ".svn" -exec rm -Rf {} \;

# Git
rm -Rf .git

# Remove useless parts of ProActive
rm ./doc-src/ProActiveRefBook.doc
rm ./doc-src/guided_tour/examples/SimpleHelloWorld/lib/ProActive.jar
rm -Rf p2p/
rm -Rf lib/client.jar dist/lib/client.jar
find . -type f -a -name "*.svg" -exec rm {} \; # svg are converted in png by hands

# Remove non GPL stuff
rm -Rf ./compile/lib/clover.*

# Remove temporary files
rm compile/junit*properties
rm -Rf classes/
rm -Rf docs/tmp/
rm -Rf doc-src/*_snippets/

sed -i "s/{version}/$VERSION/" README.txt

# Documentation is broken, PROACTIVE-385
mv *.html docs/manual/ProActive_Manual_html/

tar cvfz ${TMP}/ProActive-${VERSION}.tar.gz ${TMP_DIR}
