#!/usr/bin/env bash

set -e

failed () {
    echo "FAILED"
    exit 1
}

IMG_SRC=local
REPO_ID="mem-rdf"
CRE="tomcat:tomcat"
SEA_URL="http://localhost:8080/semanticAnnotator/annotation"
REPO_URL="http://localhost:8080/rdf4j-server/repositories/${REPO_ID}"
SPARQL_ENDPOINT="http://sea:8080/rdf4j-server/repositories/${REPO_ID}"
GRLC_URL="http://localhost:8088/api-local"
GRLC_PATHS=(countAnnotations getAuthors getEvents getLocations getMeasurements getTaxa)
CSV_DIR=data/csv

# clean-up RDF store
echo -ne "Delete triples\t... "
[[ $(curl -s -u "$CRE" -w "%{http_code}" -X DELETE "${REPO_URL}/statements") -eq 204 ]] \
    && echo "OK" || failed

N=$(curl -s -u "$CRE" "{$REPO_URL}/size")
[[ $N != 0 ]] && exit 1

# store triples in RDF store
for json in $(ls data/json/$IMG_SRC/*.json | sort)
do
    echo -ne "Create triples from $json\t... "
    [[ $(curl -s -u "$CRE" -w "%{http_code}" -H "Content-Type: application/json" -d @"$json" "$SEA_URL") -eq 201 ]] \
        && echo "OK" || failed
done

# count triples
echo -ne "Number of triples\t... "
N=$(curl -s -u "$CRE" "${REPO_URL}/size")
echo "$N"
[[ $N == 0 ]] && exit 1

# upload SPARQL queries
rm -fr queries
git clone https://github.com/LINNAE-project/queries
docker cp ./queries grlc:/home/grlc/

# query Web API endpoints to fetch results in CSV
mkdir -p $CSV_DIR
for path in "${GRLC_PATHS[@]}"
do
   local_url="$GRLC_URL/$path"
   csv_file="${CSV_DIR}/${path}.csv"
   echo -ne "Test Web API endpoint '$local_url'\t... "
   [[ $(curl -sL -H "accept: text/csv" -w "%{http_code}" "$local_url" --data-urlencode "endpoint=${SPARQL_ENDPOINT}" -o "$csv_file") -eq 200 && -s "$csv_file" ]] \
       && echo "OK" || failed
done
echo "Done."
