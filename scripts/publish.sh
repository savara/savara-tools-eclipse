#!/bin/bash
# Hudson script used to publish Tycho-built p2 update sites (copied from JBossTools build/publish.sh)
# NOTE: sources MUST be checked out into ${WORKSPACE}/sources 

# to use timestamp when naming dirs instead of ${BUILD_ID}-H${BUILD_NUMBER}, use:
# BUILD_ID=2010-08-31_19-16-10; timestamp=$(echo $BUILD_ID | tr -d "_-"); timestamp=${timestamp:0:12}; echo $timestamp; # 201008311916

# where to create the stuff to publish
STAGINGDIR=${WORKSPACE}/results/${JOB_NAME}

# https://jira.jboss.org/browse/JBIDE-6956 "jbosstools-3.2.0.M2" is too verbose, use "3.2.0.M2" instead
JOBNAMEREDUX=${JOB_NAME/.aggregate}; JOBNAMEREDUX=${JOBNAMEREDUX/jbosstools-}

# releases get named differently than snapshots
if [[ ${RELEASE} == "Yes" ]]; then
	ZIPSUFFIX="${BUILD_ID}-H${BUILD_NUMBER}"
else
	ZIPSUFFIX="SNAPSHOT"
fi

# define target update zip filename
SNAPNAME="${JOB_NAME}-Update-${ZIPSUFFIX}.zip"
# define target sources zip filename
SRCSNAME="${JOB_NAME}-Sources-${ZIPSUFFIX}.zip"
# define suffix to use for additional update sites
SUFFNAME="-Update-${ZIPSUFFIX}.zip"

DESTINATION="savara@filemgmt.jboss.org:/downloads_htdocs/savara/tmp"
RSYNC_OPTIONS="-arzq --delete"
#RSYNC_OPTIONS="-arzv --protocol=28 --delete"

# cleanup from last time
rm -fr ${WORKSPACE}/results; mkdir -p ${STAGINGDIR}

siteZip=${WORKSPACE}/sources/site/target/site_assembly.zip
if [[ ! -f ${WORKSPACE}/sources/site/target/site_assembly.zip ]]; then
	siteZip=${WORKSPACE}/sources/site/target/site.zip
fi
z=$siteZip

# note the job name, build number, SVN rev, and build ID of the latest snapshot zip
mkdir -p ${STAGINGDIR}/logs
bl=${STAGINGDIR}/logs/BUILDLOG.txt
rm -f ${bl}; wget -q http://hudson.qa.jboss.com/hudson/job/${JOB_NAME}/${BUILD_NUMBER}/consoleText -O ${bl} --timeout=900 --wait=10 --random-wait --tries=10 --retry-connrefused --no-check-certificate

# JBDS-1361 - fetch XML and then sed it into plain text
rl=${STAGINGDIR}/logs/SVN_REVISION
rm -f ${rl}.txt ${rl}.xml; wget -O ${rl}.xml "http://hudson.qa.jboss.com/hudson/job/${JOB_NAME}/api/xml?wrapper=changeSet&depth=1&xpath=//build[1]/changeSet/revision" --timeout=900 --wait=10 --random-wait --tries=30 --retry-connrefused --no-check-certificate --server-response
sed -e "s#<module>\(http[^<>]\+\)</module><revision>\([0-9]\+\)</revision>#\1\@\2\n#g" ${rl}.xml | sed -e "s#<[^<>]\+>##g" > ${rl}.txt 


METAFILE="${BUILD_ID}-H${BUILD_NUMBER}.txt"
touch ${STAGINGDIR}/logs/${METAFILE}
METAFILE=build.properties

echo "JOB_NAME = ${JOB_NAME}" >> ${STAGINGDIR}/logs/${METAFILE}
echo "BUILD_NUMBER = ${BUILD_NUMBER}" >> ${STAGINGDIR}/logs/${METAFILE}
echo "BUILD_ID = ${BUILD_ID}" >> ${STAGINGDIR}/logs/${METAFILE}
echo "WORKSPACE = ${WORKSPACE}" >> ${STAGINGDIR}/logs/${METAFILE}
echo "HUDSON_SLAVE = $(uname -a)" >> ${STAGINGDIR}/logs/${METAFILE}
echo "RELEASE = ${RELEASE}" >> ${STAGINGDIR}/logs/${METAFILE}
echo "ZIPSUFFIX = ${ZIPSUFFIX}" >> ${STAGINGDIR}/logs/${METAFILE}

