#!/bin/bash
#
# Sample build script for a Django project.
#
BUILD_DIR=`dirname $0`
WORKSPACE=${WORKSPACE:-${BUILD_DIR}/.env}
VIRTUALENV=${VIRTUALENV:-`which virtualenv`}
if [[ -z ${VIRTUAL_ENV} ]]
then
    echo "Not under virtualenv. Creating and/or activating one"
    Activate=${WORKSPACE}/bin/activate
    if [ ! -f ${Activate} ]
    then
        echo "Creating virtualenv in ${WORKSPACE}"
        ${VIRTUALENV} ${WORKSPACE}
    else
        echo "Virtualenv found in ${WORKSPACE}"
    fi
    source ${Activate}
else
    echo "Using virtualenv at ${VIRTUAL_ENV}"
fi
PIP=${PIP:-`which pip`}
${PIP} install --quiet -r ${BUILD_DIR}/requirements.txt
