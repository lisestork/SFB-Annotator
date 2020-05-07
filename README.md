# Semantic Field Book Annotator

[![Published in J. Web Semant.](https://img.shields.io/badge/published%20in-JWebSemant-blue.svg)](https://doi.org/10.1016/j.websem.2018.06.002)

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

Compile Java sources.

```
git clone https://github.com/lisestork/SFB-Annotator.git
cd SFB-Annotator
mvn install  # see ./target dir
```

Deploy Tomcat using Docker.

```
PORT=8080
BASE_URL=http://localhost:$PORT
CONTAINER=test
CATALINA_HOME=/usr/local/tomcat/
docker run -d -p $PORT:$PORT --name $CONTAINER tomcat:8-jdk8-corretto
docker exec -t $CONTAINER cp -R $CATALINA_HOME/webapps.dist/manager $CATALINA_HOME/webapps
docker cp target/semanticAnnotator.war $CONTAINER:$CATALINA_HOME/webapps
docker exec -t $CONTAINER chown -R root.root $CATALINA_HOME/webapps
```

Edit config files:
- `tomcat-users.xml` - add user/role entries 

```
  <role rolename="manager-gui"/>
  <user username="tomcat" password="tomcat" roles="manager-gui"/>
```

`docker exec -it $CONTAINER vi $CATALINA_HOME/conf/tomcat-users.xml`

- `context.xml` - add comments `<!-- <Valve.../> -->`

`docker exec -it $CONTAINER vi $CATALINA_HOME/webapps/manager/META-INF/context.xml`

Open URL(s) in a web browser:
- `$BASE_URL/manager/`
- `$BASE_URL/semanticAnnotator/`
