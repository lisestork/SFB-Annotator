#!/usr/bin/env bash

set -xe

CONF=conf/create_store.txt
DATA_DIR=/var/rdf4j/server

cat $CONF | console.sh -e -f -d $DATA_DIR
