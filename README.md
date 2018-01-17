

To do: 
- fix pagination [done]
- fix the loading of images
- Retrieve entities from db to use for annotation. 
- Enable attachment of measurement table to their initiator (e.g. humanobservation -> hasDerivative -> measurementorfact (measurementtable) -> measuresOrDescribes -> Dentibus (teeth). 
- Make sure that when an occurrencerecord spans multiple pages, the humanobservation instance is attached to multiple pages, either by connecting it with a new annotation object to the page or with the same annotation object. A new annotation object seems better as it will store the date of the annotation and the annotator. 
