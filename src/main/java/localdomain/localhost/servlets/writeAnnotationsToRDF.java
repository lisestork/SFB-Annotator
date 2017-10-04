package localdomain.localhost.servlets;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
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
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class WriteAnnotations
 */
public class writeAnnotationsToRDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		//get parameters that were sent with the httpRequest
		String anno = request.getParameter("annotation");
		
		//convert string to annotation object to store into rdf
		anno = anno.replaceAll("\"", "\\\"");
		anno = anno.replaceAll("\\[", "").replaceAll("\\]","");
	    
        JSONObject json = new JSONObject(anno);
        System.out.println(json);
        
        //retrieve key-value pairs
	        
        //annotation information
        String date = json.getString("date");
        String annotator = json.getString("annotator");

        //target
        String source = json.getString("source");
        String selector = json.getString("selector");
        String target = source+selector;
        
        //textual body
        String verbatim = json.getString("verbatim");
        String language = json.getString("language");
        
        //semantic body
        String type = json.getString("type");
        String property = json.getString("property");
        
 		String nc = "http://testingsense.liacs.nl/rdf/nc#";
		String oa = "http://www.w3.org/ns/oa#";
		String dwc = "http://rs.tdwg.org/dwc/terms/";
		String dwciri = "http://rs.tdwg.org/dwc/iri/";
		String dsw = "http://purl.org/dsw/";
		String ncd = "http://rs.tdwg.org/ontology/voc/Collection#";
		String ncdType = "http://rs.tdwg.org/ontology/voc/CollectionType#";
		String dcterms = "http://purl.org/dc/terms/";
		String dc = "http://purl.org/dc/elements/1.1/";
		String ex = "http://testingsense.liacs.nl/rdf/example/";
		String uberon = "http://purl.obolibrary.org/obo/";
		String gn = "http://www.geonames.org/ontology#";
		String foaf = "http://xmlns.com/foaf/0.1/";
		String nhc = "http://example.org/nhc/";
		
	    //Connect to RDF server   
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = "AN1";
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();

		//create valuefactory for IRIs
		ValueFactory f = repo.getValueFactory();
		
		//retrieve nr of annotations
		String query = "SELECT (COUNT(DISTINCT ?s) AS ?totalNumberOfInstances) WHERE { ?s rdf:type <http://www.w3.org/ns/oa#Annotation> }";
		int annoID = QueryTripleStore(query, "AN1", "totalNumberOfInstances");
		
		//class initialization
		IRI annotationClass = f.createIRI(oa, "Annotation");
		IRI targetClass = f.createIRI(oa, "Target");
		IRI selectorClass = f.createIRI(oa, "Selector");
		IRI sourceClass = f.createIRI(oa, "Source");
		IRI textualTagClass = f.createIRI(oa, "TextualTag");
		
		//property initialization
		IRI hasSourceProperty = f.createIRI(oa, "hasSource");
		IRI hasSelectorProperty = f.createIRI(oa, "hasSelector");
		IRI hasBodyProperty = f.createIRI(oa, "hasBody");
		IRI hasTargetProperty = f.createIRI(oa, "hasTarget");
		IRI createdProperty = f.createIRI(dcterms, "created");
		IRI dateProperty = f.createIRI(dc, "date");
		
		//instance initialization
		IRI annotationIRI = f.createIRI(nc, "anno"+annoID);
		IRI textualBodyIRI = f.createIRI(nc, "textualBody"+annoID);
		IRI targetIRI = f.createIRI(nc, target);
		IRI sourceIRI = f.createIRI(nc, source);
		IRI selectorIRI = f.createIRI(nc, selector);
		IRI annotatorIRI = f.createIRI(nc, annotator.replaceAll("\\s","_"));

		//STORE TRANSCRIPTION
		rdf4jServer = "http://localhost:8080/rdf4j-server/";
		repositoryID = "AN1";
		repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();
		
		try (RepositoryConnection conn = repo.getConnection()){	
			
		   conn.begin();
		   
		   ////NORMAL ANNO, TARGET & BODY ASSERTIONS/////////
		   
		   //textual body
		   conn.add(textualBodyIRI, RDF.TYPE, textualTagClass);
		   conn.add(textualBodyIRI, RDFS.LABEL, f.createLiteral(verbatim));
		   conn.add(textualBodyIRI,  DC.FORMAT, f.createLiteral("text/plain"));
		   if(!language.equals("")){ conn.add(textualBodyIRI, DC.LANGUAGE, f.createLiteral(language));}
		   
		   //target
		   conn.add(targetIRI, RDF.TYPE, targetClass);
		   conn.add(sourceIRI,  RDF.TYPE, sourceClass);
		   conn.add(targetIRI, hasSourceProperty, sourceIRI);
		   conn.add(targetIRI, hasSelectorProperty, selectorIRI);
		   conn.add(selectorIRI, RDF.TYPE, selectorClass);
		   conn.add(selectorIRI, RDF.VALUE, f.createLiteral(selector));
		   
		   //link annotation to all metadata, incl body & target
		   conn.add(annotationIRI, RDF.TYPE, annotationClass);
		   conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
		   conn.add(annotationIRI, createdProperty, annotatorIRI);
		   conn.add(annotationIRI, dateProperty, f.createLiteral(date));
		   conn.add(annotationIRI, hasBodyProperty, textualBodyIRI);		   
		   conn.add(annotationIRI, hasTargetProperty, targetIRI);
		   
		   conn.commit();

		};

        //retrieve semantic annotations    
		if(property.equals("hasIdentification")){
			System.out.println("Annotated identification, add semantics");

			String belongstotaxon = json.getString("belongstotaxon");
			String rank = json.getString("rank");
			String person = json.getString("person");
			String organismID = json.getString("organismID");
			
		    //query the DB for the taxon nr
		    String queryTax = "SELECT (COUNT(DISTINCT ?taxon) AS ?totalNumberOfInstances) WHERE { ?taxon <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/Taxon>}";
	    	int taxonNr = QueryTripleStore(queryTax, "AN", "totalNumberOfInstances");		
			
			//retrieve nr of annotations
			query = "SELECT (COUNT(DISTINCT ?s) AS ?totalNumberOfInstances) WHERE { ?s rdf:type <http://www.w3.org/ns/oa#Annotation> }";
			annoID = QueryTripleStore(query, "AN1", "totalNumberOfInstances");

			//SEMANTIC IRI'S
			//instances
			IRI identificationIRI = f.createIRI(nc, "identification"+organismID);
			IRI humanObservationIRI = f.createIRI(nc, "humanObservation"+organismID);
			IRI organismIRI = f.createIRI(nc, "organism"+organismID);
			IRI occurrenceIRI = f.createIRI(nc, "occurrence"+organismID);
			IRI eventIRI = f.createIRI(nc, "event"+organismID);
			IRI dateIRI = f.createIRI(nc, "date"+organismID);
			IRI taxonIRI = f.createIRI(nc, "taxon"+taxonNr); 
			IRI locationIRI = f.createIRI(nc, "location"+organismID);	
			IRI taxonRankIRI = f.createIRI(nc, rank);
			IRI HOannotationIRI = f.createIRI(nc, "anno"+annoID);
			IRI belongsToTaxonIRI = null;
			if (!belongstotaxon.equals("")){ belongsToTaxonIRI = f.createIRI(belongstotaxon);};
			IRI personIRI = null; 
			if (!person.equals("")){ personIRI = f.createIRI(person);};

			//properties
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
			IRI verbatimEventDateProperty = f.createIRI(dwciri, "verbatimEventDate");
			IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
			IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");
			IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");	
			
			//class
			IRI identificationClass = f.createIRI(dwc, "Identification");
			IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
			IRI humanObservationClass = f.createIRI(dwc, "HumanObservation");
			IRI organismClass  = f.createIRI(dwc, "Organism");
			IRI eventClass = f.createIRI(dwc, "Event");
			IRI taxonClass = f.createIRI(dwc, "Taxon");
			IRI locationClass = f.createIRI(dcterms, "Location");
			IRI dateClass = f.createIRI(nhc, "Date");
			
			try (RepositoryConnection conn = repo.getConnection()){	
					
				conn.begin();

				//add to annotation object 
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);		   
				conn.add(taxonIRI, RDFS.LABEL, f.createLiteral(verbatim));

				//link instances
				conn.add(occurrenceIRI, hasEvidenceProperty, humanObservationIRI);
				conn.add(occurrenceIRI, atEventProperty, eventIRI);
				conn.add(occurrenceIRI, occurrenceOfProperty, organismIRI);	
				conn.add(occurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID));
				if(!person.equals("")){conn.add(occurrenceIRI, recordedByProperty, personIRI);};
				conn.add(occurrenceIRI, RDF.TYPE, occurrenceClass);
				
				conn.add(eventIRI, verbatimEventDateProperty, dateIRI);
				conn.add(eventIRI, locatedAtProperty, locationIRI);
				conn.add(eventIRI, eventOfProperty, occurrenceIRI);
				conn.add(eventIRI,  RDF.TYPE, eventClass);
				
				conn.add(locationIRI,  locatesProperty, eventIRI);
				conn.add(organismIRI, hasIdentificationProperty, identificationIRI);
				conn.add(organismIRI,  hasOccurrenceProperty, occurrenceIRI);
				conn.add(organismIRI,  hasDerivativeProperty, humanObservationIRI);
				conn.add(organismIRI, organismIDProperty, f.createLiteral(organismID));
				conn.add(organismIRI, RDF.TYPE, organismClass);
				
				conn.add(identificationIRI, toTaxonProperty, taxonIRI);
				conn.add(identificationIRI, isBasedOnProperty, humanObservationIRI);
				if(!person.equals("")){conn.add(identificationIRI,  identifiedByProperty, personIRI);};
				conn.add(identificationIRI,  identifiesProperty, organismIRI);
				conn.add(identificationIRI, identificationIDProperty, f.createLiteral(organismID));
				conn.add(identificationIRI,  RDF.TYPE, identificationClass);
				
				conn.add(humanObservationIRI, isBasisForIdProperty, identificationIRI);
				conn.add(humanObservationIRI,  derivedFromProperty, organismIRI);
				conn.add(humanObservationIRI,  evidenceForProperty, occurrenceIRI);
				conn.add(humanObservationIRI,  RDF.TYPE, humanObservationClass);
				
				conn.add(HOannotationIRI, hasBodyProperty, humanObservationIRI);
				conn.add(HOannotationIRI, hasTargetProperty, sourceIRI);
			    conn.add(HOannotationIRI, RDF.TYPE, annotationClass);
			    conn.add(annotatorIRI, RDF.TYPE, FOAF.PERSON);
			    conn.add(HOannotationIRI, createdProperty, annotatorIRI);
			    conn.add(HOannotationIRI, dateProperty, f.createLiteral(date));
				
				if(!belongstotaxon.equals("")){conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);};
				conn.add(taxonIRI,  taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI,  RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(taxonIRI, RDF.TYPE, taxonClass);

				conn.add(dateIRI,  RDF.TYPE, dateClass);
				//add classes
				conn.add(locationIRI, RDF.TYPE, locationClass);
				
				if(!person.equals("")){conn.add(personIRI,  RDF.TYPE, FOAF.PERSON);};
				
				conn.commit();	
			};
		} else if (property.equals("additionalIdentification")){
			String organismID = json.getString("organismID");
			
			IRI organismIRI = f.createIRI(nc, "organism"+organismID);	
			IRI toTaxonProperty = f.createIRI(dwciri, "toTaxon");
			IRI additionalIdentificationProperty = f.createIRI(nhc, "additionalIdentification");			

			//find amount of taxa
		    String queryTax = "SELECT (COUNT(DISTINCT ?taxon) AS ?totalNumberOfInstances) WHERE { ?taxon <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/Taxon>}";
	    	int taxonNr = QueryTripleStore(queryTax, "AN1", "totalNumberOfInstances");    	

	    	//find additional identifications
		    String queryId = "SELECT (COUNT(DISTINCT ?additonalIdentification) AS ?totalNumberOfInstances) WHERE { <+"+organismIRI+"> <"+additionalIdentificationProperty+"> ?additonalIdentification}";
	    	int additionalIdNr = QueryTripleStore(queryId, "AN1", "totalNumberOfInstances");	
	
			IRI addIdentificationIRI = f.createIRI(nc, "identification"+organismID+"_add"+additionalIdNr);
			IRI addTaxonIRI = f.createIRI(nc, "taxon"+taxonNr); 
			
			IRI identificationIDProperty = f.createIRI(dwc, "identificationID");

			IRI identificationClass = f.createIRI(dwc, "Identification");
			IRI taxonClass = f.createIRI(dwc, "Taxon");

			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();
				conn.add(addIdentificationIRI, toTaxonProperty, addTaxonIRI);
				conn.add(addIdentificationIRI, identificationIDProperty, f.createLiteral(organismID+"_add"+additionalIdNr));
				conn.add(addIdentificationIRI,  RDF.TYPE, identificationClass);
				
				conn.add(organismIRI,  additionalIdentificationProperty, addIdentificationIRI);			
				conn.add(annotationIRI, hasBodyProperty, addTaxonIRI);		
				
				conn.add(addTaxonIRI, RDFS.LABEL, f.createLiteral(verbatim));	
				conn.add(addTaxonIRI,  RDF.TYPE, taxonClass);
				conn.commit();	
			};

		} else if (property.equals("verbatimEventDate")){
			String year = json.getString("year");
			String month = json.getString("month");
			String day = json.getString("day");
			String organismID = json.getString("organismID");
			
			IRI dateIRI = f.createIRI(nc, "date"+organismID);
			IRI yearProperty = f.createIRI(dwc, "year");
			IRI monthProperty = f.createIRI(dwc, "month");
			IRI dayProperty = f.createIRI(dwc, "day");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, dateIRI);	
				conn.add(dateIRI, RDFS.LABEL, f.createLiteral(verbatim));
				if(!year.equals("")){conn.add(dateIRI,  yearProperty, f.createLiteral(year));};
				if(!month.equals("")){conn.add(dateIRI,  monthProperty, f.createLiteral(month));};
				if(!day.equals("")){conn.add(dateIRI,  dayProperty, f.createLiteral(day));};
				conn.commit();	
			};
			
		} else if (property.equals("measuresOrDescribes")){
			String organismID = json.getString("organismID");
			
			IRI measurementOrFactIRI = f.createIRI(nc, "measurementOrFact"+organismID);
			IRI measuresOrDescribesProperty = f.createIRI(nhc,"measuresOrDescribes" );
			IRI humanObservationIRI = f.createIRI(nc, "humanObservation"+organismID);
			IRI derivedFromProperty = f.createIRI(dsw, "derivedFrom");			
			IRI measurementOrFactClass = f.createIRI(dwc, "measurementOrFact");

			if (type.equals("property") || type.equals("attribute")){
				String propertyorattribute = json.getString("propertyorattribute");

				IRI propertyOrAttributeIRI = f.createIRI(nc, verbatim);	
				IRI propertyOrAttributeClass = f.createIRI("http://purl.obolibrary.org/obo/NCIT_C20189");
				if(!propertyorattribute.equals("")){propertyOrAttributeClass = f.createIRI(propertyorattribute);};

				try (RepositoryConnection conn = repo.getConnection()){	
					
					conn.begin();
					conn.add(annotationIRI, hasBodyProperty, propertyOrAttributeIRI);	
					conn.add(propertyOrAttributeIRI,  RDF.TYPE, propertyOrAttributeClass);
					conn.add(propertyOrAttributeIRI,  RDFS.LABEL, f.createLiteral(verbatim));
					conn.add(measurementOrFactIRI, measuresOrDescribesProperty, propertyOrAttributeIRI);
					conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI );
					conn.add(measurementOrFactIRI,  RDF.TYPE, measurementOrFactClass);
					conn.commit();	
				};
				
			} else if (type.equals("anatomicalentity")){
				String anatomicalentity = json.getString("anatomicalentity");
				
				IRI anatomicalEntityIRI = f.createIRI(nc, verbatim);				
				IRI anatomicalEntityClass = f.createIRI("http://purl.obolibrary.org/obo/UBERON_0001062");
				if(!anatomicalEntityClass.equals("")){anatomicalEntityClass = f.createIRI(anatomicalentity);};

				try (RepositoryConnection conn = repo.getConnection()){	
					
					conn.begin();	
					conn.add(annotationIRI, hasBodyProperty, anatomicalEntityIRI);	
					conn.add(anatomicalEntityIRI,  RDF.TYPE, anatomicalEntityClass);
					conn.add(anatomicalEntityIRI,  RDFS.LABEL, f.createLiteral(verbatim));
					conn.add(measurementOrFactIRI, measuresOrDescribesProperty, anatomicalEntityIRI);
					conn.add(measurementOrFactIRI, derivedFromProperty, humanObservationIRI );
					conn.add(measurementOrFactIRI,  RDF.TYPE, measurementOrFactClass);
					conn.commit();	
				};
			}
		} else if (property.equals("locatedAt")){
			String geonamesfeature = json.getString("geonamesfeature");
			String organismID = json.getString("organismID");
			
			IRI geonamesFeatureIRI = null;
			if (!geonamesfeature.equals("")){geonamesFeatureIRI = f.createIRI(geonamesfeature);};
			IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
			IRI featureClass = f.createIRI(gn, "Feature");		
			IRI locationIRI = f.createIRI(nc, "location"+organismID);	

			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, locationIRI);	
				conn.add(locationIRI, RDFS.LABEL, f.createLiteral(verbatim));
				if(!geonamesfeature.equals("")){
					conn.add(locationIRI,  inDescribedPlaceProperty, geonamesFeatureIRI);
					conn.add(geonamesFeatureIRI, RDF.TYPE, featureClass);
				};	
				conn.commit();	
			};
			
		} else if (property.equals("additionalLocatedAt")){
			String geonamesfeature = json.getString("geonamesfeature");
			String organismID = json.getString("organismID");
			
			IRI organismIRI = f.createIRI(nc, "organism"+organismID);	
			IRI additionalOccurrenceProperty = f.createIRI(nhc, "additionalOccurrence");	
			
					//find additional identifications
		    String queryOcc = "SELECT (COUNT(DISTINCT ?additionalOccurrence) AS ?totalNumberOfInstances) WHERE { <+"+organismIRI+"> <"+additionalOccurrenceProperty+"> ?additionalOccurrence}";
	    	int additionalOccNr = QueryTripleStore(queryOcc, "AN1", "totalNumberOfInstances");	
	
	    	IRI addOccurrenceIRI = f.createIRI(nc, "occurrence"+organismID+"_add"+additionalOccNr);
			IRI addEventIRI = f.createIRI(nc, "event"+organismID+"_add"+additionalOccNr);
			IRI addLocationIRI = f.createIRI(nc, "location"+organismID+"_add"+additionalOccNr);	
			IRI addDateIRI = f.createIRI(nc, "date"+organismID+"_add"+additionalOccNr);
			IRI geonamesFeatureIRI = null;
			if (!geonamesfeature.equals("")){geonamesFeatureIRI = f.createIRI(geonamesfeature);};
			
			IRI inDescribedPlaceProperty = f.createIRI(dwciri, "inDescribedPlace");
			IRI atEventProperty = f.createIRI(dsw, "atEvent");
			IRI verbatimEventDateProperty = f.createIRI(dwciri, "verbatimEventDate");
			IRI locatedAtProperty = f.createIRI(dsw, "locatedAt");	
			IRI locatesProperty = f.createIRI(dsw, "locates");
			IRI eventOfProperty = f.createIRI(dsw, "eventOf");	
			IRI occurrenceIDProperty = f.createIRI(dwc, "occurrenceID");

			IRI featureClass = f.createIRI(gn, "Feature");	
			IRI locationClass = f.createIRI(dcterms, "Location");
			IRI occurrenceClass = f.createIRI(dwc, "Occurrence");
			IRI eventClass = f.createIRI(dwc, "Event");

			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();
				conn.add(organismIRI, additionalOccurrenceProperty, addOccurrenceIRI);
				conn.add(addOccurrenceIRI, atEventProperty, addEventIRI);
				conn.add(addOccurrenceIRI, occurrenceIDProperty, f.createLiteral(organismID+"_add"+additionalOccNr));
				conn.add(addOccurrenceIRI, RDF.TYPE, occurrenceClass);
				conn.add(addEventIRI, verbatimEventDateProperty, addDateIRI);
				conn.add(addEventIRI,  locatedAtProperty, addLocationIRI);
				conn.add(addEventIRI,  eventOfProperty, addOccurrenceIRI);
				conn.add(addEventIRI,  RDF.TYPE, eventClass);
				conn.add(addLocationIRI,  locatesProperty, addEventIRI);
				conn.add(addLocationIRI, RDFS.LABEL, f.createLiteral(verbatim));
				conn.add(addLocationIRI, RDF.TYPE, locationClass);
				conn.add(annotationIRI, hasBodyProperty, addLocationIRI);	
				if(!geonamesfeature.equals("")){
					conn.add(addLocationIRI,  inDescribedPlaceProperty, geonamesFeatureIRI);
					conn.add(geonamesFeatureIRI,  RDF.TYPE, featureClass);};
				conn.commit();	
			};
		} else if (property.equals("scientificNameAuthorship")){
			String person = json.getString("person");
			String belongstotaxon = json.getString("belongstotaxon");
			
			IRI personIRI = f.createIRI(person);
			IRI belongsToTaxonIRI = f.createIRI(belongstotaxon);
			IRI scientificNameAuthorshipProperty = f.createIRI(nhc, "scientificNameAuthorship");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, personIRI);	
				conn.add(personIRI,  RDF.TYPE, FOAF.PERSON);
				conn.add(belongsToTaxonIRI, scientificNameAuthorshipProperty, personIRI);
				conn.commit();	
			};
			
		} else if (property.equals("identifiedBy")){
			String person = json.getString("person");
			String organismID = json.getString("organismID");
			
			IRI personIRI = f.createIRI(person);
			IRI identificationIRI = f.createIRI(nc, "identification"+organismID);
			
			IRI identifiedByProperty = f.createIRI(dwciri, "identifiedBy");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, personIRI);	
				conn.add(personIRI,  RDF.TYPE, FOAF.PERSON);
				conn.add(identificationIRI, identifiedByProperty, personIRI);
				conn.commit();	
			};

		} else if (property.equals("recordedBy")){
			String person = json.getString("person");
			String organismID = json.getString("organismID");
			
			IRI personIRI = f.createIRI(person);
			IRI occurrenceIRI = f.createIRI(nc, "occurrence"+organismID);
			
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, personIRI);	
				conn.add(personIRI,  RDF.TYPE, FOAF.PERSON);
				conn.add(occurrenceIRI, recordedByProperty, personIRI);
				conn.commit();	
			};
		} else if (property.equals("additionalRecordedBy")){
			String person = json.getString("person");
			String occurrenceID = json.getString("occurrenceID");
			
			IRI addPersonIRI = f.createIRI(person);
			IRI addOccurrenceIRI = f.createIRI(nc, "occurrence"+occurrenceID);
			
			IRI recordedByProperty = f.createIRI(dwciri, "recordedBy");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, addPersonIRI);	
				conn.add(addPersonIRI,  RDF.TYPE, FOAF.PERSON);
				conn.add(addOccurrenceIRI, recordedByProperty, addPersonIRI);
				conn.commit();	
			};
		} else if (property.equals("type") && type.equals("person")){
			String instance = json.getString("instance");
			
			IRI instanceIRI = f.createIRI(instance);
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);	
				conn.add(instanceIRI,  RDF.TYPE, FOAF.PERSON);
				conn.commit();	
			};
		} else if (property.equals("type") && type.equals("location")){
			String instance = json.getString("instance");
			
			IRI instanceIRI = f.createIRI(instance);
			IRI locationClass = f.createIRI(dcterms, "Location");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, instanceIRI);	
				conn.add(instanceIRI,  RDF.TYPE, locationClass);
				conn.commit();	
			};
		} else if (property.equals("type") && type.equals("taxon")){
			String belongstotaxon = json.getString("belongstotaxon");
			String rank = json.getString("rank");
			
			//find amount of taxa
		    String queryTax = "SELECT (COUNT(DISTINCT ?taxon) AS ?totalNumberOfInstances) WHERE { ?taxon <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rs.tdwg.org/dwc/terms/Taxon>}";
	    	int taxonNr = QueryTripleStore(queryTax, "AN1", "totalNumberOfInstances");    	
	    	
			IRI taxonIRI = f.createIRI(nc, "taxon"+taxonNr);
			IRI taxonRankIRI = f.createIRI(nc, rank);
			IRI belongsToTaxonIRI = null;
			if(!belongstotaxon.equals("")){belongsToTaxonIRI = f.createIRI(belongstotaxon);};
			
			IRI belongsToTaxonProperty = f.createIRI(nhc, "belongsToTaxon");
			IRI taxonRankProperty = f.createIRI(nhc, "taxonRank");

			IRI taxonClass = f.createIRI(dwc, "Taxon");
			
			try (RepositoryConnection conn = repo.getConnection()){	
				
				conn.begin();	
				conn.add(annotationIRI, hasBodyProperty, taxonIRI);	
				conn.add(taxonIRI,  RDF.TYPE, taxonClass);
				if(!belongstotaxon.equals("")){conn.add(taxonIRI, belongsToTaxonProperty, belongsToTaxonIRI);};
				conn.add(taxonIRI,  taxonRankProperty, taxonRankIRI);
				conn.add(taxonIRI,  RDFS.LABEL, f.createLiteral(verbatim));
				conn.commit();	
			};
		};			
	};
	
	public int QueryTripleStore(String query, String repID, String valueOf){
	    //Connect to RDF server   
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = repID;
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();
		
		//Retrieve ANNNOTATION NUMBER:
		Value valueCount = null;
			int intCount = 0;
			//COUNT NUMBER OF ANNOTATIONS FOR NEXT ANNO COUNT
	
			try (RepositoryConnection conn = repo.getConnection()){	
					
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
				
				List<BindingSet> resultList;
				try (TupleQueryResult result = tupleQuery.evaluate()) {
						resultList = QueryResults.asList(result);		
					}
				System.out.println(resultList);
				valueCount = resultList.get(0).getValue(valueOf);
	
			}
			System.out.println(valueCount);
			if (valueCount != null){
				Literal literal = (Literal)valueCount;
				intCount = literal.intValue()+1;			
			} else {
				intCount = 1;
			}
		return intCount;
	};
	
	public String retrieveValues(String type){
		
		String values = new String ();
		


		return values; 
	};
}
