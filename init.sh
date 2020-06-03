#!/usr/bin/env bash

set -xe

REPO_ID=mem-rdf
CONF=conf/create_store.txt
DATA_DIR=/var/rdf4j/server

sed "s/<REPO_ID>/${REPO_ID}/" | console.sh -e -f -d $DATA_DIR
