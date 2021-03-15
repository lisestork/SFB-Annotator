#!/usr/bin/env bash

set -e

usage () {
    echo "Usage: $0 [JSON INFILE] [RDF OUTFILE {.ttl|.jsonld}]"
    exit 1
}

failed () {
    echo "FAILED"
    exit 1
}

# check input arg(s)
if [ $# -ne "2" ]; then
  usage
fi

JSON_FILE="$1"
RDF_FILE="$2"
FILEXT="${RDF_FILE##*.}"
REPO_ID="mem-rdf"
CRE="tomcat:tomcat"
PORT="8080"
BASE_URL="http://localhost:${PORT}"
SEA_URL="${BASE_URL}/semanticAnnotator/writeAnnotationsToRDF"
REPO_URL="${BASE_URL}/rdf4j-server/repositories/${REPO_ID}"

declare -A MIME
MIME=( [ttl]=text/turtle [jsonld]=application/ld+json )

# check RDF filext
[[ ${MIME[$FILEXT]+_} ]] && echo "MIME: ${MIME[$FILEXT]}" || usage

# validate JSON input
echo -ne "Validate $JSON_FILE\t... "
[[ $(python -m json.tool < "$JSON_FILE") ]] \
    && echo "OK" || failed

# create triples
echo -ne "Create triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -H "Content-Type: application/json" -d @"$JSON_FILE" "$SEA_URL") -eq 200 ]] \
    && echo "OK" || failed

# count triples
echo -ne "Count triples\t... "
N=$(curl -s -u "$CRE" "${REPO_URL}/size")
echo "N=$N"
[[ $N == 0 ]] && exit 1

# dump triples
echo -ne "Dump triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -H "Accept: ${MIME[$FILEXT]}" "${REPO_URL}/statements" -o "$RDF_FILE") -eq 200 ]] \
    && echo "OK" || failed
cat "$RDF_FILE"
echo

# delete triples
echo -ne "Delete triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -X DELETE "${REPO_URL}/statements") -eq 204 ]] \
    && echo "OK" || failed

# count triples
echo -ne "Count triples\t... "
N=$(curl -s -u "$CRE" "{$REPO_URL}/size")
echo "N=$N"
echo "---"
[[ $N != 0 ]] && exit 1 || exit 0
