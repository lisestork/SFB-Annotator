package localdomain.localhost.servlets;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
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

		// get parameters that were sent with the httpRequest
		String anno = request.getParameter("annotation");

		// convert string to annotation object to store into rdf
		anno = anno.replaceAll("\"", "\\\"");
		anno = anno.replaceAll("\\[", "").replaceAll("\\]", "");

		JSONObject json = new JSONObject(anno);
		System.out.println(json);

		// retrieve key-value pairs
		// annotation information
		String date = json.getString("date");
		String annotator = json.getString("annotator");

		// target
		String source = json.getString("source");
		String selector = json.getString("selector");
		String target = source + selector;

		// textual body
		String verbatim = json.getString("verbatim");
		String verbatimNoSpace = verbatim.replaceAll("\\s+", "");
		String language = json.getString("language");

		// semantic body
		String type = json.getString("type");
		String property = json.getString("property");

		String dwc = "http://rs.tdwg.org/dwc/terms/";
		String dwciri = "http://rs.tdwg.org/dwc/iri/";
		String dsw = "http://purl.org/dsw/";
		String gn = "http://www.geonames.org/ontology#";
		String ncit = "http://identifiers.org/ncit/";
		String nhc = "http://makingsense.liacs.nl/rdf/nhc/";
		String oa = "http://www.w3.org/ns/oa#";

		// Connect to RDF server
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);

		// repo.initialize()
		// create valuefactory for IRIs
		ValueFactory f = repo.getValueFactory();

		// retrieve nr of annotations
		String query = "SELECT ?value WHERE {?iri rdf:type <http://www.w3.org/ns/oa#Annotation> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
		int annoID = QueryTripleStore(query, repo, "value");

		// class initialization
		IRI annotationClass = f.createIRI(oa, "Annotation");
		IRI targetClass = f.createIRI(oa, "Target");
		IRI selectorClass = f.createIRI(oa, "Selector");
		IRI sourceClass = f.createIRI(oa, "Source");
		IRI textualTagClass = f.createIRI(oa, "TextualTag");

		// property initialization
		IRI hasSourceProperty = f.createIRI(oa, "hasSource");
		IRI hasSelectorProperty = f.createIRI(oa, "hasSelector");
		IRI hasBodyProperty = f.createIRI(oa, "hasBody");
		IRI hasTargetProperty = f.createIRI(oa, "hasTarget");

		// instance initialization
		IRI annotationIRI = f.createIRI(nhc, "anno" + annoID);
		IRI textualBodyIRI = f.createIRI(nhc, "textualBody" + annoID);
		IRI targetIRI = f.createIRI(nhc, target);
		IRI sourceIRI = f.createIRI(nhc, source);
		IRI selectorIRI = f.createIRI(nhc, selector);
		IRI annotatorIRI = f.createIRI(annotator);

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			//// NORMAL ANNO, TARGET & BODY ASSERTIONS/////////
			// textual body
			conn.add(textualBodyIRI, RDF.TYPE, textualTagClass);
			conn.add(textualBodyIRI, RDFS.LABEL, f.createLiteral(verbatim));
			conn.add(textualBodyIRI, DCTERMS.FORMAT, f.createLiteral("text/plain"));

			if (!language.equals("")) {
				conn.add(textualBodyIRI, DCTERMS.LANGUAGE, f.createLiteral(language));
			}

			// target
			conn.add(targetIRI, RDF.TYPE, targetClass);
			conn.add(sourceIRI, RDF.TYPE, sourceClass);
			conn.add(targetIRI, hasSourceProperty, sourceIRI);
			conn.add(targetIRI, hasSelectorProperty, selectorIRI);
			conn.add(selectorIRI, RDF.TYPE, selectorClass);
			conn.add(selectorIRI, RDF.VALUE, f.createLiteral(selector));

			// link annotation to all metadata, incl body & target
			conn.add(annotationIRI, RDF.TYPE, annotationClass);
			conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
			conn.add(annotationIRI, RDF.VALUE, f.createLiteral(annoID));
			conn.add(annotationIRI, DCTERMS.CREATED, annotatorIRI);
			conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date));
			conn.add(annotationIRI, hasBodyProperty, textualBodyIRI);
			conn.add(annotationIRI, hasTargetProperty, targetIRI);
			conn.commit();
		}

		// retrieve semantic annotations
		if (property.equals("hasIdentification")) {
			System.out.println("Annotated identification, add semantics");

			String belongstotaxon = json.getString("belongstotaxon");
			String rank = json.getString("rank");
			String person = json.getString("person");
			String organismID = json.getString("organismID");

			// query the DB for the taxon nr
			String queryTax = "SELECT ?value WHERE {?iri rdf:type <http://rs.tdwg.org/dwc/terms/Taxon> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
			int taxonNr = QueryTripleStore(queryTax, repo, "value");

			// retrieve nr of annotations
			query = "SELECT ?value WHERE {?iri rdf:type <http://www.w3.org/ns/oa#Annotation> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
			annoID = QueryTripleStore(query, repo, "value");

			// SEMANTIC IRI'S
			// instances
			IRI identificationIRI = f.createIRI(nhc, "identification" + organismID);
			IRI humanObservationIRI = f.createIRI(nhc, "humanObservation" + organismID);
			IRI organismIRI = f.createIRI(nhc, "organism" + organismID);
			IRI occurrenceIRI = f.createIRI(nhc, "occurrence" + organismID);
			IRI eventIRI = f.createIRI(nhc, "event" + organismID);
			IRI dateIRI = f.createIRI(nhc, "date" + organismID);
			IRI taxonIRI = f.createIRI(nhc, "taxon" + taxonNr);
			IRI locationIRI = f.createIRI(nhc, "location" + organismID);
			IRI taxonRankIRI = f.createIRI(nhc, rank);
			IRI HOannotationIRI = f.createIRI(nhc, "anno" + annoID);
			IRI belongsToTaxonIRI = null;
			IRI personIRI = null;

			if (!belongstotaxon.equals("")) {
				belongsToTaxonIRI = f.createIRI(belongstotaxon);
			}

			if (!person.equals("")) {
				personIRI = f.createIRI(person);
			}

			// properties
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
			IRI hasDerivativeProperty = f.createIRI(dsw, "hasDerivative");
			IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
			IRI verbatimEventDateProperty = f.createIRI(nhc, "verbatimEventDate");
			IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
			IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");
			IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");

			// class
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");
			IRI identificationClass = f.createIRI(dwc, "Identification");
			IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
			IRI humanObservationClass = f.createIRI(dwc, "HumanObservation");
			IRI organismClass = f.createIRI(dwc, "Organism");
			IRI eventClass = f.createIRI(dwc, "Event");
			IRI taxonClass = f.createIRI(dwc, "Taxon");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				// add to annotation object
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);
				conn.add(taxonIRI, RDF.TYPE, semanticTagClass);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));

				// link instances
				conn.add(occurrenceIRI, hasEvidenceProperty, humanObservationIRI);
				conn.add(occurrenceIRI, atEventProperty, eventIRI);
				conn.add(occurrenceIRI, occurrenceOfProperty, organismIRI);
				conn.add(occurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID));

				if (!person.equals("")) {
					conn.add(occurrenceIRI, recordedByProperty, personIRI);
				}

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

				if (!person.equals("")) {
					conn.add(identificationIRI, identifiedByProperty, personIRI);
				}

				conn.add(identificationIRI, identifiesProperty, organismIRI);
				conn.add(identificationIRI, identificationIDProperty, f.createLiteral(organismID));
				conn.add(identificationIRI, RDF.TYPE, identificationClass);
				conn.add(humanObservationIRI, isBasisForIdProperty, identificationIRI);
				conn.add(humanObservationIRI, derivedFromProperty, organismIRI);
				conn.add(humanObservationIRI, evidenceForProperty, occurrenceIRI);
				conn.add(humanObservationIRI, RDF.TYPE, humanObservationClass);
				conn.add(HOannotationIRI, hasBodyProperty, humanObservationIRI);
				conn.add(HOannotationIRI, hasTargetProperty, sourceIRI);
				conn.add(HOannotationIRI, RDF.TYPE, annotationClass);
				conn.add(HOannotationIRI, RDF.VALUE, f.createLiteral(annoID));
				conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(HOannotationIRI, DCTERMS.CREATED, annotatorIRI);
				conn.add(HOannotationIRI, DCTERMS.DATE, f.createLiteral(date));

				if (!belongstotaxon.equals("")) {
					conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				}

				conn.add(taxonIRI, taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(taxonIRI, RDF.TYPE, taxonClass);
				conn.add(taxonIRI, RDF.VALUE, f.createLiteral(taxonNr));
				conn.add(dateIRI, RDF.TYPE, DCTERMS.DATE);
				// add classes
				conn.add(locationIRI, RDF.TYPE, DCTERMS.LOCATION);

				if (!person.equals("")) {
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				}
				conn.commit();
			}
		} else if (property.equals("additionalIdentification")) {
			String organismID = json.getString("organismID");
			String identificationID = json.getString("identificationID");
			String belongstotaxon = json.getString("belongstotaxon");
			String rank = json.getString("rank");

			IRI organismIRI = f.createIRI(nhc, "organism" + organismID);
			IRI toTaxonProperty = f.createIRI(dwciri, "toTaxon");
			IRI additionalIdentificationProperty = f.createIRI(nhc, "additionalIdentification");
			IRI additionalProperty = f.createIRI(nhc, "additional");

			// find amount of taxa
			String queryTax = "SELECT ?value WHERE {?iri rdf:type <http://rs.tdwg.org/dwc/terms/Taxon> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
			int taxonNr = QueryTripleStore(queryTax, repo, "value");
			IRI taxonRankIRI = f.createIRI(nhc, rank);
			IRI belongsToTaxonIRI = null;

			if (!belongstotaxon.equals("")) {
				belongsToTaxonIRI = f.createIRI(belongstotaxon);
			}

			IRI addIdentificationIRI = f.createIRI(nhc, "identification" + organismID + "_id" + identificationID);
			IRI addTaxonIRI = f.createIRI(nhc, "taxon" + taxonNr);
			IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");
			IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
			IRI identificationIDProperty = f.createIRI(dwc, "identificationID");

			IRI identificationClass = f.createIRI(dwc, "Identification");
			IRI taxonClass = f.createIRI(dwc, "Taxon");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(additionalIdentificationProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(addIdentificationIRI, toTaxonProperty, addTaxonIRI);
				conn.add(addIdentificationIRI, identificationIDProperty,
						f.createLiteral(organismID + "_id" + identificationID));
				conn.add(addIdentificationIRI, RDF.TYPE, identificationClass);

				conn.add(organismIRI, additionalIdentificationProperty, addIdentificationIRI);
				conn.add(annotationIRI, hasBodyProperty, addTaxonIRI);
				conn.add(addTaxonIRI, RDF.TYPE, semanticTagClass);

				conn.add(addTaxonIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(addTaxonIRI, RDF.TYPE, taxonClass);
				conn.add(addTaxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				conn.add(addTaxonIRI, taxonRankProperty, taxonRankIRI);
				conn.add(addTaxonIRI, RDF.VALUE, f.createLiteral(taxonNr));
				conn.commit();
			}
		} else if (property.equals("verbatimEventDate")) {
			int year = json.getInt("year");
			int month = json.getInt("month");
			int day = json.getInt("day");
			String organismID = json.getString("organismID");

			IRI dateIRI = f.createIRI(nhc, "date" + organismID);
			IRI yearProperty = f.createIRI(dwc, "year");
			IRI monthProperty = f.createIRI(dwc, "month");
			IRI dayProperty = f.createIRI(dwc, "day");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

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
			String organismID = json.getString("organismID");
			String queryMeasurementOrFact = "SELECT (COUNT(DISTINCT ?measurements) AS ?totalNumberOfInstances) WHERE { ?measurements <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/MeasurementOrFact>}";
			int measurementOrFactNr = QueryTripleStore(queryMeasurementOrFact, repo, "totalNumberOfInstances");
			IRI measurementOrFactIRI = f.createIRI(nhc, "measurementOrFact" + measurementOrFactNr);
			IRI hasDerivativeProperty = f.createIRI(dsw, "hasDerivative");
			IRI humanObservationIRI = f.createIRI(nhc, "humanObservation" + organismID);
			IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
			IRI measurementOrFactClass = f.createIRI(dwc, "MeasurementOrFact");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

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
			String propertyorattribute = json.getString("propertyorattribute");
			String organismID = json.getString("organismID");
			String queryMeasurementOrFact = "SELECT (COUNT(DISTINCT ?measurements) AS ?totalNumberOfInstances) WHERE { ?measurements <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/MeasurementOrFact>}";
			int measurementOrFactNr = QueryTripleStore(queryMeasurementOrFact, repo, "totalNumberOfInstances");
			String queryPropertyOrAttribute = "SELECT (COUNT(DISTINCT ?propertyorattribute) AS ?totalNumberOfInstances) WHERE { ?propertyorattribute <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type . ?type <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://identifiers.org/ncit/C20189>}";
			int propertyOrAttributeNr = QueryTripleStore(queryPropertyOrAttribute, repo, "totalNumberOfInstances");
			IRI measurementOrFactIRI = f.createIRI(nhc, "measurementOrFact" + measurementOrFactNr);
			IRI measuresOrDescribesProperty = f.createIRI(nhc, "measuresOrDescribes");
			IRI hasDerivativeProperty = f.createIRI(dsw, "hasDerivative");
			IRI humanObservationIRI = f.createIRI(nhc, "humanObservation" + organismID);
			IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
			IRI measurementOrFactClass = f.createIRI(dwc, "MeasurementOrFact");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");
			IRI propertyOrAttributeIRI = f.createIRI(nhc, "propertyOrAttribute" + propertyOrAttributeNr);
			IRI propertyOrAttributeClass = f.createIRI(propertyorattribute);
			IRI propertyOrAttributeTopClass = f.createIRI(ncit, "C20189");

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
			String anatomicalentity = json.getString("anatomicalentity");
			String organismID = json.getString("organismID");
			String queryMeasurementOrFact = "SELECT (COUNT(DISTINCT ?measurements) AS ?totalNumberOfInstances) WHERE { ?measurements <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/MeasurementOrFact>}";
			int measurementOrFactNr = QueryTripleStore(queryMeasurementOrFact, repo, "totalNumberOfInstances");
			String queryAnatomicalEntity = "SELECT (COUNT(DISTINCT ?anatomicalentity) AS ?totalNumberOfInstances) WHERE { ?anatomicalentity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type . ?type <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://purl.obolibrary.org/obo/UBERON_0001062>}";
			int anatomicalEntityNr = QueryTripleStore(queryAnatomicalEntity, repo, "totalNumberOfInstances");
			IRI measurementOrFactIRI = f.createIRI(nhc, "measurementOrFact" + measurementOrFactNr);
			IRI measuresOrDescribesProperty = f.createIRI(nhc, "measuresOrDescribes");
			IRI hasDerivativeProperty = f.createIRI(dsw, "hasDerivative");
			IRI humanObservationIRI = f.createIRI(nhc, "humanObservation" + organismID);
			IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
			IRI measurementOrFactClass = f.createIRI(dwc, "MeasurementOrFact");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");
			IRI anatomicalEntityIRI = f.createIRI(nhc, "anatomicalEntity" + anatomicalEntityNr);
			IRI anatomicalEntityClass = f.createIRI(anatomicalentity);
			IRI anatomicalEntityTopClass = f.createIRI("http://purl.obolibrary.org/obo/UBERON_0001062");

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
			String geonamesfeature = json.getString("geonamesfeature");
			String organismID = json.getString("organismID");
			IRI geonamesFeatureIRI = null;

			if (!geonamesfeature.equals("")) {
				geonamesFeatureIRI = f.createIRI(geonamesfeature);
			}

			IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
			IRI featureClass = f.createIRI(gn, "Feature");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");
			IRI locationIRI = f.createIRI(nhc, "location" + organismID);

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, locationIRI);
				conn.add(locationIRI, RDF.TYPE, semanticTagClass);
				conn.add(locationIRI, RDFS.LABEL, f.createLiteral(verbatim));

				if (!geonamesfeature.equals("")) {
					conn.add(locationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
					conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
				}
				conn.commit();
			}
		} else if (property.equals("additionalLocatedAt")) {
			String geonamesfeature = json.getString("geonamesfeature");
			String organismID = json.getString("organismID");
			String occurrenceID = json.getString("occurrenceID");

			IRI organismIRI = f.createIRI(nhc, "organism" + organismID);
			IRI additionalOccurrenceProperty = f.createIRI(nhc, "additionalOccurrence");
			IRI additionalProperty = f.createIRI(nhc, "additional");
			IRI addOccurrenceIRI = f.createIRI(nhc, "occurrence" + organismID + "_occ" + occurrenceID);
			IRI addEventIRI = f.createIRI(nhc, "event" + organismID + "_occ" + occurrenceID);
			IRI addLocationIRI = f.createIRI(nhc, "location" + organismID + "_occ" + occurrenceID);
			IRI addDateIRI = f.createIRI(nhc, "date" + organismID + "_occ" + occurrenceID);
			IRI geonamesFeatureIRI = null;

			if (!geonamesfeature.equals("")) {
				geonamesFeatureIRI = f.createIRI(geonamesfeature);
			}

			IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
			IRI atEventProperty = f.createIRI(dsw, "atEvent");
			IRI verbatimEventDateProperty = f.createIRI(nhc, "verbatimEventDate");
			IRI locatedAtProperty = f.createIRI(dsw, "locatedAt");
			IRI locatesProperty = f.createIRI(dsw, "locates");
			IRI eventOfProperty = f.createIRI(dsw, "eventOf");
			IRI occurrenceIDProperty = f.createIRI(dwc, "occurrenceID");
			IRI featureClass = f.createIRI(gn, "Feature");
			IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
			IRI eventClass = f.createIRI(dwc, "Event");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

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

				if (!geonamesfeature.equals("")) {
					conn.add(addLocationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
					conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
				}
				conn.commit();
			}
		} else if (property.equals("scientificNameAuthorship")) {
			String person = json.getString("person");
			String belongstotaxon = json.getString("belongstotaxon");

			IRI personIRI = f.createIRI(person);
			IRI belongsToTaxonIRI = f.createIRI(belongstotaxon);
			IRI scientificNameAuthorshipProperty = f.createIRI(nhc, "scientificNameAuthorship");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(belongsToTaxonIRI, scientificNameAuthorshipProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("identifiedBy")) {
			String person = json.getString("person");
			String organismID = json.getString("organismID");

			IRI personIRI = f.createIRI(person);
			IRI identificationIRI = f.createIRI(nhc, "identification" + organismID);
			IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(identificationIRI, identifiedByProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("recordedBy")) {
			String person = json.getString("person");
			String organismID = json.getString("organismID");

			IRI personIRI = f.createIRI(person);
			IRI occurrenceIRI = f.createIRI(nhc, "occurrence" + organismID);
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, personIRI);
				conn.add(personIRI, RDF.TYPE, semanticTagClass);
				conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(occurrenceIRI, recordedByProperty, personIRI);
				conn.commit();
			}
		} else if (property.equals("additionalRecordedBy")) {
			String person = json.getString("person");
			String organismID = json.getString("organismID");
			String occurrenceID = json.getString("occurrenceID");

			IRI addPersonIRI = f.createIRI(person);
			IRI organismIRI = f.createIRI(nhc, "organism" + organismID);
			IRI addOccurrenceIRI = f.createIRI(nhc, "occurrence" + organismID + "_occ" + occurrenceID);
			IRI additionalProperty = f.createIRI(nhc, "additional");
			IRI additionalOccurrenceProperty = f.createIRI(nhc, "additionalOccurrence");
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
			IRI occurrenceIDProperty = f.createIRI(dwc, "occurrenceID");
			IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
				conn.add(addOccurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID + "_occ" + occurrenceID));
				conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
				conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
				conn.add(annotationIRI, hasBodyProperty, addPersonIRI);
				conn.add(addPersonIRI, RDF.TYPE, semanticTagClass);
				conn.add(addPersonIRI, RDF.TYPE, FOAF.PERSON);
				conn.add(addOccurrenceIRI, recordedByProperty, addPersonIRI);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("person")) {
			String instance = json.getString("instance");

			IRI instanceIRI = f.createIRI(instance);
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);
				conn.add(instanceIRI, RDF.TYPE, semanticTagClass);
				conn.add(instanceIRI, RDF.TYPE, FOAF.PERSON);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("location")) {
			String instance = json.getString("instance");

			IRI instanceIRI = f.createIRI(instance);
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);
				conn.add(instanceIRI, RDF.TYPE, semanticTagClass);
				conn.add(instanceIRI, RDF.TYPE, DCTERMS.LOCATION);
				conn.commit();
			}
		} else if (property.equals("type") && type.equals("taxon")) {
			String belongstotaxon = json.getString("belongstotaxon");
			String rank = json.getString("rank");

			// find amount of taxa
			String queryTax = "SELECT ?value WHERE {?iri rdf:type <http://rs.tdwg.org/dwc/terms/Taxon> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
			int taxonNr = QueryTripleStore(queryTax, repo, "value");
			IRI taxonIRI = f.createIRI(nhc, "taxon" + taxonNr);
			IRI taxonRankIRI = f.createIRI(nhc, rank);
			IRI belongsToTaxonIRI = null;

			if (!belongstotaxon.equals("")) {
				belongsToTaxonIRI = f.createIRI(belongstotaxon);
			}

			IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
			IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");
			IRI taxonClass = f.createIRI(dwc, "Taxon");
			IRI semanticTagClass = f.createIRI(oa, "SemanticTag");

			try (RepositoryConnection conn = repo.getConnection()) {
				conn.begin();
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);
				conn.add(taxonIRI, RDF.TYPE, semanticTagClass);
				conn.add(taxonIRI, RDF.TYPE, taxonClass);

				if (!belongstotaxon.equals("")) {
					conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);
				}

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
		// Connect to RDF server
		// String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		// String repositoryID = repID;
		// Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		// repo.initialize();

		// Retrieve ANNNOTATION NUMBER:
		Value count = null;
		int c = 0;

		// COUNT NUMBER OF ANNOTATIONS FOR NEXT ANNO COUNT
		// try (RepositoryConnection conn = repo.getConnection()) {
		// TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		// List<BindingSet> resultList;
		// try (TupleQueryResult result = tupleQuery.evaluate()) {
		// resultList = QueryResults.asList(result);
		// }
		// valueCount = resultList.get(0).getValue(valueOf);
		// }

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
