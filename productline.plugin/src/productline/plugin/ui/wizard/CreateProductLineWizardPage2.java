package productline.plugin.ui.wizard;

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

public class CreateProductLineWizardPage2 extends WizardPage {

	private Composite container;
	
	private final String BUTTON_DATA_KEY_ID = "ID";
	private final String BUTTON_DATA_VALUE_IMPORT_FROM_WEB = "IMPORT_FROM_WEB";
	private final String BUTTON_DATA_VALUE_IMPORT_FROM_FILE = "IMPORT_FROM_FILE";
	
	private Label lWebUserName;
	private Text tWebUserName;
	private Label lWebPassword;
	private Text tWebPassword;
	private Label lWebUrl;
	private Text tWebUrl;
	
	private Label lFilePath;
	private Text tFilePath;
	private Button bFilePath;
	
	protected CreateProductLineWizardPage2(String pageName) {
		super(pageName);
		setTitle("Import");
		setMessage("Import product line either from web or from yaml configuration file.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
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
								.equals(BUTTON_DATA_VALUE_IMPORT_FROM_WEB)) {
							setEnableToFileSection(true);
							setEnableToWebSection(false);
						} else {
							setEnableToFileSection(false);
							setEnableToWebSection(true);
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
		//New DB section
		Button button = new Button(container, SWT.RADIO);
		button.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_IMPORT_FROM_WEB);
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		button.setLayoutData(buttonData);
		button.setText("Create new DB");
		button.addListener(SWT.Selection, listener);		
		
		lWebUserName = new Label(container, SWT.NONE);
		lWebUserName.setText("Username:");		
		tWebUserName = new Text(container, SWT.BORDER);
		
		lWebPassword = new Label(container, SWT.NONE);
		lWebPassword.setText("Password:");		
		tWebPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		
		lWebUrl = new Label(container, SWT.NONE);
		lWebUrl.setText("URL:");		
		tWebUrl = new Text(container, SWT.BORDER);
		
		
		FormData dataUserName = new FormData();
		dataUserName.top = new FormAttachment(tWebUserName, 5, SWT.CENTER);
		dataUserName.left = new FormAttachment(0, 30);
		lWebUserName.setLayoutData(dataUserName);
		
		FormData dataUserNameNewText = new FormData();
		dataUserNameNewText.top = new FormAttachment(button, 5);
		dataUserNameNewText.left = new FormAttachment(lWebUserName, 5);
		dataUserNameNewText.right = new FormAttachment(100, -5);
		tWebUserName.setLayoutData(dataUserNameNewText);
		
		FormData dataPasswordNewLabel = new FormData();
		dataPasswordNewLabel.top = new FormAttachment(tWebPassword, 5, SWT.CENTER);
		dataPasswordNewLabel.left = new FormAttachment(0, 30);
		lWebPassword.setLayoutData(dataPasswordNewLabel);
		
		FormData dataPasswordNewText = new FormData();
		dataPasswordNewText.top = new FormAttachment(tWebUserName, 5);
		dataPasswordNewText.left = new FormAttachment(lWebUserName, 5);
		dataPasswordNewText.right = new FormAttachment(100, -5);
		tWebPassword.setLayoutData(dataPasswordNewText);
		
		FormData dataUrlLabel = new FormData();
		dataUrlLabel.top = new FormAttachment(tWebUrl, 5, SWT.CENTER);
		dataUrlLabel.left = new FormAttachment(0, 30);
		lWebUrl.setLayoutData(dataUrlLabel);
		
		FormData dataUrlText = new FormData();
		dataUrlText.top = new FormAttachment(tWebPassword, 5);
		dataUrlText.left = new FormAttachment(lWebUserName, 5);
		dataUrlText.right = new FormAttachment(100, -5);
		tWebUrl.setLayoutData(dataUrlText);
		
		
		//Existing DB section
		Button button2 = new Button(container, SWT.RADIO);
		button2.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_IMPORT_FROM_FILE);
		FormData button2Data = new FormData();
		button2Data.top = new FormAttachment(tWebUrl, 5);
		button2Data.left = new FormAttachment(0, 5);
		button2.setLayoutData(button2Data);
		button2.setText("Use existing DB");
		button2.addListener(SWT.Selection, listener);
		
		
		lFilePath = new Label(container, SWT.NONE);
		lFilePath.setText("Path");
		tFilePath = new Text(container, SWT.SINGLE | SWT.BORDER);
		bFilePath = new Button(container, SWT.PUSH);
		bFilePath.setText("Browse");
		bFilePath.addListener(SWT.Selection, createFileBrowserListener());
		
		
		FormData dataPathExistingLabel = new FormData();
		dataPathExistingLabel.top = new FormAttachment(tFilePath, 5, SWT.CENTER);
		dataPathExistingLabel.left = new FormAttachment(0, 30);
		lFilePath.setLayoutData(dataPathExistingLabel);
		
		FormData dataPathExistingText = new FormData();
		dataPathExistingText.top = new FormAttachment(button2, 5);
		dataPathExistingText.left = new FormAttachment(lWebUserName, 5);
		dataPathExistingText.right = new FormAttachment(85, 0);
		tFilePath.setLayoutData(dataPathExistingText);
		
		FormData dataPathExistingButton = new FormData();
		dataPathExistingButton.top = new FormAttachment(tFilePath, 5, SWT.CENTER);
		dataPathExistingButton.left = new FormAttachment(tFilePath, 5);
		dataPathExistingButton.right = new FormAttachment(100, -5);
		bFilePath.setLayoutData(dataPathExistingButton);
		
	}
	
	private void setEnableToWebSection(boolean enable) {
		tWebUserName.setEnabled(enable);
		tWebPassword.setEnabled(enable);
		tWebUrl.setEnabled(enable);
	}

	private void setEnableToFileSection(boolean enable) {
		tFilePath.setEnabled(enable);
		bFilePath.setEnabled(enable);
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
		        tFilePath.setText(fd.open());
			}
		};
	}

	public Text gettWebUserName() {
		return tWebUserName;
	}

	public void settWebUserName(Text tWebUserName) {
		this.tWebUserName = tWebUserName;
	}

	public Text gettWebPassword() {
		return tWebPassword;
	}

	public void settWebPassword(Text tWebPassword) {
		this.tWebPassword = tWebPassword;
	}

	public Text gettWebUrl() {
		return tWebUrl;
	}

	public void settWebUrl(Text tWebUrl) {
		this.tWebUrl = tWebUrl;
	}

	public Text gettFilePath() {
		return tFilePath;
	}

	public void settFilePath(Text tFilePath) {
		this.tFilePath = tFilePath;
	}
	
	

}
