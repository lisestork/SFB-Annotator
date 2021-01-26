package localdomain.localhost.servlets;

import java.io.IOException;

import java.util.Locale;
import java.util.UUID;
import java.util.Arrays;
import java.util.HashMap;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import org.apache.commons.io.IOUtils;

import org.json.JSONObject;

/**
 * Servlet implementation class WriteAnnotations
 */
public class writeAnnotationsToRDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get httpRequest parameters
		String anno = IOUtils.toString(request.getReader());
		JSONObject json = new JSONObject(anno);

		// retrieve key-value pairs
		Integer year = (json.isNull("year")) ? null : json.getInt("year");
		Integer month = (json.isNull("month")) ? null : json.getInt("month");
		Integer day = (json.isNull("day")) ? null : json.getInt("day");

		String date = (json.isNull("date")) ? "" : json.getString("date").trim();
		String annotator = (json.isNull("annotator")) ? "" : json.getString("annotator").replaceAll("/$|\\s+$", "");
		String source = (json.isNull("source")) ? "" : json.getString("source").replaceAll("/$|\\s+$", "");
		String selector = (json.isNull("selector")) ? "" : json.getString("selector").trim();
		String belongstotaxon = (json.isNull("belongstotaxon"))
				? ""
				: json.getString("belongstotaxon").replaceAll("/$|\\s+$", "");
		String rank = (json.isNull("rank")) ? "" : json.getString("rank").replaceAll("/$|\\s+$", "");
		String person = (json.isNull("person")) ? "" : json.getString("person").replaceAll("/$|\\s+$", "");
		String organismID = (json.isNull("organismID")) ? "" : json.getString("organismID").trim();
		String occurrenceID = (json.isNull("occurrenceID")) ? "" : json.getString("occurrenceID").trim();
		String identificationID = (json.isNull("identificationID")) ? "" : json.getString("identificationID").trim();
		String geonamesfeature = (json.isNull("geonamesfeature"))
				? ""
				: json.getString("geonamesfeature").replaceAll("/$|\\s+$", "");
		String anatomicalentity = (json.isNull("anatomicalentity")) ? "" : json.getString("anatomicalentity").trim();
		String verbatim = (json.isNull("verbatim")) ? "" : json.getString("verbatim").trim();
		String type = (json.isNull("type")) ? "" : json.getString("type").trim();
		String property = (json.isNull("property")) ? "" : json.getString("property").trim();
		String propertyorattribute = (json.isNull("propertyorattribute"))
				? ""
				: json.getString("propertyorattribute").trim();
		String instance = (json.isNull("instance")) ? "" : json.getString("instance").trim();
		String lang = (json.isNull("language")) ? "" : json.getString("language").trim();
		String uuid = UUID.randomUUID().toString();
		String filext = source.substring(source.lastIndexOf(".") + 1).toLowerCase();
		String[] arr = source.split("/");
		String mime = "application/octet-stream"; // default: unknown file type

		// set MIME type based on file suffix
		HashMap<String, String> mapMime = new HashMap<>();
		mapMime.put("jpeg", "image/jpeg");
		mapMime.put("jpg", "image/jpeg");
		mapMime.put("tiff", "image/tiff");
		mapMime.put("tif", "image/tiff");
		mapMime.put("png", "image/png");

		if (mapMime.containsKey(filext)) {
			mime = mapMime.get(filext);
		}

		// return well-formed IETF BCP 47 language tag
		try {
			lang = Locale.forLanguageTag(lang).toLanguageTag();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return date/time in ISO 8601 format
		try {
			date = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(ZonedDateTime.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// namespace prefixes
		String dcmitype = "http://purl.org/dc/dcmitype/";
		String dsw = "http://purl.org/dsw/";
		String dwc = "http://rs.tdwg.org/dwc/terms/";
		String dwciri = "http://rs.tdwg.org/dwc/iri/";
		String gn = "http://sws.geonames.org/";
		String iso = "http://iso639-3.sil.org/code/";
		String img = String.join("/", Arrays.copyOfRange(arr, 0, arr.length - 1)) + "/";
		String mf = "http://www.w3.org/TR/media-frags/";
		String ncit = "http://identifiers.org/ncit/";
		String nc = "http://makingsense.liacs.nl/rdf/nc/";
		String nhc = "http://makingsense.liacs.nl/rdf/nhc/";
		String oa = "http://www.w3.org/ns/oa#";
		String obo = "http://purl.obolibrary.org/obo/";
		String orcid = "http://orcid.org/";
		String taxon = "http://identifiers.org/taxonomy/";
		String viaf = "http://viaf.org/viaf/";

		// Connect to RDF server
		String host = "http://localhost:8080/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(host + "rdf4j-server/", repositoryID);
		ValueFactory f = repo.getValueFactory();

		// init class
		IRI anatomicalEntityTopClass = f.createIRI(obo, "UBERON_0001062");
		IRI annotationClass = f.createIRI(oa, "Annotation");
		IRI featureClass = f.createIRI(gn, "Feature");
		IRI targetClass = f.createIRI(oa, "Target");
		IRI fragmentSelectorClass = f.createIRI(oa, "FragmentSelector");
		IRI sourceClass = f.createIRI(oa, "Source");
		IRI textualBodyClass = f.createIRI(oa, "TextualBody");
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
		// IRI hasPurposeProperty = f.createIRI(oa, "hasPurpose");
		IRI motivatedByProperty = f.createIRI(oa, "motivatedBy");
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
		IRI taxonRankProperty = f.createIRI(dwc, "taxonRank");
		IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
		IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
		IRI measuresOrDescribesProperty = f.createIRI(nhc, "measuresOrDescribes");
		IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
		IRI scientificNameAuthorshipProperty = f.createIRI(dwc, "scientificNameAuthorship");
		Resource propertyOrAttributeClass = (propertyorattribute.equals(""))
				? f.createBNode()
				: f.createIRI(propertyorattribute);

		// init instances
		IRI annotationIRI = f.createIRI(host, "rdf/nc/annotation/" + uuid);
		IRI sourceIRI = f.createIRI(source);
		IRI identificationIRI = f.createIRI(nc, "identification" + organismID);
		IRI humanObservationIRI = f.createIRI(nc, "humanObservation" + organismID);
		IRI organismIRI = f.createIRI(nc, "organism" + organismID);
		IRI occurrenceIRI = f.createIRI(nc, "occurrence" + organismID);
		IRI eventIRI = f.createIRI(nc, "event" + organismID);
		IRI dateIRI = f.createIRI(nc, "date" + organismID);
		IRI locationIRI = f.createIRI(nc, "location" + organismID);
		IRI addIdentificationIRI = f.createIRI(nc, "identification" + organismID + "_id" + identificationID);
		IRI addOccurrenceIRI = f.createIRI(nc, "occurrence" + organismID + "_occ" + occurrenceID);
		IRI addEventIRI = f.createIRI(nc, "event" + organismID + "_occ" + occurrenceID);
		IRI addLocationIRI = f.createIRI(nc, "location" + organismID + "_occ" + occurrenceID);
		IRI addDateIRI = f.createIRI(nc, "date" + organismID + "_occ" + occurrenceID);

		Resource instanceIRI = (instance.equals("")) ? f.createBNode() : f.createIRI(instance);
		Resource annotatorIRI = (annotator.equals("")) ? f.createBNode() : f.createIRI(annotator);
		Resource personIRI = (person.equals("")) ? f.createBNode() : f.createIRI(person);
		Resource taxonRankIRI = (rank.equals("")) ? f.createBNode() : f.createIRI(rank);
		Resource belongsToTaxonIRI = (belongstotaxon.equals("")) ? f.createBNode() : f.createIRI(belongstotaxon);
		Resource geonamesFeatureIRI = (geonamesfeature.equals("")) ? f.createBNode() : f.createIRI(geonamesfeature);

		BNode targetBNode = f.createBNode();
		BNode textualBodyBNode = f.createBNode();
		BNode taxonBNode = f.createBNode();
		BNode selectorBNode = f.createBNode();

		// query RDF store
		String query2 = "SELECT ?value WHERE { ?iri rdf:type <" + taxonClass.toString()
				+ "> . ?iri rdf:value ?value } ORDER BY DESC(?value) LIMIT 1";
		String query3 = "SELECT (COUNT(DISTINCT ?measurements) AS ?totalNumberOfInstances) WHERE { ?measurements rdf:type <"
				+ measurementOrFactClass.toString() + "> . }";
		int measurementOrFactNr = QueryTripleStore(query3, repo);
		IRI measurementOrFactIRI = f.createIRI(nc, "measurementOrFact" + measurementOrFactNr);

		String query4 = "SELECT (COUNT(DISTINCT ?propertyorattribute) AS ?totalNumberOfInstances) WHERE { ?propertyorattribute rdf:type ?type . ?type rdfs:subClassOf <"
				+ propertyOrAttributeTopClass.toString() + "> . }";
		int propertyOrAttributeNr = QueryTripleStore(query4, repo);
		IRI propertyOrAttributeIRI = f.createIRI(nc, "propertyOrAttribute" + propertyOrAttributeNr);

		String query5 = "SELECT (COUNT(DISTINCT ?anatomicalentity) AS ?totalNumberOfInstances) WHERE { ?anatomicalentity rdf:type ?type . ?type rdfs:subClassOf <"
				+ anatomicalEntityTopClass.toString() + "> . }";
		int anatomicalEntityNr = QueryTripleStore(query5, repo);
		IRI anatomicalEntityIRI = f.createIRI(nc, "anatomicalEntity" + anatomicalEntityNr);

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			// add namespace prefixes
			conn.setNamespace("dsw", dsw);
			conn.setNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
			conn.setNamespace("dcmitype", dcmitype);
			conn.setNamespace("dwc", dwc);
			conn.setNamespace("dwciri", dwciri);
			conn.setNamespace(FOAF.PREFIX, FOAF.NAMESPACE);
			conn.setNamespace("iso", iso);
			conn.setNamespace("img", img);
			conn.setNamespace("mf", mf);
			conn.setNamespace("ncit", ncit);
			conn.setNamespace("nhc", nhc);
			conn.setNamespace(RDF.PREFIX, RDF.NAMESPACE);
			conn.setNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
			conn.setNamespace("obo", obo);
			conn.setNamespace("orcid", orcid);
			conn.setNamespace("oa", oa);
			conn.setNamespace("taxon", taxon);
			conn.setNamespace("viaf", viaf);
			conn.setNamespace("gn", gn);
			// add triples
			conn.add(annotationIRI, RDF.TYPE, annotationClass);
			conn.add(annotationIRI, hasBodyProperty, textualBodyBNode);
			conn.add(annotationIRI, hasTargetProperty, targetBNode);
			conn.add(annotationIRI, motivatedByProperty, f.createIRI(oa, "describing"));
			conn.add(annotationIRI, DCTERMS.CREATOR, annotatorIRI);
			conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date, DCTERMS.W3CDTF));
			conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
			conn.add(targetBNode, DCTERMS.FORMAT, f.createLiteral(mime));
			conn.add(targetBNode, hasSourceProperty, sourceIRI);
			conn.add(targetBNode, hasSelectorProperty, selectorBNode);
			conn.add(targetBNode, RDF.TYPE, targetClass);
			conn.add(textualBodyBNode, RDF.TYPE, textualBodyClass);
			conn.add(textualBodyBNode, DCTERMS.FORMAT, f.createLiteral("text/plain"));
			conn.add(textualBodyBNode, DCTERMS.LANGUAGE, f.createIRI(iso, lang));
			conn.add(textualBodyBNode, RDF.VALUE, f.createLiteral(verbatim, lang));
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(dcmitype, "StillImage"));
			conn.add(sourceIRI, RDF.TYPE, FOAF.IMAGE);
			conn.add(selectorBNode, RDF.TYPE, fragmentSelectorClass);
			conn.add(selectorBNode, RDF.VALUE, f.createLiteral(selector.replace("#", "")));
			conn.add(selectorBNode, DCTERMS.CONFORMS_TO, f.createIRI(mf));
			conn.add(sourceIRI, RDF.TYPE, sourceClass);

			switch (property) {
				case "hasIdentification" :
					conn.add(annotationIRI, hasBodyProperty, taxonBNode);
					conn.add(annotationIRI, hasBodyProperty, humanObservationIRI);
					conn.add(annotationIRI, hasTargetProperty, sourceIRI);
					conn.add(eventIRI, verbatimEventDateProperty, dateIRI);
					conn.add(eventIRI, locatedAtProperty, locationIRI);
					conn.add(eventIRI, eventOfProperty, occurrenceIRI);
					conn.add(eventIRI, RDF.TYPE, eventClass);
					conn.add(dateIRI, RDF.TYPE, DCTERMS.DATE);
					conn.add(humanObservationIRI, isBasisForIdProperty, identificationIRI);
					conn.add(humanObservationIRI, derivedFromProperty, organismIRI);
					conn.add(humanObservationIRI, evidenceForProperty, occurrenceIRI);
					conn.add(humanObservationIRI, RDF.TYPE, humanObservationClass);
					conn.add(identificationIRI, toTaxonProperty, taxonBNode);
					conn.add(identificationIRI, isBasedOnProperty, humanObservationIRI);
					conn.add(identificationIRI, identifiedByProperty, personIRI);
					conn.add(identificationIRI, identifiesProperty, organismIRI);
					conn.add(identificationIRI, identificationIDProperty, f.createLiteral(organismID));
					conn.add(identificationIRI, RDF.TYPE, identificationClass);
					conn.add(locationIRI, locatesProperty, eventIRI);
					conn.add(locationIRI, RDF.TYPE, DCTERMS.LOCATION);
					conn.add(occurrenceIRI, hasEvidenceProperty, humanObservationIRI);
					conn.add(occurrenceIRI, atEventProperty, eventIRI);
					conn.add(occurrenceIRI, occurrenceOfProperty, organismIRI);
					conn.add(occurrenceIRI, recordedByProperty, personIRI);
					conn.add(occurrenceIRI, RDF.TYPE, occurrenceClass);
					conn.add(organismIRI, hasIdentificationProperty, identificationIRI);
					conn.add(organismIRI, hasOccurrenceProperty, occurrenceIRI);
					conn.add(organismIRI, hasDerivativeProperty, humanObservationIRI);
					conn.add(organismIRI, organismIDProperty, f.createLiteral(organismID));
					conn.add(organismIRI, RDF.TYPE, organismClass);
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
					conn.add(taxonBNode, belongsToTaxonProperty, belongsToTaxonIRI);
					conn.add(taxonBNode, taxonRankProperty, taxonRankIRI);
					conn.add(taxonBNode, RDFS.LABEL, f.createLiteral(verbatim, lang));
					conn.add(taxonBNode, RDF.TYPE, taxonClass);
					break;
				/*
				 * case "additionalIdentification" : break;
				 */
				case "verbatimEventDate" :
					conn.add(annotationIRI, hasBodyProperty, dateIRI);
					conn.add(dateIRI, RDFS.LABEL, f.createLiteral(verbatim, lang));

					if (year != null) {
						conn.add(dateIRI, yearProperty, f.createLiteral(year));
					}
					if (month != null) {
						conn.add(dateIRI, monthProperty, f.createLiteral(month));
					}
					if (day != null) {
						conn.add(dateIRI, dayProperty, f.createLiteral(day));
					}
					break;
				case "locatedAt" :
					conn.add(annotationIRI, hasBodyProperty, locationIRI);
					conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
					conn.add(locationIRI, RDFS.LABEL, f.createLiteral(verbatim, lang));
					conn.add(locationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
					break;
				case "additionalLocatedAt" :
					conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
					conn.add(addEventIRI, verbatimEventDateProperty, addDateIRI);
					conn.add(addEventIRI, locatedAtProperty, addLocationIRI);
					conn.add(addEventIRI, eventOfProperty, addOccurrenceIRI);
					conn.add(addEventIRI, RDF.TYPE, eventClass);
					conn.add(addOccurrenceIRI, atEventProperty, addEventIRI);
					conn.add(addOccurrenceIRI, occurrenceIDProperty,
							f.createLiteral(organismID + "_occ" + occurrenceID));
					conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
					conn.add(addLocationIRI, locatesProperty, addEventIRI);
					conn.add(addLocationIRI, RDFS.LABEL, f.createLiteral(verbatim, lang));
					conn.add(addLocationIRI, RDF.TYPE, DCTERMS.LOCATION);
					conn.add(addLocationIRI, inDescribedPlaceProperty, geonamesFeatureIRI);
					conn.add(annotationIRI, hasBodyProperty, addLocationIRI);
					conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
					conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
					break;
				case "scientificNameAuthorship" :
					conn.add(annotationIRI, hasBodyProperty, personIRI);
					conn.add(belongsToTaxonIRI, scientificNameAuthorshipProperty, personIRI);
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
					break;
				case "identifiedBy" :
					conn.add(annotationIRI, hasBodyProperty, personIRI);
					conn.add(identificationIRI, identifiedByProperty, personIRI);
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
					break;
				case "recordedBy" :
					conn.add(annotationIRI, hasBodyProperty, personIRI);
					conn.add(occurrenceIRI, recordedByProperty, personIRI);
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
					break;
				case "additionalRecordedBy" :
					conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
					conn.add(addOccurrenceIRI, recordedByProperty, personIRI);
					conn.add(addOccurrenceIRI, occurrenceIDProperty,
							f.createLiteral(organismID + "_occ" + occurrenceID));
					conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
					conn.add(additionalOccurrenceProperty, RDFS.SUBPROPERTYOF, additionalProperty);
					conn.add(annotationIRI, hasBodyProperty, personIRI);
					conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
					conn.add(personIRI, RDF.TYPE, FOAF.PERSON);
					break;
				case "type" :
					if (type.equals("person")) {
						conn.add(annotationIRI, hasBodyProperty, instanceIRI);
						conn.add(instanceIRI, RDF.TYPE, FOAF.PERSON);
					} else if (type.equals("location")) {
						conn.add(annotationIRI, hasBodyProperty, instanceIRI);
						conn.add(instanceIRI, RDF.TYPE, DCTERMS.LOCATION);
					} else if (type.equals("taxon")) {
						conn.add(annotationIRI, hasBodyProperty, belongsToTaxonIRI);
						conn.add(belongsToTaxonIRI, RDF.TYPE, taxonClass);
						conn.add(textualBodyBNode, belongsToTaxonProperty, belongsToTaxonIRI);
						conn.add(belongsToTaxonIRI, taxonRankProperty, taxonRankIRI);
					}
					break;
				default :
					break;
			}

			switch (type) {
				case "measurementorfact" :
					conn.add(annotationIRI, hasBodyProperty, measurementOrFactIRI);
					conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
					conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
					conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
					break;
				case "propertyorattribute" :
					conn.add(annotationIRI, hasBodyProperty, propertyOrAttributeIRI);
					conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
					conn.add(measurementOrFactIRI, measuresOrDescribesProperty, propertyOrAttributeIRI);
					conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
					conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
					conn.add(propertyOrAttributeIRI, RDF.TYPE, propertyOrAttributeClass);
					conn.add(propertyOrAttributeClass, RDFS.SUBCLASSOF, propertyOrAttributeTopClass);
					conn.add(propertyOrAttributeIRI, RDFS.LABEL, f.createLiteral(verbatim, lang));
					break;
				case "anatomicalentity" :
					conn.add(annotationIRI, hasBodyProperty, anatomicalEntityIRI);
					conn.add(anatomicalEntityIRI, RDF.TYPE, anatomicalEntityClass);
					conn.add(anatomicalEntityClass, RDFS.SUBCLASSOF, anatomicalEntityTopClass);
					conn.add(anatomicalEntityIRI, RDFS.LABEL, f.createLiteral(verbatim, lang));
					conn.add(humanObservationIRI, hasDerivativeProperty, measurementOrFactIRI);
					conn.add(measurementOrFactIRI, measuresOrDescribesProperty, anatomicalEntityIRI);
					conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI);
					conn.add(measurementOrFactIRI, RDF.TYPE, measurementOrFactClass);
					break;
				default :
					break;
			}
			conn.commit();
		}
	}

	public int QueryTripleStore(String query, Repository repo) {
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
}
