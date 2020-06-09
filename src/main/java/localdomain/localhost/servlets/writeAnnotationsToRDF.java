package localdomain.localhost.servlets;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.json.JSONObject;

/**
 * Servlet implementation class WriteAnnotations
 */
public class writeAnnotationsToRDF extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get httpRequest parameters
		String anno = request.getParameter("annotation");

		// convert string to annotation object to store into rdf
		anno = anno.replaceAll("\"", "\\\"");
		anno = anno.replaceAll("\\[", "").replaceAll("\\]", "");

		JSONObject json = new JSONObject(anno);
		// System.out.println(json);

		// retrieve key-value pairs
		String date = (json.isNull("date")) ? "" : json.getString("date").trim();
		String annotator = (json.isNull("annotator")) ? "" : json.getString("annotator").trim();
		String source = (json.isNull("source")) ? "" : json.getString("source").trim();
		String selector = (json.isNull("selector")) ? "" : json.getString("selector").trim();
		String target = source + selector;
		String belongstotaxon = (json.isNull("belongstotaxon")) ? "" : json.getString("belongstotaxon").trim();
		String rank = (json.isNull("rank")) ? "" : json.getString("rank").trim();
		String person = (json.isNull("person")) ? "" : json.getString("person").trim();
		String organismID = (json.isNull("organismID")) ? "" : json.getString("organismID").trim();
		String occurrenceID = (json.isNull("occurrenceID")) ? "" : json.getString("occurrenceID").trim();
		String identificationID = (json.isNull("identificationID")) ? "" : json.getString("identificationID").trim();
		String geonamesfeature = (json.isNull("geonamesfeature")) ? "" : json.getString("geonamesfeature").trim();
		String anatomicalentity = (json.isNull("anatomicalentity")) ? "" : json.getString("anatomicalentity").trim();
		String verbatim = (json.isNull("verbatim")) ? "" : json.getString("verbatim").trim();
		String language = (json.isNull("language")) ? "" : json.getString("language").trim();
		String type = (json.isNull("type")) ? "" : json.getString("type").trim();
		String property = (json.isNull("property")) ? "" : json.getString("property").trim();
		String propertyorattribute = (json.isNull("propertyorattribute"))
				? ""
				: json.getString("propertyorattribute").trim();
		String instance = (json.isNull("instance")) ? "" : json.getString("instance").trim();

		// ns prefixes
		String dwc = "http://rs.tdwg.org/dwc/terms/";
		String dwciri = "http://rs.tdwg.org/dwc/iri/";
		String dsw = "http://purl.org/dsw/";
		String gn = "http://www.geonames.org/ontology#";
		String obo = "http://purl.obolibrary.org/obo/";
		String ncit = "http://identifiers.org/ncit/";
		String nhc = "http://makingsense.liacs.nl/rdf/nhc/";
		String oa = "http://www.w3.org/ns/oa#";

		// Connect to RDF server
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		// repo.initialize()
		ValueFactory f = repo.getValueFactory();

		// init class
		IRI anatomicalEntityTopClass = f.createIRI(obo, "UBERON_0001062");
		IRI annotationClass = f.createIRI(oa, "Annotation");
		IRI featureClass = f.createIRI(gn, "Feature");
		IRI targetClass = f.createIRI(oa, "Target");
		IRI selectorClass = f.createIRI(oa, "Selector");
		IRI sourceClass = f.createIRI(oa, "Source");
		IRI textualTagClass = f.createIRI(oa, "TextualTag");
		IRI semanticTagClass = f.createIRI(oa, "SemanticTag");
		IRI identificationClass = f.createIRI(dwc, "Identification");
		IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
		IRI humanObservationClass = f.createIRI(dwc, "HumanObservation");
		IRI measurementOrFactClass = f.createIRI(dwc, "MeasurementOrFact");
		IRI organismClass = f.createIRI(dwc, "Organism");
		IRI eventClass = f.createIRI(dwc, "Event");
		IRI taxonClass = f.createIRI(dwc, "Taxon");
		IRI propertyOrAttributeTopClass = f.createIRI(ncit, "C20189");
		Resource anatomicalEntityClass = (anatomicalentity.equals(""))
				? f.createBNode()
				: f.createIRI(anatomicalentity);

		// init properties
		IRI additionalOccurrenceProperty = f.createIRI(nhc, "additionalOccurrence");
		IRI additionalProperty = f.createIRI(nhc, "additional");
		IRI additionalIdentificationProperty = f.createIRI(nhc, "additionalIdentification");
		IRI yearProperty = f.createIRI(dwc, "year");
		IRI monthProperty = f.createIRI(dwc, "month");
		IRI dayProperty = f.createIRI(dwc, "day");
		IRI hasSourceProperty = f.createIRI(oa, "hasSource");
		IRI hasSelectorProperty = f.createIRI(oa, "hasSelector");
		IRI hasBodyProperty = f.createIRI(oa, "hasBody");
		IRI hasTargetProperty = f.createIRI(oa, "hasTarget");
		IRI hasDerivativeProperty = f.createIRI(dsw, "hasDerivative");
		IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
		IRI identifiesProperty = f.createIRI(dsw, "identifies");
		IRI hasIdentificationProperty = f.createIRI(dsw, "hasIdentification");
		IRI identificationIDProperty = f.createIRI(dwc, "identificationID");
		IRI occurrenceIDProperty = f.createIRI(dwc, "occurrenceID");
		IRI isBasedOnProperty = f.createIRI(dsw, "isBasedOn");
		IRI isBasisForIdProperty = f.createIRI(dsw, "isBasisForId");
		IRI hasOccurrenceProperty = f.createIRI(dsw, "hasOccurrence");
		IRI occurrenceOfProperty = f.createIRI(dsw, "occurrenceOf");
		IRI organismIDProperty = f.createIRI(dwc, "organismID");
		IRI atEventProperty = f.createIRI(dsw, "atEvent");
		IRI eventOfProperty = f.createIRI(dsw, "eventOf");
		IRI hasEvidenceProperty = f.createIRI(dsw, "hasEvidence");
		IRI evidenceForProperty = f.createIRI(dsw, "evidenceFor");
		IRI toTaxonProperty = f.createIRI(dwciri, "toTaxon");
		IRI locatesProperty = f.createIRI(dsw, "locates");
		IRI locatedAtProperty = f.createIRI(dsw, "locatedAt");
		IRI verbatimEventDateProperty = f.createIRI(nhc, "verbatimEventDate");
		IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
		IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");
		IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
		IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
		IRI measuresOrDescribesProperty = f.createIRI(nhc, "measuresOrDescribes");
		IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
		IRI scientificNameAuthorshipProperty = f.createIRI(nhc, "scientificNameAuthorship");
		Resource propertyOrAttributeClass = (propertyorattribute.equals(""))
				? f.createBNode()
				: f.createIRI(propertyorattribute);

		// init instances
		IRI targetIRI = f.createIRI(nhc, target);
		IRI sourceIRI = f.createIRI(nhc, source);
		IRI selectorIRI = f.createIRI(nhc, selector);
		IRI identificationIRI = f.createIRI(nhc, "identification" + organismID);
		IRI humanObservationIRI = f.createIRI(nhc, "humanObservation" + organismID);
		IRI organismIRI = f.createIRI(nhc, "organism" + organismID);
		IRI occurrenceIRI = f.createIRI(nhc, "occurrence" + organismID);
		IRI eventIRI = f.createIRI(nhc, "event" + organismID);
		IRI dateIRI = f.createIRI(nhc, "date" + organismID);
		IRI taxonRankIRI = f.createIRI(nhc, rank);
		IRI locationIRI = f.createIRI(nhc, "location" + organismID);
		IRI addIdentificationIRI = f.createIRI(nhc, "identification" + organismID + "_id" + identificationID);
		IRI addOccurrenceIRI = f.createIRI(nhc, "occurrence" + organismID + "_occ" + occurrenceID);
		IRI addEventIRI = f.createIRI(nhc, "event" + organismID + "_occ" + occurrenceID);
		IRI addLocationIRI = f.createIRI(nhc, "location" + organismID + "_occ" + occurrenceID);
		IRI addDateIRI = f.createIRI(nhc, "date" + organismID + "_occ" + occurrenceID);
		Resource instanceIRI = (instance.equals("")) ? f.createBNode() : f.createIRI(instance);
		Resource annotatorIRI = (annotator.equals("")) ? f.createBNode() : f.createIRI(annotator);
		Resource personIRI = (person.equals("")) ? f.createBNode() : f.createIRI(person);
		Resource belongsToTaxonIRI = (belongstotaxon.equals("")) ? f.createBNode() : f.createIRI(belongstotaxon);
		Resource geonamesFeatureIRI = (geonamesfeature.equals("")) ? f.createBNode() : f.createIRI(geonamesfeature);

		// query RDF store
		String query1 = "SELECT ?value WHERE {?iri rdf:type <http://www.w3.org/ns/oa#Annotation> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
		int annotationID = QueryTripleStore(query1, repo, "value");
		IRI annotationIRI = f.createIRI(nhc, "anno" + annotationID);
		IRI textualBodyIRI = f.createIRI(nhc, "textualBody" + annotationID);

		String query2 = "SELECT ?value WHERE { ?iri rdf:type <" + taxonClass.toString()
				+ "> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
		int taxonNr = QueryTripleStore(query2, repo, "value");
		IRI taxonIRI = f.createIRI(nhc, "taxon" + taxonNr);

		String query3 = "SELECT (COUNT(DISTINCT ?measurements) AS ?totalNumberOfInstances) WHERE { ?measurements rdf:type <"
				+ measurementOrFactClass.toString() + "> . }";
		int measurementOrFactNr = QueryTripleStore(query3, repo, "totalNumberOfInstances");
		IRI measurementOrFactIRI = f.createIRI(nhc, "measurementOrFact" + measurementOrFactNr);

		String query4 = "SELECT (COUNT(DISTINCT ?propertyorattribute) AS ?totalNumberOfInstances) WHERE { ?propertyorattribute rdf:type ?type . ?type rdfs:subClassOf <"
				+ propertyOrAttributeTopClass.toString() + "> . }";
		int propertyOrAttributeNr = QueryTripleStore(query4, repo, "totalNumberOfInstances");
		IRI propertyOrAttributeIRI = f.createIRI(nhc, "propertyOrAttribute" + propertyOrAttributeNr);

		String query5 = "SELECT (COUNT(DISTINCT ?anatomicalentity) AS ?totalNumberOfInstances) WHERE { ?anatomicalentity rdf:type ?type . ?type rdfs:subClassOf <"
				+ anatomicalEntityTopClass.toString() + "> . }";
		int anatomicalEntityNr = QueryTripleStore(query5, repo, "totalNumberOfInstances");
		IRI anatomicalEntityIRI = f.createIRI(nhc, "anatomicalEntity" + anatomicalEntityNr);

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			conn.add(textualBodyIRI, RDF.TYPE, textualTagClass);
			conn.add(textualBodyIRI, RDFS.LABEL, f.createLiteral(verbatim));
			conn.add(textualBodyIRI, DCTERMS.FORMAT, f.createLiteral("text/plain"));
			conn.add(textualBodyIRI, DCTERMS.LANGUAGE, f.createLiteral(language));
			conn.add(targetIRI, RDF.TYPE, targetClass);
			conn.add(sourceIRI, RDF.TYPE, sourceClass);
			conn.add(targetIRI, hasSourceProperty, sourceIRI);
			conn.add(targetIRI, hasSelectorProperty, selectorIRI);
			conn.add(selectorIRI, RDF.TYPE, selectorClass);
			conn.add(selectorIRI, RDF.VALUE, f.createLiteral(selector));
			conn.add(annotationIRI, RDF.TYPE, annotationClass);
			conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
			conn.add(annotationIRI, RDF.VALUE, f.createLiteral(annotationID));
			conn.add(annotationIRI, DCTERMS.CREATED, annotationIRI);
			conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date));
			conn.add(annotationIRI, hasBodyProperty, textualBodyIRI);
			conn.add(annotationIRI, hasTargetProperty, targetIRI);
			conn.commit();
		}

		// retrieve semantic annotations
		if (property.equals("hasIdentification")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);
				conn.add(taxonIRI, RDF.TYPE, semanticTagClass);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(occurrenceIRI, hasEvidenceProperty, humanObservationIRI);
				conn.add(occurrenceIRI, atEventProperty, eventIRI);
				conn.add(occurrenceIRI, occurrenceOfProperty, organismIRI);
				conn.add(occurrenceIRI, recordedByProperty, personIRI);
				conn.add(occurrenceIRI, RDF.TYPE, occurrenceClass);
				conn.add(eventIRI, verbatimEventDateProperty, dateIRI);
				conn.add(eventIRI, locatedAtProperty, locationIRI);
				conn.add(eventIRI, eventOfProperty, occurrenceIRI);
				conn.add(eventIRI, RDF.TYPE, eventClass);
				conn.add(locationIRI, locatesProperty, eventIRI);
				conn.add(organismIRI, hasIdentificationProperty, identificationIRI);
				conn.add(organismIRI, hasOccurrenceProperty, occurrenceIRI);
				conn.add(organismIRI, hasDerivativeProperty, humanObservationIRI);
				conn.add(organismIRI, organismIDProperty, f.createLiteral(organismID));
				conn.add(organismIRI, RDF.TYPE, organismClass);
				conn.add(identificationIRI, toTaxonProperty, taxonIRI);
				conn.add(identificationIRI, isBasedOnProperty, humanObservationIRI);
				conn.add(identificationIRI, identifiedByProperty, personIRI);
				conn.add(identificationIRI, identifiesProperty, organismIRI);
				conn.add(identificationIRI, identificationIDProperty, f.createLiteral(organismID));
				conn.add(identificationIRI, RDF.TYPE, identificationClass);
				conn.add(humanObservationIRI, isBasisForIdProperty, identificationIRI);
				conn.add(humanObservationIRI, derivedFromProperty, organismIRI);
				conn.add(humanObservationIRI, evidenceForProperty, occurrenceIRI);
				conn.add(humanObservationIRI, RDF.TYPE, humanObservationClass);
				conn.add(annotationIRI, hasBodyProperty, humanObservationIRI);
				conn.add(annotationIRI, hasTargetProperty, sourceIRI);
				conn.add(annotationIRI, RDF.TYPE, annotationClass);
				conn.add(annotationIRI, RDF.VALUE, f.createLiteral(annotationID));
				conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(annotationIRI, DCTERMS.CREATED, annotatorIRI);
				conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date));
				conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				conn.add(taxonIRI, taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(taxonIRI, RDF.TYPE, taxonClass);
				conn.add(taxonIRI, RDF.VALUE, f.createLiteral(taxonNr));
				conn.add(dateIRI, RDF.TYPE, DCTERMS.DATE);
				conn.add(locationIRI, RDF.TYPE, DCTERMS.LOCATION);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.commit();
			}
		} else if (property.equals("additionalIdentification")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(additionalIdentificationProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(addIdentificationIRI, toTaxonProperty, taxonIRI);
				conn.add(addIdentificationIRI, identificationIDProperty,
						f.createLiteral(organismID + "_id" + identificationID));
				conn.add(addIdentificationIRI, RDF.TYPE, identificationClass);
				conn.add(organismIRI, additionalIdentificationProperty, addIdentificationIRI);
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);
				conn.add(taxonIRI, RDF.TYPE, semanticTagClass);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(taxonIRI, RDF.TYPE, taxonClass);
				conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				conn.add(taxonIRI, taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI, RDF.VALUE, f.createLiteral(taxonNr));
				conn.commit();
			}
		} else if (property.equals("verbatimEventDate")) {
			int year = json.getInt("year");
			int month = json.getInt("month");
			int day = json.getInt("day");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, dateIRI);
				conn.add(dateIRI, RDF.TYPE, semanticTagClass);
				conn.add(dateIRI, RDFS.LABEL, f.createLiteral(verbatim));

				if (year != 0) {
					conn.add(dateIRI, yearProperty, f.createLiteral(year));
				}
				if (month != 0) {
					conn.add(dateIRI, monthProperty, f.createLiteral(month));
				}
				if (day != 0) {
					conn.add(dateIRI, dayProperty, f.createLiteral(day));
				}
				conn.commit();
			}
		} else if (type.equals("measurementorfact")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
				conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
				conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
				conn.add(annotationIRI, hasBodyProperty, measurementOrFactIRI);
				conn.add(measurementOrFactIRI, RDF.TYPE, semanticTagClass);
				conn.commit();
			}
		} else if (type.equals("propertyorattribute")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, propertyOrAttributeIRI);
				conn.add(propertyOrAttributeIRI, RDF.TYPE, semanticTagClass);
				conn.add(propertyOrAttributeIRI, RDF.TYPE, propertyOrAttributeClass);
				conn.add(propertyOrAttributeClass, RDFS.SUBCLASSOF, propertyOrAttributeTopClass);
				conn.add(propertyOrAttributeIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(measurementOrFactIRI, measuresOrDescribesProperty, propertyOrAttributeIRI);
				conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
				conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
				conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
				conn.commit();
			}
		} else if (type.equals("anatomicalentity")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, anatomicalEntityIRI);
				conn.add(anatomicalEntityIRI, RDF.TYPE, semanticTagClass);
				conn.add(anatomicalEntityIRI, RDF.TYPE, anatomicalEntityClass);
				conn.add(anatomicalEntityClass, RDFS.SUBCLASSOF, anatomicalEntityTopClass);
				conn.add(anatomicalEntityIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(measurementOrFactIRI, measuresOrDescribesProperty, anatomicalEntityIRI);
				conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
				conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
				conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
				conn.commit();
			}
		} else if (property.equals("locatedAt")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, locationIRI);
				conn.add(locationIRI, RDF.TYPE, semanticTagClass);
				conn.add(locationIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(locationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
				conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
				conn.commit();
			}
		} else if (property.equals("additionalLocatedAt")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
				conn.add(addOccurrenceIRI, atEventProperty, addEventIRI);
				conn.add(addOccurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID + "_occ" + occurrenceID));
				conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
				conn.add(addEventIRI, verbatimEventDateProperty, addDateIRI);
				conn.add(addEventIRI, locatedAtProperty, addLocationIRI);
				conn.add(addEventIRI, eventOfProperty, addOccurrenceIRI);
				conn.add(addEventIRI, RDF.TYPE, eventClass);
				conn.add(addLocationIRI, locatesProperty, addEventIRI);
				conn.add(addLocationIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(addLocationIRI, RDF.TYPE, DCTERMS.LOCATION);
				conn.add(annotationIRI, hasBodyProperty, addLocationIRI);
				conn.add(addLocationIRI, RDF.TYPE, semanticTagClass);
				conn.add(addLocationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
				conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
				conn.commit();
			}
		} else if (property.equals("scientificNameAuthorship")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(belongsToTaxonIRI, scientificNameAuthorshipProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("identifiedBy")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(identificationIRI, identifiedByProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("recordedBy")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(occurrenceIRI, recordedByProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("additionalRecordedBy")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
				conn.add(addOccurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID + "_occ" + occurrenceID));
				conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
				conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(addOccurrenceIRI, recordedByProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("person")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);
				conn.add(instanceIRI, RDF.TYPE, semanticTagClass);
				conn.add(instanceIRI, RDF.TYPE, FOAF.PERSON);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("location")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);
				conn.add(instanceIRI, RDF.TYPE, semanticTagClass);
				conn.add(instanceIRI, RDF.TYPE, DCTERMS.LOCATION);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("taxon")) {
			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);
				conn.add(taxonIRI, RDF.TYPE, semanticTagClass);
				conn.add(taxonIRI, RDF.TYPE, taxonClass);
				conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				conn.add(taxonIRI, taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(taxonIRI, RDF.VALUE, f.createLiteral(taxonNr));
				conn.commit();
			}
		}
		// //Connect to virtuoso server
		// //Initialize Remote Repository Manager
		// RepositoryManager repositoryManager = new RemoteRepositoryManager(
		// "http://localhost:8080/rdf4j-server/" );
		// repositoryManager.initialize();
		//
		// //Set Virtuoso (or any other) repositoryID on
		// http://hostname:portno/openrdf-sesame
		// Repository repository = repositoryManager.getRepository("NCVIRT");
		//
		// ValueFactory fact = repository.getValueFactory();
		//
		// IRI object = fact.createIRI(oa, "TextualTag");
		// IRI subject = fact.createIRI(nhc,"textualBody");
		//
		// // Open a connection to this repository
		// try (RepositoryConnection conn = repository.getConnection()){
		//
		// conn.begin();
		// conn.add(object, RDF.TYPE, subject);
		// conn.commit();
		// }
	}

	public int QueryTripleStore(String query, Repository repo, String valueOf) {
		Value count = null;
		int c = 0;

		try (RepositoryConnection conn = repo.getConnection()) {
			TupleQuery tupleQuery = conn.prepareTupleQuery(query);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) { // iterate over the result
					BindingSet bindingSet = result.next();
					count = bindingSet.getValue("valueOf");
				}
			}
		}

		if (count != null) {
			Literal literal = (Literal) count;
			c = literal.intValue() + 1;
		} else {
			c = 1;
		}
		return c;
	}

	public String retrieveValues(String type) {
		String values = new String();
		return values;
	}
}
