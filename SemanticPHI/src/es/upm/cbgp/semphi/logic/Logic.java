package es.upm.cbgp.semphi.logic;

import es.upm.cbgp.semphi.gui.MainGUI;
import es.upm.cbgp.semphi.phiextractor.PhiXMLExtractor;

public class Logic {

	public void loadXML(String f, MainGUI mg) throws Exception {
		Thread t = new Thread(new PhiXMLExtractor(f, mg));
		t.start();
	}
}
