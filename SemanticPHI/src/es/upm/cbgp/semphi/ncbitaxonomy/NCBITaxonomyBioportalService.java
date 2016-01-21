package es.upm.cbgp.semphi.ncbitaxonomy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.upm.cbgp.semphi.logic.Constants;
import es.upm.cbgp.semphi.logic.StaticUtils;
import es.upm.cbgp.semphi.objects.NCBITaxonHierarchy;

public class NCBITaxonomyBioportalService {

	private final String API_KEY = "a865fdd8-dbe0-4516-a666-0b05dd5a372c";
	private final String NCBI_TAXONOMY_BIOPORTAL_URL = "http://data.bioontology.org/ontologies/NCBITAXON/classes/";
	private final String NCBI_TAXONOMY_CONCEPT_URL = "http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FNCBITAXON%2F";
	private final String PARENT_STRING = "/parents";
	private final String FORMAT_XML = "format=xml";
	private final String API_KEY_STRING = "apikey=";
	private final int FINAL_ORGANISM_ID = 131567;
	private Map<Integer, LinkedList<NCBITaxonHierarchy>> hierarchies = new HashMap<Integer, LinkedList<NCBITaxonHierarchy>>();
	
	private LinkedList<Integer> idsError = new LinkedList<Integer>();

	private static NCBITaxonomyBioportalService INSTANCE = new NCBITaxonomyBioportalService();
	private int currentStartID;

