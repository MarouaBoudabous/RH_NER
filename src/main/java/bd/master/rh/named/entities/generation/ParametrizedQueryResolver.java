package bd.master.rh.named.entities.generation;

import java.util.Map;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class ParametrizedQueryResolver implements IParametrizedQueryResolver {

	public ResultSet parametrizedQueryEndpoint(String query, Map<String, String> parameters,Map<String,String> prefixes, String endpoint) {
		//Initialize the parametrized sparql query 
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		//Set the query string
		pss.setCommandText(query);
		// Set Namespaces prefixes
		pss.setNsPrefixes(prefixes);
		// Set parameters ... if not empty
		if(!parameters.isEmpty()) {
			parameters.forEach((k,v)->{
				pss.setLiteral(k, v);
				});
		}
		// Log the query string to detect eventual syntax errors
		System.out.println(pss.asQuery().serialize());
		// Create the query factory using the given  endpoint adress.
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, pss.toString());
		// Execute the query 
		ResultSet rs = exec.execSelect();
		// Return the resultset
		return rs;
	}

}