#echo "$z ..."
if [[ $z != "" ]] && [[ -f $z ]] ; then
	# unzip into workspace for publishing as unpacked site
	mkdir -p ${STAGINGDIR}/all/repo
	unzip -u -o -q -d ${STAGINGDIR}/all/repo $z

	# generate MD5 sum for zip (file contains only the hash, not the hash + filename)
        for m in $(md5sum ${z}); do if [[ $m != ${z} ]]; then echo $m > ${z}.MD5; fi; done

	# copy into workspace for access by bucky aggregator (same name every time)
	rsync -aq $z ${STAGINGDIR}/all/${SNAPNAME}
	rsync -aq ${z}.MD5 ${STAGINGDIR}/all/${SNAPNAME}.MD5
fi
z=""

# create sources zip
pushd ${WORKSPACE}/sources
mkdir -p ${STAGINGDIR}/all
if [[ ${JOB_NAME/.aggregate} != ${JOB_NAME} ]] && [[ -d ${WORKSPACE}/sources/aggregate/site/zips ]]; then
	srczipname=${SRCSNAME/-Sources-/-Additional-Sources-}
else
	srczipname=${SRCSNAME}
fi
zip ${STAGINGDIR}/all/${srczipname} -q -r * -x hudson_workspace\* -x documentation\* -x download.jboss.org\* -x requirements\* \
  -x workingset\* -x labs\* -x build\* -x \*test\* -x \*target\* -x \*.class -x \*.svn\* -x \*classes\* -x \*bin\* -x \*.zip \
  -x \*docs\* -x \*reference\* -x \*releng\* -x \*.git\* -x \*/lib/\*.jar
popd
z=${STAGINGDIR}/all/${srczipname}; for m in $(md5sum ${z}); do if [[ $m != ${z} ]]; then echo $m > ${z}.MD5; fi; done

mkdir -p ${STAGINGDIR}/logs

# generate list of zips in this job
METAFILE=zip.list.txt
echo "ALL_ZIPS = \\" >> ${STAGINGDIR}/logs/${METAFILE}
for z in $(find ${STAGINGDIR} -name "*Update*.zip") $(find ${STAGINGDIR} -name "*Sources*.zip"); do
	# list zips in staging dir
	echo "${z##${STAGINGDIR}/},\\"  >> ${STAGINGDIR}/logs/${METAFILE}
done
echo ""  >> ${STAGINGDIR}/logs/${METAFILE}

# generate md5sums in a single file 
md5sumsFile=${STAGINGDIR}/logs/md5sums.txt
echo "# Update Site Zips" > ${md5sumsFile}
echo "# ----------------" >> ${md5sumsFile}
md5sum $(find . -name "*Update*.zip" | egrep -v "aggregate-Sources|nightly-Update") >> ${md5sumsFile}
echo "  " >> ${md5sumsFile}
echo "# Source Zips" >> ${md5sumsFile}
echo "# -----------" >> ${md5sumsFile}
md5sum $(find . -name "*Source*.zip" | egrep -v "aggregate-Sources|nightly-Update") >> ${md5sumsFile}
echo " " >> ${md5sumsFile}

# TODO: JBIDE-7045 this is obsolete - replace it with xslt'd transform of build.properties.all.xml (agg site overall metadata)
# generate HTML snippet, download-snippet.html, for inclusion on jboss.org
# TODO: currently broken; see https://issues.jboss.org/browse/JBIDE-7444
#      [xslt] : Error! Error checking type of the expression 'funcall(replace, [funcall(substring-after, [cast(step("attribute", 17), string), literal-expr(jbosstools-)]), literal-expr(_stable_branch), literal-expr()])'.
#     [xslt] : Fatal Error! Could not compile stylesheet

mkdir -p ${STAGINGDIR}/logs
#ANT_PARAMS=" -DZIPSUFFIX=${ZIPSUFFIX} -DJOB_NAME=${JOB_NAME} -Dinput.dir=${STAGINGDIR} -Doutput.dir=${STAGINGDIR}/logs -DWORKSPACE=${WORKSPACE}"
	# no longer using upstream continuous or nightly build in aggregation
	#if [[ ${JOB_NAME/.aggregate} != ${JOB_NAME} ]]; then # reuse snippet from upstream build
	#	ANT_PARAMS="${ANT_PARAMS} -Dtemplate.file=http://download.jboss.org/jbosstools/builds/staging/${JOB_NAME/.aggregate/.continuous}/logs/download-snippet.html"
	#fi
