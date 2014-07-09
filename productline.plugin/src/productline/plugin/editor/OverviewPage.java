package productline.plugin.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import productline.plugin.actions.AddAction;
import productline.plugin.actions.CreateCustomLineAction;
import productline.plugin.actions.ModuleElementAction;
import productline.plugin.actions.RemoveAction;
import productline.plugin.actions.ViewChildAction;
import productline.plugin.actions.WhereUsedAction;
import productline.plugin.actions.WhereUsedInCodeAction;
import productline.plugin.internal.ProductLineTreeComparator;
import productline.plugin.ui.listener.AddPackageButtonListener;
import productline.plugin.ui.listener.ModuleAddButtonListener;
import productline.plugin.ui.listener.ModuleIsVariableCheckboxListener;
import productline.plugin.ui.listener.ProductLineHiearchyMenuListener;
import productline.plugin.ui.listener.ProductLineHierarchySelectionListener;
import productline.plugin.ui.listener.RemoveElementButtonListener;
import productline.plugin.ui.listener.RemovePackageButtonListener;
import productline.plugin.ui.listener.ViewResourceDoubleClickListener;
import productline.plugin.ui.listener.model.ProductLineEventListener;
import productline.plugin.ui.providers.PackageListContentProvider;
import productline.plugin.ui.providers.ProductLineStyledLabelProvider;
import productline.plugin.ui.providers.ProductLineTreeContentProvider;
import diploma.productline.entity.Element;
import diploma.productline.entity.ElementType;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Resource;
import diploma.productline.entity.Variability;

