package productline.plugin.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import productline.plugin.ui.ProductLineIdDialog;
import productline.plugin.ui.ProjectListDialog;

public class CreateWizardOverview extends CreateWizardOverviewPOJO {

	protected CreateWizardOverview(String pageName) {
		super(pageName);
		setTitle("Create database");
		setDescription("Enter a product line name");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		// container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true));
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
				validateForm();
			}
		};

		createElements(listener);

		setControl(container);
	}

	Listener useExistingPlListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			if (bUseExistingProductLine.getSelection()) {
				checkAvailabilityExistingPLButton();
			} else {
				bExistingProductLines.setEnabled(false);
			}
		}
	};

	private void createElements(Listener listener) {
		// Product Line section
		lProductLineName = new Label(container, SWT.NONE);
		lProductLineName.setText("Product Line name:");
		tProductLineName = new Text(container, SWT.SINGLE | SWT.BORDER);

		lProductLineDescription = new Label(container, SWT.NONE);
		lProductLineDescription.setText("Product Line description:");
		tProductLineDescription = new Text(container, SWT.SINGLE | SWT.BORDER);

		// New DB section
		bCreateDb = new Button(container, SWT.RADIO);
		bCreateDb.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_NEWDB);
		bCreateDb.setText("Create new DB");
		bCreateDb.addListener(SWT.Selection, listener);

		lDefaultPath = new Label(container, SWT.NONE);
		lDefaultPath.setText("Default path:");
		tDefaultPath = new Text(container, SWT.SINGLE | SWT.BORDER);
		tDefaultPath.setEnabled(false);
		if (projectLoacation != null) {
			tDefaultPath.setText(projectLoacation.toString());
		}
		bDefaultPath = new Button(container, SWT.PUSH);
		bDefaultPath.setText("Choose project");
		bDefaultPath.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProjectListDialog dialog = new ProjectListDialog(new Shell(), tDefaultPath, CreateWizardOverview.this);
				dialog.open();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
		dataDefaultPathLabel.top = new FormAttachment(tDefaultPath, 5,
				SWT.CENTER);
		dataDefaultPathLabel.left = new FormAttachment(0, 5);
		lDefaultPath.setLayoutData(dataDefaultPathLabel);

		FormData dataDefaultPathText = new FormData();
		dataDefaultPathText.top = new FormAttachment(0, 5);
		dataDefaultPathText.left = new FormAttachment(lDefaultPath, 5);
		dataDefaultPathText.right = new FormAttachment(80, -5);
		tDefaultPath.setLayoutData(dataDefaultPathText);
		
		FormData dataDefaultPathButton = new FormData();
		dataDefaultPathButton.top = new FormAttachment(tDefaultPath, 5, SWT.CENTER);
		dataDefaultPathButton.left = new FormAttachment(tDefaultPath, 5);
		dataDefaultPathButton.right = new FormAttachment(100, -5);
		bDefaultPath.setLayoutData(dataDefaultPathButton);

		FormData dataPLNameLabel = new FormData();
		dataPLNameLabel.top = new FormAttachment(tProductLineName, 5,
				SWT.CENTER);
		dataPLNameLabel.left = new FormAttachment(0, 5);
		lProductLineName.setLayoutData(dataPLNameLabel);

		FormData dataPLNameText = new FormData();
		dataPLNameText.top = new FormAttachment(tDefaultPath, 5);
		dataPLNameText.left = new FormAttachment(lProductLineDescription, 5);
		dataPLNameText.right = new FormAttachment(100, -5);
		tProductLineName.setLayoutData(dataPLNameText);

		FormData dataPLDescriptionLabel = new FormData();
		dataPLDescriptionLabel.top = new FormAttachment(
				tProductLineDescription, 15, SWT.CENTER);
		dataPLDescriptionLabel.left = new FormAttachment(0, 5);
		lProductLineDescription.setLayoutData(dataPLDescriptionLabel);

		FormData dataPLDescriptionText = new FormData();
		dataPLDescriptionText.top = new FormAttachment(tProductLineName, 5);
		dataPLDescriptionText.left = new FormAttachment(
				lProductLineDescription, 5);
		dataPLDescriptionText.right = new FormAttachment(100, -5);
		tProductLineDescription.setLayoutData(dataPLDescriptionText);

		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(tProductLineDescription, 15);
		buttonData.left = new FormAttachment(0, 5);
		bCreateDb.setLayoutData(buttonData);

		FormData dataUserName = new FormData();
		dataUserName.top = new FormAttachment(tNewDbUserName, 5, SWT.CENTER);
		dataUserName.left = new FormAttachment(0, 30);
		lNewDbUserName.setLayoutData(dataUserName);

		FormData dataUserNameNewText = new FormData();
		dataUserNameNewText.top = new FormAttachment(bCreateDb, 5);
		dataUserNameNewText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataUserNameNewText.right = new FormAttachment(100, -5);
		tNewDbUserName.setLayoutData(dataUserNameNewText);

		FormData dataPasswordNewLabel = new FormData();
		dataPasswordNewLabel.top = new FormAttachment(tNewDbPassword, 5,
				SWT.CENTER);
		dataPasswordNewLabel.left = new FormAttachment(0, 30);
		lNewDbPassword.setLayoutData(dataPasswordNewLabel);

		FormData dataPasswordNewText = new FormData();
		dataPasswordNewText.top = new FormAttachment(tNewDbUserName, 5);
		dataPasswordNewText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPasswordNewText.right = new FormAttachment(100, -5);
		tNewDbPassword.setLayoutData(dataPasswordNewText);

		FormData dataPasswordConfirmNewLabel = new FormData();
		dataPasswordConfirmNewLabel.top = new FormAttachment(
				tNewDbPasswordConfirm, 5, SWT.CENTER);
		dataPasswordConfirmNewLabel.left = new FormAttachment(0, 30);
		lNewDbPasswordConfirm.setLayoutData(dataPasswordConfirmNewLabel);

		FormData dataPasswordConfirmNewText = new FormData();
		dataPasswordConfirmNewText.top = new FormAttachment(tNewDbPassword, 5);
		dataPasswordConfirmNewText.left = new FormAttachment(
				lNewDbPasswordConfirm, 5);
		dataPasswordConfirmNewText.right = new FormAttachment(100, -5);
		tNewDbPasswordConfirm.setLayoutData(dataPasswordConfirmNewText);

		// Existing DB section
		bExistingDb = new Button(container, SWT.RADIO);
		bExistingDb.setData(BUTTON_DATA_KEY_ID, BUTTON_DATA_VALUE_EXISTINGDB);
		FormData button2Data = new FormData();
		button2Data.top = new FormAttachment(tNewDbPasswordConfirm, 5);
		button2Data.left = new FormAttachment(0, 5);
		bExistingDb.setLayoutData(button2Data);
		bExistingDb.setText("Use existing DB");
		bExistingDb.addListener(SWT.Selection, listener);

		lExistingDbUserName = new Label(container, SWT.NONE);
		lExistingDbUserName.setText("Username:");
		tExistingDbUserName = new Text(container, SWT.BORDER);
		tExistingDbUserName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(bUseExistingProductLine.getSelection()){
					checkAvailabilityExistingPLButton();
				}
			}
		});

		lExistingDbPassword = new Label(container, SWT.NONE);
		lExistingDbPassword.setText("Password:");
		tExistingDbPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		tExistingDbPassword.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(bUseExistingProductLine.getSelection()){
					checkAvailabilityExistingPLButton();
				}
			}
		});

		lExistingDbPath = new Label(container, SWT.NONE);
		lExistingDbPath.setText("Connection string:");
		tExistingDbPath = new Text(container, SWT.SINGLE | SWT.BORDER);
		tExistingDbPath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(bUseExistingProductLine.getSelection()){
					checkAvailabilityExistingPLButton();
				}
			}
		});

		lUseExistingProductLine = new Label(container, SWT.NONE);
		lUseExistingProductLine.setText("Use existing PL:");
		bUseExistingProductLine = new Button(container, SWT.CHECK);
		bUseExistingProductLine.addListener(SWT.Selection,
				useExistingPlListener);

		lExistingId = new Label(container, SWT.NONE);
		lExistingId.setText("Existing ID:");
		tExistingId = new Text(container, SWT.SINGLE | SWT.BORDER);
		tExistingId.setEnabled(false);

		bExistingProductLines = new Button(container, SWT.PUSH);
		bExistingProductLines.setText("Get Id");
		bExistingProductLines.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openExistingProductLinesDialog(tExistingDbUserName.getText(),
						tExistingDbPassword.getText(),
						tExistingDbPath.getText(),
						tExistingId);
			}
		});

		FormData dataUserNameExistingLabel = new FormData();
		dataUserNameExistingLabel.top = new FormAttachment(tExistingDbUserName,
				5, SWT.CENTER);
		dataUserNameExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbUserName.setLayoutData(dataUserNameExistingLabel);

		FormData dataUserNameExistingText = new FormData();
		dataUserNameExistingText.top = new FormAttachment(bExistingDb, 5);
		dataUserNameExistingText.left = new FormAttachment(
				lNewDbPasswordConfirm, 5);
		dataUserNameExistingText.right = new FormAttachment(100, -5);
		tExistingDbUserName.setLayoutData(dataUserNameExistingText);

		FormData dataPasswordExistingLabel = new FormData();
		dataPasswordExistingLabel.top = new FormAttachment(tExistingDbPassword,
				5, SWT.CENTER);
		dataPasswordExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbPassword.setLayoutData(dataPasswordExistingLabel);

		FormData dataPasswordExistingText = new FormData();
		dataPasswordExistingText.top = new FormAttachment(tExistingDbUserName,
				5);
		dataPasswordExistingText.left = new FormAttachment(
				lNewDbPasswordConfirm, 5);
		dataPasswordExistingText.right = new FormAttachment(100, -5);
		tExistingDbPassword.setLayoutData(dataPasswordExistingText);

		// Existing connection String
		FormData dataPathExistingLabel = new FormData();
		dataPathExistingLabel.top = new FormAttachment(tExistingDbPath, 5,
				SWT.CENTER);
		dataPathExistingLabel.left = new FormAttachment(0, 30);
		lExistingDbPath.setLayoutData(dataPathExistingLabel);

		FormData dataPathExistingText = new FormData();
		dataPathExistingText.top = new FormAttachment(tExistingDbPassword, 5);
		dataPathExistingText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		dataPathExistingText.right = new FormAttachment(100, -5);
		tExistingDbPath.setLayoutData(dataPathExistingText);

		// Use existing ID
		FormData dataUseExistingLabel = new FormData();
		dataUseExistingLabel.top = new FormAttachment(bUseExistingProductLine,
				5, SWT.CENTER);
		dataUseExistingLabel.left = new FormAttachment(0, 30);
		lUseExistingProductLine.setLayoutData(dataUseExistingLabel);

		FormData dataUseExistingText = new FormData();
		dataUseExistingText.top = new FormAttachment(tExistingDbPath, 5);
		dataUseExistingText.left = new FormAttachment(lNewDbPasswordConfirm, 5);
		bUseExistingProductLine.setLayoutData(dataUseExistingText);

		// Use existing ID
		FormData dataUseExistingIdLabel = new FormData();
		dataUseExistingIdLabel.top = new FormAttachment(tExistingId, 5,
				SWT.CENTER);
		dataUseExistingIdLabel.left = new FormAttachment(0, 30);
		lExistingId.setLayoutData(dataUseExistingIdLabel);

		FormData dataUseExistingIdText = new FormData();
		dataUseExistingIdText.top = new FormAttachment(bUseExistingProductLine,
				5);
		dataUseExistingIdText.left = new FormAttachment(lNewDbPasswordConfirm,
				5);
		dataUseExistingIdText.right = new FormAttachment(80);
		tExistingId.setLayoutData(dataUseExistingIdText);
		
		FormData dataPathExistingButton = new FormData();
		dataPathExistingButton.top = new FormAttachment(tExistingId, 5, SWT.CENTER);
		dataPathExistingButton.left = new FormAttachment(tExistingId, 5);
		dataPathExistingButton.right = new FormAttachment(100, -5);
		bExistingProductLines.setLayoutData(dataPathExistingButton);

		/*
		 * FormData dataPathExistingButton = new FormData();
		 * dataPathExistingButton.top = new FormAttachment(tExistingDbPath, 5,
		 * SWT.CENTER); dataPathExistingButton.left = new
		 * FormAttachment(tExistingDbPath, 5); dataPathExistingButton.right =
		 * new FormAttachment(100, -5);
		 * bExistingDbPath.setLayoutData(dataPathExistingButton);
		 */

		bCreateDb.setSelection(true);
		setEnableToExistingDbSection(false);
		setEnableToNewDbSection(true);
		createNewDB = true;
		setModifyListener();
	}
	
	private void checkAvailabilityExistingPLButton(){
		if(!tExistingDbUserName.getText().trim().equals("")
				&& !tExistingDbPassword.getText().trim().equals("")
				&& !tExistingDbPath.getText().trim().equals("")){
			bExistingProductLines.setEnabled(true);
		}else{
			bExistingProductLines.setEnabled(false);
		}
	}

	private void setModifyListener() {
		ModifyListener listener = new ModifyListener() {
			/** {@inheritDoc} */
			public void modifyText(ModifyEvent e) {
				validateForm();
			}
		};
		tDefaultPath.addModifyListener(listener);
		tProductLineName.addModifyListener(listener);

		tNewDbUserName.addModifyListener(listener);
		tNewDbPassword.addModifyListener(listener);
		tNewDbPasswordConfirm.addModifyListener(listener);

		tExistingDbUserName.addModifyListener(listener);
		tExistingDbPassword.addModifyListener(listener);
		tExistingDbPath.addModifyListener(listener);
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
		bUseExistingProductLine.setEnabled(enable);
		if (bUseExistingProductLine.getSelection()) {
			if(enable){
				checkAvailabilityExistingPLButton();
			}else{
				bExistingDbPath.setEnabled(enable);
			}
		} else {
			bExistingProductLines.setEnabled(false);
		}
		/* bExistingDbPath.setEnabled(enable); */
	}

	private Listener createFileBrowserListener() {

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

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			Control control = tProductLineName;
			if (!control.setFocus()) {
				postSetFocus(control);
			}
		}
	}

	private void postSetFocus(final Control control) {
		Display display = control.getDisplay();
		if (display != null) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					control.setFocus();
				}
			});
		}
	}
}
