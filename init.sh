#!/usr/bin/env bash

set -xe

REPO_ID=mem-rdf
CONF=conf/create_store.txt
LOAD_PATH=/usr/local/tomcat/data/rdf/local/
DATA_DIR=/var/rdf4j/server

sleep 20
sed -i.org "s:<REPO_ID>:${REPO_ID}:" $CONF
sed -i.org "s:<LOAD_PATH>:${LOAD_PATH}:" $CONF
console.sh -e -f -d $DATA_DIR < $CONF