public class OverviewPage extends OverViewPagePOJO implements
		ProductLineEventListener {

	private static Logger LOG = LoggerFactory.getLogger(OverviewPage.class);

	public OverviewPage(FormEditor editor, String id, String title,
			IProject project) {
		super(editor, id, title);
		this.editor = editor;
		this.project = project;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Product Line");

		form.setExpandHorizontal(true);
		form.setExpandVertical(true);

		Composite body = form.getBody();
		body.setLayout(new FillLayout());

		SashForm sashForm = new SashForm(body, SWT.NONE);
		toolkit.adapt(sashForm);
		toolkit.adapt(sashForm, true, true);

		createHierarchySection(sashForm, toolkit);
		createRightSection(sashForm, toolkit);

		createSearchBar(managedForm);
		getSite().setSelectionProvider(treeViewer);

	}

	private void createHierarchySection(Composite sashForm,
			FormToolkit formToolkit) {
		Composite hierarchyComposite = formToolkit.createComposite(sashForm,
				SWT.NONE);
		hierarchyComposite.setLayout(new GridLayout());

		Section hierarchySection = formToolkit.createSection(
				hierarchyComposite, ExpandableComposite.TITLE_BAR);
		hierarchySection.marginHeight = 1;
		GridData gd_hierarchySection = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_hierarchySection.widthHint = 100;
		gd_hierarchySection.minimumWidth = 100;
		hierarchySection.setLayoutData(gd_hierarchySection);
		hierarchySection.setText("Product Line Hiearchy");

		formToolkit.paintBordersFor(hierarchySection);

		Tree tree = formToolkit.createTree(hierarchySection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI);
		hierarchySection.setClient(tree);

		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new ProductLineTreeContentProvider());
		treeViewer.setLabelProvider(new ProductLineStyledLabelProvider());
		treeViewer.setComparator(new ProductLineTreeComparator());

		productLine = loadData(true);
		if (productLine != null) {
			treeViewer.setInput(new Object[] { productLine });
		} else {
			treeViewer.setInput(new Object[] {});
		}
		treeViewer.expandAll();

		// Listener which refresh editor depend on the selected item
		treeViewer
				.addSelectionChangedListener(new ProductLineHierarchySelectionListener(
						this));

		
		//Creating actions which will be available in menu on the treeViewer
		actionRemove = new RemoveAction(treeViewer, properties, this);
		actionRemove.setText("Remove");
		actionAdd = new AddAction(treeViewer, project, properties, this);
		actionAdd.setText("Add");

		final ViewChildAction viewChilrenAction = new ViewChildAction();
		viewChilrenAction.setText("View children");

		final WhereUsedAction whereUsedAction = new WhereUsedAction(treeViewer);
		whereUsedAction.setText("Where used");

		final WhereUsedInCodeAction whereUsedCodeAction = new WhereUsedInCodeAction(
				treeViewer, project.getWorkspace().getRoot().getLocation()
						.toOSString(), project);
		whereUsedCodeAction.setText("Where used (in source code)");

		createCustomLine = new CreateCustomLineAction(treeViewer, properties,
				project);
		createCustomLine.setText("New Custom Line");

		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		//Register all actions to the MenuManager
		mgr.addMenuListener(new ProductLineHiearchyMenuListener(mgr,
				actionRemove, actionAdd, createCustomLine, treeViewer,
				viewChilrenAction, whereUsedAction, whereUsedCodeAction));

		treeViewer.getControl().setMenu(
				mgr.createContextMenu(treeViewer.getControl()));

		Action newModuleElementAction = new ModuleElementAction(
				"Create new Module", null, productLine, project, this,
				treeViewer, properties);

		ToolBarManager modulesToolBarManager = new ToolBarManager(SWT.FLAT);
		modulesToolBarManager.add(newModuleElementAction);

		Composite toolbarComposite = toolkit.createComposite(hierarchySection);
		GridLayout toolbarLayout = new GridLayout(1, true);
		toolbarLayout.marginHeight = 0;
		toolbarLayout.marginWidth = 0;
		toolbarComposite.setLayout(toolbarLayout);
		toolbarComposite.setBackground(null);

		modulesToolBarManager.createControl(toolbarComposite);
		hierarchySection.setTextClient(toolbarComposite);
	}

	public void createDetailProductLine(ProductLine productLine,
			ModifyListener modifyListener, ModifyListener modifyListenerOther) {
		disposeActiveElements(rightComposite.getChildren());
		resetCurrentSelectedObject();

		// Set current selected object in HiearchyTree
		currentSelectedObject = productLine;

		createDetailSection();

		lProductLineName = toolkit.createLabel(detailComposite, "Name",
				SWT.NONE);
		tProductLineName = toolkit.createText(detailComposite,
				productLine.getName());

		lProductLineDescription = toolkit.createLabel(detailComposite,
				"Description");
		lProductLineDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,
				false, false));
		tProductLineDescription = toolkit.createText(detailComposite, null,
				SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;

		GridData tdDescription = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdDescription.horizontalSpan = 3;
		tdDescription.heightHint = 75;

		addDataBindingProductLine(productLine);

		tProductLineName.addModifyListener(modifyListener);
		tProductLineDescription.addModifyListener(modifyListenerOther);

		tProductLineName.setLayoutData(tdName);
		tProductLineDescription.setLayoutData(tdDescription);

		rightComposite.layout();
	}

	public void createDetailModule(final Module module,
			ModifyListener modifyListener, ModifyListener modifyListenerOther) {
		disposeActiveElements(rightComposite.getChildren());
		resetCurrentSelectedObject();

		// Set current selected object in HiearchyTree
		currentSelectedObject = module;

		createDetailSection();
		createDependenciesPackageSection();

		lModuleName = toolkit.createLabel(detailComposite, "Name", SWT.NONE);
		tModuleName = toolkit.createText(detailComposite, module.getName());

		lModuleDescription = toolkit
				.createLabel(detailComposite, "Description");
		lModuleDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				false));
		tModuleDescription = toolkit.createText(detailComposite,
				module.getDescription(), SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		lModuleIsVariable = toolkit
				.createLabel(detailComposite, "Is variable:");
		bModuleIsVariable = toolkit
				.createButton(detailComposite, "", SWT.CHECK);

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;

		GridData tdDescription = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdDescription.horizontalSpan = 3;
		tdDescription.heightHint = 75;

		GridData bdIsVariable = new GridData(SWT.LEFT, SWT.TOP, true, false);
		bdIsVariable.horizontalSpan = 3;

		tModuleName.setLayoutData(tdName);
		tModuleDescription.setLayoutData(tdDescription);
		bModuleIsVariable.setLayoutData(bdIsVariable);

		tModuleName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				firePropertyChange(PROP_DIRTY);
			}
		});

		bAddPackage = new Button(detailResourcesDetailComposite, SWT.NONE);
		bAddPackage.setText("Add");
		bAddPackage.addListener(SWT.Selection, new ModuleAddButtonListener(
				module, project, properties, this));

		bRemovePackage = new Button(detailResourcesDetailComposite, SWT.NONE);
		bRemovePackage.setText("Remove");
		bRemovePackage.addListener(SWT.Selection,
				new RemovePackageButtonListener(module, treeViewer, properties,
						listViewerPackage));

		FormData bdAddPackage = new FormData();
		bdAddPackage.left = new FormAttachment(0, 5);
		bdAddPackage.top = new FormAttachment(0, 5);
		bdAddPackage.right = new FormAttachment(0, 70);
		bAddPackage.setLayoutData(bdAddPackage);

		FormData bdRemovePackage = new FormData();
		bdRemovePackage.left = new FormAttachment(0, 5);
		bdRemovePackage.top = new FormAttachment(bAddPackage, 5);
		bdRemovePackage.right = new FormAttachment(0, 70);
		bRemovePackage.setLayoutData(bdRemovePackage);

		// Set up list with packages
		listViewerPackage = new ListViewer(detailResourcesDetailComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List list = listViewerPackage.getList();
		FormData dListViewerPackage = new FormData();
		dListViewerPackage.top = new FormAttachment(0, 5);
		dListViewerPackage.left = new FormAttachment(bAddPackage, 5);
		dListViewerPackage.right = new FormAttachment(100, -5);
		dListViewerPackage.bottom = new FormAttachment(100, -5);
		list.setLayoutData(dListViewerPackage);
		listViewerPackage.setContentProvider(new PackageListContentProvider());
		listViewerPackage.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return ((PackageModule) element).toString();
			}
		});
		listViewerPackage.setInput(module.getPackages());

		addDataBindingModule(module);
		tModuleName.addModifyListener(modifyListener);
		tModuleDescription.addModifyListener(modifyListenerOther);
		bModuleIsVariable.addListener(SWT.Selection,
				new ModuleIsVariableCheckboxListener(this, treeViewer));

		rightComposite.layout();
	}

	public void createDetailVariability(Variability variability,
			ModifyListener modifyListener, ModifyListener modifyListenerOther) {
		disposeActiveElements(rightComposite.getChildren());
		createDetailSection();

		resetCurrentSelectedObject();

		// Set current selected object in HiearchyTree
		currentSelectedObject = variability;

		lVariabilityName = toolkit.createLabel(detailComposite, "Name",
				SWT.NONE);
		tVariabilityName = toolkit.createText(detailComposite,
				variability.getName());

		lVariabilityDescription = toolkit.createLabel(detailComposite,
				"Description");
		lVariabilityDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,
				false, false));
		tVariabilityDescription = toolkit.createText(detailComposite, null,
				SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;
		tdDescription.heightHint = 75;

		tVariabilityName.setLayoutData(tdName);
		tVariabilityDescription.setLayoutData(tdDescription);

		addDataBindingVariable(variability);
		tVariabilityDescription.addModifyListener(modifyListenerOther);
		tVariabilityName.addModifyListener(modifyListener);
		rightComposite.layout();
	}

	public void createDetailElement(Element element,
			ModifyListener modifyListener, ModifyListener modifyListenerOther) {

		final Element elementObject = element;

		disposeActiveElements(rightComposite.getChildren());
		createDetailSection();
		createResourcesSection();

		resetCurrentSelectedObject();

		// Set current selected object in HiearchyTree
		currentSelectedObject = element;

		lElementName = toolkit.createLabel(detailComposite, "Name", SWT.NONE);
		tElementName = toolkit.createText(detailComposite, element.getName());

		lElementType = toolkit.createLabel(detailComposite, "Type", SWT.NONE);
		cElementType = new Combo(detailComposite, SWT.READ_ONLY);
		cElementType.setItems(ElementType.toArray());
		if (element.getType() != null) {
			cElementType.setText(element.getType().getName());
		}
		cElementType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (elementObject.getType() == null) {
					if (!cElementType.getText().equals("")) {
						updateElementTypeValue(OverviewPage.this.currentSelectedObject);
					}
				} else {
					if (!elementObject.getType().getName()
							.equals(cElementType.getText())) {
						updateElementTypeValue(OverviewPage.this.currentSelectedObject);

					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		lElementDescription = toolkit.createLabel(detailComposite,
				"Description");
		lElementDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,
				false, false));
		tElementDescription = toolkit.createText(detailComposite,
				element.getDescription(), SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;
		tdDescription.heightHint = 75;

		GridData cdType = new GridData(SWT.FILL, SWT.TOP, true, false);
		cdType.horizontalSpan = 3;

		tElementName.setLayoutData(tdName);
		tElementDescription.setLayoutData(tdDescription);
		cElementType.setLayoutData(cdType);

		bAddPackage = new Button(detailResourcesDetailComposite, SWT.NONE);
		bAddPackage.setText("Add");

		bRemovePackage = new Button(detailResourcesDetailComposite, SWT.NONE);
		bRemovePackage.setText("Remove");

		FormData bdAddPackage = new FormData();
		bdAddPackage.left = new FormAttachment(0, 5);
		bdAddPackage.top = new FormAttachment(0, 5);
		bdAddPackage.right = new FormAttachment(0, 70);
		bAddPackage.setLayoutData(bdAddPackage);

		FormData bdRemovePackage = new FormData();
		bdRemovePackage.left = new FormAttachment(0, 5);
		bdRemovePackage.top = new FormAttachment(bAddPackage, 5);
		bdRemovePackage.right = new FormAttachment(0, 70);
		bRemovePackage.setLayoutData(bdRemovePackage);

		listViewerPackage = new ListViewer(detailResourcesDetailComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		List list = listViewerPackage.getList();
		FormData dListViewerPackage = new FormData();
		dListViewerPackage.top = new FormAttachment(0, 5);
		dListViewerPackage.left = new FormAttachment(bAddPackage, 5);
		dListViewerPackage.right = new FormAttachment(100, -5);
		dListViewerPackage.bottom = new FormAttachment(100, -5);
		list.setLayoutData(dListViewerPackage);
		listViewerPackage.setContentProvider(new PackageListContentProvider());
		listViewerPackage.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return ((Resource) element).getRelativePath();
			}
		});
		listViewerPackage.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Resource t1 = (Resource) e1;
				Resource t2 = (Resource) e2;
				return t1.getRelativePath().compareTo(t2.getRelativePath());
			};
		});
		listViewerPackage.setInput(element.getResources());
		listViewerPackage.addDoubleClickListener(new ViewResourceDoubleClickListener());

		bAddPackage.addListener(SWT.Selection, new AddPackageButtonListener(
				project, properties, listViewerPackage, elementObject));

		bRemovePackage.addListener(SWT.Selection,
				new RemoveElementButtonListener(elementObject, properties, listViewerPackage));

		addDataBindingElement(element);
		tElementName.addModifyListener(modifyListener);
		tElementDescription.addModifyListener(modifyListener);
		rightComposite.layout();
	}

	private void createRightSection(Composite sashForm, FormToolkit toolkit) {
		rightComposite = toolkit.createComposite(sashForm);
		rightComposite.setLayout(new GridLayout(1, false));
		createDetailSection();
	}

	private void createDetailSection() {
		detailSection = toolkit.createSection(rightComposite,
				ExpandableComposite.TITLE_BAR);
		detailSection.marginHeight = 1;
		GridData gd_detailSection = new GridData(SWT.FILL, SWT.TOP, true, false);
		
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Product Line Details");
		toolkit.paintBordersFor(detailSection);

		detailComposite = toolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(4, true));
		detailSection.setClient(detailComposite);
	}

	private void createDependenciesPackageSection() {
		detailResourcesDetailSection = toolkit.createSection(rightComposite,
				ExpandableComposite.TITLE_BAR);
		detailResourcesDetailSection.marginHeight = 1;
		GridData gd_detailListSection = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_detailListSection.widthHint = 100;
		gd_detailListSection.minimumWidth = 100;
		detailResourcesDetailSection.setLayoutData(gd_detailListSection);
		detailResourcesDetailSection.setText("Package dependencies");
		toolkit.paintBordersFor(detailResourcesDetailSection);

		detailResourcesDetailComposite = toolkit.createComposite(
				detailResourcesDetailSection, SWT.NONE);
		detailResourcesDetailComposite.setLayout(new FormLayout());
		detailResourcesDetailSection.setClient(detailResourcesDetailComposite);
	}

	private void createResourcesSection() {
		detailResourcesDetailSection = toolkit.createSection(rightComposite,
				ExpandableComposite.TITLE_BAR);
		detailResourcesDetailSection.marginHeight = 1;
		GridData gd_detailListSection = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_detailListSection.widthHint = 100;
		gd_detailListSection.minimumWidth = 100;
		detailResourcesDetailSection.setLayoutData(gd_detailListSection);
		detailResourcesDetailSection.setText("Resources");
		toolkit.paintBordersFor(detailResourcesDetailSection);

		detailResourcesDetailComposite = toolkit.createComposite(
				detailResourcesDetailSection, SWT.NONE);
		detailResourcesDetailComposite.setLayout(new FormLayout());
		detailResourcesDetailSection.setClient(detailResourcesDetailComposite);
	}

	private void createSearchBar(IManagedForm managedForm) {
		searchControl = new SearchControl("Filter", managedForm);
		searchMatcher = new SearchMatcher(searchControl);
		searchFilter = new DependencyFilter(new SearchMatcher(searchControl));
		treeViewer.addFilter(searchFilter);

		ScrolledForm form = managedForm.getForm();

		IToolBarManager toolBarManager = form.getForm().getToolBarManager();
		toolBarManager.add(searchControl);

		toolBarManager.add(new Separator());
		toolBarManager.add(new Action("Refresh", ImageDescriptor
				.createFromImage(PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_ELCL_SYNCED))) {
			public void run() {
				productLine = loadData(true);
				if (productLine != null) {
					treeViewer.setInput(new Object[] { productLine });
				} else {
					treeViewer.setInput(new Object[] {});
				}
				treeViewer.expandAll();
			}
		});

		form.updateToolBar();

		searchControl.getSearchText().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				isSettingSelection = true;
				selectTreeElements(searchMatcher);
				setTreeFilter(searchFilter, false);
				isSettingSelection = false;
			}
		});

		searchControl.getSearchText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				isSettingSelection = true;
				selectTreeElements(searchMatcher);
				setTreeFilter(searchFilter, false);
				isSettingSelection = false;
			}
		});
	}

	private void updateElementTypeValue(Object o) {
		if (o instanceof Element) {
			Element elem = (Element) o;
			elem.setType(ElementType.getType(cElementType.getText()));
			elem.setDirty(true);
			setDirtyState();
			treeViewer.refresh();
			treeViewer.expandAll();
		}
	}

	void selectTreeElements(SearchMatcher matcher) {
		ProductLineStyledLabelProvider treeLabelProvider = (ProductLineStyledLabelProvider) treeViewer
				.getLabelProvider();
		treeViewer.refresh();
		treeViewer.expandAll();
	}
}
