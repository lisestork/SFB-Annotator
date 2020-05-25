package localdomain.localhost.servlets;

import java.io.IOException;
import java.util.List;
import java.lang.Object;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

/**
 * Servlet implementation class WriteAnnotations
 */
public class removeAnnotationsFromRDF extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get httpRequest parameters
		String source = request.getParameter("source");
		String selector = request.getParameter("selector");

		// connect to RDF server
		String rdf4jServer = "http://localhost:8080/rdf4j-server/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(rdf4jServer, repositoryID);

		String nhc = "http://makingsense.liacs.nl/rdf/nhc/";
		ValueFactory f = repo.getValueFactory();
		IRI selectorIRI = f.createIRI(nhc, selector);
		IRI sourceIRI = f.createIRI(nhc, source);
		IRI targetIRI = f.createIRI(nhc, source + selector);

		String query1 = "SELECT DISTINCT ?x WHERE {?x <http://www.w3.org/ns/oa#hasTarget> <" + targetIRI.toString()
				+ "> . }";
		String annotationID = QueryTripleStoreString(query1, repo, "x");
		IRI annotationIRI = f.createIRI(annotationID);

		String query2 = "SELECT DISTINCT ?x WHERE { <" + annotationIRI.toString()
				+ "> <http://www.w3.org/ns/oa#hasBody> ?x . }";
		String body = QueryTripleStoreString(query2, repo, "x");
		IRI bodyIRI = f.createIRI(body);

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			conn.remove(annotationIRI, null, null);
			conn.remove(targetIRI, null, null);
			conn.remove(bodyIRI, null, null);
			conn.remove(selectorIRI, null, null);
			conn.remove(sourceIRI, null, null);
			conn.commit();
		}
	}

	public String QueryTripleStoreString(String query, Repository repo, String valueOf) {
		String annotation = new String();

		try (RepositoryConnection conn = repo.getConnection()) {
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			List<BindingSet> resultList;
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				resultList = QueryResults.asList(result);
			}
			Value valueresult = resultList.get(0).getValue(valueOf);
			annotation = valueresult.stringValue();
		}
		return annotation;
	}
}
