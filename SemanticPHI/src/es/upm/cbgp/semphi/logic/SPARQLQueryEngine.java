package es.upm.cbgp.semphi.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import es.upm.cbgp.semphi.objects.Substitution;

/**
 * Class to perform the SPARQL query.
 * 
 * @author Alejandro Rodríguez González - Centre for Biotechnology and Plant
 *         Genomics
 * 
 */
public class SPARQLQueryEngine {

	@SuppressWarnings("resource")
	public LinkedList<String> executeQuery(String uriOrig, String finalQuery,
			String endpoint, String variable) throws Exception {
		LinkedList<String> resultsToReturn = new LinkedList<String>();
		Query query = null;
		QueryEngineHTTP qexec = null;
		try {
			query = QueryFactory.create(finalQuery);
			qexec = new QueryEngineHTTP(endpoint, query);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				Resource uriClass = qs.getResource("?uris");
				if (uriClass != null) {
					String uri = uriClass.getURI();
					if (uri != null) {
						if (uri.startsWith("http:")) {
							if (!uri.equalsIgnoreCase(uriOrig)) {
								resultsToReturn.add(uriClass.getURI().trim());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return resultsToReturn;
	}

	/**
	 * Method to load a query from an external file doing some replacements on
	 * the data.
	 * 
	 * @param f
	 *            Receives the file.
	 * @param subs
	 *            List of replacements.
	 * @return Return the final query.
	 * @throws Exception
	 *             It can throw an exception.
	 */
	public String loadQueryFromFileWithSubstitutions(String f,
			ArrayList<Substitution> subs) throws Exception {
		String query = "";
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String read = bL.readLine();
			Substitution sub = getSubstituion(read, subs);
			if (sub != null) {
				read = read.replace(sub.getOriginalString(),
						sub.getReplacedString());
			}
			query += read + "\r\n";
		}
		bL.close();
		return query;
	}

	/**
	 * Method to, given a concrete string readed in the file, knows if contains
	 * the pattern that needs to be replaced.
	 * 
	 * @param read
	 *            Original string readed.
	 * @param subs
	 *            List of possible replacements.
	 * @return Return the object with the replacement (if found)
	 */
	private Substitution getSubstituion(String read,
			ArrayList<Substitution> subs) {
		for (int i = 0; i < subs.size(); i++) {
			String origStr = subs.get(i).getOriginalString();
			if (read.trim().contains(origStr.trim())) {
				return subs.get(i);
			}
		}
		return null;
	}

}
