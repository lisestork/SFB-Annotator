# Semantic Field Book Annotator

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.4602264.svg)](https://doi.org/10.5281/zenodo.4602264)
![Build](https://github.com/LINNAE-project/SFB-Annotator/workflows/continuous_integration/badge.svg)
[![Published in J. Web Semant.](https://img.shields.io/badge/published%20in-JWebSemant-blue.svg)](https://doi.org/10.1016/j.websem.2018.06.002)

The Semantic Field Book Annotator is a web application developed for domain experts to harvest structured annotations from field books, drawings and specimen labels of natural history collections. Users can draw bounding boxes over (zoomable) image scans of historical field notes, to which annotations can be attached. All metadata regarding an annotation event, annotation provenance, transcription and semantic interpretation of the text are stored in a knowledge base, which is accessible via a SPARQL endpoint and Web API.

## Prerequisites
- [Docker CE](https://docs.docker.com/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Software used
- [Eclipse RDF4J Server and Workbench](https://github.com/eclipse/rdf4j)
- [grlc](https://github.com/CLARIAH/grlc) Web API
- [Cantaloupe](https://github.com/cantaloupe-project/cantaloupe) IIIF image server
- [Mirador](https://github.com/projectmirador/mirador) IIIF image viewer
- [Annotorius](https://github.com/recogito/annotorious)
- [OpenSeadragon](https://github.com/openseadragon/openseadragon)

## Vocabularies, ontologies & specs used
- [Web Annotation Vocabulary](https://www.w3.org/TR/annotation-vocab/)
- [International Image Interoperability Framework](https://iiif.io/) (IIIF)
- [Darwin Core](https://dwc.tdwg.org/) (DwC)
- [Darwin Semantic Web](http://purl.org/dsw/) (Darwin-SW)
- [Dublin Core](http://purl.org/dc/terms/) (DC)

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
| `sea` | `8080` | [`linnae/sfb-annotator`](https://hub.docker.com/r/linnae/sfb-annotator) | Semantic Field Book Annotator |
| `melon` | `8182` | [`linnae/cantaloupe`](https://hub.docker.com/r/linnae/cantaloupe) | Cantaloupe image server |
| `mirador` | `8000` | [`linnae/mirador`](https://hub.docker.com/r/linnae/mirador) | Mirador image viewer |
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
# populate RDF store (repository) with example data
docker-compose exec sea ./init.sh

# configure sea to use a remote image archive (optional)
# default: data-local.json
BASE_DIR=/usr/local/tomcat/webapps/semanticAnnotator/data/
IMG_SRC=remote  # default: local
docker exec -it sea bash -c "cp $BASE_DIR/data-$IMG_SRC.json $BASE_DIR/data.json"

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
for json in $(ls data/json/$IMG_SRC/*.json | sort)
do
  prefix="$(basename "$json" .json)"
  suffix=ttl  # or jsonld
  rdf="$prefix.$suffix"
  ./run.sh "$json" "$rdf"
done
```

## Web apps & API endpoints
- http://localhost:8080/semanticAnnotator/
  - requires user/password: `tomcat/tomcat`
  - _Register_->_Save_->_Collections_
- http://localhost:8080/rdf4j-workbench/
  - includes an empty repository: `mem-rdf`
- http://localhost:8080/rdf4j-server/
- http://localhost:8088/ followed by
  - remote path [`/api-git/LINNAE-project/queries/`](http://localhost:8088/api-git/LINNAE-project/queries/) or
    - requires `GRLC_GITHUB_ACCESS_TOKEN` to be set in [`docker-compose.yml`](https://github.com/LINNAE-project/SFB-Annotator/blob/master/docker-compose.yml#L27)
  - local path [`/api-local/`](http://localhost:8088/api-local/)
- http://localhost:8182/iiif/2
  - sample image [`info.json`](http://localhost:8182/iiif/2/900c341c1c10fff7:MMNAT01_PM_NNM001001033_001/info.json)
  - get a [JPG](http://localhost:8182/iiif/2/900c341c1c10fff7:MMNAT01_PM_NNM001001033_001/full/max/0/default.jpg) version of this [TIF](https://trng-repository.surfsara.nl/deposit/900c341c1c10fff7/files/MMNAT01_PM_NNM001001033_001.tif) image in the archive
- http://localhost:8000 includes an example [`manifest.json`](data/manifest.json)
  - _Add Item_->_Sample Field Book_->_Change view type_ to _Book View_
