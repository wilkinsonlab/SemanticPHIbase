package es.upm.cbgp.phi2ppio.services.ncbitaxonomy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import org.biojavax.bio.taxa.NCBITaxon;
import org.biojavax.bio.taxa.io.NCBITaxonomyLoader;
import org.biojavax.bio.taxa.io.SimpleNCBITaxonomyLoader;

import es.upm.cbgp.phi2ppio.logic.Constants;

public class NCBITaxonomyDBService {

	private static NCBITaxonomyDBService nts = new NCBITaxonomyDBService();
	private BufferedReader nodes, names;
	private NCBITaxonomyLoader ntl;
	private LinkedList<NCBITaxon> taxons;

	private NCBITaxonomyDBService() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		this.taxons = new LinkedList<NCBITaxon>();
		nodes = new BufferedReader(new FileReader(
				Constants.NCBI_TAXONOMY_NODES_FILE));
		names = new BufferedReader(new FileReader(
				Constants.NCBI_TAXONOMY_NAMES_FILE));
		ntl = new SimpleNCBITaxonomyLoader();

		NCBITaxon t;
		while ((t = ntl.readNode(nodes)) != null) {
			System.out.println(t.getDisplayName());
			this.taxons.add(t);
		}
		while ((t = ntl.readName(names)) != null) {
			System.out.println(t.getDisplayName());
			this.taxons.add(t);
		}
	}

	public LinkedList<NCBITaxon> getTaxons() {
		return this.taxons;
	}
	
	public static NCBITaxonomyDBService getInstance() {
		return nts;
	}
}
