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
public class removeAnnotationsFromRDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		//get parameters that were sent with the httpRequest
		String source = request.getParameter("source");
		String selector = request.getParameter("selector");
		String occurrenceID = request.getParameter("organismID");
	    		
	    //Connect to RDF server   
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = "AN";
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();

		//create valuefactory for IRIs
		ValueFactory f = repo.getValueFactory();
		
 		String nc = "http://testingsense.liacs.nl/rdf/nc#";
 		IRI selectorIRI = f.createIRI(nc,selector);
 		IRI sourceIRI = f.createIRI(nc,source);
 		
		//retrieve annotation with page
 		IRI targetIRI = f.createIRI(nc, source+selector);
		String query1 = "SELECT DISTINCT ?x WHERE {?x <http://www.w3.org/ns/oa#hasTarget> <"+targetIRI+"> }";
		String anno = QueryTripleStoreString(query1, "AN", "x");
		
		System.out.println(anno);
		//retrieve body with annotation
		IRI annoIRI = f.createIRI(anno);
		String query2 = "SELECT DISTINCT ?x WHERE {<"+anno+"> <http://www.w3.org/ns/oa#hasBody> ?x}";
		String body = QueryTripleStoreString(query2, "AN", "x");
			
		System.out.println(anno);
		System.out.println(body);
		//STORE TRANSCRIPTION
		rdf4jServer = "http://localhost:8080/rdf4j-server/";
		repositoryID = "AN";
		repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();
		
		IRI bodyIRI = f.createIRI(body);
		System.out.println(annoIRI);
		try (RepositoryConnection conn = repo.getConnection()){	
			
			   conn.begin();
			   conn.remove(annoIRI, null, null);
			   conn.remove(targetIRI, null, null);
			   conn.remove(bodyIRI, null, null);
			   conn.remove(selectorIRI, null, null);
			   conn.remove(sourceIRI, null, null);
	
			   ////DELETE ANNOTATIONS/////////
			   conn.commit();
		}
		System.out.println("Successfully Deleted RDF Object from Triple store...");
	
	}
	
	public String QueryTripleStoreString(String query, String repID, String valueOf){
	    //Connect to RDF server   
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = repID;
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);
		repo.initialize();

		String anno = new String();
		try (RepositoryConnection conn = repo.getConnection()){			
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);				
			List<BindingSet> resultList;
			try (TupleQueryResult result = tupleQuery.evaluate()) {
					resultList = QueryResults.asList(result);		
				}
			System.out.println(resultList);
			Value valueresult = resultList.get(0).getValue(valueOf);
			anno = valueresult.stringValue();
		}
	return anno;
	}

}



