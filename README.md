# Semantic Field Book Annotator

[![Published in J. Web Semant.](https://img.shields.io/badge/published%20in-JWebSemant-blue.svg)](https://doi.org/10.1016/j.websem.2018.06.002)
[![Build Status](https://travis-ci.org/arnikz/SFB-Annotator.svg?branch=master)](https://travis-ci.org/arnikz/SFB-Annotator)

The Semantic Field Book Annotator is a web application developed for domain experts to harvest structured annotations from field books, drawings and specimen labels of natural history collections using the [Natural History Collection Ontology](https://github.com/lisestork/NHC-Ontology). Users can draw bounding boxes over (zoomable) image scans of historical field notes, to which annotations can be attached. All metadata regarding an annotation event, annotation provenance, transcription and semantic interpretation of the text are stored in a knowledge base using the [Web Annotation Data Model](https://www.w3.org/TR/annotation-model/). The knowledge base is accessible online via a [SPARQL endpoint](http://makingsense.liacs.nl/rdf4j-server/repositories/NC) (e.g. using the [YASGUI](https://yasgui.triply.cc/) query editor). Some example queries are included [here](https://github.com/lisestork/NHC-Ontology/blob/master/Example_Queries.txt).

## Dependencies
- Java 8 Runtime Environment ([OpenJDK](https://openjdk.java.net/) or [Oracle Java](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html))
- [Apache Maven](https://maven.apache.org/)
- [Apache Tomcat](https://tomcat.apache.org/)
- [Eclipse RDF4J Server and Workbench](https://rdf4j.org/documentation/tools/server-workbench/) or
- OpenLink [Virtuoso OSE](http://vos.openlinksw.com/owiki/wiki/VOS) including the [Eclipse RDF4J Provider](http://vos.openlinksw.com/owiki/wiki/VOS/VirtSesame2Provider)
- JavaScript libraries
  - [Annotorius](https://annotorious.github.io)
  - [OpenSeadragon](https://openseadragon.github.io/) 

## Installation

```
git clone https://github.com/lisestork/SFB-Annotator.git
cd SFB-Annotator
docker build -t sea .
docker run -d -p 8080:8080 sea
```

Open URL(s) in a web browser:
- `http://localhost:8080/semanticAnnotator/` (user/password: `tomcat/tomcat`)
- `http://localhost:8080/rdf4j-server/`
- `http://localhost:8080/rdf4j-workbench/`

