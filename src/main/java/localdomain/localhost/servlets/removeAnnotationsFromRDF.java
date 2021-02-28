package localdomain.localhost.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
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
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get httpRequest parameters
		String source = request.getParameter("source");
		String selector = request.getParameter("selector");

		// connect to RDF server
		String host = "http://localhost:8080/";
		String repositoryID = "mem-rdf";
		Repository repo = new HTTPRepository(host + "rdf4j-server/", repositoryID);
		ValueFactory f = repo.getValueFactory();

		String queryStr = "PREFIX rdf: <" + RDF.NAMESPACE + "> \n";
		queryStr += "PREFIX oa: <http://www.w3.org/ns/oa#> \n";
		queryStr += "SELECT DISTINCT ?annot WHERE { \n";
		queryStr += "	?annot oa:hasTarget/oa:hasSelector/rdf:value ?selector. \n";
		queryStr += "	FILTER(?selector = \"" + selector + "\")}";

		try (RepositoryConnection conn = repo.getConnection()) {
			conn.begin();
			IRI annotationIRI = null;
			TupleQuery tupleQuery = conn.prepareTupleQuery(queryStr);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				for (BindingSet solution : result) {
					annotationIRI = (IRI) solution.getValue("annot");
				}
				conn.remove(annotationIRI, null, null);
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			repo.shutDown();
		}
	}
}