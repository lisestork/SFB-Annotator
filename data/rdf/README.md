### **Annotation notes:**
Field notes are observation records with at the base the occurrence ([`dwc:Occurrence`](https://dwc.tdwg.org/terms/#dwc:Organism)) of an organism ([`dwc:Organism`](https://dwc.tdwg.org/terms/#dwc:Organism)). These observation records can either span multiple pages, or can exist of a small note, e.g., _sci. (according to) Diard Buitenzorg_, referring to an observation of the same organism in _Buitenzorg_. An observation record generally has an identification ([`dwc:Identification`](https://dwc.tdwg.org/terms/#dwc:Identification)) to a taxon ([`dwc:Taxon`](https://dwc.tdwg.org/terms/#dwc:Taxon)), and is generally created on the basis of a human observation ([`dwc:HumanObservation`](http://rs.tdwg.org/dwc/terms/HumanObservation))

The different variations for annotation are:

|class| variation | example |
|---|---|---
|`dwc:Taxon` | `rdf:type` | [`example 1_1`](#Example-1_1)\*
|       |`dsw:hasIdentification` | [`example 1_2`](#Example-1_2)
|       |`nhc:additionalIdentification`| [`example 1_3`](#Example-1_3)
|`foaf:Person` |`rdf:type` |
|       | `nhc:scientificNameAuthorship`\* | [`example 2_2`](#Example-2_2)
|       | `dwciri:identifiedBy` | [`example 2_3`](#Example-2_3)
|       | `dwciri:recordedBy`| [`example 2_4`](#Example-2_4)
|`dwc:Location` | `dsw:locatedAt` | [`example 3_2`](#Example-3_2)
|`dwc:MeasurementOrFact` | `dsw:derivedFrom` | [`example 4_2`](#Example-4_2) |
|`ncit:C20189` (propertyOrAttribute)| `nhc:measuresOrDescribes` | [`example 5_2`](#Example-5_2)|
|`uberon:0001062` (anatomicalEntity)| `nhc:measuresOrDescribes` | [`example 6_2`](#Example-6_2) |
|`nhc:Date`| `nhc:verbatimDate` | [`example 7_2`](#Example-7_2)|

\* the first variation `rdf:type` annotation is the same for every class and is therefore only shown once.

### Example 1_1

Variation: `rdf:type`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value |Notes | Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Taxon` |  The handwritten text contains a taxon name. Should be a drop-down menu with all possible classes | :x: | `nc:taxon1`\* `rdf:type` `dwc:Taxon`
| verbatim text | _Pteropus minimus_ | The verbatim text as written in the bounding box | :x: | `nc:taxon1` `rdfs:label` _"Pteropus minimus"_
| language | _la_ | `nc:taxon1` | :x: | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a taxon. | :x: | see row 1
| type | `taxon` |  auto-fill from entity type |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) (question: who do we allow to annotate, and who to interpret the data) | :x: | `nc:taxon1` `nhc:belongsToTaxon` `https://identifiers.org/taxonomy:9397`,`https://identifiers.org/taxonomy:9397` `rdf:type` `dwc:Taxon`
| Taxon rank | `scientificName` | from list (`kingdom`,`class`,`order`,`family`,`genus`, `specificEpithet`, `scientificName`) | :x: | `nc:taxon1` `dwc:taxonRank` `nc:scientificName`, `nc:scientificName` `rdf:type` `nhc:TaxonRank`

* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o .
}
```
|?n|
|--|
|0|

!FIXME!

\* **_nc_** refers to our namespace (natural committee for research in the Netherlands' Indies)

**NOTE**: this is a variation that is the same for all observable classes (`taxon`, `person`, `location`, `date`, `anatomical entity`, `measurementOrFact`). It links the bounding box to its verbatim text and corresponding class, without linking to other named entities on the page. Variety 1 is therefore omitted from further class-specific example annotations.

### Example 1_2

Variation: `dsw:hasIdentification`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value |Notes | Triple created | iff Expected
|---|-----|-----|-----|-----
| Entity type | `Taxon` | The handwritten text contains a taxon name. | :heavy_check_mark: |
| verbatim text | _Pteropus minimus_ | The verbatim text as written in the bounding box | :heavy_check_mark: |
| language | _la_ | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_ | :heavy_check_mark: |
| Select property | `Organism identification to` | The annotation of an entity that refers to the identification of an organism (a taxon name as the initialisation of a field observation record, usually a new genus or scientificName). | :heavy_check_mark: |
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) (question: who do we allow to annotate, and who to interpret the data) | :heavy_check_mark: |
| Taxon rank | `scientificName` | from list (`kingdom`,`class`,`order`,`family`,`genus`, `specificEpithet`, `scientificName`) | :heavy_check_mark: |
| Identified by | `https://viaf.org/viaf/45106482/` | Corresponds to meaning of [`dwc:identifiedBy`](`https://dwc.tdwg.org/terms/#dwc:identifiedBy`) (people, groups, or organizations who assigned the Taxon to the subject). Preferably enter an instance of the class `foaf:Person` (or organization). Here, the writer of the field book is known (_Heinrich Kuhl_), but leave empty if unknown. | :heavy_check_mark: |
| Organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically, used to link all information belonging to an observation record of a single organism occurrence together. `1` is a placeholder; an unique ID should be generated for the organism record.\* | :heavy_check_mark: |

\* In the absence of a persistent global unique identifier, construct one from a combination of identifiers in the record that will most closely make the ID globally unique. Examples: http://arctos.database.museum/guid/MSB:Mamm:233627, 000866d2-c177-4648-a200-ead4007051b9, urn:catalog:UWBM:Bird:89776

* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o .
}
```
|?n|
|--|
|55|

See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl) file.

* Delete the annotated box (page 2) via GUI.

* Check if the annotations (triples) were deleted from the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o
}
```
|?n|
|--|
|35|

!FIXME!

See [`example_1_del.ttl`](/data/rdf/example_1_del.ttl) file.

* Delete triples manually from the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/update).

```
delete where {
  ?s ?p ?o .
}
```

### Example 1_3

Variation: `nhc:additionalIdentification`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 8

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Taxon` | The handwritten text contains a taxon name. | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| verbatim text | _Pteropus_ | The verbatim text as written in the bounding box | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| language | _la_ | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_ | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| Select property | `Additional identification to` | The annotation of an entity that refers to an _additional_ (secondary) identification of an organism (a scientific name as the initialisation of a field observation record). Usually, this is a second identification performed at a later stage (with availability of more knowledge for identification). Within this field note, the organism is first identified `in the field' as a new taxon _Gymnonotus_. | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl), but `dsw:hasIdentification` should be replaced with `nhc:additionalIdentification`.
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) (question: who do we allow to annotate, and who to interpret the data) | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| Taxon rank | `genus` | from list (`kingdom`,`class`,`order`,`family`,`genus`, `specificEpithet`, `scientificName`)| :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| Identified by | | Corresponds to meaning of [`dwc:identifiedBy`](`https://dwc.tdwg.org/terms/#dwc:identifiedBy`) (people, groups, or organizations who assigned the Taxon to the subject). Preferably enter an instance of the class `foaf:Person` (or organization). _Pteropus_ is most likely added at a later stage; therefore, we do not know (for sure) who performed the identification.  | :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl).
| Organism ID | `2` | Matches to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `2` is a placeholder; an unique ID should be generated for the organism record.\*| :x: | See [`example_1_2.ttl`](/data/rdf/example_1_2.ttl). This additional identification should refer to the same organism ID as the first identification (`dsw:hasIdentification`)
| Occurrence ID | `2` | Maps to [`dwc:occurrenceID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `2` is a placeholder; some unique ID should be generated for the organism record.\* | :x: | Should potentially be removed, or should be the same as the occurrence ID generated for the first identification, as it is based on information from the same organism occurrence. Right now, the pop-up table for example_1_2 does not prompt for occurrenceID.


* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o .
}
```
|?n|
|--|
|0|

!FIXME!


### Example 2_2

Variation `nhc:scientificNameAuthorship`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Geoff_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Person` | The handwritten text contains a person name. | :x: |  
| verbatim text | _Geoff_ | The verbatim text as written in the bounding box | :x: |
| language | | [ISO code](https://www.iso.org/iso-639-language-codes.html), here not relevant | :x: |
| Select property | `Author of scientific name` | The annotation of an entity that refers to a person that was the (published) author of a scientific name. Maps to [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) The author is a crucial part of the taxon. Preferably, here, it refers to the persistent IRI of a person, such as `http://viaf.org/viaf/39377694` | :x: |
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `nc:taxon1` | This field should contain the IRI that was generated when annotating the annotated scientific name, the subject of [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) (here the annotated text: _Pteropus minimus_ on page 3) | :x: |
| viaf IRI | `http://viaf.org/viaf/39377694` | the persistent identifier for the author of the scientific name. | :x: | `nc:taxon1` [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) `http://viaf.org/viaf/39377694`. Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.

* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o .
}
```
|?n|
|--|
|0|

!FIXME!

### Example 2_3

Variation: `dwciri:identifiedBy`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Diard_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Person` | The handwritten text contains a person name. | :x: |  
| verbatim text | _Diard_ | The verbatim text as written in the bounding box | :x: |
| language | | [ISO code](https://www.iso.org/iso-639-language-codes.html), here not relevant | :x: |
| Select property | `Author of scientific name` | The annotation of an entity that refers to a person that was the (published) author of a scientific name. Maps to [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) The author is a crucial part of the taxon. Preferably, here, it refers to the persistent IRI of a person, such as `http://viaf.org/viaf/39377694` | :x: |
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `nc:taxon1` | This field should contain the IRI that was generated when annotating the annotated scientific name, the subject of [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) (here the annotated text: _Pteropus minimus_ on page 3) | :x: |
| viaf IRI | `http://viaf.org/viaf/39377694` | the persistent identifier for the author of the scientific name. | :x: | `nc:taxon1` [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) `http://viaf.org/viaf/39377694`. Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.

* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).

```
select (count(*) as ?n)
where {
  ?s ?p ?o .
}
```
|?n|
|--|
|67\*|

\* atm this count includes annotations from example_1_2, as this prompt creates the organism IRI to which these triples should be linked. See [`example_2_3.ttl`](/data/rdf/example_2_3.ttl) file.

### Example 2_4

Variation: `dwciri:recordedBy`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Geoff_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Person` | The handwritten text contains a person name. | :x: |  
| verbatim text | _Geoff_ | The verbatim text as written in the bounding box | :x: |
| language | | [ISO code](https://www.iso.org/iso-639-language-codes.html), here not relevant | :x: |
| Select property | `Author of scientific name` | The annotation of an entity that refers to a person that was the (published) author of a scientific name. Maps to [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) The author is a crucial part of the taxon. Preferably, here, it refers to the persistent IRI of a person, such as `http://viaf.org/viaf/39377694` | :x: |
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `nc:taxon1` | This field should contain the IRI that was generated when annotating the annotated scientific name, the subject of [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) (here the annotated text: _Pteropus minimus_ on page 3) | :x: |
| viaf IRI | `http://viaf.org/viaf/39377694` | the persistent identifier for the author of the scientific name. | :x: | `nc:taxon1` [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) `http://viaf.org/viaf/39377694`. Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.


### Example 3_2

Variation: `dsw:locatedAt`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Bagalonga_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `Location` | The handwritten text contains a location name. | |  
| verbatim text | _Bagalonga_ | The verbatim text as written in the bounding box | :heavy_check_mark: |
| language | | [ISO code](https://www.iso.org/iso-639-language-codes.html) |  |
| Select property | `Occurrence located at` | The annotation of an entity that refers to the location where the observation of the organism occurrence took place. Maps to `dsw:locatedAt`. The prompt `Additional occurrence located at` should be merged with this one. | :heavy_check_mark: |
| type | `taxon` | auto-fill from entity type |
| gn:geonamesfeature IRI |  | the persistent identifier for the location from the geonames ontology, e.g., `http://sws.geonames.org/1648473/` (stands for _Buitenzorg_, also _Bogor_) | :heavy_check_mark: |
| organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `1` is a placeholder; an unique ID should be generated for the organism record.* | :heavy_check_mark: |


### Example 4_2

Variation: `dsw:derivedFrom`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the measurement or fact (such as a table or a statement about the animal)

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `MeasurementOrFact` | The handwritten text contains a table or other measurement or fact. | |  
| verbatim text | leave empty |  | :heavy_check_mark: |
| language | leave empty | [ISO code](https://www.iso.org/iso-639-language-codes.html) |  |
| Select property | `Table/paragraph measures or describes` | Prompt should be changed to `Human observation has derivative` to map to the ontology, although that would be rather unclear | :heavy_check_mark: |
| type | `measurementorfact` | auto-fill from entity type |
| organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `1` is a placeholder; an unique ID should be generated for the organism record.* | :heavy_check_mark: |

See [`example_4_2.ttl`](/data/rdf/example_4_2.ttl) file.

### Example 5_2

Variation: `nhc:measuresOrDescribes` a `propertyOrAttribute`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Color_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `propertyOrAttribute` | The handwritten text contains an indication of a property or attribute of the described organism. | |  
| verbatim text | _Color |  | :heavy_check_mark: |
| language | la | [ISO code](https://www.iso.org/iso-639-language-codes.html) |  |
| Select property | `Table/paragraph measures or describes` | Indicating that a table or a paragraph describes the certain property or attribute, here the _Color_ of the animal | :heavy_check_mark: |
| type | `propertyOrAttribute` | auto-fill from entity type |
| `ncit:propertyorattribute subclass IRI` | `http://identifiers.org/ncit/C37927` | A subclass of `http://identifiers.org/ncit/C20189` or `propertyOrAttribute` from the NCIT ontology.
| organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `1` is a placeholder; an unique ID should be generated for the organism record.* | :heavy_check_mark: |

See [`example_5_2.ttl`](/data/rdf/example_5_2.ttl) file.

### Example 6_2

Variation: `nhc:measuresOrDescribes` an `anatomicalEntity`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Dentibus_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `anatomicalEntity` | The handwritten text contains the name of an anatomical entity of the described organism. | |  
| verbatim text | _Dentibus_ |  | :heavy_check_mark: |
| language | la | [ISO code](https://www.iso.org/iso-639-language-codes.html) |  |
| Select property | `Table/paragraph measures or describes` | Indicating that a table or a paragraph describes the certain anatomical entity, here the _Dentibus_ (teeth) of the animal | :heavy_check_mark: |
| type | `anatomicalEntity` | auto-fill from entity type |
| `uberon:anatomicalentity subclass IRI` | `http://purl.obolibrary.org/obo/UBERON_0003672` | A subclass of `http://purl.obolibrary.org/obo/UBERON_0001062` or `anatomical entity` from the uberon ontology.
| organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `1` is a placeholder; an unique ID should be generated for the organism record.* | :heavy_check_mark: |

See [`example_6_2.ttl`](/data/rdf/example_6_2.ttl) file.


### Example 7_2

Variation: `nhc:verbatimDate`

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 4

* Annotate -> Click and Drag -> draw a bounding box around the handwritten date _10 april 1821_

* Fill in the pop-up form/table:

|Key|Value |Notes |Triple created | Diff Expected
|---|-----|-----|-----|-----
| Entity type | `date` | The handwritten text contains a date, either a day, month year, or a combination thereof. | |  
| verbatim text | _10 april 1821_ |  | :heavy_check_mark: |
| language | nl | [ISO code](https://www.iso.org/iso-639-language-codes.html) |  |
| Select property | `Organism described on` | Indicates that the observation of the animal was on a certain date | :heavy_check_mark: |
| type | `date` | auto-fill from entity type |
| Year (yyyy) | 1821 | should not be auto-filled with a 0
| Month (mm) | 04 | should not be auto-filled with a 0
| Day (dd) | 10 | should not be auto-filled with a 0
| organism ID | `1` | Maps to [`dwc:organismID`](https://dwc.tdwg.org/terms/#dwc:occurrenceID). Should be generated automatically. `1` is a placeholder; an unique ID should be generated for the organism record.* | :heavy_check_mark: |

See [`example_7_2.ttl`](/data/rdf/example_7_2.ttl) file.

**General notes:**

* (back-end) Most annotations (except for variety 1, a class annotation) link to an annotation record, and atm, some annotations therefore depend on the prior annotation of other named entities. However, these annotation events should be able to occur un any order and the code should reflect this.  

* (relates to front-end) The entity type field should produce a drop-down menu with the possible classes  (`taxon`, `person`, `location`, `date`, `anatomical entity`, `measurementOrFact`) rather than a free-text field. Similarly, the entry to this field should limit the number of possibilities for the `select property` field. (E.g., `person` -> `Type`, `Author of scientific name`, `Organism identified by`, `Occurrence recorded by`/`Additional Occurrence recorded by`)
