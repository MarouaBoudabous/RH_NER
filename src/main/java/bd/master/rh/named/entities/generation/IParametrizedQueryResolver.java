package bd.master.rh.named.entities.generation;

import java.util.Map;

public interface IParametrizedQueryResolver {
	
	public void parametrizedQueryEndpoint(String query,Map<String,String> parameters,Map<String,String> prefixes,String endpoint);

}