	/**
	 * Constructor.
	 */
	private NCBITaxonomyBioportalService() {
		try {
			loadNCBITaxHierarchies();
		} catch (FileNotFoundException e) {
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Singleton.
	 * 
	 * @return The instance.
	 */
	public static NCBITaxonomyBioportalService getInstance() {
		return INSTANCE;
	}

	/**
	 * Method to load the already obtained NCBITaxHierarchies from a file.
	 * 
	 * @param f
	 *            Receives the file.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void loadNCBITaxHierarchies() throws Exception {
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.NCBI_TAXONOMY_HIERARCHIES));
		while (bL.ready()) {
			String readed = bL.readLine();
			String parts[] = readed.split("=");
			if (parts.length == 2) {
				int id = Integer.parseInt(parts[0]);
				String hierarchyArray[] = parts[1].split("#");
				LinkedList<NCBITaxonHierarchy> hierarchy = new LinkedList<NCBITaxonHierarchy>();
				for (int i = 0; i < hierarchyArray.length; i++) {
					String partsHier[] = hierarchyArray[i].split("@");
					if (partsHier.length == 3) {
						NCBITaxonHierarchy ob = new NCBITaxonHierarchy(
								partsHier[0], Integer.parseInt(partsHier[1]),
								partsHier[2]);
						hierarchy.add(ob);
					} else {
						System.err.println("Malformed hierarchy: "
								+ hierarchyArray[i]);
					}
				}
				if (!hierarchies.containsKey(id)) {
					hierarchies.put(id, hierarchy);
				}
			} else {
				System.err.println("Malformed line: " + readed);
			}
		}
		bL.close();
		//saveHierarchiesMap("data/ncbitaxhierarchies2.ppio");
	}

	@SuppressWarnings("unused")
	private void saveHierarchiesMap(String f) throws Exception {
		java.util.Iterator<Integer> it = this.hierarchies.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer)it.next();
			LinkedList<NCBITaxonHierarchy> hs = hierarchies.get(key);
			saveHierarchy(key, hs, f);
		}
	}

	/**
	 * Method to obtain the hierarchy of an organism given the ID.
	 * 
	 * @param id
	 *            Receives the ID.
	 * @return Returns a list with the hierarchy (first element is the more
	 *         specific one, last is the most generic)
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public LinkedList<NCBITaxonHierarchy> getHierarchyForID(int id)
			throws Exception {
//		if (true) {
//			return null;
//		}
		this.currentStartID = id;
		System.out.print("Getting hierarchy for ID '" + id + "' .. ");
		if (this.idsError.contains(id)) {
			System.out.println("Skipping! URL error before!");
			return null;
		}
		
		LinkedList<NCBITaxonHierarchy> ret = new LinkedList<NCBITaxonHierarchy>();
		if (this.hierarchies.containsKey(id)) {
			System.out.println("done! [ext file]");
			return this.hierarchies.get(id);
		} else {
			LinkedList<NCBITaxonHierarchy> bioportHierarchy = getHierarchyForID(
					id, ret);
			if (bioportHierarchy != null) {
				saveHierarchy(id, bioportHierarchy, Constants.NCBI_TAXONOMY_HIERARCHIES);
				this.hierarchies.put(id, bioportHierarchy);
				System.out.println("done! [bioportal]");
			} else {
				System.out.println("URL error!");
			}
			return bioportHierarchy;
		}
	}

	/**
	 * Method to save a hierarchy for a concrete taxon id.
	 * 
	 * @param id
	 *            Receives the ID.
	 * @param bioportHierarchy
	 *            Receives the hierarchy.
	 * @throws Exception
	 *             It can throws exception.
	 */
	private void saveHierarchy(int id,
			LinkedList<NCBITaxonHierarchy> bioportHierarchy, String file) throws Exception {
		BufferedWriter bW = new BufferedWriter(new FileWriter(file, true));
		String toWrite = id + "=";
		for (int i = 0; i < bioportHierarchy.size(); i++) {
			NCBITaxonHierarchy ob = bioportHierarchy.get(i);
			toWrite += ob.toExternal() + "#";
		}
		toWrite = toWrite.substring(0, toWrite.length() - 1);
		bW.write(toWrite);
		bW.newLine();
		bW.close();

	}

	/**
	 * Recursive method to obtain the hierarchy.
	 * 
	 * @param id
	 *            ID
	 * @param ret
	 *            The list which will contain the hierarchy.
	 * @return Returns the list.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private LinkedList<NCBITaxonHierarchy> getHierarchyForID(int id,
			LinkedList<NCBITaxonHierarchy> ret) throws Exception {
		String cIDURL = NCBI_TAXONOMY_BIOPORTAL_URL + NCBI_TAXONOMY_CONCEPT_URL + id + "?" + API_KEY_STRING
				+ API_KEY + "&" + FORMAT_XML;
		boolean exit = false;

		URL currentIDURL = new URL(cIDURL);
		Document doc = getDocumentFor(currentIDURL);
		if (doc == null) {
			return null;
		}

		String prefLabel = ((Element) doc.getElementsByTagName("prefLabel")
				.item(0)).getTextContent();
		String cui = ((Element) doc.getElementsByTagName("cui").item(0))
				.getTextContent();
		String idTaxonURL = ((Element) doc.getElementsByTagName("id").item(0))
				.getTextContent();
		String idTaxon = idTaxonURL.substring(idTaxonURL.lastIndexOf('/') + 1,
				idTaxonURL.length());
		int idTaxonNumber = Integer.parseInt(idTaxon);

		if (idTaxonNumber == FINAL_ORGANISM_ID) {
			exit = true;
		}
		NCBITaxonHierarchy nth = new NCBITaxonHierarchy(prefLabel,
				idTaxonNumber, cui);
		ret.add(nth);

		if (exit) {
			return ret;
		}

		String parentURLStr = NCBI_TAXONOMY_BIOPORTAL_URL + NCBI_TAXONOMY_CONCEPT_URL + id + PARENT_STRING
				+ "?" + FORMAT_XML + "&" + API_KEY_STRING + API_KEY;
		URL parentURL = new URL(parentURLStr);
		doc = getDocumentFor(parentURL);
		String idTaxonParentURL = ((Element) doc.getElementsByTagName("id")
				.item(0)).getTextContent();
		String idTaxonPURL = idTaxonParentURL.substring(
				idTaxonParentURL.lastIndexOf('/') + 1,
				idTaxonParentURL.length());
		int idTaxonPURLNumber = Integer.parseInt(idTaxonPURL);

		// System.out.println(prefLabel + " - " + idTaxon + " -> " +
		// idTaxonPURL);
		return getHierarchyForID(idTaxonPURLNumber, ret);
	}

	public LinkedList<Integer> getIDSError() {
		return this.idsError;
	}
	/**
	 * Method to obtain a DOM document for a given URL.
	 * 
	 * @param url
	 *            Receives the URL.
	 * @return Returns the document.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private Document getDocumentFor(URL url) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(url.openStream());
		} catch (Exception e) {
			String error = "Error with URL: " + url.toString() + " - Error: "
					+ e.getMessage() + " - Start ID: " + this.currentStartID;
			StaticUtils.write("errorsNCBI.log", error);
			if (!idsError.contains(this.currentStartID)) {
				this.idsError.add(this.currentStartID);
			}
			return null;
		}
	}
}
