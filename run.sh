#!/usr/bin/env bash

set -e


# check input arg(s)
if [ $# -ne "2" ]; then
  echo "Usage: $0 [JSON INFILE] [RDF OUTFILE]"
  exit 1
fi

JSON_FILE="$1"
RDF_FILE="$2"
REPO_ID="mem-rdf"
CRE="tomcat:tomcat"
PORT="8080"
BASE_URL="http://localhost:${PORT}"
URL0="${BASE_URL}/semanticAnnotator/files/MMNAT01_AF_NNM001001033_001.jpg"
URL1="${BASE_URL}/semanticAnnotator/writeAnnotationsToRDF"
URL2="${BASE_URL}/rdf4j-server/repositories/${REPO_ID}"

# check if the web app servers the image file(s)
echo -ne "Image file(s) served\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" "$URL0" -o test.jpg) -eq 200 ]] \
    && echo "OK" || (echo "FAILED" && exit 1)

# validate JSON input
echo -ne "Validate $JSON_FILE\t... "
[[ $(python -m json.tool < "$JSON_FILE") ]] \
    && echo "OK" || (echo "FAILED" && exit 1)

# create triples
echo -ne "Create triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -H "Content-Type: application/json" -d @"$JSON_FILE" "$URL1") -eq 200 ]] \
    && echo "OK" || (echo "FAILED" && exit 1)

# count triples
echo -ne "Count triples\t... "
N=$(curl -s -u "$CRE" "{$URL2}/size")
echo "N=$N"
[[ $N == 0 ]] && exit 1

# dump triples in RDF/Turtle
echo -ne "Dump triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -H 'Accept: text/turtle' "${URL2}/statements" -o "$RDF_FILE") -eq 200 ]] \
    && echo "OK" || (echo "FAILED" && exit 1)
cat "$RDF_FILE"
echo

# delete triples
echo -ne "Delete triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -X DELETE "${URL2}/statements") -eq 204 ]] \
    && echo "OK" || (echo "FAILED" && exit 1)

# count triples
echo -ne "Count triples\t... "
N=$(curl -s -u "$CRE" "{$URL2}/size")
echo "N=$N"
echo "---"
[[ $N != 0 ]] && exit 1 || exit 0
