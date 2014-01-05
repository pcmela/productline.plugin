package productline.plugin.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class CreateProductLineWizardPage1 extends WizardPage {

	private final String BUTTON_DATA_KEY_ID = "ID";
	private final String BUTTON_DATA_VALUE_NEWDB = "NEW_DB";
	private final String BUTTON_DATA_VALUE_EXISTINGDB = "EXISTING_DB";

	private Composite container;

	private Label lNewDbUserName;
	private Text tNewDbUserName;
	private Label lNewDbPassword;
	private Text tNewDbPassword;

	private Label lExistingDbUserName;
	private Text tExistingDbUserName;
	private Label lExistingDbPassword;
	private Text tExistingDbPassword;

	protected CreateProductLineWizardPage1(String pageName) {
		super(pageName);
		setTitle("First Pagte");
		setDescription("First Page");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
							setEnableToExistingDbSection(false);
							setEnableToNewDbSection(true);
						} else {
							setEnableToExistingDbSection(true);
							setEnableToNewDbSection(false);
						}
					}
				}
				((Button) e.widget).setSelection(true);
			}
		};

		Button button = new Button(container, SWT.RADIO);
		button.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_NEWDB);
		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 2;
		button.setLayoutData(buttonData);
		button.setText("Create new DB");
		button.addListener(SWT.Selection, listener);
		createExistingDbSection();

		Button button2 = new Button(container, SWT.RADIO);
		button2.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_EXISTINGDB);
		GridData button2Data = new GridData();
		button2Data.horizontalSpan = 2;
		button2.setLayoutData(button2Data);
		button2.setText("Use existing DB");
		button2.addListener(SWT.Selection, listener);
		createNewDbSection();

		setControl(container);
	}

	private void createNewDbSection() {
		lNewDbUserName = new Label(container, SWT.NONE);
		lNewDbUserName.setText("Username:");
		tNewDbUserName = new Text(container, SWT.BORDER);
		GridData dataUserName = new GridData();
		dataUserName.grabExcessHorizontalSpace = true;
		dataUserName.horizontalAlignment = GridData.FILL;
		tNewDbUserName.setLayoutData(dataUserName);
		
		

		lNewDbPassword = new Label(container, SWT.NONE);
		lNewDbPassword.setText("Password");
		tNewDbPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		GridData dataPassword = new GridData();
		dataPassword.grabExcessHorizontalSpace = true;
		dataPassword.horizontalAlignment = GridData.FILL;
		tNewDbPassword.setLayoutData(dataPassword);
	}

	private void setEnableToNewDbSection(boolean enable) {
		tNewDbUserName.setEnabled(enable);
		tNewDbPassword.setEnabled(enable);
	}

	private void setEnableToExistingDbSection(boolean enable) {
		tExistingDbUserName.setEnabled(enable);
		tExistingDbPassword.setEnabled(enable);
	}

	private void createExistingDbSection() {
		lExistingDbUserName = new Label(container, SWT.NONE);
		lExistingDbUserName.setText("Username:");
		tExistingDbUserName = new Text(container, SWT.BORDER);
		GridData dataUserName = new GridData();
		dataUserName.grabExcessHorizontalSpace = true;
		dataUserName.horizontalAlignment = GridData.FILL;
		tExistingDbUserName.setLayoutData(dataUserName);

		lExistingDbPassword = new Label(container, SWT.NONE);
		lExistingDbPassword.setText("Password");
		tExistingDbPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		GridData dataPassword = new GridData();
		dataPassword.grabExcessHorizontalSpace = true;
		dataPassword.horizontalAlignment = GridData.FILL;
		tExistingDbPassword.setLayoutData(dataPassword);
	}

}
