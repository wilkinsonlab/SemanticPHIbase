package es.upm.cbgp.semphi.phiextractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
import es.upm.cbgp.semphi.phiextractor.ProvenanceData.ManualOrAutomatic;

public class PhiXMLExtractor extends Observable implements Runnable {

	private PHIFile phiFile;
	private LinkedList<PhiEntry> phiEntries;
	private LinkedList<String> hostsToSkip;
	private LinkedList<String> pathogensToSkip;
	private Map<String, String> ec2goMappings;

	public PhiXMLExtractor(String f, Observer obs) throws Exception {
		// this.file = f;
		this.phiEntries = new LinkedList<PhiEntry>();
		this.hostsToSkip = new LinkedList<String>();
		this.pathogensToSkip = new LinkedList<String>();
		this.loadEC2GOMappings();
		this.addObserver(obs);
	}

	public PhiXMLExtractor(PHIFile pf) throws Exception {
		this.phiFile = pf;
		this.phiEntries = new LinkedList<PhiEntry>();
		this.hostsToSkip = new LinkedList<String>();
		this.pathogensToSkip = new LinkedList<String>();
		this.loadEC2GOMappings();
	}

	/**
	 * Method to get the PHIFile object.
	 * @return Return the value.
	 */
	public PHIFile getPHIFile() {
		return this.phiFile;
	}
	public LinkedList<PhiEntry> getPHIEntries() {
		return this.phiEntries;
	}

