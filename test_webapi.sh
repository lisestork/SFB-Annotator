#!/usr/bin/env bash

set -e

failed() {
    echo "FAILED"
    exit 1
}

REPO_ID=mem-rdf
SPARQL_ENDPOINT="http://sea:8080/rdf4j-server/repositories/${REPO_ID}"
GRLC_URL="http://localhost:8088/api-local"
GRLC_PATHS=(countAnnotations getAuthors getEvents getLocations getMeasurements getTaxa)
CSV_DIR=data/csv

# upload SPARQL queries
rm -fr queries
git clone https://github.com/LINNAE-project/queries
docker cp ./queries grlc:/home/grlc/

# query Web API endpoints to fetch results in CSV
mkdir -p $CSV_DIR
for path in "${GRLC_PATHS[@]}"; do
    local_url="$GRLC_URL/$path"
    csv_file="${CSV_DIR}/${path}.csv"
    echo -ne "Test Web API endpoint '$local_url'\t... "
    [[ $(curl -sL -H "accept: text/csv" -w "%{http_code}" "$local_url" --data-urlencode "endpoint=${SPARQL_ENDPOINT}" -o "$csv_file") -eq 200 && -s "$csv_file" ]] &&
        echo "OK" || failed
done
echo "Done."
