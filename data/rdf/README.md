### Example 1_1: annotating a taxon in a field book, variety 1 [!FIXME!]

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value |Notes | Diff Expected
|---|-----|-----|-----
| Entity type | `Taxon` |  The handwritten text contains a taxon name. | `nc:taxon1` `rdf:type` `dwc:Taxon`
| verbatim text | _Pteropus minimus_ | The verbatim text as written in the bounding box | `nc:taxon1` `rdfs:label` _"Pteropus minimus"_
| language | _la_ | `nc:taxon1` | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_
| Select property | `Type`  | This refers to an entity annotation without further interpretation, merely specifying that the bounding box contains a taxon. | see row 1
| type | `taxon` |  auto-fill from entity type |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) (question: who do we allow to annotate, and who to interpret the data) | `nc:taxon1` `nhc:belongsToTaxon` `https://identifiers.org/taxonomy:9397`
| Taxon rank | `scientificName` | from list (`kingdom`,`class`,`order`,`family`,`genus`, `specificEpithet`, `scientificName`) | `nc:taxon1` `dwc:taxonRank` `nc:scientificName`

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

### Example 1_2: annotating a taxon in a field book, variety 2

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 3

* Annotate -> Click and Drag -> draw a bounding box around the written text _Pteropus minimus_

* Fill in the pop-up form/table:

|Key|Value |Notes |Diff Expected
|---|-----|-----|-----
| Entity type | `Taxon` | The handwritten text contains a taxon name.
| verbatim text | _Pteropus minimus_ | The verbatim text as written in the bounding box
| language | _la_ | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_
| Select property | `Organism identification to` | The annotation of an entity that refers to the identification of an organism (a scientific name as the initialisation of a field observation record).
| type | `taxon` | auto-fill from entity type |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy. Choice should be made whether this can be an IRI from an external database, or whether it should point to an already annotated verbatim entity. (such as _Chiroptera_ on page 2) (question: who do we allow to annotate, and who to interpret the data)
| Taxon rank | `scientificName` | from list (`kingdom`,`class`,`order`,`family`,`genus`, `specificEpithet`, `scientificName`)
| Identified by | `https://viaf.org/viaf/45106482/` | Corresponds to meaning of `dwc:identifiedBy` (people, groups, or organizations who assigned the Taxon to the subject). Preferably enter an instance of the class `foaf:Person` (or organization). Here, the writer of the field book is known (_Heinrich Kuhl_), but leave empty if unknown.
| Organism ID | `1` | Should be generated automatically. `1` is a placeholder; some unique ID should be generated for the organism record.

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
