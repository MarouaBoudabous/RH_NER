package bd.master.rh.named.entities.generation;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class LocationGenrator {

	public void queryEndpoint(String query, String endpoint) {
		// Create a query with the given query string
		Query sparqlQuery = QueryFactory.create(query);
		System.out.println(sparqlQuery.serialize());
		
		// Create the execution factory using the given endpoint
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
		// Set Timeout
		// ((QueryEngineHTTP) exec).addParam("timeout", "10000");
		// Execute Query
		int iCount = 0;
		ResultSet rs = exec.execSelect();
		ResultSetFormatter.out(System.out, rs);
		// while (rs.hasNext()) {
		// // Get Result
		// QuerySolution qs = rs.next();
		//
		// // Get Variable Names
		// Iterator<String> itVars = qs.varNames();
		//
		// // Count
		// iCount++;
		// System.out.println("Result " + iCount + ": ");
		//
		// // Display Result
		// while (itVars.hasNext()) {
		// String szVar = itVars.next().toString();
		// String szVal = qs.get(szVar).toString();
		//
		// System.out.println("[" + szVar + "]: " + szVal);
		// }
	}
}
