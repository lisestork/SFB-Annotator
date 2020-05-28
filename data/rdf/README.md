### Example 1: annotating a field book

* Go to http://localhost:8080/semanticAnnotator/

* Register -> Save

* Collections -> manuscript MMNAT01_AF -> page 2

* Annotate -> Click and Drag -> draw a box around the written text _Chiropterae_

* Fill in the pop-up form/table:

|Key|Value|Notes
|---|-----|-----
| Entity type | `Taxon` |
| verbatim text | `Chiropterae` |
| language | `la` | [ISO code](https://www.iso.org/iso-639-language-codes.html) for _latin_
| Select property | `Organism identification` |
| type | `taxon` | auto-fill |
| Belongs to taxon | `https://identifiers.org/taxonomy:9397` | refers to _Chiroptera_ (order) in NCBI Taxonomy
| Taxon rank | `order` |
| Identified By | | left empty, perhaps not needed given a registered annotator?
| Organism ID | | left empty, perhaps not needed given the IRI in _Belongs to taxon_?

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

See [`example_1.ttl`](/data/rdf/example_1.ttl) file.

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
