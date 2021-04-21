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
set prefixes=annot http://localhost:8080/rdf/nc/annotation/
set prefixes=dcmitype http://purl.org/dc/dcmitype/
set prefixes=dsw http://purl.org/dsw/
set prefixes=dwc http://rs.tdwg.org/dwc/terms/
set prefix=dwciri http://rs.tdwg.org/dwc/iri/
set prefixes=gbif http://www.gbif.org/species/
set prefix=gn http://sws.geonames.org/
set prefixes=img http://localhost:8080/semanticAnnotator/files/
set prefixes=iso http://iso639-3.sil.org/code/
set prefixes=mf http://www.w3.org/TR/media-frags/
set prefixes=oa http://www.w3.org/ns/oa#
set prefixes=obo http://purl.obolibrary.org/obo/
set prefixes=orcid http://orcid.org/
set prefix=viaf http://viaf.org/viaf/
set prefixes
set showprefix=true
EOF
)$'\n'

# load example files in RDF
for file in "$LOAD_PATH"/*.ttl; do
 CMD+="load $file"$'\n'
done

CMD+=$(
 cat <<EOF
sparql
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
