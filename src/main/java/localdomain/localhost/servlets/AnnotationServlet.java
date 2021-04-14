package localdomain.localhost.servlets;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.IOException;

import java.util.Locale;
import java.util.UUID;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.MissingResourceException;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;

import javax.servlet.annotation.WebServlet;
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
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(loadOnStartup = 1, urlPatterns = "/annotation")
public class AnnotationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String host = "http://localhost:8080/";
	private static final String repositoryID = "mem-rdf";
	private static final Repository repo = new HTTPRepository(host + "rdf4j-server/", repositoryID);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get httpRequest parameters
		String anno = IOUtils.toString(request.getReader());
		JSONObject json = new JSONObject(anno);

		// retrieve key-value pairs
		String date = (json.isNull("date")) ? "" : json.getString("date").trim();
		String creator = (json.isNull("creator")) ? "" : json.getString("creator").replaceAll("/$|\\s+$", "");
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

		// init instances
		ValueFactory f = repo.getValueFactory();
		Literal verbatimLiteral;
		BNode targetBNode = f.createBNode();
		BNode textualBodyBNode = f.createBNode();
		BNode taxonBNode = f.createBNode();
		BNode selectorBNode = f.createBNode();
		IRI annotationIRI = f.createIRI(annot);
		IRI sourceIRI = f.createIRI(source);
		Resource instanceRes;
		Resource creatorRes;
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

		// get IRI for valid URL otherwise Bnode
		try {
			URL url = new URL(instance);
			instanceRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			instanceRes = f.createBNode();
		}

		try {
			URL url = new URL(creator);
			creatorRes = f.createIRI(url.toString());
		} catch (MalformedURLException e) {
			creatorRes = f.createBNode();
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
			conn.setNamespace(OWL.PREFIX, OWL.NAMESPACE);
			conn.setNamespace("viaf", viaf);
			conn.setNamespace("gn", gn);
			conn.setNamespace("gbif", gbif);

			// add triples
			conn.add(annotationIRI, RDF.TYPE, f.createIRI(oa, "Annotation"));
			conn.add(annotationIRI, f.createIRI(oa, "hasBody"), textualBodyBNode);
			conn.add(annotationIRI, f.createIRI(oa, "hasTarget"), targetBNode);
			conn.add(annotationIRI, f.createIRI(oa, "motivatedBy"), f.createIRI(oa, "describing"));
			conn.add(annotationIRI, DCTERMS.CREATOR, creatorRes);
			conn.add(annotationIRI, DCTERMS.DATE, f.createLiteral(date, DCTERMS.W3CDTF));
			conn.add(creatorRes, RDF.TYPE, FOAF.PERSON);
			conn.add(targetBNode, DCTERMS.FORMAT, f.createLiteral(mime));
			conn.add(targetBNode, f.createIRI(oa, "hasSource"), sourceIRI);
			conn.add(targetBNode, f.createIRI(oa, "hasSelector"), selectorBNode);
			conn.add(targetBNode, RDF.TYPE, f.createIRI(oa, "Target"));
			conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(oa, "TextualBody"));
			conn.add(textualBodyBNode, DCTERMS.FORMAT, f.createLiteral("text/plain"));
			conn.add(textualBodyBNode, DCTERMS.LANGUAGE, f.createIRI(iso, lang));
			conn.add(textualBodyBNode, RDF.VALUE, verbatimLiteral);
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(dcmitype, "StillImage"));
			conn.add(sourceIRI, RDF.TYPE, FOAF.IMAGE);
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(dwc, "HumanObservation"));
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(dsw, "Token"));
			conn.add(selectorBNode, RDF.TYPE, f.createIRI(oa, "FragmentSelector"));
			conn.add(selectorBNode, RDF.VALUE, f.createLiteral(selector.replace("#", "")));
			conn.add(selectorBNode, DCTERMS.CONFORMS_TO, f.createIRI(mf));
			conn.add(sourceIRI, RDF.TYPE, f.createIRI(oa, "Source"));

			switch (type) {
				case "taxon" :
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dwc, "Taxon"));
					conn.add(textualBodyBNode, f.createIRI(dwc, "scientificName"), verbatimLiteral);
					if (taxonRankRes.isIRI()) {
						conn.add(identificationRes, f.createIRI(dwc, "taxonRank"), taxonRankRes);
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, identificationRes);
					}
					break;
				case "person" :
					conn.add(textualBodyBNode, RDF.TYPE, FOAF.PERSON);
					conn.add(textualBodyBNode, RDF.TYPE, DCTERMS.AGENT);
					conn.add(textualBodyBNode, FOAF.NAME, verbatimLiteral);
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
					}
					break;
				case "location" :
					conn.add(textualBodyBNode, f.createIRI(dwc, "verbatimLocality"), verbatimLiteral);
					conn.add(textualBodyBNode, RDF.TYPE, DCTERMS.LOCATION);
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dwc, "Location"));
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
						conn.add(textualBodyBNode, f.createIRI(dwciri, "inDescribedPlace"), instanceRes);
					}
					break;
				case "measurementorfact" :
					conn.add(textualBodyBNode, f.createIRI(dsw, "derivedFrom"), sourceIRI);
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dwc, "MeasurementOrFact"));
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dsw, "Token"));
					if (instanceRes.isIRI()) {
						conn.add(textualBodyBNode, DCTERMS.IDENTIFIER, instanceRes);
						conn.add(textualBodyBNode, f.createIRI(dwciri, "measurementType"), instanceRes);
					}
					break;
				case "event" :
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dwc, "Event"));
					conn.add(textualBodyBNode, RDF.TYPE, f.createIRI(dcmitype, "Event"));
					conn.add(textualBodyBNode, f.createIRI(dwc, "verbatimEventDate"), verbatimLiteral);
					if (!instance.equals("")) {
						conn.add(textualBodyBNode, f.createIRI(dwc, "eventDate"),
								f.createLiteral(instance, DCTERMS.W3CDTF));
					}
					break;
				default :
					break;
			}
			conn.commit();
		} catch (RepositoryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long before = 0, after = 0;
		// get HTTP request parameters
		String anno = IOUtils.toString(request.getReader());
		JSONObject json = new JSONObject(anno);
		// retrieve key-value pairs
		String source = json.getString("source");
		String selector = json.getString("selector");
		// construct DELETE query
		String queryStr = String.join("\n", "PREFIX dcterms: <" + DCTERMS.NAMESPACE + ">",
				"PREFIX oa: <http://www.w3.org/ns/oa#>", "PREFIX rdf: <" + RDF.NAMESPACE + ">",
				"PREFIX xsd: <" + XSD.NAMESPACE + ">", "DELETE {?s ?p ?o} WHERE {", "  SELECT * WHERE {{",
				"    SELECT ?s (COUNT(?s) AS ?n) WHERE {", "      ?annot oa:hasTarget ?bnode ;",
				"        (oa:hasBody|oa:hasTarget|dcterms:creator|oa:hasSource|oa:hasSelector|dcterms:identifier)* ?s .",
				"      ?bnode oa:hasSource <" + source + "> .", "      ?bnode oa:hasSelector/rdf:value ?selector .",
				"      OPTIONAL { ?ss ?pp ?s } .", "	     FILTER(?selector = xsd:string(\"" + selector + "\"))",
				"    }", "    GROUP BY ?s", "  }", "  ?s ?p ?o .", "  FILTER (?n = 1)}}");

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			before = conn.size();
			Update query = conn.prepareUpdate(QueryLanguage.SPARQL, queryStr);
			query.execute();
			conn.commit();
			after = conn.size();
		} catch (MalformedQueryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (RepositoryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (UpdateExecutionException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			if (before == after) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String jsonStr = "";
		// construct SELECT query
		String queryStr = String.join("\n", "PREFIX oa: <http://www.w3.org/ns/oa#>",
				"PREFIX dcterms: <" + DCTERMS.NAMESPACE + ">", "PREFIX xsd: <" + XSD.NAMESPACE + ">",
				"PREFIX foaf: <" + FOAF.NAMESPACE + ">", "PREFIX dwc: <http://rs.tdwg.org/dwc/terms/>", "SELECT",
				"  ?annotation ?type ?verbatim ?creator ?date ?source ?selector", "WHERE {",
				"  ?annotation oa:hasTarget ?bnodeTarget ;", "    oa:hasBody ?bnodeBody ;",
				"    dcterms:creator ?creator ;", "    dcterms:date ?date .", "  ?bnodeBody a ?class ;",
				"    rdf:value ?verbatim .", "  ?bnodeTarget oa:hasSource ?source ;",
				"    oa:hasSelector/rdf:value ?selector .",
				"  FILTER(?class IN (foaf:Person, dwc:Taxon, dwc:Location, dwc:Event, dwc:MeasurementOrFact))",
				"  BIND(REPLACE(STR(?class), '.+/', '') AS ?type)", "}");

		try (RepositoryConnection conn = repo.getConnection()) {
			JSONArray array = new JSONArray();
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryStr);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				for (BindingSet bs : result) {
					JSONObject item = new JSONObject();
					item.put("annotation", bs.getValue("annotation").stringValue());
					item.put("type", bs.getValue("type").stringValue());
					item.put("verbatim", bs.getValue("verbatim").stringValue());
					item.put("creator", bs.getValue("creator").stringValue());
					item.put("date", bs.getValue("date").stringValue());
					item.put("source", bs.getValue("source").stringValue());
					item.put("selector", bs.getValue("selector").stringValue());
					array.put(item);
				}
				jsonStr = array.toString();
			} catch (QueryEvaluationException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			}
		} catch (RepositoryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			response.setContentType("application/json");
			response.getWriter().write(jsonStr);
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
