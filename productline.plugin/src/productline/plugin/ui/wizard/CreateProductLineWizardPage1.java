package productline.plugin.ui.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateProductLineWizardPage1 extends WizardPage {

	private final String BUTTON_DATA_KEY_ID = "ID";
	private final String BUTTON_DATA_VALUE_NEWDB = "NEW_DB";
	private final String BUTTON_DATA_VALUE_EXISTINGDB = "EXISTING_DB";

	private IPath projectLoacation;
	
	private Composite container;

	private Label lDefaultPath;
	private Text tDefaultPath;
	
	private Label lProductLineName;
	private Text tProductLineName;
	private Label lProductLineDescription;
	private Text tProductLineDescription;
	
	private Label lNewDbUserName;
	private Text tNewDbUserName;
	private Label lNewDbPassword;
	private Text tNewDbPassword;
	private Label lNewDbPasswordConfirm;
	private Text tNewDbPasswordConfirm;

	private Label lExistingDbUserName;
	private Text tExistingDbUserName;
	private Label lExistingDbPassword;
	private Text tExistingDbPassword;
	private Label lExistingDbPath;
	private Text tExistingDbPath;
	private Button bExistingDbPath;
	
	private boolean createNewDB = true;
	

	protected CreateProductLineWizardPage1(String pageName) {
		super(pageName);
		setTitle("Create database");
		setDescription("Create database for storing specification of product line");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		//container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FormLayout layout = new FormLayout();
		container.setLayout(layout);

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				Control[] children = container.getChildren();
				for (int i = 0; i < children.length; i++) {
					Control child = children[i];
					if (e.widget != child && child instanceof Button
							&& (child.getStyle() & SWT.RADIO) != 0) {
						((Button) child).setSelection(false);
						if (((Button) child).getData(BUTTON_DATA_KEY_ID)
								.equals(BUTTON_DATA_VALUE_NEWDB)) {
							setEnableToExistingDbSection(true);
							setEnableToNewDbSection(false);
							createNewDB = false;
						} else {
							setEnableToExistingDbSection(false);
							setEnableToNewDbSection(true);
							createNewDB = true;
						}
					}
				}
				((Button) e.widget).setSelection(true);
			}
		};

		createElements(listener);
		

		setControl(container);
	}
	
	private void createElements(Listener listener){
		//Product Line section
		lProductLineName = new Label(container, SWT.NONE);
		lProductLineName.setText("Product Line name:");
		tProductLineName = new Text(container, SWT.SINGLE | SWT.BORDER);	
		
		lProductLineDescription = new Label(container, SWT.NONE);
		lProductLineDescription.setText("Product Line description:");
		tProductLineDescription = new Text(container, SWT.SINGLE | SWT.BORDER);
		
		//New DB section
		Button button = new Button(container, SWT.RADIO);
		button.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_NEWDB);
		button.setText("Create new DB");
		button.addListener(SWT.Selection, listener);
		
		lDefaultPath = new Label(container, SWT.NONE);
		lDefaultPath.setText("Default path:");
		tDefaultPath = new Text(container, SWT.SINGLE | SWT.BORDER);
		tDefaultPath.setEnabled(false);
		if(projectLoacation != null){
			tDefaultPath.setText(projectLoacation.toString());
		}
		
		lNewDbUserName = new Label(container, SWT.NONE);
		lNewDbUserName.setText("Username:");		
		tNewDbUserName = new Text(container, SWT.BORDER);
		
		lNewDbPassword = new Label(container, SWT.NONE);
		lNewDbPassword.setText("Password:");		
		tNewDbPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);		
		
		lNewDbPasswordConfirm = new Label(container, SWT.NONE);
		lNewDbPasswordConfirm.setText("Confirm password:");		
		tNewDbPasswordConfirm = new Text(container, SWT.PASSWORD | SWT.BORDER);
		
		
		FormData dataDefaultPathLabel = new FormData();
		dataDefaultPathLabel.top = new FormAttachment(tDefaultPath, 5, SWT.CENTER);
		dataDefaultPathLabel.left = new FormAttachment(0, 5);
		lDefaultPath.setLayoutData(dataDefaultPathLabel);
		
		FormData dataDefaultPathText = new FormData();
		dataDefaultPathText.top = new FormAttachment(0, 5);
		dataDefaultPathText.left = new FormAttachment(lDefaultPath, 5);
		dataDefaultPathText.right = new FormAttachment(100, -5);
		tDefaultPath.setLayoutData(dataDefaultPathText);
		
		FormData dataPLNameLabel = new FormData();
		dataPLNameLabel.top = new FormAttachment(tProductLineName, 5, SWT.CENTER);
		dataPLNameLabel.left = new FormAttachment(0, 5);
		lProductLineName.setLayoutData(dataPLNameLabel);
		
		FormData dataPLNameText = new FormData();
		dataPLNameText.top = new FormAttachment(tDefaultPath, 5);
		dataPLNameText.left = new FormAttachment(lProductLineDescription, 5);
		dataPLNameText.right = new FormAttachment(100, -5);
		tProductLineName.setLayoutData(dataPLNameText);
		
		FormData dataPLDescriptionLabel = new FormData();
		dataPLDescriptionLabel.top = new FormAttachment(tProductLineDescription, 15, SWT.CENTER);
		dataPLDescriptionLabel.left = new FormAttachment(0, 5);
		lProductLineDescription.setLayoutData(dataPLDescriptionLabel);
		
		FormData dataPLDescriptionText = new FormData();
		dataPLDescriptionText.top = new FormAttachment(tProductLineName, 5);
		dataPLDescriptionText.left = new FormAttachment(lProductLineDescription, 5);
		dataPLDescriptionText.right = new FormAttachment(100, -5);
		tProductLineDescription.setLayoutData(dataPLDescriptionText);
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(tProductLineDescription, 15);
		buttonData.left = new FormAttachment(0, 5);
		button.setLayoutData(buttonData);
		
		
		FormData dataUserName = new FormData();
		dataUserName.top = new FormAttachment(tNewDbUserName, 5, SWT.CENTER);
		dataUserName.left = new FormAttachment(0, 30);
		lNewDbUserName.setLayoutData(dataUserName);
		
		FormData dataUserNameNewText = new FormData();
		dataUserNameNewText.top = new FormAttachment(button, 5);
		dataUserNameNewText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataUserNameNewText.right = new FormAttachment(100, -5);
		tNewDbUserName.setLayoutData(dataUserNameNewText);
		
		FormData dataPasswordNewLabel = new FormData();
		dataPasswordNewLabel.top = new FormAttachment(tNewDbPassword, 5, SWT.CENTER);
		dataPasswordNewLabel.left = new FormAttachment(0, 30);
		lNewDbPassword.setLayoutData(dataPasswordNewLabel);
		
		FormData dataPasswordNewText = new FormData();
		dataPasswordNewText.top = new FormAttachment(tNewDbUserName, 5);
		dataPasswordNewText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPasswordNewText.right = new FormAttachment(100, -5);
		tNewDbPassword.setLayoutData(dataPasswordNewText);
		
		FormData dataPasswordConfirmNewLabel = new FormData();
		dataPasswordConfirmNewLabel.top = new FormAttachment(tNewDbPasswordConfirm, 5, SWT.CENTER);
		dataPasswordConfirmNewLabel.left = new FormAttachment(0, 30);
		lNewDbPasswordConfirm.setLayoutData(dataPasswordConfirmNewLabel);
		
		FormData dataPasswordConfirmNewText = new FormData();
		dataPasswordConfirmNewText.top = new FormAttachment(tNewDbPassword, 5);
		dataPasswordConfirmNewText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPasswordConfirmNewText.right = new FormAttachment(100, -5);
		tNewDbPasswordConfirm.setLayoutData(dataPasswordConfirmNewText);
		
		
		//Existing DB section
		Button button2 = new Button(container, SWT.RADIO);
		button2.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_EXISTINGDB);
		FormData button2Data = new FormData();
		button2Data.top = new FormAttachment(tNewDbPasswordConfirm, 5);
		button2Data.left = new FormAttachment(0, 5);
		button2.setLayoutData(button2Data);
		button2.setText("Use existing DB");
		button2.addListener(SWT.Selection, listener);
		
		lExistingDbUserName = new Label(container, SWT.NONE);
		lExistingDbUserName.setText("Username:");
		tExistingDbUserName = new Text(container, SWT.BORDER);
		
		lExistingDbPassword = new Label(container, SWT.NONE);
		lExistingDbPassword.setText("Password");
		tExistingDbPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		
		lExistingDbPath = new Label(container, SWT.NONE);
		lExistingDbPath.setText("Path");
		tExistingDbPath = new Text(container, SWT.SINGLE | SWT.BORDER);
		bExistingDbPath = new Button(container, SWT.PUSH);
		bExistingDbPath.setText("Browse");
		bExistingDbPath.addListener(SWT.Selection, createFileBrowserListener());
		
		FormData dataUserNameExistingLabel = new FormData();
		dataUserNameExistingLabel.top = new FormAttachment(tExistingDbUserName, 5, SWT.CENTER);
		dataUserNameExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbUserName.setLayoutData(dataUserNameExistingLabel);
		
		FormData dataUserNameExistingText = new FormData();
		dataUserNameExistingText.top = new FormAttachment(button2, 5);
		dataUserNameExistingText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataUserNameExistingText.right = new FormAttachment(100, -5);
		tExistingDbUserName.setLayoutData(dataUserNameExistingText);
		
		FormData dataPasswordExistingLabel = new FormData();
		dataPasswordExistingLabel.top = new FormAttachment(tExistingDbPassword, 5, SWT.CENTER);
		dataPasswordExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbPassword.setLayoutData(dataPasswordExistingLabel);
		
		FormData dataPasswordExistingText = new FormData();
		dataPasswordExistingText.top = new FormAttachment(tExistingDbUserName, 5);
		dataPasswordExistingText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPasswordExistingText.right = new FormAttachment(100, -5);
		tExistingDbPassword.setLayoutData(dataPasswordExistingText);
		
		FormData dataPathExistingLabel = new FormData();
		dataPathExistingLabel.top = new FormAttachment(tExistingDbPath, 5, SWT.CENTER);
		dataPathExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbPath.setLayoutData(dataPathExistingLabel);
		
		FormData dataPathExistingText = new FormData();
		dataPathExistingText.top = new FormAttachment(tExistingDbPassword, 5);
		dataPathExistingText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPathExistingText.right = new FormAttachment(85, 0);
		tExistingDbPath.setLayoutData(dataPathExistingText);
		
		FormData dataPathExistingButton = new FormData();
		dataPathExistingButton.top = new FormAttachment(tExistingDbPath, 5, SWT.CENTER);
		dataPathExistingButton.left = new FormAttachment(tExistingDbPath, 5);
		dataPathExistingButton.right = new FormAttachment(100, -5);
		bExistingDbPath.setLayoutData(dataPathExistingButton);
		
		button.setSelection(true);
		setEnableToExistingDbSection(false);
		setEnableToNewDbSection(true);
		createNewDB = false;
	}


	private void setEnableToNewDbSection(boolean enable) {
		tNewDbUserName.setEnabled(enable);
		tNewDbPassword.setEnabled(enable);
		tNewDbPasswordConfirm.setEnabled(enable);
	}

	private void setEnableToExistingDbSection(boolean enable) {
		tExistingDbUserName.setEnabled(enable);
		tExistingDbPassword.setEnabled(enable);
		tExistingDbPath.setEnabled(enable);
		bExistingDbPath.setEnabled(enable);
	}
	
	private Listener createFileBrowserListener(){
		
		return new Listener() {
			@Override
			public void handleEvent(Event e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.h2.db" };
		        fd.setFilterExtensions(filterExt);
		        tExistingDbPath.setText(fd.open());
			}
		};
	}

	public Text gettNewDbUserName() {
		return tNewDbUserName;
	}

	public void settNewDbUserName(Text tNewDbUserName) {
		this.tNewDbUserName = tNewDbUserName;
	}

	public Text gettNewDbPassword() {
		return tNewDbPassword;
	}

	public void settNewDbPassword(Text tNewDbPassword) {
		this.tNewDbPassword = tNewDbPassword;
	}

	public Text gettNewDbPasswordConfirm() {
		return tNewDbPasswordConfirm;
	}

	public void settNewDbPasswordConfirm(Text tNewDbPasswordConfirm) {
		this.tNewDbPasswordConfirm = tNewDbPasswordConfirm;
	}

	public Text gettExistingDbUserName() {
		return tExistingDbUserName;
	}

	public void settExistingDbUserName(Text tExistingDbUserName) {
		this.tExistingDbUserName = tExistingDbUserName;
	}

	public Text gettExistingDbPassword() {
		return tExistingDbPassword;
	}

	public void settExistingDbPassword(Text tExistingDbPassword) {
		this.tExistingDbPassword = tExistingDbPassword;
	}

	public Text gettExistingDbPath() {
		return tExistingDbPath;
	}

	public void settExistingDbPath(Text tExistingDbPath) {
		this.tExistingDbPath = tExistingDbPath;
	}

	public boolean isCreateNewDB() {
		return createNewDB;
	}

	public void setCreateNewDB(boolean createNewDB) {
		this.createNewDB = createNewDB;
	}

	public IPath getProjectLoacation() {
		return projectLoacation;
	}

	public void setProjectLoacation(IPath projectLoacation) {
		this.projectLoacation = projectLoacation;
	}

	public Text gettProductLineName() {
		return tProductLineName;
	}

	public void settProductLineName(Text tProductLineName) {
		this.tProductLineName = tProductLineName;
	}

	public Text gettProductLineDescription() {
		return tProductLineDescription;
	}

	public void settProductLineDescription(Text tProductLineDescription) {
		this.tProductLineDescription = tProductLineDescription;
	}

	

}
