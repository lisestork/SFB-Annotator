# Semantic Field Book Annotator

[![Build Status](https://travis-ci.org/LINNAE-project/SFB-Annotator.svg?branch=master)](https://travis-ci.org/LINNAE-project/SFB-Annotator)
[![Published in J. Web Semant.](https://img.shields.io/badge/published%20in-JWebSemant-blue.svg)](https://doi.org/10.1016/j.websem.2018.06.002)

The Semantic Field Book Annotator is a web application developed for domain experts to harvest structured annotations from field books, drawings and specimen labels of natural history collections using the [Natural History Collection Ontology](https://github.com/lisestork/NHC-Ontology). Users can draw bounding boxes over (zoomable) image scans of historical field notes, to which annotations can be attached. All metadata regarding an annotation event, annotation provenance, transcription and semantic interpretation of the text are stored in a knowledge base using the [Web Annotation Data Model](https://www.w3.org/TR/annotation-model/). The knowledge base is accessible online via a [SPARQL](http://makingsense.liacs.nl/rdf4j-server/repositories/NC) (e.g. using the [YASGUI](https://yasgui.triply.cc/) query editor) or Web API.

## Prerequisites
- [Docker CE](https://docs.docker.com/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Software used
- [Eclipse RDF4J Server and Workbench](https://rdf4j.org/documentation/tools/server-workbench/)
- [Cantaloupe](https://cantaloupe-project.github.io/) IIIF image server
- [grlc](https://www.research-software.nl/software/grlc) Web API
- JavaScript libraries
  - [Annotorius](https://annotorious.github.io)
  - [OpenSeadragon](https://openseadragon.github.io/)

## Install & deploy

**1. Clone this repository.**

```bash
git clone https://github.com/LINNAE-project/SFB-Annotator.git
```
**2. Install Docker Compose.**

```bash
pip install docker-compose
```

**3. Start Docker service(s).**

| Service | Port | Docker Image | Description |
| ------- | ---- | -------------| ----------- |
| `sea` | `8080` | [`linnae/sfb-annotator:latest`](https://hub.docker.com/r/linnae/sfb-annotator) | Semantic Field Book Annotator |
| `melon` | `8182` | [`linnae/cantaloupe:latest`](https://hub.docker.com/r/linnae/cantaloupe) | Cantaloupe image server |
| `grlc` | `8088` | [`clariah/grlc`](https://hub.docker.com/r/clariah/grlc) | Web API (optional)|

```bash
cd SFB-Annotator
# list available services
docker-compose config --services

# start all services or one-by-one
docker-compose up -d # or append [SERVICE]
```

**4. Configure service(s).**

```bash
# populate an empty RDF store (repository)
docker-compose exec sea ./init.sh

# configure sea to use a remote image archive (optional)
# default: data-local.json
JSON=/usr/local/tomcat/webapps/semanticAnnotator/data/
docker exec -it sea bash -c "cp $JSON/data-remote.json $JSON/data.json"

# configure grlc to use local path (optional)
git clone https://github.com/LINNAE-project/queries
docker cp ./queries grlc:/home/grlc/
```

**5. Build Docker image and deploy container locally (development)**

```bash
docker build -t linnae/sfb-annotator:local .
docker run --name sea -d -p 8080:8080 linnae/sfb-annotator:local
docker exec sea ./init.sh

# generate RDF triples for example inputs (annotation events)
for json in $(find data -name *.json | sort)
do
  ttl="data/rdf/$(basename $json .json).ttl"
  ./run.sh "$json" "$ttl" 
done
```

## Web app URLs & API endoints
- http://localhost:8080/semanticAnnotator/
  - requires user/password: `tomcat/tomcat`
- http://localhost:8080/rdf4j-workbench/
  - includes an empty repository: `mem-rdf`
- http://localhost:8080/rdf4j-server/
- http://localhost:8088/ followed by
  - remote path [`/api-git/LINNAE-project/queries/`](http://localhost:8088/api-git/LINNAE-project/queries/) or
    - requires `GRLC_GITHUB_ACCESS_TOKEN` to be set in [`docker-compose.yml`](https://github.com/LINNAE-project/SFB-Annotator/blob/master/docker-compose.yml#L27)
  - local path [`/api-local/`](http://localhost:8088/api-local/)
- http://localhost:8182/iiif/2
  - e.g. sample image [`info.json`](http://localhost:8182/iiif/2/900c341c1c10fff7%2Ffiles%2FMMNAT01_PM_NNM001001033_001/info.json)
  - e.g. get [JPG](http://localhost:8182/iiif/2/900c341c1c10fff7%2Ffiles%2FMMNAT01_PM_NNM001001033_001/full/max/0/default.jpg) version of this [TIF](https://trng-repository.surfsara.nl/deposit/900c341c1c10fff7/files/MMNAT01_PM_NNM001001033_001.tif) image deposited in the archive 
