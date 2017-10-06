This README file will be updated between 05-10-2017 and 06-10-2017 to provide specifications about how to set up the working programme. 

To do: 
- Retrieve entities from db to use for annotation. 
- Enable attachment of measurement table to their initiator (e.g. humanobservation -> hasDerivative -> measurementorfact (measurementtable) -> measuresOrDescribes -> Dentibus (teeth). 
- Make sure that when an occurrencerecord spans multiple pages, the humanobservation instance is attached to multiple pages, either by connecting it with a new annotation object to the page or with the same annotation object. A new annotation object seems better as it will store the date of the annotation and the annotator. 
