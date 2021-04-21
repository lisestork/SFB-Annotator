#!/usr/bin/env bash

set -e

REPO_ID=mem-rdf
LOAD_PATH=data/rdf/local/
DATA_DIR=/var/rdf4j/server
CMD=$(
 cat <<EOF
drop $REPO_ID
create memory
$REPO_ID
Memory Store
10000
true
0

show r
open $REPO_ID
clear
EOF
)$'\n'

# load example files in RDF
for file in "$LOAD_PATH"/*.ttl; do
 CMD+="load $file"$'\n'
done

CMD+=$(
 cat <<EOF
sparql
PREFIX oa: <http://www.w3.org/ns/oa#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX dwc: <http://rs.tdwg.org/dwc/terms/>
SELECT *
WHERE {
 ?annot a oa:Annotation ;
  oa:hasBody/a ?class .
 FILTER(?class IN (foaf:Person, dwc:Taxon, dwc:Location, dwc:Event, dwc:MeasurementOrFact))
}
.
sparql
SELECT (COUNT(*) AS ?n_triples)
WHERE {
 ?s ?p ?o .
}
.
close
quit
EOF
)

# wait for the server to start
sleep 20            
# populate repository with examples
echo "$CMD" | console.sh -e -f -d "$DATA_DIR"
