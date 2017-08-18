package bd.master.rh.named.entities.generation;

import java.util.Map;

import org.apache.jena.query.ResultSet;

public interface IParametrizedQueryResolver {
	
	public ResultSet parametrizedQueryEndpoint(String query,Map<String,String> parameters,Map<String,String> prefixes,String endpoint);

}
