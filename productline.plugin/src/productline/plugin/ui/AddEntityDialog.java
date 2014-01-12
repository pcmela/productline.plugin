package productline.plugin.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

import productline.plugin.editor.IPackageListViewer;
import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import diploma.productline.HibernateUtil;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;
import productline.plugin.ui.PackageListDialog;

public class AddEntityDialog extends TitleAreaDialog implements
		IPackageListViewer {

	private String title;
	private String message;

	private BaseProductLineEntity parent;

	private Class className;

	//General Section
	private Label lName;
	private Text tName;
	private Label lDescription;
	private Text tDescition;
	
	//Module Section
	private Button btnAddPackage;
	private List list;
	private ListViewer listViewer;
	

	private IProject project;

	public AddEntityDialog(Shell parentShell, BaseProductLineEntity parent,
			Class className, IProject project) {
		super(parentShell);

		this.project = project;
		this.message = "Some message...";
		this.parent = parent;

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

	public void setPackageListInput(Set<IPackageFragment> elements) {
		listViewer.setInput(elements);
	}

	@Override
	public void create() {
		super.create();
		setTitle(title);
		setMessage(message, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
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
		}else if(className == Variability.class){
			createVariabilitySection(container);
		}else if(className == Element.class){
			createElementSection(container);
		}
		return null;
	}

	private void createElementSection(Composite container) {
		createGeneralSection(container);
	}

	private void createVariabilitySection(Composite container) {
		createGeneralSection(container);
	}

	private void createModuleSection(Composite container) {
		createGeneralSection(container);
		new Label(container, SWT.NONE);

		btnAddPackage = new Button(container, SWT.NONE);
		btnAddPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnAddPackage.setText("Add package");
		btnAddPackage.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				PackageListDialog dialog = new PackageListDialog(new Shell(),
						project, AddEntityDialog.this);
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
	
	private void createGeneralSection(Composite container){
		lName = new Label(container, SWT.NONE);
		lName.setText("Name:");

		GridData dataName = new GridData();
		dataName.grabExcessHorizontalSpace = true;
		dataName.horizontalAlignment = GridData.FILL;

		tName = new Text(container, SWT.BORDER);
		tName.setLayoutData(dataName);

		lDescription = new Label(container, SWT.NONE);
		lDescription.setText("Description:");

		GridData dataDescription = new GridData();
		dataDescription.grabExcessHorizontalSpace = true;
		dataDescription.horizontalAlignment = GridData.FILL;
		tDescition = new Text(container, SWT.BORDER);
		tDescition.setLayoutData(dataDescription);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		if(className == Module.class){
			saveModuleInput();
		}else if(className == Variability.class){
			saveVariabilityInput();
		}else if(className == Element.class){
			saveElementInput();
		}
	}
	
	private void saveVariabilityInput(){
		Variability v = new Variability();
		v.setName(tName.getText());
		v.setId(tName.getText());
		v.setModule(((VariabilityTreeContainer)parent).getParent());
		
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		session.save(v);
		session.getTransaction().commit();
	}
	
	private void saveElementInput(){
		Element e = new Element();
		e.setName(tName.getText());
		e.setId(tName.getText());
		e.setDescription(tDescition.getText());
		e.setModule(((ElementTreeContainer)parent).getParent());
		
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}
	
	private void saveModuleInput(){
		Object obj = listViewer.getInput();
		if (obj instanceof HashSet) {
			Module m = new Module();
			m.setName(tName.getText());
			m.setDescription(tDescition.getText());

			HashSet<IPackageFragment> list = ((HashSet<IPackageFragment>) obj);
			Set<PackageModule> packages = new HashSet<>();

			for (IPackageFragment pkg : list) {
				PackageModule p = new PackageModule();
				p.setModule(m);
				p.setName(pkg.getElementName());
				packages.add(p);
			}
			m.setProductLine((ProductLine) parent);
			m.setPackages(packages);

			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			session.save(m);
			session.getTransaction().commit();
		}
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}
}
