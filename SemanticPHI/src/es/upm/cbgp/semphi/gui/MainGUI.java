package es.upm.cbgp.semphi.gui;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ProgressBar;

import es.upm.cbgp.semphi.logic.ConfigManager;
import es.upm.cbgp.semphi.logic.Constants;
import es.upm.cbgp.semphi.logic.Logic;
import es.upm.cbgp.semphi.logic.StaticUtils;
import es.upm.cbgp.semphi.objects.Notification;
import es.upm.cbgp.semphi.phiextractor.PhiXMLExtractor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MainGUI implements Observer {

	private boolean lastEntryAvailable;
	private Notification lastNotification;
	protected Shell shlSemanticphi;
	private Text TXTFile;
	private Text TXTLastEntry;
	private Button BTFile;
	private Button BTLoad;
	private Button CHSKOS;
	private Combo CMBProcess;
	private Label LBFrom;
	private Spinner SPFrom;
	private Label LBTo;
	private Spinner SPTo;
	private Button BTProcess;
	private Button BTStop;
	private Label LBStatus;
	private ProgressBar progressBar;
	private Logic logic;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainGUI window = new MainGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlSemanticphi.open();
		shlSemanticphi.layout();
		while (!shlSemanticphi.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlSemanticphi = new Shell();
		shlSemanticphi.setSize(565, 459);
		shlSemanticphi.setText("SemanticPHI");

		Group grpPhiXmlFile = new Group(shlSemanticphi, SWT.NONE);
		grpPhiXmlFile.setText("PHI XML File");
		grpPhiXmlFile.setBounds(10, 0, 529, 94);

		Label lblNewLabel = new Label(grpPhiXmlFile, SWT.NONE);
		lblNewLabel.setBounds(10, 31, 114, 15);
		lblNewLabel.setText("Location of XML file:");

		TXTFile = new Text(grpPhiXmlFile, SWT.BORDER);
		TXTFile.setEditable(false);
		TXTFile.setBounds(126, 28, 312, 21);

		BTFile = new Button(grpPhiXmlFile, SWT.NONE);
		BTFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				selectFile();
			}
		});
		BTFile.setBounds(444, 26, 75, 25);
		BTFile.setText("Browse");

		BTLoad = new Button(grpPhiXmlFile, SWT.NONE);
		BTLoad.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				loadPHIXML();
			}
		});
		BTLoad.setEnabled(false);
		BTLoad.setBounds(444, 61, 75, 25);
		BTLoad.setText("Load");

		Group grpSemantization = new Group(shlSemanticphi, SWT.NONE);
		grpSemantization.setText("Semantization");
		grpSemantization.setBounds(10, 100, 529, 200);

		Label lblLastEntryProcessed = new Label(grpSemantization, SWT.NONE);
		lblLastEntryProcessed.setBounds(10, 31, 114, 15);
		lblLastEntryProcessed.setText("Last entry processed");

		TXTLastEntry = new Text(grpSemantization, SWT.BORDER);
		TXTLastEntry.setEditable(false);
		TXTLastEntry.setBounds(130, 28, 91, 21);

		CHSKOS = new Button(grpSemantization, SWT.CHECK);
		CHSKOS.setEnabled(false);
		CHSKOS.setBounds(352, 31, 167, 16);
		CHSKOS.setText("Annotate dataset with SKOS");

		Label lblProcessDataset = new Label(grpSemantization, SWT.NONE);
		lblProcessDataset.setBounds(10, 76, 114, 15);
		lblProcessDataset.setText("Process dataset");

		CMBProcess = new Combo(grpSemantization, SWT.NONE);
		CMBProcess.setEnabled(false);
		CMBProcess.setBounds(130, 73, 162, 23);

		LBFrom = new Label(grpSemantization, SWT.NONE);
		LBFrom.setBounds(298, 77, 37, 15);
		LBFrom.setText("From");

		SPFrom = new Spinner(grpSemantization, SWT.BORDER);
		SPFrom.setBounds(335, 74, 78, 22);

		LBTo = new Label(grpSemantization, SWT.NONE);
		LBTo.setBounds(419, 77, 21, 15);
		LBTo.setText("To");

		SPTo = new Spinner(grpSemantization, SWT.BORDER);
		SPTo.setBounds(446, 74, 73, 22);

		BTProcess = new Button(grpSemantization, SWT.NONE);
		BTProcess.setEnabled(false);
		BTProcess.setBounds(444, 120, 75, 25);
		BTProcess.setText("Process");

		BTStop = new Button(grpSemantization, SWT.NONE);
		BTStop.setEnabled(false);
		BTStop.setBounds(444, 151, 75, 25);
		BTStop.setText("Stop");

		Group grpProgress = new Group(shlSemanticphi, SWT.NONE);
		grpProgress.setText("Progress");
		grpProgress.setBounds(10, 306, 529, 102);

		progressBar = new ProgressBar(grpProgress, SWT.NONE);
		progressBar.setBounds(10, 28, 509, 28);

		LBStatus = new Label(grpProgress, SWT.NONE);
		LBStatus.setBounds(10, 72, 448, 15);
		LBStatus.setText("New Label");
		init();
	}

	protected void loadPHIXML() {
		try {
			fullInterfaceActivation(false);
			logic.loadXML(this.TXTFile.getText(), this);
		} catch (Exception e) {
			ShowMessage.showMessage("Error",
					"Error loading XML file: " + e.getMessage(),
					SWT.ICON_ERROR, this.shlSemanticphi);
		}
	}

	private void loadInterfaceActivation(boolean b) {
		this.BTFile.setEnabled(b);
		this.BTLoad.setEnabled(b);
	}
	
	protected void selectFile() {
		FileDialog fd = new FileDialog(this.shlSemanticphi, SWT.OPEN);
		fd.setText("Open");
		String selected = fd.open();
		if (!StaticUtils.isEmpty(selected)) {
			this.BTLoad.setEnabled(true);
			this.TXTFile.setText(selected);
			try {
				ConfigManager.setConfig(Constants.XML_FILE, selected);
			} catch (Exception e) {
				ShowMessage.showMessage("Error",
						"Error saving XML file location: " + e.getMessage(),
						SWT.ICON_ERROR, this.shlSemanticphi);
			}
		}
	}

	private void init() {
		try {
			this.BTLoad.setEnabled(false);
			String xmlFile = ConfigManager.getConfig(Constants.XML_FILE);
			if (!StaticUtils.isEmpty(xmlFile)) {
				this.TXTFile.setText(xmlFile);
				this.BTLoad.setEnabled(true);
			}
			activationRangeEntries(false);
			this.CMBProcess.add("Entire dataset");
			this.CMBProcess.add("From last entry");
			this.CMBProcess.add("Range");
			this.CMBProcess.select(0);
			String lastEntry = ConfigManager.getConfig(Constants.LAST_ENTRY);
			if (!StaticUtils.isEmpty(lastEntry)) {
				this.TXTLastEntry.setText(lastEntry);
				lastEntryAvailable = true;
			}
			else {
				lastEntryAvailable = false;
				this.TXTLastEntry.setText("No registry");
			}
			this.logic = new Logic();
		} catch (Exception e) {
			ShowMessage.showMessage("Error",
					"Error loading GUI: " + e.getMessage(), SWT.ICON_ERROR,
					this.shlSemanticphi);
		}
	}

	private void activationRangeEntries(boolean b) {
		this.LBFrom.setVisible(b);
		this.LBTo.setVisible(b);
		this.SPFrom.setVisible(b);
		this.SPTo.setVisible(b);
	}

	public void update(Observable o, Object arg) {
		if (o instanceof PhiXMLExtractor) {
			if (arg instanceof Notification) {
				lastNotification = (Notification) arg;
				switch (lastNotification.getType()) {
				case Constants.ERROR:
					Display.getDefault().asyncExec(new Runnable() {
					    public void run() {
							ShowMessage.showMessage("Error", lastNotification.getMessage(),
									SWT.ICON_ERROR, shlSemanticphi);
							loadInterfaceActivation(true);
							fullInterfaceActivation(false);
					    }
					});

					break;
				case Constants.OK:
					Display.getDefault().asyncExec(new Runnable() {
					    public void run() {
							ShowMessage.showMessage("File loaded", lastNotification.getMessage(),
									SWT.ICON_INFORMATION, shlSemanticphi);
							fullInterfaceActivation(true);
					    }
					});
					

					break;
				}
			}
		}
	}

	private void fullInterfaceActivation(boolean b) {
		this.BTFile.setEnabled(b);
		this.BTLoad.setEnabled(b);
		this.CMBProcess.setEnabled(b);
		this.CHSKOS.setEnabled(b);
		this.BTProcess.setEnabled(b);
	}


}
