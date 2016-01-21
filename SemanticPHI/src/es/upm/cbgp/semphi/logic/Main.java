package es.upm.cbgp.semphi.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import es.upm.cbgp.semphi.ont.OntologyPopulator;
import es.upm.cbgp.semphi.phiextractor.PHIFile;
import es.upm.cbgp.semphi.phiextractor.PhiXMLExtractor;

public class Main {
	private final static int V3_VERSION = 1;
	private final static int V4_VERSION = 2;
	private final static int EXIT_OPTION = 3;
	private BufferedReader keyboard;
	
	public Main() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		keyboard = new BufferedReader(new InputStreamReader(System.in));
		int option = -1;
		while (true) {
			System.out.println("");
			System.out.println("Select an option");
			System.out.println("\t1. Create dump from XML v3");
			System.out.println("\t2. Create dump from XML v4");
			System.out.println("\t3. Exit");
			option = readInt();
			switch (option) {
			case V3_VERSION:
				execute("data/PHI_Data_3.8.xml", "3.8");
				break;
			case V4_VERSION:
				execute("data/PHI_data_4.xml", "4");
				break;
			case EXIT_OPTION:
				System.out.println("Exiting..");
				keyboard.close();
				System.exit(0);
				break;
			}
		}
	}

	private void execute(String f, String v) throws Exception {
		keyboard.close();
		System.out.println("Executing with version " + v);
		PhiXMLExtractor pxe = new PhiXMLExtractor(new PHIFile(f, v));
		pxe.load();
		OntologyPopulator op = new OntologyPopulator(pxe, true);
		op.run();
		System.exit(-1);
	}

	
	public int readInt() {
		try {
			System.out.print("> ");
			String rd = keyboard.readLine();
			System.out.println();
			return Integer.parseInt(rd);
		}
		catch (Exception e) {
			return -1;
		}
	}
	public static void main(String[] args) {
		new Main();
	}

}
