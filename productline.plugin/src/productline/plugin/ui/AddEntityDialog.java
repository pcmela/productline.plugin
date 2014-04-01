package productline.plugin.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.actions.LTKLauncher;

import productline.plugin.editor.IPackageListViewer;
import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.internal.ElementSetTreeContainer;
import productline.plugin.internal.VariabilitySetTreeContainer;
import productline.plugin.ui.providers.PackageListContentProvider;
import productline.plugin.ui.providers.PackageListLabelProvider;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ElementDAO;
import diploma.productline.dao.ModuleDAO;
import diploma.productline.dao.PackageDAO;
import diploma.productline.dao.VariabilityDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.ElementType;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Type;
import diploma.productline.entity.Variability;

public class AddEntityDialog extends TitleAreaDialog implements
		IPackageListViewer {

	private String title;
	private String message;

	private BaseProductLineEntity parent;

	protected Class className;

	// General Section
	private Label lName;
	private Text tName;
	private Label lDescription;
	private Text tDescition;

	// Module Section
	private Label lIsVariable;
	private Button bIsVariable;
	private Button btnAddPackage;
	private List list;
	private ListViewer listViewer;
	private Properties properties;

	// Element Section
	private Label lElementType;
	private Combo cElementType;

	private IProject project;

	public AddEntityDialog(Shell parentShell, BaseProductLineEntity parent,
			Class className, IProject project, Properties properties) {
		super(parentShell);

		this.project = project;
		this.message = "Some message...";
		this.parent = parent;
		this.properties = properties;

		this.className = className;
		if (className == ProductLine.class) {
			this.title = "Creating new Product Line";
		} else if (className == Module.class) {
			this.title = "Creating new Module";
		} else if (className == Variability.class) {
			this.title = "Creating new Variability";
		} else if (className == Element.class) {
			this.title = "Creating new Element";
		}
	}

	public void setPackageListInput(Set<PackageModule> elements) {
		listViewer.setInput(elements);
	}

	@Override
	public void create() {
		super.create();
		setTitle(title);
		setMessage(message, IMessageProvider.INFORMATION);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		validateForm();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		/*
		 * createFirstName(container); createLastName(container);
		 */
		if (className == Module.class) {
			createModuleSection(container);
		} else if (className == Variability.class) {
			createVariabilitySection(container);
		} else if (className == Element.class) {
			createElementSection(container);
		}

		return null;
	}

	private void createElementSection(Composite container) {
		createGeneralSection(container);

		lElementType = new Label(container, SWT.NONE);
		lElementType.setText("Type:");

		GridData dataName = new GridData();
		dataName.grabExcessHorizontalSpace = true;
		dataName.horizontalAlignment = GridData.FILL;

		cElementType = new Combo(container, SWT.READ_ONLY);
		cElementType.setItems(ElementType.toArray());
		cElementType.setLayoutData(dataName);
		cElementType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				validateForm();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				validateForm();
			}
		});
	}

	private void createVariabilitySection(Composite container) {
		createGeneralSection(container);
	}

	private void createModuleSection(Composite container) {
		createGeneralSection(container);

		lIsVariable = new Label(container, SWT.NONE);
		lIsVariable.setText("Is Variable:");

		GridData dataName = new GridData();
		dataName.grabExcessHorizontalSpace = true;
		dataName.horizontalAlignment = GridData.FILL;

		bIsVariable = new Button(container, SWT.CHECK);
		bIsVariable.setLayoutData(dataName);

		new Label(container, SWT.NONE);

		btnAddPackage = new Button(container, SWT.NONE);
		btnAddPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnAddPackage.setText("Add package");
		btnAddPackage.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				PackageListDialog dialog = new PackageListDialog(new Shell(),
						project, AddEntityDialog.this, null, properties);
				dialog.setStoredElements((Set<?>) listViewer.getInput());
				dialog.open();

			}
		});
		new Label(container, SWT.NONE);

		listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		list = listViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));

		listViewer.setContentProvider(new PackageListContentProvider());

		listViewer.setLabelProvider(new PackageListLabelProvider());
	}

	private void createGeneralSection(Composite container) {
		lName = new Label(container, SWT.NONE);
		lName.setText("Name:");

		GridData dataName = new GridData();
		dataName.grabExcessHorizontalSpace = true;
		dataName.horizontalAlignment = GridData.FILL;

		tName = new Text(container, SWT.BORDER);
		tName.setLayoutData(dataName);
		tName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateForm();
			}
		});

		lDescription = new Label(container, SWT.NONE);
		lDescription.setText("Description:");

		GridData dataDescription = new GridData();
		dataDescription.grabExcessHorizontalSpace = true;
		dataDescription.horizontalAlignment = GridData.FILL;
		tDescition = new Text(container, SWT.BORDER);
		tDescition.setLayoutData(dataDescription);
	}

	private void validateForm() {
		StringBuilder message = new StringBuilder();
		boolean valid = true;

		if (tName.getText().equals("")) {
			valid = false;
			message.append("You must enter valid name for new item!");
		}

		if (AddEntityDialog.this.className == Element.class) {
			if (cElementType.getText().equals("")) {
				valid = false;
				if (!message.toString().equals(""))
					message.append("\n");
				message.append("You must select type of Element!");
			}
		}
		if (!valid) {
			AddEntityDialog.this.setMessage(message.toString(),
					IMessageProvider.ERROR);
		} else {
			AddEntityDialog.this.setMessage("");
		}
		getButton(IDialogConstants.OK_ID).setEnabled(valid);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		if (className == Module.class) {
			saveModuleInput();
		} else if (className == Variability.class) {
			saveVariabilityInput();
		} else if (className == Element.class) {
			saveElementInput();
		}
	}

	private void saveVariabilityInput() {
		Variability v = new Variability();
		v.setName(tName.getText());
		// v.setId(tName.getText());
		v.setModule(((VariabilitySetTreeContainer) parent).getParent());

		try (Connection con = DaoUtil.connect(properties)) {
			VariabilityDAO vDao = new VariabilityDAO();
			vDao.save(v, con);
		} catch (ClassNotFoundException err) {
			// TODO Auto-generated catch block
			DefaultMessageDialog.driversNotFoundDialog("H2");
			err.printStackTrace();
		} catch (SQLException err) {
			// TODO Auto-generated catch block
			DefaultMessageDialog.sqlExceptionDialog(err.getMessage());
			err.printStackTrace();
		}
	}

	private void saveElementInput() {
		Element e = new Element();
		e.setName(tName.getText());
		e.setDescription(tDescition.getText());
		e.setModule(((ElementSetTreeContainer) parent).getParent());

		String typeValue = cElementType.getText();
		ElementType et = ElementType.get(typeValue);
		Type t = new Type();
		t.setId(et.getId());
		t.setName(et.toString());
		e.setType(t);

		try (Connection con = DaoUtil.connect(properties)) {
			ElementDAO eDao = new ElementDAO();
			eDao.save(e, con);
		} catch (ClassNotFoundException err) {
			// TODO Auto-generated catch block
			err.printStackTrace();
		} catch (SQLException err) {
			// TODO Auto-generated catch block
			err.printStackTrace();
		}
	}

	private void saveModuleInput() {
		Set<PackageModule> packages = (Set<PackageModule>) listViewer
				.getInput();
		Module m = new Module();

		m.setName(tName.getText());
		m.setDescription(tDescition.getText());
		m.setProductLine((ProductLine) parent);
		m.setPackages(packages);

		try (Connection con = DaoUtil.connect(properties)) {
			ModuleDAO mDao = new ModuleDAO();
			m.setId(mDao.save(m, con));

			if (packages != null) {
				for (PackageModule pkg : packages) {
					pkg.setModule(m);
					PackageDAO pDao = new PackageDAO();
					pkg.setId(pDao.save(pkg, con));
				}
			}
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
		} catch (SQLException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
		}

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}
}
