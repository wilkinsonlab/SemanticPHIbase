package es.upm.cbgp.semphi.test;

import java.util.LinkedList;
import java.util.Map;

import es.upm.cbgp.semphi.logic.StaticUtils;
import es.upm.cbgp.semphi.objects.NCBITaxonHierarchy;

public class PhiEntry {

	private String phiBaseAccessionNumber;
	private String phiBaseAccession;
	private String dbType;
	private String accession;
	private String associatedStrain;
	private String geneName;
	private int pathogenNcbiTaxonomyID = -1;
	private String pathogenSpecies;
	private String strain;
	private String diseaseName;
	private String monocotDicotPlant;
	private int hostNcbiTaxonomyID = -1;
	private String experimentalHost;
	private String function;
	private String database;
	private String phenotypeOfMutant;
	private String hostResponse;
	private String AA_Sequence;
	private String NT_Sequence;
	private String locusID;
	private String inVitroGrowth;

	private boolean prePenetrationDefect;
	private boolean penetrationDefect;
	private boolean postPenetrationDefect;
	private boolean lethalKnockOut;
	private boolean weHaveLethalKnockout = false;
	private String inducer;
	private LinkedList<String> experimentalEvidences;
	private LinkedList<String> goAnnotations;
	private ProvenanceData provenanceData;

	private LinkedList<NCBITaxonHierarchy> pathogenSpeciesHierarchy;
	private LinkedList<NCBITaxonHierarchy> hostSpeciesHierarchy;

	public LinkedList<NCBITaxonHierarchy> getHostSpeciesHierarchy() {
		return hostSpeciesHierarchy;
	}

	public void setHostSpeciesHierarchy(
			LinkedList<NCBITaxonHierarchy> hostSpeciesHierarchy) {
		this.hostSpeciesHierarchy = hostSpeciesHierarchy;
	}

	public LinkedList<NCBITaxonHierarchy> getPathogenSpeciesHierarchy() {
		return pathogenSpeciesHierarchy;
	}

	public void setPathogenSpeciesHierarchy(
			LinkedList<NCBITaxonHierarchy> pathogenSpeciesHierarchy) {
		this.pathogenSpeciesHierarchy = pathogenSpeciesHierarchy;
	}

	public void setExperimentalEvidences(
			LinkedList<String> experimentalEvidences) {
		this.experimentalEvidences = experimentalEvidences;
	}

	public PhiEntry() {
		this.experimentalEvidences = new LinkedList<String>();
	}

	public String toString() {
		String ret = "";
		ret += "\tPhi Accession number: " + getPhiBaseAccessionNumber() + "\n";
		ret += "\tPhi Base Accession: " + getPhiBaseAccession() + "\n";
		ret += "\tDB Type: " + getDbType() + "\n";
		ret += "\tAccession: " + getAccession() + "\n";
		ret += "\tAssociated Strain: " + getAssociatedStrain() + "\n";
		ret += "\tGene name: " + getGeneName() + "\n";
		ret += "\tPathogen NCBI Taxonomy ID: " + getPathogenNcbiTaxonomyID()
				+ "\n";
		ret += "\tPathogen species: " + getPathogenSpecies() + "\n";
		ret += "\tStrain: " + getStrain() + "\n";
		ret += "\tDisease name: " + getDiseaseName() + "\n";
		ret += "\tHost response: " + getHostResponse() + "\n";
		ret += "\tMonocot Dicot Plant: " + getMonocotDicotPlant() + "\n";
		ret += "\tHost NCBI Taxonomy ID: " + getHostNcbiTaxonomyID() + "\n";
		ret += "\tExperimental host: " + getExperimentalHost() + "\n";
		ret += "\tFunction: " + getFunction() + "\n";
		ret += "\tGO Annotation: "
				+ StaticUtils.listToStringSeparatedBy(getGOAnnotations(), ' ')
				+ "\n";
		ret += "\tDatabase: " + getDatabase() + "\n";
		ret += "\tPhenotype of mutant: " + getPhenotypeOfMutant() + "\n";
		ret += "\tIs pre-penetration defect: " + isPrePenetrationDefect()
				+ "\n";
		ret += "\tIs prenetration defect: " + isPenetrationDefect() + "\n";
		ret += "\tIs post penetration defect: " + isPostPenetrationDefect()
				+ "\n";
		ret += "\tInducer: " + getInducer() + "\n";
		ret += "\tAA Sequence: " + getAASequence() + "\n";
		ret += "\tNT Sequence: " + getNTSequence() + "\n";
		ret += "\tExperimental evidences: "
				+ StaticUtils.getStringFromList(getExperimentalEvidences())
				+ "\n";
		ret += "\tProvenance data: " + getProvenanceData().toString() + "\n";
		return ret;
	}

	public String getHostResponse() {
		return this.hostResponse;
	}

	public String getPhiBaseAccessionNumber() {
		return phiBaseAccessionNumber;
	}

	public void setPhiBaseAccessionNumber(String p) {
		this.phiBaseAccessionNumber = p;
	}

	public String getPhiBaseAccession() {
		return phiBaseAccession;
	}

	public void setPhiBaseAccession(String p) {
		this.phiBaseAccession = p;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String p) {
		this.dbType = p;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String p) {
		this.accession = p;
	}

