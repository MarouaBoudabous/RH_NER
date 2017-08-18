package bd.master.rh.named.entities.generation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

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
			ResultSet locations = q.queryEndpoint(query, endpoint);
			printDataToTabDelimitedFile(locations, "Country", "label","countries.txt");
			// Generating named entities for cities from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER
			// performance....
			String cityQuery = "SELECT distinct ?city ?city_name   WHERE {\n" + "?city a dbo:City;\n"
					+ "rdfs:label ?city_name;\n" + "dbo:country ?country .\n" + "?country rdfs:label ?label .\n"
					+ "?country dbo:isoCodeRegion ?cd" + "." + "FILTER langMatches( lang(?city_name),\"fr\" ) \n"
					+ "FILTER langMatches( lang(?label), \"fr\" )	 \n" + "    FILTER regex(str(?cd), ?code)}";
			Map<String, String> parameters = new java.util.HashMap<>();
			String test = "TUN, TN";
			System.out.println(test);
			parameters.put("?code", test);
			Map<String, String> prefixes = new java.util.HashMap<>();
			prefixes.put("dbo", "http://dbpedia.org/ontology/");
			prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ParametrizedQUeryResolver cityGenerator = new ParametrizedQUeryResolver();
			ResultSet cities =cityGenerator.parametrizedQueryEndpoint(cityQuery, parameters, prefixes, endpoint);
			
			printDataToTabDelimitedFile(cities, "City", "city_name","cities.txt");
			// Generating named entities for cities from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER
			// performance....
			String universityQuery = "SELECT distinct ?school ?label ?cd WHERE{\n"
					+ "?school a <http://schema.org/CollegeOrUniversity>.\n" + "?school rdfs:label ?label .\n"
					+ "?school dbo:city ?city.\n" + "?city dbo:country ?country.\n"
					+ "?country dbo:isoCodeRegion ?cd.\n" + "FILTER regex(str(?cd), ?code)\n"
					+ " FILTER (LANGMATCHES(LANG(?label),'fr'))}";
			ParametrizedQUeryResolver universityGeneartor = new ParametrizedQUeryResolver();
			ResultSet schools =universityGeneartor.parametrizedQueryEndpoint(universityQuery, parameters, prefixes, endpoint);
			printDataToTabDelimitedFile(schools, "School", "label","schools.txt");

			// Generating named entities for companies from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER
			// performance....
			// PREFIX dbo: <http://dbpedia.org/ontology/>
			// PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
			// SELECT distinct ?org ?label WHERE{
			// ?org a dbo:Company .
			// ?org dbo:industry ?industry.
			// ?industy rdfs:label ?label.
			// FILTER (LANGMATCHES(LANG(?label),'fr'))
			// FILTER regex(str(?label), "Informatique")
			// }

		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public static void printDataToTabDelimitedFile(ResultSet results, String entityLabel, String varName , String fileName) {
		try {
			// Tab delimited file will be written to data with the name tab-file.csv
			FileWriter fos = new FileWriter(fileName);
			PrintWriter dos = new PrintWriter(fos);
			int i = 0;
			try {
				while (results.hasNext()) {
					System.out.println(i);
					// Get Variable Names
					Iterator<String> itVars = results.next().varNames();
					List<String> varNames = new ArrayList<String>();
					// while (itVars.hasNext()) {
					// varNames.add(varNames.size(), itVars.next().toString());
					// }
					// Get Result
					QuerySolution qs = results.next();
					String value= qs.get(varName).toString();
					
					dos.print(value.substring(0, value.indexOf("@")) + "\t");
					dos.print(entityLabel);

					dos.println();
					i++;
				}
			} catch (NoSuchElementException e) {
				dos.close();
				fos.close();
			}

			dos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
		}
	}
}
