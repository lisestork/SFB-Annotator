### Use cases
Field notes are collections of observation records ([`dwc:HumanObservation`](https://dwc.tdwg.org/terms/#humanobservation)) that describe the occurrence ([`dwc:Occurrence`](https://dwc.tdwg.org/terms/#occurrence)) of an organism ([`dwc:Organism`](http://rs.tdwg.org/dwc/terms/Organism)) at some location ([`dwc:Location`](https://dwc.tdwg.org/terms/#location)) during some time ([`dwc:Event`](https://dwc.tdwg.org/terms/#event)). An observation record most often includes an identification ([`dwc:Identification`](https://dwc.tdwg.org/terms/#identification)) to a taxon ([`dwc:Taxon`](https://dwc.tdwg.org/terms/#taxon)), and accompanying measurements and facts ([`dwc:MeasurementOrFact`](https://dwc.tdwg.org/terms/#measurementorfact)) that were used for identification. These observation records usually span multiple field notes, but can also be very short utterances such as _Sci. Diard Buitenzorg_ (on [page 3](data/jpg/MMNAT01_AF_NNM001001033_003.jpg)), most likely referring to the occurrence of another organism with the same identification.

**Table 1.** List of examples with annotation classes and properties.
| class                                                                                                                                                                             | property   | example                                           | revised            |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------- | ------------------------------------------------- | ------------------ |
| [`dwc:Taxon`](https://dwc.tdwg.org/terms/#taxon)                                                                                                                                  | `rdf:type` | [`1_1`](#Example-1_1)                             | :heavy_check_mark: |
| [`foaf:Person`](http://xmlns.com/foaf/spec/#term_Person)                                                                                                                          | `rdf:type` | [`2_1`](#Example-2_1)                             | :heavy_check_mark: |
| [`dwc:Location`](https://dwc.tdwg.org/terms/#location), [`dcterms:Location`](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/#http://purl.org/dc/terms/Location) | `rdf:type` | [`3_1`](#Example-3_1)                             | :heavy_check_mark: |
| [`dwc:MeasurementOrFact`](https://dwc.tdwg.org/terms/#measurementorfact)                                                                                                          | `rdf:type` | [`4_1`](#Example-4_1)(#Example-5_1)(#Example-6_1) | :heavy_check_mark: |
| [`dwc:Event`](https://dwc.tdwg.org/terms/#event)                                                                                                                                  | `rdf:type` | [`7_1`](#Example-7_1)                             | :heavy_check_mark: |


### Example 1_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_1_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_1_1.ttl) | [SVG](/doc/models/example_1_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

| Key                               | Value                                                      | Notes                                                                                                                                                                                                                                                                                                                                                                           | Diff Expected                                                          |
| --------------------------------- | ---------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| Entity type/`text`                | `Taxon`                                                    | The handwritten text contains a taxon name.                                                                                                                                                                                                                                                                                                                                     | Should be a drop-down menu with all possible classes                   |
| verbatim text/`verbatim`          | `Pteropus minimus`                                         | The verbatim text as written in the bounding box                                                                                                                                                                                                                                                                                                                                |
| language/`language`               | `lat`                                                      | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_                                                                                                                                                                                                                                                                                                                 | add autocomplete/drop down menu                                        |
| Select property/`property`        | `Type`                                                     | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a taxon.                                                                                                                                                                                                                                                   |
| type/`type`                       | `taxon`                                                    | auto-fill according to the Entity type                                                                                                                                                                                                                                                                                                                                          |
| Belongs to taxon/`belongstotaxon` | `http://www.gbif.org/species/9180402`                      | Point to an IRI, if known (e.g., using GBIF or NCBI Taxonomy), or to an exisiting (verbatim) annotation such as _Chiroptera_ (on [page 2](data/jpg/MMNAT01_AF_NNM001001033_002.jpg)).                                                                                                                                                                                           | A distinction should be made between transcription and interpretation. |
| Taxon rank/`rank`                 | `http://purl.obolibrary.org/obo/TAXRANK_0000006` (species) | from list ([`kingdom`](http://purl.obolibrary.org/obo/TAXRANK_0000017),[`class`](http://purl.obolibrary.org/obo/TAXRANK_0000002),[`order`](http://purl.obolibrary.org/obo/TAXRANK_0000003),[`family`](http://purl.obolibrary.org/obo/TAXRANK_0000004),[`genus`](http://purl.obolibrary.org/obo/TAXRANK_0000005) or [`species`](http://purl.obolibrary.org/obo/TAXRANK_0000006)) | should be drop-down menu according to the taxon IRI above              |


### Example 2_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_2_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_2_1.ttl) | [SVG](/doc/models/example_2_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Geoff_

* Fill in the pop-up form/table:

| Key                        | Value                           | Notes                                                                                                                          | Diff Expected                                                |
| -------------------------- | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------ |
| Entity type/`text`         | `Person`                        | The handwritten text contains a person name.                                                                                   | Should be a drop-down menu with all possible classes         |
| verbatim text/`verbatim`   | `Geoff`                         | The verbatim text as written in the bounding box                                                                               |
| language                   |                                 | NA (default: und)                                                                                                              |
| Select property/`property` | `Type`                          | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a person. |
| type/`type`                | `person`                        | auto-fill from entity type                                                                                                     |
| Instance/`instance`        | `http://viaf.org/viaf/39377694` | Link the bounding box to the IRI if known. Here the person Étienne Geoffroy-Saint-Hilaire.                                     | Preferably these can be retrieved with semantic autocomplete |


### Example 3_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_3_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_3_1.ttl) | [SVG](/doc/models/example_3_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Buitenzorg_

* Fill in the pop-up form/table:

| Key             | Value                             | Notes                                                                                                                            | Diff Expected                                        |
| --------------- | --------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| Entity type     | `Location`                        | The handwritten text contains a location name.                                                                                   | Should be a drop-down menu with all possible classes |
| verbatim text   | `Buitenzorg`                      | The verbatim text as written in the bounding box                                                                                 |
| language        | `dut`                             | [ISO 639-3 code](https://iso639-3.sil.org/code/latl) for _Dutch_                                                                 |
| Select property | `Type`                            | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a location. |
| type            | `location`                        | auto-fill from entity type                                                                                                       |
| Instance        | `http://sws.geonames.org/1648473` | Add IRI if known (optional). For example, the location _Buitenzorg_ currently refers to _Bogor_.                                 |


### Example 4_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_4_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_4_1.ttl) | [SVG](/doc/models/example_4_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the table

* Fill in the pop-up form/table:

| Key             | Value                                                                                                                                   | Notes                                                                                                                                       | Diff Expected                                        |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| Entity type     | `MeasurementOrFact`                                                                                                                     | The handwritten text contains a measurement or fact.                                                                                        | Should be a drop-down menu with all possible classes |
| verbatim text   | `- Longitud. tota --- 1,0,3\n- corporis --- 0,2,9\n- tota --- 0,2,9\n- capitis --- 0,1,1\n- caudae --- 0,0,1\n- digit. medii --- 0,3,0` | [Markdown](https://www.markdownguide.org/) list or table                                                                                    |
| language        | `lat`                                                                                                                                   | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_                                                                             |
| Select property | `Type`                                                                                                                                  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a measurement or fact. |
| type            | `measurementorfact`                                                                                                                     | auto-fill from entity type                                                                                                                  |
| Instance        |                                                                                                                                         | Add IRI if known (optional).                                                                                                                |


### Example 5_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_5_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_5_1.ttl) | [SVG](/doc/models/example_5_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Color_

* Fill in the pop-up form/table:

| Key             | Value                                                 | Notes                                                                                                                                              | Diff Expected                                        |
| --------------- | ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| Entity type     | `MeasurementOrFact`                                   | The handwritten text contains a property or attribute name.                                                                                        | Should be a drop-down menu with all possible classes |
| verbatim text   | `Color`                                               | The verbatim text as written in the bounding box                                                                                                   |
| language        | `lat`                                                 | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_                                                                                    |
| Select property | `Type`                                                | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a property or attribute name. |
| type            | `measurementorfact`                                   | auto-fill from entity type                                                                                                                         |
| Instance        | `http://purl.obolibrary.org/obo/PATO_0000014` (color) | Add IRI if known (optional).                                                                                                                       |


### Example 6_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_6_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_6_1.ttl) | [SVG](/doc/models/example_6_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the handwritten word _Dentibus_

* Fill in the pop-up form/table:

| Key             | Value                                                       | Notes                                                                                                                                                  | Diff Expected                                        |
| --------------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------- |
| Entity type     | `MeasurementOrFact`                                         | The handwritten text contains the name of an anatomical entity.                                                                                        | Should be a drop-down menu with all possible classes |
| verbatim text   | `Dentibus`                                                  | The verbatim text as written in the bounding box                                                                                                       |
| language        | `lat`                                                       | [ISO 639-3 code](https://iso639-3.sil.org/code/lat) for _Latin_                                                                                        |
| Select property | `Type`                                                      | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains the name of an anatomical entity. |
| type            | `measurementorfact`                                         | auto-fill from entity type                                                                                                                             |
| Instance        | `http://purl.obolibrary.org/obo/UBERON_0001062` (dentition) | Add IRI if known (optional).                                                                                                                           |


### Example 7_1

Property: `rdf:type`

Input: [JSON](/data/json/local/example_7_1.json)

Output: [RDF/Turtle](/data/rdf/local/example_7_1.ttl) | [SVG](/doc/models/example_7_1.svg)

Steps:

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 4

* Annotate -> Click and Drag -> draw a bounding box around the handwritten words _10 April 1821_

* Fill in the pop-up form/table:

| Key             | Value           | Notes                                                                                                                        | Diff Expected                                        |
| --------------- | --------------- | ---------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| Entity type     | `date`          | The handwritten text contains a date.                                                                                        | Should be a drop-down menu with all possible classes |
| verbatim text   | `10 April 1821` | The verbatim text as written in the bounding box                                                                             |
| language        | `ger`           | [ISO 639-3 code](https://iso639-3.sil.org/code/ger) for _German_                                                             |
| Select property | `Type`          | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a date. |
| type            | `date`          | auto-fill from entity type                                                                                                   |
| Instance        | `1821-04-10`    | [ISO 8601](https://www.w3.org/TR/NOTE-datetime) date/time format (`YYYY-MM-DD`)                                              |
