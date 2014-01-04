package productline.plugin.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import productline.plugin.ui.AddEntityDialog;
import productline.plugin.ui.ProductLineTreeContentProvider;
import productline.plugin.ui.ProductLineTreeLabelProvider;
import diploma.productline.configuration.YamlExtractor;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class OverviewPage extends FormPage {

	SearchControl searchControl;

	SearchMatcher searchMatcher;

	Composite currentComposite;

	DependencyFilter searchFilter;
	TreeViewer treeViewer;
	boolean isSettingSelection = false;

	// Elements for details of Module
	Label lModuleName;
	Text tModuleName;
	Label lModuleDescription;
	Text tModuleDescription;

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

	public OverviewPage(FormEditor editor, String id, String title) {
		super(editor, id, title);

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
		createDetailSection(sashForm, toolkit);

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

		String path = "C:\\Users\\IBM_ADMIN\\Desktop\\Neon.yaml";
		ProductLine productLine = YamlExtractor.extract(path);

		treeViewer.setInput(productLine);
		treeViewer.expandAll();

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object selection = ((TreeSelection) treeViewer.getSelection())
						.getFirstElement();

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
		
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				TreeSelection selection = ((TreeSelection) treeViewer
						.getSelection());
				if (!selection.isEmpty()) {
					Object o = selection.getFirstElement();
					if ((o instanceof Variability) || (o instanceof Element)) {
						mgr.add(actionRemove);
					} else {
						mgr.add(actionAdd);
						mgr.add(actionRemove);
					}
				}
			}
		});

		treeViewer.getControl().setMenu(
				mgr.createContextMenu(treeViewer.getControl()));

	}

	private void createDetailModule(Module module) {
		disposeActiveElements();

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

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;

		tModuleName.setLayoutData(tdName);
		tModuleDescription.setLayoutData(tdDescription);

		detailComposite.layout();
	}

	private void createDetailVariability(Variability variability) {
		disposeActiveElements();

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

		tVariabilityName.setLayoutData(tdName);
		tVariabilityDescription.setLayoutData(tdDescription);

		detailComposite.layout();

	}

	private void createDetailElement(Element element) {
		disposeActiveElements();

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

		GridData tdDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
		tdDescription.horizontalSpan = 3;

		tElementName.setLayoutData(tdName);
		tElementDescription.setLayoutData(tdDescription);

		detailComposite.layout();
	}

	private void disposeActiveElements() {
		Control[] active = detailComposite.getChildren();
		for (Control c : active) {
			c.dispose();
		}

	}

	private void createDetailSection(Composite sashForm, FormToolkit toolkit) {

		detailSection = toolkit.createSection(sashForm,
				ExpandableComposite.TITLE_BAR);
		detailSection.marginHeight = 1;
		GridData gd_detailSection = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_detailSection.widthHint = 100;
		gd_detailSection.minimumWidth = 100;
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Product Line Hiearchy");
		toolkit.paintBordersFor(detailSection);

		detailComposite = toolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(4, true));
		detailSection.setClient(detailComposite);

	}

	private void createSearchBar(IManagedForm managedForm) {
		searchControl = new SearchControl("Filter", managedForm);
		searchMatcher = new SearchMatcher(searchControl);
		searchFilter = new DependencyFilter(new SearchMatcher(searchControl));
		treeViewer.addFilter(searchFilter); // by default is filtered

		ScrolledForm form = managedForm.getForm();

		IToolBarManager toolBarManager = form.getForm().getToolBarManager();
		toolBarManager.add(searchControl);

		toolBarManager.add(new Separator());
		toolBarManager.add(new Action("Refresh", ImageDescriptor
				.createFromImage(PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_ELCL_SYNCED))) {
			public void run() {
				loadData(true);
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

	void loadData(final boolean force) {
		if (treeViewer.getTree().isDisposed()) {
			return;
		}

		treeViewer.setInput(null);

		String path = "C:\\Users\\IBM_ADMIN\\Desktop\\Neon.yaml";
		ProductLine productLine = YamlExtractor.extract(path);

		treeViewer.setInput(productLine);
	}

	static class DependencyFilter extends ViewerFilter {
		protected SearchMatcher matcher;

		public DependencyFilter(SearchMatcher matcher) {
			this.matcher = matcher;
		}

		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (matcher != null && !matcher.isEmpty()) {
				return matcher.isMatchingArtifact(element);
			}
			return true;
		}

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

	class AddAction extends Action {
		@Override
		public void runWithEvent(Event event) {
			if (((TreeSelection) treeViewer.getSelection()).getFirstElement() instanceof BaseProductLineEntity) {
				BaseProductLineEntity entity = (BaseProductLineEntity) ((TreeSelection) treeViewer
						.getSelection()).getFirstElement();
				
				AddEntityDialog dialog = new AddEntityDialog(new Shell(), "Title", "Message");
				dialog.create();
				if (dialog.open() == Window.OK) {
				  System.out.println(dialog.getFirstName());
				  System.out.println(dialog.getLastName());
				} 
			} else {

			}
		}
	}

}
