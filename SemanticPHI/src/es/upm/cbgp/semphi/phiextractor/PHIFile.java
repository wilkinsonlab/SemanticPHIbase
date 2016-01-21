package es.upm.cbgp.semphi.phiextractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import es.upm.cbgp.semphi.logic.Constants;

public class PHIFile {

	private String VERSIONS[] = { "3.8", "4" };
	private String file;
	private String version;
	private Map<String, String> xmlTags;

	public PHIFile(String f, String v) throws Exception {
		boolean correctVersion = false;
		for (int i = 0; i < VERSIONS.length; i++) {
			if (VERSIONS[i].equalsIgnoreCase(v)) {
				correctVersion = true;
			}
		}
		if (!correctVersion) {
			throw new Exception("Incorrect version");
		}
		this.file = f;
		this.version = v;
		this.loadXMLTags();
	}

	private void loadXMLTags() throws Exception {
		String mapTagsFile = Constants.XML_TAGS_MAP + version + ".map";
		this.xmlTags = new HashMap<String, String>();
		@SuppressWarnings("resource")
		BufferedReader buffer = new BufferedReader(new FileReader(mapTagsFile));
		while (buffer.ready()) {
			String rd = buffer.readLine();
			String parts[] = rd.split("\t");
			if (parts.length == 2) {
				String key = parts[0];
				String val = parts[1];
				xmlTags.put(key, val);
			} else {
				throw new Exception("Error reading map tags file ("
						+ mapTagsFile + "): " + rd);
			}
		}
		buffer.close();
	}

	public String getFile() {
		return file;
	}

	public String getXMLTag(String xt) {
		return xmlTags.get(xt);
	}

	public String getVersion() {
		return this.version;
	}
}
