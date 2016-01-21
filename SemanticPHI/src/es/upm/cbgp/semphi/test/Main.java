package es.upm.cbgp.semphi.test;

import es.upm.cbgp.semphi.phiextractor.PHIFile;
import es.upm.cbgp.semphi.phiextractor.PhiXMLExtractor;

public class Main {

	public Main() {
		PhiXMLExtractor pxe;
		try {
			pxe = new PhiXMLExtractor(new PHIFile("data/phidb.xml", "3.8"));
			pxe.load();
//			pxe.getDifferentHosts();
			pxe.getAllTags();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) {
		new Main();
	}

}
