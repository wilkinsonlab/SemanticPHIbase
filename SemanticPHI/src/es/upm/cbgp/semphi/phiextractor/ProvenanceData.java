package es.upm.cbgp.semphi.phiextractor;

import java.util.LinkedList;

public class ProvenanceData {

	private String enteredBy;
	private LinkedList<String> literatureIDs;
	private String literatureSource;
	private String comments;
	private String reference;
	private String yearPublished;
	private String DOI;
	
	public String getDOI() {
		return DOI;
	}
	public void setDOI(String dOI) {
		DOI = dOI;
	}

	private ManualOrAutomatic typeExtraction;
	
	public ProvenanceData() {
		this.literatureIDs = new LinkedList<String>();
	}
	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public LinkedList<String> getLiteratureIDs() {
		return literatureIDs;
	}

	public void addLiteratureID(String literatureID) {
		this.literatureIDs.add(literatureID);
	}

	public String getLiteratureSource() {
		return literatureSource;
	}

	public void setLiteratureSource(String literatureSource) {
		this.literatureSource = literatureSource;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(String year) {
		this.yearPublished = year;
	}

	public ManualOrAutomatic getTypeExtraction() {
		return typeExtraction;
	}

	public void setTypeExtraction(ManualOrAutomatic typeExtraction) {
		this.typeExtraction = typeExtraction;
	}

	enum ManualOrAutomatic {
		MANUAL, AUTOMATIC
	}
}
