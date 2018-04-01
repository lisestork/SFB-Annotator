# SFB-Annotator
The Semantic Field Book Annotator (SFB-A) is a web application, developed for domain experts, to harvest structured annotations from field books, drawings and specimen labels of natural history collections using the NHC-Ontology [https://github.com/lisestork/NHC-Ontology]. 

Making use of the annotorious annotation API [https://annotorious.github.io] and the openseadragon API [https://openseadragon.github.io/], users can draw bounding boxes over (zoomable) image scans to which annotations can be attached. Provenance is stored regarding the annotation event, the target of the annotation (digitised manuscript scans) and the transcription and semantic interpretation using the Web Annotation Data Model<sup>1</sup>.

<sup>1</sup>The Web Annotation Model [https://www.w3.org/TR/annotation-model/].

## Annotation guidelines 


## To do: 
- fix pagination <b>[done]</b>
- fix the loading of images 
- Retrieve entities from db to use for annotation. 
- Enable attachment of measurement table to their initiator (e.g. humanobservation -> hasDerivative -> measurementorfact      (measurementtable) -> measuresOrDescribes -> Dentibus (teeth). 
- Make sure that when an occurrencerecord spans multiple pages, the humanobservation instance is attached to multiple pages, either by connecting it with a new annotation object to the page or with the same annotation object. A new annotation object seems better as it will store the date of the annotation and the annotator. 
- Automatic assignment of number to an organism observation record in case a new organism observation is annotated. Otherwise the specific organism can be chosen (the taxon name) to attach new annotations to.
- Change nhc namespace
- Change the assignment of dwc:year dwc:month dwc:day to point to event, not nhc:Date. 