#for buildxml in ${WORKSPACE}/build/results/build.xml ${WORKSPACE}/sources/build/results/build.xml ${WORKSPACE}/sources/results/build.xml; do
#	if [[ -f ${buildxml} ]]; then
#		ANT_SCRIPT=${buildxml}
#	fi
#done
#ant -f ${ANT_SCRIPT} ${ANT_PARAMS}

# ${bl} is full build log; see above
mkdir -p ${STAGINGDIR}/logs
# filter out Maven test failures
fl=${STAGINGDIR}/logs/FAIL_LOG.txt
# ignore warning lines and checksum failures
sed -ne "/\[WARNING\]\|CHECKSUM FAILED/ ! p" ${bl} | sed -ne "/<<< FAI/,+9 p" | sed -e "/AILURE/,+9 s/\(.\+AILURE.\+\)/\n----------\n\n\1/g" > ${fl}
sed -ne "/\[WARNING\]\|CHECKSUM FAILED/ ! p" ${bl} | sed -ne "/ FAI/ p" | sed -e "/AILURE \[/ s/\(.\+AILURE \[.\+\)/\n----------\n\n\1/g" >> ${fl}
sed -ne "/\[WARNING\]\|CHECKSUM FAILED/ ! p" ${bl} | sed -ne "/ SKI/ p" | sed -e "/KIPPED \[/ s/\(.\+KIPPED \[.\+\)/\n----------\n\n\1/g" >> ${fl}
fc=$(sed -ne "/FAI\|LURE/ p" ${fl} | wc -l)
if [[ $fc != "0" ]]; then
	echo "" >> ${fl}; echo -n "FAI" >> ${fl}; echo -n "LURES FOUND: "$fc >> ${fl};
fi 
fc=$(sed -ne "/KIPPED/ p" ${fl} | wc -l)
if [[ $fc != "0" ]]; then
	echo "" >> ${fl}; echo -n "SKI" >> ${fl}; echo -n "PS FOUND: "$fc >> ${fl};
fi 
el=${STAGINGDIR}/logs/ERRORLOG.txt
# ignore warning lines and checksum failures
sed -ne "/\[WARNING\]\|CHECKSUM FAILED/ ! p" ${bl} | sed -ne "/<<< ERR/,+9 p" | sed -e "/RROR/,+9 s/\(.\+RROR.\+\)/\n----------\n\n\1/g" > ${el}
sed -ne "/\[WARNING\]\|CHECKSUM FAILED/ ! p" ${bl} | sed -ne "/\[ERR/,+2 p"   | sed -e "/ROR\] Fai/,+2 s/\(.\+ROR\] Fai.\+\)/\n----------\n\n\1/g" >> ${el}
ec=$(sed -ne "/ERR\|RROR/ p" ${el} | wc -l) 
if [[ $ec != "0" ]]; then
	echo "" >> ${el}; echo -n "ERR" >> ${el}; echo "ORS FOUND: "$ec >> ${el};
fi

# publish to download.jboss.org, unless errors found - avoid destroying last-good update site
if [[ $ec == "0" ]] && [[ $fc == "0" ]]; then
echo "PUBLISHING...."
	# publish build dir (including update sites/zips/logs/metadata
	if [[ -d ${STAGINGDIR} ]]; then
		echo "STAGING IS A DIRECTORY"

		# and create/replace a snapshot dir w/ static URL
echo "RSYNC TO $DESTINATION/builds/staging/"
echo "Command: rsync ${RSYNC_OPTIONS} ${STAGINGDIR} $DESTINATION/builds/staging/"
		date; rsync ${RSYNC_OPTIONS} ${STAGINGDIR} $DESTINATION/builds/staging/
	fi
fi
date

# publish updated log
bl=${STAGINGDIR}/logs/BUILDLOG.txt
rm -f ${bl}; wget -q http://hudson.qa.jboss.com/hudson/job/${JOB_NAME}/${BUILD_NUMBER}/consoleText -O ${bl} --timeout=900 --wait=10 --random-wait --tries=10 --retry-connrefused --no-check-certificate

date; rsync ${RSYNC_OPTIONS} ${STAGINGDIR}/logs $DESTINATION/builds/staging/${JOB_NAME}/

