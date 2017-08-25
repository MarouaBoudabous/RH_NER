package bd.master.rh.named.entities.generation;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

public class NamedEntityExtractor {

	public ResultSet queryEndpoint(String query, String endpoint) {
		// Create a query with the given query string
		Query sparqlQuery = QueryFactory.create(query);
		// Log the query string to detect eventual syntax errors...
		System.out.println(sparqlQuery.serialize());
		// Create the execution factory using the given endpoint
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
		// Execute Query
		ResultSet rs = exec.execSelect();
		// return the result Set
		return rs;
	}
}