	public void getAllTags() throws Exception {
		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(phiFile.getFile());
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("*");

		int totalSize = doc.getElementsByTagName(
				phiFile.getXMLTag(Constants.XML_TAG_PHI_BASE_ENTRY))
				.getLength();
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
		Document doc = dBuilder.parse(this.phiFile.getFile());
		doc.normalize();
		NodeList nList = doc.getElementsByTagName(phiFile
				.getXMLTag(Constants.XML_TAG_PHI_BASE_ENTRY));
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
		this.notifyObservers(new Notification(Constants.OK,
				"PHI file successfully loaded. Number of entries: "
						+ phiEntries.size()));
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
		String tag = null;
		tag = phiFile.getXMLTag(Constants.XML_TAG_ENTERED_BY);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setEnteredBy(currentNode.getTextContent());
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_MANUAL_OR_TEXT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setTypeExtraction(currentNode.getTextContent()
						.equalsIgnoreCase("M") ? ManualOrAutomatic.MANUAL
						: ManualOrAutomatic.AUTOMATIC);
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_LITERATURE_ID);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
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
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_LITERATURE_SOURCE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setLiteratureSource(currentNode.getTextContent());
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_COMMENTS);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setComments(currentNode.getTextContent());
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_REFERENCE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setReference(currentNode.getTextContent());
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_YEAR_PUBLISHED);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setYearPublished(currentNode.getTextContent());
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_DOI);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				pd.setDOI(currentNode.getTextContent());
			}
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
		String tag = null;
		tag = phiFile.getXMLTag(Constants.XML_TAG_EXPERIMENTAL_HOST);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				String expHost = currentNode.getTextContent();
				if (expHost != null) {
					if (this.hostsToSkip.contains(expHost.toUpperCase()))
						return null;
					pe.setExperimentalHost(expHost);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PHI_BASE_ACC_NUMBER);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPhiBaseAccessionNumber(currentNode.getTextContent()
							.trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PHI_BASE_ACCESSION);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPhiBaseAccession(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_IDENTIFIER_TYPE_PROTEIN_ID);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setIdentifierTypeProteinID(currentNode.getTextContent()
							.trim());
				}
			}
		}
		tag = phiFile
				.getXMLTag(Constants.XML_TAG_IDENTIFIER_TYPE_GENE_LOCUS_ID);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setIdentifierTypeGeneLocusID(currentNode
							.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_DB_TYPE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setDbType(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_ACCESSION);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setAccession(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_ASSOCIATED_STRAIN);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setAssociatedStrain(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_GENE_NAME);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setGeneName(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_NCBI_TAX_ID_PATHOGEN);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPathogenNcbiTaxonomyID(StaticUtils
							.parseInt(currentNode.getTextContent().trim()));
				}
			}

			int pathNCBITaxID = pe.getPathogenNcbiTaxonomyID();
			if (pathNCBITaxID != -1) {
				// pe.setPathogenSpeciesHierarchy(NCBITaxonomyBioportalService
				// .getInstance().getHierarchyForID(pathNCBITaxID));
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PATHOGEN_SPECIES);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					String pathogen = currentNode.getTextContent().trim();
					if (this.pathogensToSkip.contains(pathogen.toUpperCase()))
						return null;
					pe.setPathogenSpecies(pathogen);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_STRAIN);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setStrain(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_DISEASE_NAME);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setDiseaseName(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_MONOCOT_DICOT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setMonocotDicotPlant(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_NCBI_TAX_ID_HOST);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
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
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_FUNCTION);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setFunction(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_DATABASE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setDatabase(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_GO_ANNOTATION);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setGOAnnotation(currentNode.getTextContent().trim(),
							ec2goMappings);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PHENOTYPE_OF_MUTANT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPhenotypeOfMutant(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PRE_PENETRATION_DEFECT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPrePenetrationDefect(currentNode.getTextContent()
							.trim().equalsIgnoreCase("no") ? false : true);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_PENETRATION_DEFECT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPenetrationDefect(currentNode.getTextContent().trim()
							.equalsIgnoreCase("no") ? false : true);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_POST_PENETRATION_DEFECT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setPostPenetrationDefect(currentNode.getTextContent()
							.trim().equalsIgnoreCase("no") ? false : true);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_HOST_RESPONSE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (!StaticUtils.isEmpty(currentNode.getTextContent())) {
					pe.setHostResponse(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_INDUCER);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setInducer(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_AA_SEQ);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setAASequence(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_NT_SEQ);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setNTSequence(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile
				.getXMLTag(Constants.XML_TAG_ESSENTIAL_GENE_LETHAL_KNOCKOUT);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setLethalKnockout(currentNode.getTextContent()
							.toUpperCase().trim().equalsIgnoreCase("NO") ? false
							: true);
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_LOCUS_ID);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setLocusID(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_IVG);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					pe.setInVitroGrowth(currentNode.getTextContent().trim());
				}
			}
		}
		tag = phiFile.getXMLTag(Constants.XML_TAG_EXPERIMENTAL_EVIDENCE);
		if (tag != null) {
			currentNode = elem.getElementsByTagName(tag).item(0);
			if (currentNode != null) {
				if (currentNode.getTextContent() != null) {
					String parts[] = currentNode.getTextContent().trim()
							.split(";");
					for (int i = 0; i < parts.length; i++) {
						pe.addExperimentalEvidence(parts[i].trim());
					}
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

	public void getDifferentHosts() {

		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			PhiEntry pe = this.phiEntries.get(i);
			String ee = pe.getExperimentalHost();
			if (ee != null) {
				ee = ee.trim().toUpperCase();
				if (!ret.contains(ee)) {
					ret.add(ee);
				}
			}
		}
		System.out.println("Hosts: " + ret.size());
		for (int i = 0; i < ret.size(); i++) {
			System.out.println(ret.get(i));
		}

	}

	public void getDifferentX() {
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < this.phiEntries.size(); i++) {
			PhiEntry pe = this.phiEntries.get(i);
			String ee = pe.getIdentifierTypeGeneLocusID();
			if (ee != null) {
				ee = ee.trim().toUpperCase();
				if (!ret.contains(ee)) {
					ret.add(ee);
				}
			}
		}
		System.out.println("protein id type diferentes: " + ret.size());
		for (int i = 0; i < ret.size(); i++) {
			System.out.println(ret.get(i));
		}

	}

//	private boolean contains(String s, String[] where) {
//		for (int i = 0; i < where.length; i++) {
//			if (where[i].equalsIgnoreCase(s)) {
//				return true;
//			}
//		}
//		return false;
//	}

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
			this.notifyObservers(new Notification(Constants.ERROR,
					"Error loading PHI XML file: " + e.getMessage()));
		}

	}
}