	public String getAssociatedStrain() {
		return associatedStrain;
	}

	public void setAssociatedStrain(String p) {
		this.associatedStrain = p;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String p) {
		this.geneName = p;
	}

	public int getPathogenNcbiTaxonomyID() {
		return pathogenNcbiTaxonomyID;
	}

	public void setPathogenNcbiTaxonomyID(int pathogenNcbiTaxonomyID) {
		this.pathogenNcbiTaxonomyID = pathogenNcbiTaxonomyID;
	}

	public String getPathogenSpecies() {
		return pathogenSpecies;
	}

	public void setPathogenSpecies(String p) {
		this.pathogenSpecies = p;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String p) {
		this.strain = p;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String p) {
		this.diseaseName = p;
	}

	public String getMonocotDicotPlant() {
		return monocotDicotPlant;
	}

	public void setMonocotDicotPlant(String p) {
		this.monocotDicotPlant = p;
	}

	public int getHostNcbiTaxonomyID() {
		return hostNcbiTaxonomyID;
	}

	public void setHostNcbiTaxonomyID(int hostNcbiTaxonomyID) {
		this.hostNcbiTaxonomyID = hostNcbiTaxonomyID;
	}

	public String getExperimentalHost() {
		return experimentalHost;
	}

	public void setExperimentalHost(String p) {
		this.experimentalHost = p;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String p) {
		this.function = p;
	}

	public LinkedList<String> getGOAnnotations() {
		return this.goAnnotations;
	}

	public void setGOAnnotation(String p, Map<String, String> ec2go) {
		if (database != null) {
			if (p.trim().length() > 0) {
				if (this.database.equalsIgnoreCase("GO")) {
					this.goAnnotations = new LinkedList<String>();
					if (p.contains(";")) {
						String parts[] = p.split(";");
						for (int i = 0; i < parts.length; i++) {
							String pa = parts[i];
							if (pa.contains(",")) {
								String partsComma[] = pa.split(",");
								for (int j = 0; j < partsComma.length; j++) {
									if (partsComma[j].contains("GO")) {
										this.goAnnotations.add(partsComma[j]);
									}
								}
							}
						}
					} else {
						if (p.contains(",")) {
							String partsComma[] = p.split(",");
							for (int j = 0; j < partsComma.length; j++) {
								if (partsComma[j].contains("GO")) {
									this.goAnnotations.add(partsComma[j]);
								}
							}
						} else {
							if (p.contains("GO")) {
								this.goAnnotations.add(p);
							}
						}
					}
				}
				if (this.database.equalsIgnoreCase("EC")) {
					this.goAnnotations = new LinkedList<String>();
					String goA = ec2go.get(p);
					this.goAnnotations.add(goA);
				}
			}
		}

	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String p) {
		this.database = p;
	}

	public String getPhenotypeOfMutant() {
		return phenotypeOfMutant;
	}

	public void setPhenotypeOfMutant(String p) {
		this.phenotypeOfMutant = p;
	}

	public boolean isPrePenetrationDefect() {
		return prePenetrationDefect;
	}

	public void setPrePenetrationDefect(boolean prePenetrationDefect) {
		this.prePenetrationDefect = prePenetrationDefect;
	}

	public boolean isPenetrationDefect() {
		return penetrationDefect;
	}

	public void setPenetrationDefect(boolean penetrationDefect) {
		this.penetrationDefect = penetrationDefect;
	}

	public boolean isPostPenetrationDefect() {
		return postPenetrationDefect;
	}

	public void setPostPenetrationDefect(boolean postPenetrationDefect) {
		this.postPenetrationDefect = postPenetrationDefect;
	}

	public String getInducer() {
		return inducer;
	}

	public void setInducer(String p) {
		this.inducer = p;
	}

	public LinkedList<String> getExperimentalEvidences() {
		return experimentalEvidences;
	}

	public void addExperimentalEvidence(String p) {
		if (!StaticUtils.isEmpty(p)) {
			this.experimentalEvidences.add(p);
		}
	}

	public ProvenanceData getProvenanceData() {
		return provenanceData;
	}

	public void setProvenanceData(ProvenanceData provenanceData) {
		this.provenanceData = provenanceData;
	}

	public void setHostResponse(String p) {
		this.hostResponse = p;
	}

	public void setAASequence(String p) {
		this.AA_Sequence = p;
	}

	public void setNTSequence(String p) {
		this.NT_Sequence = p;
	}

	public String getAASequence() {
		return AA_Sequence;
	}

	public String getNTSequence() {
		return NT_Sequence;
	}

	public void setLethalKnockout(boolean b) {
		weHaveLethalKnockout = true;
		this.lethalKnockOut = b;
	}

	public String getLethalKnockout() {
		if (!weHaveLethalKnockout) {
			return null;
		}
		return Boolean.toString(this.lethalKnockOut);
	}

	public void setLocusID(String t) {
		this.locusID = t;
	}

	public String getLocusID() {
		return this.locusID;
	}

	public void setInVitroGrowth(String t) {
		this.inVitroGrowth = t;
	}

	public String getInVitroGrowth() {
		return this.inVitroGrowth;
	}

}
