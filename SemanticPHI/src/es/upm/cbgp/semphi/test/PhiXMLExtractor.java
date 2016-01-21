package es.upm.cbgp.semphi.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.upm.cbgp.semphi.logic.Constants;
import es.upm.cbgp.semphi.logic.StaticUtils;
import es.upm.cbgp.semphi.objects.Notification;
import es.upm.cbgp.semphi.test.ProvenanceData.ManualOrAutomatic;


public class PhiXMLExtractor extends Observable implements Runnable {

	private String file;
	private LinkedList<PhiEntry> phiEntries;
	private LinkedList<String> hostsToSkip;
	private LinkedList<String> pathogensToSkip;
	private Map<String, String> ec2goMappings;

	public PhiXMLExtractor(String f, Observer obs) throws Exception {
		this.file = f;
		this.phiEntries = new LinkedList<PhiEntry>();
		this.hostsToSkip = new LinkedList<String>();
		this.pathogensToSkip = new LinkedList<String>();
		this.loadEC2GOMappings();
		this.addObserver(obs);
	}

	public PhiXMLExtractor(String f) throws Exception {
		this.file = f;
		this.phiEntries = new LinkedList<PhiEntry>();
		this.hostsToSkip = new LinkedList<String>();
		this.pathogensToSkip = new LinkedList<String>();
		this.loadEC2GOMappings();
	}
	
	public LinkedList<PhiEntry> getPHIEntries() {
		return this.phiEntries;
	}

	public void getAllTags() throws Exception {
		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("*");
		int totalSize = doc.getElementsByTagName("PHI-base_entry").getLength();
		for (int i = 0; i < nodeList.getLength(); i++) {
			// Get element
			Element element = (Element) nodeList.item(i);
			String elm = element.getNodeName();

			if (!tags.containsKey(elm)) {
				tags.put(elm, new Integer(1));
			} else {
				int numAp = tags.get(elm);
				numAp++;
				tags.put(elm, new Integer(numAp));
			}
		}
		System.out.println("Total size: " + totalSize);
		Iterator<String> keySetIterator = tags.keySet().iterator();
		while (keySetIterator.hasNext()) {
			String key = keySetIterator.next();
			System.out.println(key + " - " + tags.get(key));
		}
	}

