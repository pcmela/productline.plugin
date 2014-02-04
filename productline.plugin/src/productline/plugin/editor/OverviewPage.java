package productline.plugin.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import productline.plugin.ui.AddEntityDialog;
import productline.plugin.ui.CreateNewCustomLineDialog;
import productline.plugin.ui.PackageListContentProvider;
import productline.plugin.ui.PackageListDialog;
import productline.plugin.ui.ProductLineTreeContentProvider;
import productline.plugin.ui.ProductLineTreeLabelProvider;
import diploma.productline.DaoUtil;
import diploma.productline.dao.PackageDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class OverviewPage extends ProductLineFormPage implements
		IPackageListViewer {

	private SearchControl searchControl;
	private SearchMatcher searchMatcher;
	private Composite currentComposite;
	private ProductLine productLine;

	DependencyFilter searchFilter;
	TreeViewer treeViewer;
	boolean isSettingSelection = false;

	// Elements for details of ProductLine
	Label lProductLineName;
	Text tProductLineName;
	Label lProductLineDescription;
	Text tProductLineDescription;

	// Elements for details of Module
	Label lModuleName;
	Text tModuleName;
	Label lModuleDescription;
	Text tModuleDescription;
	private ListViewer listViewerPackage;
	private Label lPackage;
	private Button bAddPackage;
	private Button bRemovePackage;

	// Elements for details of Variability
	Label lVariabilityName;
	Text tVariabilityName;
	Label lVariabilityDescription;
	Text tVariabilityDescription;

	// Elements for details of Element
	Label lElementName;
	Text tElementName;
	Label lElementDescription;
	Text tElementDescription;

	FormToolkit toolkit;
	Composite detailComposite;
	Section detailSection;
	Section detailPackageDependenciesModuleSection;
	Composite detailPackageDependenciesModuleComposite;
	Composite rightComposite;

	IProject project;

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
		treeViewer.setLabelProvider(new ProductLineTreeLabelProvider());

		/*
		 * String path = "C:\\Users\\IBM_ADMIN\\Desktop\\Neon.yaml"; ProductLine
		 * productLine = YamlExtractor.extract(path);
		 */
		productLine = loadData(true);
		treeViewer.setInput(new Object[] { productLine });
		treeViewer.expandAll();

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object selection = ((TreeSelection) treeViewer.getSelection())
						.getFirstElement();

				if (selection instanceof ProductLine) {
					createDetailProductLine((ProductLine) selection);
				}
				if (selection instanceof Module) {
					createDetailModule((Module) selection);
				} else if (selection instanceof Variability) {
					createDetailVariability((Variability) selection);
				} else if (selection instanceof Element) {
					createDetailElement((Element) selection);
				} else {
					return;
				}
			}
		});

		final RemoveAction actionRemove = new RemoveAction();
		actionRemove.setText("Remove");

		final AddAction actionAdd = new AddAction();
		actionAdd.setText("Add");

		final CreateCustomLineAction createCustomLine = new CreateCustomLineAction();
		createCustomLine.setText("New Custom Line");

		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				TreeSelection selection = ((TreeSelection) treeViewer
						.getSelection());
				if (!selection.isEmpty()) {
					Object o = selection.getFirstElement();
					if (o instanceof ProductLine) {
						mgr.add(actionAdd);
						mgr.add(createCustomLine);
					}
					if (o instanceof VariabilityTreeContainer
							|| o instanceof ElementTreeContainer) {
						mgr.add(actionAdd);
					}
					if (o instanceof Variability || o instanceof Element
							|| o instanceof Module) {
						mgr.add(actionRemove);
					}
				}
			}
		});

		treeViewer.getControl().setMenu(
				mgr.createContextMenu(treeViewer.getControl()));

		Action newModuleElementAction = new Action("Create new Module", null) {
			public void run() {
				AddEntityDialog dialog = new AddEntityDialog(new Shell(),
						productLine, Module.class, project, properties);
				dialog.create();
				if (dialog.open() == Window.OK) {
					System.out.println("Module Created");
					productLine = loadData(false);
					treeViewer.setInput(new Object[] { productLine });
				}
			}
		};

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

	private void createDetailProductLine(ProductLine productLine) {
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

		tProductLineName.setLayoutData(tdName);
		tProductLineDescription.setLayoutData(tdDescription);

		rightComposite.layout();
	}

	private void createDetailModule(final Module module) {
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

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;

		GridData tdDescription = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdDescription.horizontalSpan = 3;
		tdDescription.heightHint = 75;

		tModuleName.setLayoutData(tdName);
		tModuleDescription.setLayoutData(tdDescription);

		tModuleName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				firePropertyChange(PROP_DIRTY);
			}
		});

		bAddPackage = new Button(detailPackageDependenciesModuleComposite,
				SWT.NONE);
		bAddPackage.setText("Add");
		bAddPackage.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				PackageListDialog dialog = new PackageListDialog(new Shell(),
						project, OverviewPage.this, module, properties);
				dialog.setStoredElements(module.getPackages());
				dialog.open();

			}
		});

		bRemovePackage = new Button(detailPackageDependenciesModuleComposite,
				SWT.NONE);
		bRemovePackage.setText("Remove");
		bRemovePackage.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// MessageDialog.openConfirm(new Shell(), "Remove package", );
				PackageModule pkg = (PackageModule) ((IStructuredSelection) listViewerPackage
						.getSelection()).getFirstElement();
				boolean ok = MessageDialog.openConfirm(
						new Shell(),
						"Remove package",
						"Are you sure that you want to remove package "
								+ pkg.getName() + "?");
				if (ok) {
					try (Connection con = DaoUtil.connect(properties)) {
						PackageDAO pDao = new PackageDAO(properties);
						pDao.delete(pkg.getId(), con);
						Set<PackageModule> packages = pDao
								.getPackagesWhithChildsByModule(module, con);
						listViewerPackage.setInput(packages);
						((Module) ((IStructuredSelection) treeViewer
								.getSelection()).getFirstElement())
								.setPackages(packages);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

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

		listViewerPackage = new ListViewer(
				detailPackageDependenciesModuleComposite, SWT.BORDER
						| SWT.V_SCROLL);
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

		rightComposite.layout();
	}

	private void createDetailVariability(Variability variability) {
		disposeActiveElements(rightComposite.getChildren());
		createDetailSection();

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
		tdName.heightHint = 75;

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;

		tVariabilityName.setLayoutData(tdName);
		tVariabilityDescription.setLayoutData(tdDescription);

		rightComposite.layout();
	}

	private void createDetailElement(Element element) {
		disposeActiveElements(rightComposite.getChildren());
		createDetailSection();

		lElementName = toolkit.createLabel(detailComposite, "Name", SWT.NONE);
		tElementName = toolkit.createText(detailComposite, element.getName());

		lElementDescription = toolkit.createLabel(detailComposite,
				"Description");
		lElementDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,
				false, false));
		tElementDescription = toolkit.createText(detailComposite,
				element.getDescription(), SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		GridData tdName = new GridData(SWT.FILL, SWT.TOP, true, false);
		tdName.horizontalSpan = 3;
		tdName.heightHint = 75;

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;

		tElementName.setLayoutData(tdName);
		tElementDescription.setLayoutData(tdDescription);

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
		/*
		 * gd_detailSection.widthHint = 100; gd_detailSection.minimumWidth =
		 * 100;
		 */
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Product Line Hiearchy");
		toolkit.paintBordersFor(detailSection);

		detailComposite = toolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(4, true));
		detailSection.setClient(detailComposite);
	}

	private void createDependenciesPackageSection() {
		detailPackageDependenciesModuleSection = toolkit.createSection(
				rightComposite, ExpandableComposite.TITLE_BAR);
		detailPackageDependenciesModuleSection.marginHeight = 1;
		GridData gd_detailListSection = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_detailListSection.widthHint = 100;
		gd_detailListSection.minimumWidth = 100;
		detailPackageDependenciesModuleSection
				.setLayoutData(gd_detailListSection);
		detailPackageDependenciesModuleSection.setText("Package dependencies");
		toolkit.paintBordersFor(detailPackageDependenciesModuleSection);

		detailPackageDependenciesModuleComposite = toolkit.createComposite(
				detailPackageDependenciesModuleSection, SWT.NONE);
		detailPackageDependenciesModuleComposite.setLayout(new FormLayout());
		detailPackageDependenciesModuleSection
				.setClient(detailPackageDependenciesModuleComposite);
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
				treeViewer.setInput(new Object[] { productLine });
			}
		});

		form.updateToolBar();

		searchControl.getSearchText().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				isSettingSelection = true;
				// selectListElements(searchMatcher);
				selectTreeElements(searchMatcher);
				setTreeFilter(searchFilter, false);
				isSettingSelection = false;
			}
		});

		searchControl.getSearchText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				isSettingSelection = true;
				// selectListElements(searchMatcher);
				selectTreeElements(searchMatcher);
				setTreeFilter(searchFilter, false);
				isSettingSelection = false;
			}
		});
	}

	protected void setTreeFilter(ViewerFilter filter, boolean force) {
		// currentFilter = filter;
		if (filter != null
				&& (force || (treeViewer.getFilters().length > 0 && treeViewer
						.getFilters()[0] != filter))) {
			treeViewer.addFilter(filter);
		}
	}

	void selectTreeElements(SearchMatcher matcher) {
		ProductLineTreeLabelProvider treeLabelProvider = (ProductLineTreeLabelProvider) treeViewer
				.getLabelProvider();
		// treeLabelProvider.setMatcher(matcher);
		treeViewer.refresh();
		treeViewer.expandAll();
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return true;
	}

	class RemoveAction extends Action {
		@Override
		public void runWithEvent(Event event) {

			if (((TreeSelection) treeViewer.getSelection()).getFirstElement() instanceof BaseProductLineEntity) {
				BaseProductLineEntity entity = (BaseProductLineEntity) ((TreeSelection) treeViewer
						.getSelection()).getFirstElement();

				boolean result = MessageDialog.openConfirm(new Shell(),
						"Confirm", "Are you sure that you want to remove "
								+ entity.getClass().getName() + " with name \""
								+ entity.toString() + "\"?");

				if (result) {
					System.out.println("OK");
				} else {
					System.out.println("FAILED");
				}
			} else {

			}
		}
	}

	class CreateCustomLineAction extends Action {

		@Override
		public void runWithEvent(Event event) {
			super.runWithEvent(event);
			Object input = treeViewer.getInput();
			ProductLine productLine = null;
			if (input instanceof Object[]) {
				if (((Object[]) input)[0] instanceof ProductLine) {
					productLine = (ProductLine) ((Object[]) input)[0];
				}
			} else {
				productLine = (ProductLine) input;
			}

			CreateNewCustomLineDialog dialog = new CreateNewCustomLineDialog(
					new Shell(), productLine, "", OverviewPage.this.project);
			dialog.open();
		}

	}

	class AddAction extends Action {
		@Override
		public void runWithEvent(Event event) {
			if (((TreeSelection) treeViewer.getSelection()).getFirstElement() instanceof BaseProductLineEntity) {
				BaseProductLineEntity entity = (BaseProductLineEntity) ((TreeSelection) treeViewer
						.getSelection()).getFirstElement();

				if (entity instanceof ProductLine) {
					AddEntityDialog dialog = new AddEntityDialog(new Shell(),
							entity, Module.class, project, properties);
					dialog.create();
					if (dialog.open() == Window.OK) {
						treeViewer.setInput(new Object[] { loadData(false) });
					}
				} else if (entity instanceof VariabilityTreeContainer) {
					AddEntityDialog dialog = new AddEntityDialog(new Shell(),
							entity, Variability.class, project, properties);
					dialog.create();
					if (dialog.open() == Window.OK) {
						treeViewer.setInput(new Object[] { loadData(false) });
					}
				} else if (entity instanceof ElementTreeContainer) {
					AddEntityDialog dialog = new AddEntityDialog(new Shell(),
							entity, Element.class, project, properties);
					dialog.create();
					if (dialog.open() == Window.OK) {
						treeViewer.setInput(new Object[] { loadData(false) });
					}
				}
			} else {

			}
		}
	}

	@Override
	public void setPackageListInput(Set<IPackageFragment> elements) {
		if (currentSelectedObject instanceof Module) {
			Module m = (Module) currentSelectedObject;
			Set<PackageModule> packages = new HashSet<>();

			for (IPackageFragment pkg : elements) {
				PackageModule p = new PackageModule();
				p.setModule(m);
				p.setName(pkg.getElementName());
				packages.add(p);
			}
			((Module) currentSelectedObject).getPackages().addAll(packages);
		}
		listViewerPackage.setInput(((Module) currentSelectedObject)
				.getPackages());
	}

}
