package localdomain.localhost.servlets;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.IOException;

import java.util.Locale;
import java.util.UUID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.MissingResourceException;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;

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
		String date = (json.isNull("date")) ? "" : json.getString("date").trim();
		String annotator = (json.isNull("annotator")) ? "" : json.getString("annotator").replaceAll("/$|\\s+$", "");
		String source = (json.isNull("source")) ? "" : json.getString("source").replaceAll("/$|\\s+$", "");
		String selector = (json.isNull("selector")) ? "" : json.getString("selector").trim();
		String belongstotaxon = (json.isNull("belongstotaxon"))
				? ""
				: json.getString("belongstotaxon").replaceAll("/$|\\s+$", "");
		String rank = (json.isNull("rank")) ? "" : json.getString("rank").replaceAll("/$|\\s+$", "");
		String person = (json.isNull("person")) ? "" : json.getString("person").replaceAll("/$|\\s+$", "");
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
		String instance = (json.isNull("instance")) ? "" : json.getString("instance").replaceAll("/$|\\s+$", "");
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

		// connect to RDF server
		String host = "http://localhost:8080/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(host + "rdf4j-server/", repositoryID);
		ValueFactory f = repo.getValueFactory();

		// namespace prefixes
		String annot = host + "rdf/nc/annotation/" + uuid;
		String dcmitype = "http://purl.org/dc/dcmitype/";
		String dsw = "http://purl.org/dsw/";
		String dwc = "http://rs.tdwg.org/dwc/terms/";
		String dwciri = "http://rs.tdwg.org/dwc/iri/";
		String gn = "http://sws.geonames.org/";
		String gbif = "http://www.gbif.org/species/";
		String iso = "http://iso639-3.sil.org/code/";
		String img = String.join("/", Arrays.copyOfRange(arr, 0, arr.length - 1)) + "/";
		String mf = "http://www.w3.org/TR/media-frags/";
		String oa = "http://www.w3.org/ns/oa#";
		String obo = "http://purl.obolibrary.org/obo/";
		String orcid = "http://orcid.org/";
		String viaf = "http://viaf.org/viaf/";

		// init class
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
		IRI locationClass = f.createIRI(dwc, "Location");
		IRI tokenClass = f.createIRI(dsw, "Token");

		// init properties
		IRI hasSourceProperty = f.createIRI(oa, "hasSource");
		IRI hasSelectorProperty = f.createIRI(oa, "hasSelector");
		IRI hasBodyProperty = f.createIRI(oa, "hasBody");
		IRI hasTargetProperty = f.createIRI(oa, "hasTarget");
		IRI motivatedByProperty = f.createIRI(oa, "motivatedBy");
		IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");
		IRI taxonRankProperty = f.createIRI(dwc, "taxonRank");
		IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
		IRI measurementTypeProperty = f.createIRI(dwciri, "measurementType");
		IRI toTaxonProperty = f.createIRI(dwciri, "toTaxon");
		IRI scientificNameProperty = f.createIRI(dwc, "scientificName");
		IRI eventDateProperty = f.createIRI(dwc, "eventDate");
		IRI verbatimEventDateProperty = f.createIRI(dwc, "verbatimEventDate");
		IRI verbatimLocalityProperty = f.createIRI(dwc, "verbatimLocality");

		// init instances
		Literal verbatimLiteral;
		BNode targetBNode = f.createBNode();
		BNode textualBodyBNode = f.createBNode();
		BNode taxonBNode = f.createBNode();
		BNode selectorBNode = f.createBNode();
		IRI annotationIRI = f.createIRI(annot);
		IRI sourceIRI = f.createIRI(source);
		Resource instanceRes;
		Resource annotatorRes;
		Resource identificationRes;
		Resource taxonRankRes;

		// get MIME type given file suffix
		if (mapMime.containsKey(filext)) {
			mime = mapMime.get(filext);
		}

		// get well-formed ISO 639-3 language tag
		try {
			lang = Locale.forLanguageTag(lang).getISO3Language();
		} catch (MissingResourceException e) {
			lang = "und";
		} finally {
			if (lang.equals("")) {
				lang = "und";
			}
			verbatimLiteral = f.createLiteral(verbatim, lang);
		}

		// get datetime in ISO 8601 format
		try {
			date = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(ZonedDateTime.parse(date));
		} catch (DateTimeException e) {
			ZonedDateTime d = ZonedDateTime.now();
			date = DateTimeFormatter.ISO_INSTANT.format(d);
		}

		try {
			instance = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.parse(instance));
		} catch (DateTimeException e) {
			instance = "";
		}

		// get IRI for valid URL otherwise Bnode
		try {
			URL url = new URL(instance);
			instanceRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			instanceRes = f.createBNode();
		}

		try {
			URL url = new URL(annotator);
			annotatorRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			annotatorRes = f.createBNode();
		}

		try {
			URL url = new URL(belongstotaxon);
			identificationRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			identificationRes = f.createBNode();
		}

		try {
			URL url = new URL(rank);
			taxonRankRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			taxonRankRes = f.createBNode();
		}

		// construct RDF graph
		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			// add namespace prefixes
			conn.setNamespace("annot", annot);
			conn.setNamespace("dsw", dsw);
			conn.setNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
			conn.setNamespace("dcmitype", dcmitype);
			conn.setNamespace("dwc", dwc);
			conn.setNamespace("dwciri", dwciri);
			conn.setNamespace(FOAF.PREFIX, FOAF.NAMESPACE);
			conn.setNamespace("iso", iso);
			conn.setNamespace("img", img);
			conn.setNamespace("mf", mf);
			conn.setNamespace(RDF.PREFIX, RDF.NAMESPACE);
			conn.setNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
			conn.setNamespace("obo", obo);
			conn.setNamespace("orcid", orcid);
			conn.setNamespace("oa", oa);
			conn.setNamespace("viaf", viaf);
			conn.setNamespace("gn", gn);
			conn.setNamespace("gbif", gbif);

			// add triples
			conn.add(annotationIRI, RDF.TYPE, annotationClass);
			conn.add(annotationIRI, hasBodyProperty, textualBodyBNode);
			conn.add(annotationIRI, hasTargetProperty, targetBNode);
			conn.add(annotationIRI, motivatedByProperty, f.createIRI(oa, "describing"));
			conn.add(annotationIRI, DCTERMS.CREATOR, annotatorRes);
			conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date, DCTERMS.W3CDTF));
			conn.add(annotatorRes, RDF.TYPE, FOAF.PERSON);
			conn.add(targetBNode, DCTERMS.FORMAT, f.createLiteral(mime));
			conn.add(targetBNode, hasSourceProperty, sourceIRI);
			conn.add(targetBNode, hasSelectorProperty, selectorBNode);
			conn.add(targetBNode, RDF.TYPE, targetClass);
			conn.add(textualBodyBNode, RDF.TYPE, textualBodyClass);
			conn.add(textualBodyBNode, DCTERMS.FORMAT, f.createLiteral("text/plain"));
			conn.add(textualBodyBNode, DCTERMS.LANGUAGE, f.createIRI(iso, lang));
			conn.add(textualBodyBNode, RDF.VALUE, verbatimLiteral);
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(dcmitype, "StillImage"));
			conn.add(sourceIRI, RDF.TYPE, FOAF.IMAGE);
			conn.add(sourceIRI, RDF.TYPE, humanObservationClass);
			conn.add(sourceIRI, RDF.TYPE, tokenClass);
			conn.add(selectorBNode, RDF.TYPE, fragmentSelectorClass);
			conn.add(selectorBNode, RDF.VALUE, f.createLiteral(selector.replace("#", "")));
			conn.add(selectorBNode, DCTERMS.CONFORMS_TO, f.createIRI(mf));
			conn.add(sourceIRI, RDF.TYPE, sourceClass);

			switch (type) {
				case "taxon" :
					conn.add(identificationRes, RDF.TYPE, identificationClass);
					conn.add(identificationRes, toTaxonProperty, textualBodyBNode);
					conn.add(textualBodyBNode, RDF.TYPE, taxonClass);
					conn.add(textualBodyBNode, derivedFromProperty, sourceIRI);
					conn.add(textualBodyBNode, scientificNameProperty, verbatimLiteral);
					if (taxonRankRes.isIRI()) {
						conn.add(identificationRes, taxonRankProperty, taxonRankRes);
					}
					break;
				case "person" :
					conn.add(textualBodyBNode, RDF.TYPE, FOAF.PERSON);
					conn.add(textualBodyBNode, FOAF.NAME, verbatimLiteral);
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
					}
					break;
				case "location" :
					conn.add(textualBodyBNode, verbatimLocalityProperty, verbatimLiteral);
					conn.add(textualBodyBNode, RDF.TYPE, DCTERMS.LOCATION);
					conn.add(textualBodyBNode, RDF.TYPE, locationClass);
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
						conn.add(textualBodyBNode, inDescribedPlaceProperty, instanceRes);
					}
					break;
				case "measurementorfact" :
					conn.add(textualBodyBNode, derivedFromProperty, sourceIRI);
					conn.add(textualBodyBNode, RDF.TYPE, measurementOrFactClass);
					break;
				case "propertyorattribute" :
					conn.add(textualBodyBNode, derivedFromProperty, sourceIRI);
					conn.add(textualBodyBNode, RDF.TYPE, measurementOrFactClass);
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
						conn.add(measurementOrFactClass, measurementTypeProperty, instanceRes);
					}
					break;
				case "anatomicalentity" :
					conn.add(textualBodyBNode, derivedFromProperty, sourceIRI);
					conn.add(textualBodyBNode, RDF.TYPE, measurementOrFactClass);
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
						conn.add(measurementOrFactClass, measurementTypeProperty, instanceRes);
					}
					break;
				case "date" :
					conn.add(textualBodyBNode, RDF.TYPE, eventClass);
					conn.add(eventClass, verbatimEventDateProperty, verbatimLiteral);
					if (!instance.equals("")) {
						conn.add(eventClass, eventDateProperty, f.createLiteral(instance, DCTERMS.W3CDTF));
					}
					break;
				default :
					break;
			}
			conn.commit();
		}
	}
}
