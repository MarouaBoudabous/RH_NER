package bd.master.rh.named.entities.generation;

import java.util.Map;

public class NamedEntityGenerator {

	public static void main(String[] args) {
		// SPARQL Query
		String query = "PREFIX dbo: <http://dbpedia.org/ontology/>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n select distinct ?country ?label ?code where{?country a dbo:Country.\n"
				+ "OPTIONAL{?country rdfs:label ?label}.\n" + "OPTIONAL{?country dbo:isoCodeRegion ?code}\n"
				+ "Filter(LANGMATCHES(LANG(?label),'fr'))" + "}";

		// Arguments
		// if (args != null && args.length == 1) {
		// szQuery = new String(
		// Files.readAllBytes(Paths.get(args[0])),
		// Charset.defaultCharset());
		// }

		// DBPedia Endpoint
		String endpoint = "http://fr.dbpedia.org/sparql";

		// Query DBPedia
		try {
			// Generating named entities for countries from dbpedia knowledge base .....
			LocationGenrator q = new LocationGenrator();
			q.queryEndpoint(query, endpoint);
			//Generating named entities for cities from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER performance....
			String cityQuery = "SELECT distinct ?city ?city_name   WHERE {\n" + "?city a dbo:City;\n"
					+ "rdfs:label ?city_name;\n" + "dbo:country ?country .\n" + "?country rdfs:label ?label .\n"
					+ "?country dbo:isoCodeRegion ?cd" + "." + "FILTER langMatches( lang(?city_name),\"fr\" ) \n"
					+ "FILTER langMatches( lang(?label), \"fr\" )	 \n" + "    FILTER regex(str(?cd), ?code)}";
			Map<String, String> parameters = new java.util.HashMap<>();
			String test = "TUN, TN" ;
			System.out.println(test);
			parameters.put("?code", test);
			Map<String, String> prefixes = new java.util.HashMap<>();
			prefixes.put("dbo", "http://dbpedia.org/ontology/");
			prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ParametrizedQUeryResolver cityGenerator = new ParametrizedQUeryResolver();
			cityGenerator.parametrizedQueryEndpoint(cityQuery, parameters, prefixes, endpoint);
			
			//Generating named entities for cities from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER performance....
			String universityQuery = "SELECT distinct ?school ?label ?cd WHERE{\n" + "?school a <http://schema.org/CollegeOrUniversity>.\n"
					+ "?school rdfs:label ?label .\n" + "?school dbo:city ?city.\n" + "?city dbo:country ?country.\n"
					+ "?country dbo:isoCodeRegion ?cd.\n"+ "FILTER regex(str(?cd), ?code)\n"
					+ " FILTER (LANGMATCHES(LANG(?label),'fr'))}";
			ParametrizedQUeryResolver universityGeneartor = new ParametrizedQUeryResolver();
			universityGeneartor.parametrizedQueryEndpoint(universityQuery, parameters, prefixes, endpoint);
			
			//Generating named entities for companies from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER performance....
//			PREFIX  dbo:  <http://dbpedia.org/ontology/>
//				PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
//				SELECT distinct ?org ?label  WHERE{
//				?org a dbo:Company .
//				?org dbo:industry  ?industry.
//				?industy rdfs:label ?label.
//				FILTER (LANGMATCHES(LANG(?label),'fr'))
//				FILTER regex(str(?label), "Informatique")
//				}

		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
}
