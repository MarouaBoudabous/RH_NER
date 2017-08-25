package bd.master.rh.named.entities.generation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class NamedEntityGenerator {

	public static void main(String[] args) {
		final Boolean areNull;
		// Build sparql query to detect countries.
		String query = "PREFIX dbo: <http://dbpedia.org/ontology/>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n select distinct ?country ?label ?code where{?country a dbo:Country.\n"
				+ "OPTIONAL{?country rdfs:label ?label}.\n" + "OPTIONAL{?country dbo:isoCodeRegion ?code}\n"
				+ "Filter(LANGMATCHES(LANG(?label),'fr'))" + "}";
		// Later on all parameters will be get as WS params.
		// DBPedia Endpoint
		String endpoint = "http://fr.dbpedia.org/sparql";

		// Query DBPedia
		try {
			// Extract countries...
			extractNamedEntities(query, endpoint, "countries.txt", "label", "Country");

			// set useful namespaces prefixes to run parametrized queries...
			Map<String, String> prefixes = new java.util.HashMap<>();
			prefixes.put("dbo", "http://dbpedia.org/ontology/");
			prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

			// Generating named entities for cities from dbpedia knowledge base .....
			// We will use parametrized query to get cities by country to enhance NER
			// performance....

			String cityQuery = "SELECT distinct ?city ?city_name   WHERE {\n" + "?city a dbo:City;\n"
					+ "rdfs:label ?city_name;\n" + "dbo:country ?country .\n" + "?country rdfs:label ?label .\n"
					+ "?country dbo:isoCodeRegion ?cd" + "." + "FILTER langMatches( lang(?city_name),\"fr\" ) \n"
					+ "FILTER langMatches( lang(?label), \"fr\" )	 \n" + "    FILTER regex(str(?cd), ?code)}";
			String code = "TUN, TN";
			Map<String, String> parameters = new java.util.HashMap<>();
			parameters.put("?code", code);
			extractNamedEntitiesUsingParameters(endpoint, cityQuery, prefixes, parameters, "City", "city_name",
					"namedEntities.txt");
			String universityQuery = "SELECT distinct ?school ?label ?cd WHERE{\n"
					+ "?school a <http://schema.org/CollegeOrUniversity>.\n" + "?school rdfs:label ?label .\n"
					+ "?school dbo:city ?city.\n" + "?city dbo:country ?country.\n"
					+ "?country dbo:isoCodeRegion ?cd.\n" + "FILTER regex(str(?cd), ?code)\n"
					+ " FILTER (LANGMATCHES(LANG(?label),'fr'))}";
			extractNamedEntitiesUsingParameters(endpoint, universityQuery, prefixes, parameters, "School", "label",
					"namedEntities.txt");
			FileWriterUtils.printCsvDataToTabDelimitedFile("", "Skill", "namedEntities.txt");
			FileWriterUtils.printCsvDataToTabDelimitedFile("", "Occupation", "namedEntities.txt");
			FileWriterUtils.printCsvDataToTabDelimitedFile("", "Occupation", "namedEntities.txt");
			
			
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	/**
	 * @param endpoint
	 * @param prefixes
	 */
	private static void extractNamedEntitiesUsingParameters(String endpoint, String query, Map<String, String> prefixes,
			Map<String, String> parameters, String EntityLabel, String VarName, String fileName) {

		ParametrizedQueryResolver generator = new ParametrizedQueryResolver();
		ResultSet results = generator.parametrizedQueryEndpoint(query, parameters, prefixes, endpoint);
		printDataToTabDelimitedFile(results, EntityLabel, VarName, fileName);

	}

	/**
	 * @param query
	 * @param endpoint
	 */
	private static void extractNamedEntities(String query, String endpoint, String fileName, String varName,
			String entityLabel) {
		// Generating named entities for countries from dbpedia knowledge base .....
		NamedEntityExtractor q = new NamedEntityExtractor();
		ResultSet locations = q.queryEndpoint(query, endpoint);
		printDataToTabDelimitedFile(locations, entityLabel, varName, fileName);
	}

	public static void printDataToTabDelimitedFile(ResultSet results, String entityLabel, String varName,
			String fileName) {
		try {
			// Tab delimited file will be written to data with the name tab-file.csv
			FileWriter fos = new FileWriter(fileName, true);
			PrintWriter dos = new PrintWriter(fos);
			int i = 0;
			try {
				while (results.hasNext()) {
					QuerySolution qs = results.next();
					Iterator<String> itVars = qs.varNames();
					while (itVars.hasNext()) {
						String value = itVars.next().toString();
						if (value.equalsIgnoreCase("label")) {
							dos.print(qs.get(value).toString().substring(0, qs.get(value).toString().indexOf("@"))
									.toLowerCase() + "\t");
							dos.print(entityLabel);

							dos.println();
						}

					}

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

	public static String getCountryCode(String code) {
		String result = "";
		// Build sparql query to detect countries.

		String query = "PREFIX  dbo:  <http://dbpedia.org/ontology/>\n"
				+ "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "SELECT DISTINCT  ?code \n" + "WHERE\n"
				+ "  {  ?country  a                     dbo:Country.\n" + "     ?country  rdfs:label  ?label.\n"
				+ "     ?country  dbo:isoCodeRegion  ?code \n" + "     FILTER langMatches(lang(?label), \"fr\")\n"
				+ "     filter (contains(?label, \"" + code + "\"))\n" + "  }";

		// DBPedia Endpoint
		String endpoint = "http://fr.dbpedia.org/sparql";
		NamedEntityExtractor q = new NamedEntityExtractor();
		ResultSet results = q.queryEndpoint(query, endpoint);
		while (results.hasNext()) {
			result = results.next().get("code").toString();
			break;
		}
		return result;

	}

}
