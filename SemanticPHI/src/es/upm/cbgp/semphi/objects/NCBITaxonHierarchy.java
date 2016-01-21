package es.upm.cbgp.semphi.objects;

public class NCBITaxonHierarchy {
	
	private String label;
	private int id;
	private String CUI;
	
	public NCBITaxonHierarchy(String l, int i, String c) {
		this.label = l;
		this.id = i;
		this.CUI = c;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCUI() {
		return CUI;
	}
	public void setCUI(String cUI) {
		CUI = cUI;
	}

	public String toExternal() {
		return label + "@" + id + "@" + CUI;
	}
	
}
