#!/bin/bash
JARDIR=`dirname $1`
JARNAME=`basename $1`

VERSION=$2

OLDVERSION=`echo $JARNAME | sed 's/.*_\(.*\).jar.*/\1/'`
NEWVERSION=`echo $OLDVERSION | sed 's/\(.*\)SNAPSHOT/\1/'`$VERSION

echo Changing version from $OLDVERSION to $NEWVERSION

WORKINGDIR=$JARDIR/tmp-$JARNAME

# Unpack jar
unzip -qd $WORKINGDIR $1

pushd $WORKINGDIR

sed -i "s/Bundle-Version: $OLDVERSION/Bundle-Version: $NEWVERSION/g" META-INF/MANIFEST.MF

NEWJARNAME=`echo $JARNAME | sed "s/$OLDVERSION/$NEWVERSION/g"`

zip -qr $JARDIR/$NEWJARNAME *

popd

# Remove working dir
rm -r $WORKINGDIR

# Remove old jar
rm -f $1

