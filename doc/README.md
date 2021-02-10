### Use cases
Field notes are collections of observation records ([`dwc:HumanObservation`](https://dwc.tdwg.org/terms/#humanobservation)) that describe the occurrence ([`dwc:Occurrence`](https://dwc.tdwg.org/terms/#occurrence)) of an organism ([`dwc:Organism`](http://rs.tdwg.org/dwc/terms/Organism)) at some location ([`dwc:Location`](https://dwc.tdwg.org/terms/#location)) during some time ([`dwc:Event`](https://dwc.tdwg.org/terms/#event)). An observation record most often includes an identification ([`dwc:Identification`](https://dwc.tdwg.org/terms/#identification)) to a taxon ([`dwc:Taxon`](https://dwc.tdwg.org/terms/#taxon)), and accompanying measurements and facts ([`dwc:MeasurementOrFact`](https://dwc.tdwg.org/terms/#measurementorfact), [`UBERON:0001062`](http://purl.obolibrary.org/obo/UBERON_0001062) or [`ncit:C20189`](http://purl.obolibrary.org/obo/NCIT_C20189)) that were used for identification. These observation records usually span multiple field notes, but can also be very short utterances such as _Sci. Diard Buitenzorg_ (on [page 3](https://github.com/LINNAE-project/SFB-Annotator/blob/master/data/jpg/MMNAT01_AF_NNM001001033_003.jpg)), most likely referring to the occurrence of another organism with the same identification.

**Table 1.** List of examples with annotation classes and properties.
| class | property | example | revised |
|---|---|---|---|
|[`dwc:Taxon`](https://dwc.tdwg.org/terms/#taxon)|`rdf:type`|[`1_1`](#Example-1_1)|:x:
||`dsw:hasIdentification`|[`1_2`](#Example-1_2)| :x:
|[`foaf:Person`](http://xmlns.com/foaf/spec/#term_Person)|`rdf:type`|[`2_1`](#Example-2_1)|:heavy_check_mark:
||`nhc:scientificNameAuthorship`|[`2_2`](#Example-2_2)|:x:
||`dwciri:identifiedBy`|[`2_3`](#Example-2_3)|:x:
||`dwciri:recordedBy`|[`2_4`](#Example-2_4)|:x:
|[`dwc:Location`](https://dwc.tdwg.org/terms/#location), [`dcterms:Location`](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/#http://purl.org/dc/terms/Location)|`rdf:type`|[`3_1`](#Example-3_1)|:heavy_check_mark:
||`dsw:locatedAt`|[`3_2`](#Example-3_2)|:x:
|[`dwc:MeasurementOrFact`](https://dwc.tdwg.org/terms/#measurementorfact)|`rdf:type`|[`4_1`](#Example-4_1)|:heavy_check_mark:
||`dsw:derivedFrom`|[`4_2`](#Example-4_2)|:x:
|[`ncit:C20189`](http://purl.obolibrary.org/obo/NCIT_C20189)|`rdf:type`|[`5_1`](#Example-5_1)|:heavy_check_mark:
||`nhc:measuresOrDescribes`|[`5_2`](#Example-5_2)|:x:
|[`UBERON:0001062`](http://purl.obolibrary.org/obo/UBERON_0001062)|`rdf:type`|[`6_1`](#Example-6_1)|:heavy_check_mark:
||`nhc:measuresOrDescribes`|[`6_2`](#Example-6_2)|:x:
|[`dwc:Event`](https://dwc.tdwg.org/terms/#event)|`rdf:type`|[`7_1`](#Example-7_1)|:heavy_check_mark:
||`nhc:verbatimDate`|[`7_2`](#Example-7_2)|:x:


### Example 1_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_1_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_1_1.ttl)|[SVG](/data/rdf/local/example_1_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value|Notes|Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Taxon` |  The handwritten text contains a taxon name. | Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | `Pteropus minimus` | The verbatim text as written in the bounding box |
| language/`language` | `lat` | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_ |  add autocomplete/drop down menu
| Select property/`property` | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a taxon. |
| type/`type` | `taxon` |  auto-fill according to the Entity type |
| Belongs to taxon/`belongstotaxon` | `http://identifiers.org/taxonomy/9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) | **question:** who do we allow to annotate, and who to interpret the data
| Taxon rank/`rank` | `http://purl.obolibrary.org/obo/TAXRANK_0000003` (order) | from list ([`kingdom`](http://purl.obolibrary.org/obo/TAXRANK_0000017),[`class`](http://purl.obolibrary.org/obo/TAXRANK_0000002),[`order`](http://purl.obolibrary.org/obo/TAXRANK_0000003),[`family`](http://purl.obolibrary.org/obo/TAXRANK_0000004),[`genus`](http://purl.obolibrary.org/obo/TAXRANK_0000005) or [`species`](http://purl.obolibrary.org/obo/TAXRANK_0000006)) | should be drop-down menu according to the taxon IRI above


### Example 1_2

Property: [`dsw:hasIdentification`](http://purl.org/dsw/hasIdentification)

Input: [JSON](/data/json/remote/example_1_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_1_2.ttl)|[JSON-LD](/data/rdf/remote/example_1_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value|Notes|Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Taxon` | The handwritten text contains a taxon name. | Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | _Pteropus minimus_ | The verbatim text as written in the bounding box |
| language/`language` | _lat_ | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _latin_ | add autocomplete/drop down menu
| Select property/`property` | Organism identification to/`hasIdentification` | The annotation of an entity that refers to the identification of an organism (a taxon name as the initialisation of a field observation record, usually a new genus or scientificName). |
| type/`type` | `taxon` | auto-fill from entity type |
| Belongs to taxon/`belongstotaxon` | `https://identifiers.org/taxonomy/9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) | **question:** who do we allow to annotate, and who to interpret the data
| Taxon rank/`rank` | `http://purl.obolibrary.org/obo/TAXRANK_0000006` (species) | from list ([`kingdom`](http://purl.obolibrary.org/obo/TAXRANK_0000017),[`class`](http://purl.obolibrary.org/obo/TAXRANK_0000002),[`order`](http://purl.obolibrary.org/obo/TAXRANK_0000003),[`family`](http://purl.obolibrary.org/obo/TAXRANK_0000004),[`genus`](http://purl.obolibrary.org/obo/TAXRANK_0000005), [`species`](`http://purl.obolibrary.org/obo/TAXRANK_0000006`)) | should be drop-down menu
| Identified by/`person` | `https://viaf.org/viaf/45106482/` | Corresponds to meaning of [`dwc:identifiedBy`](https://dwc.tdwg.org/terms/#dwc:identifiedBy) (people, groups, or organizations who assigned the Taxon to the subject). Preferably enter an instance of the class `foaf:Person` (or organization). Here, the writer of the field book is known (_Heinrich Kuhl_), but leave empty if unknown. | should be retrievable from pre-populated triple store

\* In the absence of a persistent global unique identifier, construct one from a combination of identifiers in the record that will most closely make the ID globally unique. Examples: http://arctos.database.museum/guid/MSB:Mamm:233627, 000866d2-c177-4648-a200-ead4007051b9, urn:catalog:UWBM:Bird:89776.


### Example 2_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_2_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_2_1.ttl)|[SVG](/data/rdf/local/example_2_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Geoff_

* Fill in the pop-up form/table:

|Key|Value|Notes|Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Person` |  The handwritten text contains a person name. | Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | `Geoff` | The verbatim text as written in the bounding box |
| language | | NA (default: und) |
| Select property/`property` | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a person. |
| type | `person` |  auto-fill from entity type |
| Instance/`instance` | `http://viaf.org/viaf/39377694` | Link the bounding box to the IRI if known. Here the person Étienne Geoffroy-Saint-Hilaire. | Preferably these can be retrieved with semantic autocomplete


### Example 2_2

Property: [`nhc:scientificNameAuthorship`](http://makingsense.liacs.nl/rdf/nhc/scientificNameAuthorship)

Input: [JSON](/data/json/remote/example_2_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_2_2.ttl)|[JSON-LD](/data/rdf/remote/example_2_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Geoff_

* Fill in the pop-up form/table:

|Key|Value|Notes|Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Person` | The handwritten text contains a person name. | Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | _Geoff_ | The verbatim text as written in the bounding box |
| language | | | _AK_: remove
| Select property/`property` | Author of scientific name/`scientificNameAuthorship` | The annotation of an entity that refers to a person that was the (published) author of a scientific name. Maps to [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) The author is a crucial part of the taxon. Preferably, here, it refers to the persistent IRI of a person, such as `http://viaf.org/viaf/39377694` |
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon/`belongstotaxon` | `nc:taxon1` | This field should contain the IRI that was generated when annotating the annotated scientific name, the subject of [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) (here the annotated text: _Pteropus minimus_ on page 3) | **important note** The way it is setup now requires prior annotation of the taxon to which this name belongs. We should consider whether this makes sense.
| viaf IRI/`person` | `http://viaf.org/viaf/39377694` | the persistent identifier for the author of the scientific name. |  `nc:taxon1` [`dwc:scientificNameAuthorship`](https://dwc.tdwg.org/terms/#dwc:scientificNameAuthorship) `http://viaf.org/viaf/39377694`. Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.


### Example 2_3

Property: [`dwciri:identifiedBy`](https://dwc.tdwg.org/list/#dwciri_identifiedBy)

Input: [JSON](/data/json/remote/example_2_3.json)

Output: [RDF/Turtle](/data/rdf/remote/example_2_3.ttl)|[JSON-LD](/data/rdf/remote/example_2_3.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Diard_

* Fill in the pop-up form/table:

|Key|Value|Notes | Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Person` | The handwritten text contains a person name. | Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | _Diard_ | The verbatim text as written in the bounding box |
| language | | | _AK_: remove
| Select property/`property` | Organism identified by/`identifiedBy` | Maps to `dwciri:identifiedBy`
| type | `taxon` | auto-fill from entity type |
| viaf IRI/`person` | `http://viaf.org/viaf/39377694` | the persistent identifier for the person. | Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.


### Example 2_4

Property: [`dwciri:recordedBy`](https://dwc.tdwg.org/list/#dwciri_recordedBy)

Input: [JSON](/data/json/remote/example_2_4.json)

Output: [RDF/Turtle](/data/rdf/remote/example_2_4.ttl)|[JSON-LD](/data/rdf/remote/example_2_4.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Diard_

* Fill in the pop-up form/table:

|Key|Value|Notes|Diff Expected
|---|-----|-----|-----
| Entity type/`text` | `Person` | The handwritten text contains a person name. |  Should be a drop-down menu with all possible classes
| verbatim text/`verbatim` | _Diard_ | The verbatim text as written in the bounding box |
| language | | | _AK_: remove
| Select property/`property` | Occurrence recorded by/`recordedBy` | The annotation of an entity that refers to a person that recorded the occurrence of the organism. Maps to `dwciri:recordedBy`|
| type | `taxon` | auto-fill from entity type |
| viaf IRI/`person` | `http://viaf.org/viaf/39377694` | the persistent identifier for the person. |  Prompt should be changed to, e.g., person IRI instead of viaf IRI, as it could also be an orcid or other persistent identifier.


### Example 3_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_3_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_3_1.ttl)|[SVG](/data/rdf/local/example_3_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Buitenzorg_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `Location` |  The handwritten text contains a location name. | Should be a drop-down menu with all possible classes
| verbatim text | `Buitenzorg` | The verbatim text as written in the bounding box |
| language/`language` | `dut` | [ISO 639-3 code](https://iso639-3.sil.org/code/latl) for _Dutch_ |
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a location. |
| type | `location` |  auto-fill from entity type |
| instance | `http://sws.geonames.org/1648473` | Link the bounding box to the IRI if known. Here the location _Buitenzorg_, currently called _Bogor_. | Preferably these instances can be retrieved with semantic autocomplete


### Example 3_2

Property: `dsw:locatedAt`

Input: [JSON](/data/json/remote/example_3_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_3_2.ttl)|[JSON-LD](/data/rdf/remote/example_3_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Bagalonga_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `Location` | The handwritten text contains a location name. | Should be a drop-down menu with all possible classes|  
| verbatim text | _Bagalonga_ | The verbatim text as written in the bounding box |
| language | | |add autocomplete/drop down menu
| Select property | `Occurrence located at` | The annotation of an entity that refers to the location where the observation of the organism occurrence took place. Maps to `dsw:locatedAt`. The prompt `Additional occurrence located at` should be merged with this one. |
| type | `location` | auto-fill from entity type |
| gn:geonamesfeature IRI/`geonamesfeature` | ? | Link the bounding box to the place IRI if known (_Bagalonga_ not found via https://www.geonames.org/)
| Organism ID | | |


### Example 4_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_4_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_4_1.ttl)|[SVG](/data/rdf/local/example_4_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the table

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `MeasurementOrFact` |  The handwritten text contains a measurement or fact. | Should be a drop-down menu with all possible classes
| verbatim text | [Markdown](https://www.markdownguide.org/) table (or list) ||
| language/`language` | `lat` | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_ |
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a measurement or fact. |
| type | `measurementorfact` |  auto-fill from entity type |
| instance ||| Remove this field.


### Example 4_2

Property: `dsw:derivedFrom`

Input: [JSON](/data/json/remote/example_4_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_4_2.ttl)|[JSON-LD](/data/rdf/remote/example_4_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the measurement or fact (such as a table or a statement about the animal)

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|----
| Entity type | `MeasurementOrFact` | The handwritten text contains a table or other measurement or fact. | Should be a drop-down menu with all possible classes|  
| verbatim text | | | |
| language | | | add autocomplete/drop down menu |
| Select property | `Identification based on (table)` |  should be table _or_ paragraph| |
| type | `measurementorfact` | auto-fill from entity type |


### Example 5_1

Property: `rdf:type`

Input: [JSON](/data/json/remote/example_5_1.json)

Output: [RDF/Turtle](/data/rdf/remote/example_5_1.ttl)|[SVG](/data/rdf/local/example_5_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Color_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `PropertyOrAttribute` |  The handwritten text contains a property or attribute name. | Should be a drop-down menu with all possible classes
| verbatim text | `Color` | The verbatim text as written in the bounding box |
| language/`language` | `lat` | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_ |
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a property or attribute name. |
| type | `propertyOrAttribute` |  auto-fill from entity type |
| instance | `http://identifiers.org/ncit/C37927` (Color) | Link the bounding box to the IRI if known. | Preferably these instances can be retrieved with semantic autocomplete


### Example 5_2

Property: `nhc:measuresOrDescribes` and `propertyOrAttribute`

Input: [JSON](/data/json/remote/example_5_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_5_2.ttl)|[JSON-LD](/data/rdf/remote/example_5_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Color_

* Fill in the pop-up form/table:

|Key|Value |Notes  | Diff Expected
|---|-----|-----|-----
| Entity type | `propertyOrAttribute` | The handwritten text contains an indication of a property or attribute of the described organism. | Should be a drop-down menu with all possible classes|  
| verbatim text | _Color_ |  |  |
| language/`language` | _lat_ | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _latin_ |add autocomplete/drop down menu
| Select property | `Table/paragraph measures or describes` | Indicating that a table or a paragraph describes the certain property or attribute, here the _Color_ of the animal | |
| type | `propertyOrAttribute` | auto-fill from entity type |
| `ncit:propertyorattribute subclass IRI` | `http://identifiers.org/ncit/C37927` | A subclass of `http://identifiers.org/ncit/C20189` or `propertyOrAttribute` from the NCIT ontology.


### Example 6_1

Property: `rdf:type`

Input: [JSON](/data/json/remote/example_6_1.json)

Output: [RDF/Turtle](/data/rdf/remote/example_6_1.ttl)|[SVG](/data/rdf/local/example_6_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Dentibus_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `AnatomicalEntity` |  The handwritten text contains the name of an anatomical entity. | Should be a drop-down menu with all possible classes
| verbatim text | `Dentibus` | The verbatim text as written in the bounding box |
| language/`language` | `lat` | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_ |
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains the name of an anatomical entity. |
| type | `anatomicalentity` |  auto-fill from entity type |
| instance | `http://purl.obolibrary.org/obo/UBERON_0001062` (dentition)| Link the bounding box to the IRI if known. Here the anatomical entity _Dentibus_. | Preferably these instances can be retrieved with semantic autocomplete

* Check generated triples in the [RDF store](http://localhost:8080/rdf4j-workbench/repositories/mem-rdf/query).


### Example 6_2

Property: `nhc:measuresOrDescribes` and `anatomicalEntity`

Input: [JSON](/data/json/remote/example_6_2.json)

Output: [RDF/Turtle](/data/rdf/remote/example_6_2.ttl)|[JSON-LD](/data/rdf/remote/example_6_2.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Dentibus_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `anatomicalEntity` | The handwritten text contains the name of an anatomical entity of the described organism. | Should be a drop-down menu with all possible classes|  
| verbatim text | _Dentibus_ |  | |
| language/`language` | _lat_ | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _latin_ | add autocomplete/drop down menu
| Select property | `Table/paragraph measures or describes` | Indicating that a table or a paragraph describes the certain anatomical entity, here the _Dentibus_ (teeth) of the animal | |
| type | `anatomicalEntity` | auto-fill from entity type |
| `uberon:anatomicalentity subclass IRI` | `http://purl.obolibrary.org/obo/UBERON_0003672` | A subclass of `http://purl.obolibrary.org/obo/UBERON_0001062` or `anatomical entity` from the uberon ontology.



### Example 7_1

Property: `rdf:type`

Input: [JSON](/data/json/remote/example_7_1.json)

Output: [RDF/Turtle](/data/rdf/remote/example_7_1.ttl)|[SVG](/data/rdf/local/example_7_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 4

* Annotate -> Click and Drag -> draw a bounding box around the handwritten words _10 April 1821_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `date` |  The handwritten text contains a date. | Should be a drop-down menu with all possible classes
| verbatim text | `10 April 1821` | The verbatim text as written in the bounding box |
| language/`language` | `ger` | [ISO 639-3 code](https://iso639-3.sil.org/code/ger) for _German_ |
| Select property | `Type` | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a date. |
| type | `date` | auto-fill from entity type |
| instance | | This field is currently not used. |


### Example 7_2

Property: `nhc:verbatimDate` and `rdf:type`\*

Input: [JSON](/data/json/remote/example_7_1.json)

Output: [RDF/Turtle](/data/rdf/remote/example_7_1.ttl)|[JSON-LD](/data/rdf/remote/example_7_1.jsonld)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 4

* Annotate -> Click and Drag -> draw a bounding box around the handwritten date _10 april 1821_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `date` | The handwritten text contains a date, either a day, month year, or a combination thereof. | Should be a drop-down menu with all possible classes|  
| verbatim text | _10 april 1821_ |  Text as is, no formatting| |
| language/`language` | _lat_ | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _latin_ | add autocomplete/drop down menu
| Select property | `Organism described on` | Indicates that the observation of the animal was on a certain date |  |
| type | `date` | auto-fill from entity type |
| Year (yyyy) | 1821 || should not be auto-filled with a 0
| Month (mm) | 04 | |should not be auto-filled with a 0
| Day (dd) | 10 | |should not be auto-filled with a 0


\* For the `rdf:type` variant of `nhc:Date`, the fields should be the same, only omitting the field `organism ID`.

**General notes:**

* (back-end) Most annotations (except for variety 1, a class annotation) link to an annotation record, and atm, some annotations therefore depend on the prior annotation of other named entities. However, these annotation events should be able to occur un any order and the code should reflect this.  

* (relates to front-end) The entity type field should produce a drop-down menu with the possible classes  (`taxon`, `person`, `location`, `date`, `anatomical entity`, `measurementOrFact`) rather than a free-text field. Similarly, the entry to this field should limit the number of possibilities for the `select property` field. (E.g., `person` -> `Type`, `Author of scientific name`, `Organism identified by`, `Occurrence recorded by`/`Additional Occurrence recorded by`)