	public void load() throws Exception {
		this.hostsToSkip = StaticUtils
				.loadFileAsStrings(Constants.HOSTS_EXCLUDED_FILE);
		this.pathogensToSkip = StaticUtils
				.loadFileAsStrings(Constants.PATHOGENS_EXCLUDED_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.normalize();
		NodeList nList = doc.getElementsByTagName("PHI-base_entry");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) nNode;
				PhiEntry entry = getEntryFromElement(elem);
				if (entry != null) {
					ProvenanceData pd = getProvenanceDataFromElement(elem);
					entry.setProvenanceData(pd);
					this.phiEntries.add(entry);
				}
			}
		}
		this.setChanged();
		this.notifyObservers(new Notification(Constants.OK, "PHI file successfully loaded. Number of entries: " + phiEntries.size()));
		System.out.println("Total size: " + phiEntries.size());
	}

	/**
	 * Get the provenance data from the node.
	 * 
	 * @param elem
	 *            Receives the node.
	 * @return The object.
	 */
	private ProvenanceData getProvenanceDataFromElement(Element elem) {
		ProvenanceData pd = new ProvenanceData();
		Node currentNode = null;
		currentNode = elem.getElementsByTagName("Entered_by").item(0);
		if (currentNode != null) {
			pd.setEnteredBy(currentNode.getTextContent());
		}
		currentNode = elem.getElementsByTagName("Manual_or_textmining").item(0);
		if (currentNode != null) {
			pd.setTypeExtraction(currentNode.getTextContent().equalsIgnoreCase(
					"M") ? ManualOrAutomatic.MANUAL
					: ManualOrAutomatic.AUTOMATIC);
		}
		currentNode = elem.getElementsByTagName("Literature_ID").item(0);
		if (currentNode != null) {
			LinkedList<String> partsWithHyphen = new LinkedList<String>();
			String parts[] = currentNode.getTextContent().split(";");
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i].trim();
				if (!part.contains("-")) {
					pd.addLiteratureID(parts[i].trim());
				} else {
					partsWithHyphen.add(part);
				}
			}
			for (int i = 0; i < partsWithHyphen.size(); i++) {
				String part = partsWithHyphen.get(i);
				parts = part.split("-");
				for (int j = 0; j < parts.length; j++) {
					pd.addLiteratureID(parts[j].trim());
				}
			}
		}
		currentNode = elem.getElementsByTagName("Literature_source").item(0);
		if (currentNode != null) {
			pd.setLiteratureSource(currentNode.getTextContent());
		}
		currentNode = elem.getElementsByTagName("Comments").item(0);
		if (currentNode != null) {
			pd.setComments(currentNode.getTextContent());
		}
		currentNode = elem.getElementsByTagName("Reference").item(0);
		if (currentNode != null) {
			pd.setReference(currentNode.getTextContent());
		}
		currentNode = elem.getElementsByTagName("Year_published").item(0);
		if (currentNode != null) {
			pd.setYearPublished(currentNode.getTextContent());
		}
		currentNode = elem.getElementsByTagName("DOI").item(0);
		if (currentNode != null) {
			pd.setDOI(currentNode.getTextContent());
		}
		return pd;
	}

	/**
	 * Get the PhiEntry object from the node.
	 * 
	 * @param elem
	 *            Receives the node.
	 * @return The object.
	 */
	private PhiEntry getEntryFromElement(Element elem) throws Exception {
		PhiEntry pe = new PhiEntry();
		Node currentNode = null;
		currentNode = elem.getElementsByTagName("Experimental_host").item(0);
		if (currentNode != null) {
			String expHost = currentNode.getTextContent();
			if (expHost != null) {
				if (this.hostsToSkip.contains(expHost.toUpperCase()))
					return null;
				pe.setExperimentalHost(expHost);
			}
		}
		currentNode = elem.getElementsByTagName("PHI-base_accession_no")
				.item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPhiBaseAccessionNumber(currentNode.getTextContent()
						.trim());
			}
		}
		currentNode = elem.getElementsByTagName("PHI-base_accession").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPhiBaseAccession(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("DB_Type").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setDbType(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Accession").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setAccession(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Associated_strain").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setAssociatedStrain(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Gene_name").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setGeneName(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Pathogen_NCBI_Taxonomy_ID")
				.item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPathogenNcbiTaxonomyID(StaticUtils.parseInt(currentNode
						.getTextContent().trim()));
			}
		}
		int pathNCBITaxID = pe.getPathogenNcbiTaxonomyID();
		if (pathNCBITaxID != -1) {
			// pe.setPathogenSpeciesHierarchy(NCBITaxonomyBioportalService
			// .getInstance().getHierarchyForID(pathNCBITaxID));
		}
		currentNode = elem.getElementsByTagName("Pathogen_species").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				String pathogen = currentNode.getTextContent().trim();
				if (this.pathogensToSkip.contains(pathogen.toUpperCase()))
					return null;
				pe.setPathogenSpecies(pathogen);
			}
		}
		currentNode = elem.getElementsByTagName("Strain").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setStrain(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Disease_name").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setDiseaseName(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Monocot_Dicot_plant").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setMonocotDicotPlant(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Host_NCBI_Taxonomy_ID")
				.item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setHostNcbiTaxonomyID(StaticUtils.parseInt(currentNode
						.getTextContent().trim()));
			}
		}
		int hostNCBITaxID = pe.getHostNcbiTaxonomyID();
		if (hostNCBITaxID != -1) {
			// pe.setHostSpeciesHierarchy(NCBITaxonomyBioportalService
			// .getInstance().getHierarchyForID(hostNCBITaxID));
		}
		currentNode = elem.getElementsByTagName("Function").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setFunction(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Database").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setDatabase(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("GO_annotation").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setGOAnnotation(currentNode.getTextContent().trim(),
						ec2goMappings);
			}
		}
		currentNode = elem.getElementsByTagName("Phenotype_of_mutant").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPhenotypeOfMutant(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Pre-penetration_defect").item(
				0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPrePenetrationDefect(currentNode.getTextContent().trim()
						.equalsIgnoreCase("no") ? false : true);
			}
		}
		currentNode = elem.getElementsByTagName("Penetration_defect").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPenetrationDefect(currentNode.getTextContent().trim()
						.equalsIgnoreCase("no") ? false : true);
			}
		}
		currentNode = elem.getElementsByTagName("Post-penetration_defect")
				.item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setPostPenetrationDefect(currentNode.getTextContent().trim()
						.equalsIgnoreCase("no") ? false : true);
			}
		}
		currentNode = elem.getElementsByTagName("Host_response").item(0);
		if (currentNode != null) {
			if (!StaticUtils.isEmpty(currentNode.getTextContent())) {
				pe.setHostResponse(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Inducer").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setInducer(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("AA_sequence").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setAASequence(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("NT_sequence").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setNTSequence(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName(
				"Essential_gene_Lethal_knockout").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setLethalKnockout(currentNode.getTextContent().toUpperCase()
						.trim().equalsIgnoreCase("NO") ? false : true);
			}
		}
		currentNode = elem.getElementsByTagName("Locus_ID").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setLocusID(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("In_vitro_growth").item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				pe.setInVitroGrowth(currentNode.getTextContent().trim());
			}
		}
		currentNode = elem.getElementsByTagName("Experimental_evidence")
				.item(0);
		if (currentNode != null) {
			if (currentNode.getTextContent() != null) {
				String parts[] = currentNode.getTextContent().trim().split(";");
				for (int i = 0; i < parts.length; i++) {
					pe.addExperimentalEvidence(parts[i].trim());
				}
			}
		}
		return pe;
	}

	public void showPathogenInteractions() {
		System.out.println("Entries: " + this.phiEntries.size());
		int x = 0;
		LinkedList<String> pathogens = new LinkedList<String>();
		LinkedList<String> interactions = new LinkedList<String>();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			PhiEntry pe = this.phiEntries.get(i);
			String interaction = pe.getPathogenSpecies() + "/"
					+ pe.getExperimentalHost() + "/" + pe.getExperimentalHost();
			System.out.println(pe.getPathogenSpecies() + " is pathogen of "
					+ pe.getExperimentalHost() + " [" + pe.getDiseaseName()
					+ "]");
			System.out.println(pe.toString());

			if (!pathogens.contains(pe.getPathogenSpecies())) {
				pathogens.add(pe.getPathogenSpecies());
			}
			if (!interactions.contains(interaction)) {
				interactions.add(interaction);
			}
		}
		System.out.println("TOTAL: " + x);
		System.out
				.println("Number of different pathogens: " + pathogens.size());
		System.out.println("Number of different interactions: "
				+ interactions.size());
	}

	public void getDifferentX2() {

		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			PhiEntry pe = this.phiEntries.get(i);
			String ee = pe.getDiseaseName();
			if (ee != null) {
				ee = ee.trim().toUpperCase();
				if (!ret.contains(ee)) {
					ret.add(ee);
				}
			}
		}
		System.out.println("Disease names: " + ret.size());
		for (int i = 0; i < ret.size(); i++) {
			System.out.println(ret.get(i));
		}

	}

	public void getDifferentX() {
		String skippedDbs[] = new String[] { "ENTREZ PROTEIN", "UNIPROT",
				"PDB", "ENTREZ NUCLEOTIDE", "GENBANK" };
		LinkedList<String> ret = new LinkedList<String>();
		Map<String, LinkedList<String>> howMany = new HashMap<String, LinkedList<String>>();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			PhiEntry pe = this.phiEntries.get(i);
			String ee = pe.getDbType();
			if (ee != null) {
				ee = ee.trim().toUpperCase();
				if (!contains(ee, skippedDbs)) {
					if (!ret.contains(ee)) {
						ret.add(ee);
						LinkedList<String> lst = new LinkedList<String>();
						lst.add(pe.getAccession().trim());
						howMany.put(ee, lst);
					} else {
						LinkedList<String> lst = howMany.get(ee);
						lst.add(pe.getAccession().trim());
						howMany.put(ee, lst);
					}
				}
			}
		}
		System.out.println("DB type diferentes: " + ret.size());
		// for (int i = 0; i < ret.size(); i++) {
		// System.out.println(ret.get(i));
		// }
		Iterator<Entry<String, LinkedList<String>>> it = howMany.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, LinkedList<String>> val = it.next();
			String db = val.getKey();
			System.out.println(db);
			LinkedList<String> vals = val.getValue();
			for (int i = 0; i < vals.size(); i++) {
				System.out.println("\t" + vals.get(i).trim());
			}

		}
	}

	private boolean contains(String s, String[] where) {
		for (int i = 0; i < where.length; i++) {
			if (where[i].equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	private void loadEC2GOMappings() throws Exception {
		// http://geneontology.org/external2go/ec2go
		this.ec2goMappings = new HashMap<String, String>();
		BufferedReader bL = new BufferedReader(new FileReader(
				Constants.EC2GO_MAPPINGS_FILE));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split(" > ");
			if (parts.length == 2) {
				String ec = parts[0];
				String partsGO[] = parts[1].split(";");
				if (partsGO.length == 2) {
					this.ec2goMappings.put(ec.trim(), partsGO[1].trim());
				}
			} else {
				System.err
						.println("Error reading EC2GO mappings file. Incorrect format: "
								+ rd);
			}
		}
		bL.close();
	}
	public void run() {
		try {
			this.load();
		} catch (Exception e) {
			this.setChanged();
			this.notifyObservers(new Notification(Constants.ERROR, "Error loading PHI XML file: " + e.getMessage()));
		}
		
	}
}
