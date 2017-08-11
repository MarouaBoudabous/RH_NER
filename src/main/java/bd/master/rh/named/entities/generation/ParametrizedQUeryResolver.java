package bd.master.rh.named.entities.generation;

import java.util.Map;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class ParametrizedQUeryResolver implements IParametrizedQueryResolver {

	public void parametrizedQueryEndpoint(String query, Map<String, String> parameters,Map<String,String> prefixes, String endpoint) {
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setCommandText(query);
		pss.setNsPrefixes(prefixes);
		if(!parameters.isEmpty()) {
			parameters.forEach((k,v)->{
				pss.setLiteral(k, v);
				});
		}
		System.out.println(pss.asQuery().serialize());
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, pss.toString());
		ResultSet rs = exec.execSelect();
		ResultSetFormatter.out(System.out, rs);
		
	}

}
