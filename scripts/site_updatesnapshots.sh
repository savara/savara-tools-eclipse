#!/bin/bash
CURDIR=`pwd`
ASSEMBLY=$CURDIR/$1/site_assembly.zip

echo Base directory=$CURDIR/$1

rm -f $ASSEMBLY

VERSION=v`date +%Y%m%d%H%M`

for i in `ls $CURDIR/$1/site/plugins/*.SNAPSHOT.jar`;
        do
                #echo File: $i
		./site_updatejarver.sh $i $VERSION
        done  

echo Repackage site assembly

pushd $1/site
zip -qr $ASSEMBLY *

popd
