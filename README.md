# Semantic Field Book Annotator

[![Published in J. Web Semant.](https://img.shields.io/badge/published%20in-JWebSemant-blue.svg)](https://doi.org/10.1016/j.websem.2018.06.002)

The Semantic Field Book Annotator is a web application developed for domain experts to harvest structured annotations from field books, drawings and specimen labels of natural history collections using the [Natural History Collection Ontology](https://github.com/lisestork/NHC-Ontology). Users can draw bounding boxes over (zoomable) image scans of historical field notes, to which annotations can be attached. All metadata regarding an annotation event, annotation provenance, transcription and semantic interpretation of the text are stored in a knowledge base using the [Web Annotation Data Model](https://www.w3.org/TR/annotation-model/). The knowledge base is accessible online via a [SPARQL endpoint](http://makingsense.liacs.nl/rdf4j-server/repositories/NC) (e.g. using the [YASGUI](https://yasgui.triply.cc/) query editor). Some example queries are included [here](https://github.com/lisestork/NHC-Ontology/blob/master/Example_Queries.txt).

## Dependencies
- JavaScript libraries
  - [Annotorius](https://annotorious.github.io)
  - [OpenSeadragon](https://openseadragon.github.io/) 

## Setting up the SFB-Annotator
- Set up a stable version of the [Apache Tomcat Java Servlet Container](http://tomcat.apache.org/)
- Deploy the [RDF4j-Server and RDF4j-Workbench](http://docs.rdf4j.org/server-workbench-console/) in the Tomcat Servlet Container.
- In case the virtuoso server is preferred over the RDF4j server, the RDF4j framework can still be used: the RDF4j-workbench is used to create a link to the Virtuoso server using the [Virtuoso Eclipse RDF4J Provider](http://vos.openlinksw.com/owiki/wiki/VOS/VirtSesame2Provider).
- Deploy the SFB-Annotator on the Tomcat Servlet Container using the tomcat manager.

!!! ADD TO WIKI !!!
## Annotation guidelines
Some screenshots can be found in the [image folder](/images). 

Steps to take to annotate a full observation record: 
- First annotate the taxonomical name, an action which automatically builds the basis of an [observation record graph](https://github.com/lisestork/NHC-Ontology/blob/master/Images/RecordGraph.png), and specify an organism ID, e.g., 1. If a taxonomical name is not annotated, the basis graph is not instantiated and thus other annotations can not be linked to the observation record graph with its corresponding organism ID.
    - First click on 'annotate' in the upper right corner of the image. 
    - Draw a bounding box around the taxonomical name. 
    - Choose the class 'Taxon' in the first field. 
    - Transcribe the text (e.g. the taxonomical name) in the field 'verbatim text' and add the language in the 'language' field if known.
    - Choose the relation 'Organism identified to: Taxon' relation from the 'select property' drop-down menu. Meta-data fields appear that ask for the taxon rank, higher classification, person who did the identification and the organism id. 
    - Enter the IRI of the first known higher taxon (which will be retrievable using autocomplete and the name). 
    - Enter the VIAF IRI from the person that did the identification (which can also be retrieved using autocomplete if the  database is prepopulated with viaf IRIs of expedition companions).
    - Enter the organism id. At the moment it has to be inserted manually, but hovering over other annotations of the same record will show this ID. e.g.: 
    <img width="300" src="/images/organismID.png">
 
- Then start annotating the other named entities of the following set: {location, person, anatomicalentity, propertyorattribute, measurementorfact, date}. A predicate must be selected next, for instance if a person is selected, there are three options: 1. Organism identified by: person, 2. Organism collected by: person, 3. taxon scientificNameAuthorship: person. In each case, specify the organism ID to which the entities belong. e.g., 1.

!!! ADD TO GH ISSUES !!!
## To do: 
- fix pagination <b>[done]</b>
- fix the loading of images (if a folder closed and another loaded before all images were finished loading from the first folder, images keep loading and the openseadragon tilesources get overwritten). 
- Retrieve entities from db to use for annotation with autocomplete (as in previous version):
  <img width="250" src="/images/Temauto.png">
  
- Enable attachment of measurement table to their initiator (e.g. humanobservation -> hasDerivative -> measurementorfact      (measurementtable) -> measuresOrDescribes -> Dentibus (teeth). 
- Make sure that when an occurrencerecord spans multiple pages, the humanobservation instance is attached to multiple pages, either by connecting it with a new annotation object to the page or with the same annotation object. A new annotation object seems better as it will store the date of the annotation and the annotator. 
- Automatic assignment of number to an organism observation record in case a new organism observation is annotated. Otherwise the specific organism can be chosen (the taxon name) to attach new annotations to.
- Change nhc namespace
- Change the assignment of dwc:year dwc:month dwc:day to point to event, not nhc:Date. 
- Remove SQL database storage
