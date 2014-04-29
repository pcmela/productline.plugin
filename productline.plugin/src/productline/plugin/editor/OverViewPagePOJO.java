package productline.plugin.editor;

import java.util.Properties;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import productline.plugin.actions.AddAction;
import productline.plugin.actions.CreateCustomLineAction;
import productline.plugin.actions.RemoveAction;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class OverViewPagePOJO extends ProductLineFormPage implements
		IPackageListViewer {

	protected SearchControl searchControl;
	protected SearchMatcher searchMatcher;
	protected Composite currentComposite;
	protected ProductLine productLine;

	protected DependencyFilter searchFilter;
	protected TreeViewer treeViewer;
	protected boolean isSettingSelection = false;

	// Elements for details of ProductLine
	protected Label lProductLineName;
	protected Text tProductLineName;
	protected Label lProductLineDescription;
	protected Text tProductLineDescription;

	// Elements for details of Module
	protected Label lModuleName;
	protected Text tModuleName;
	protected Label lModuleDescription;
	protected Text tModuleDescription;
	protected ListViewer listViewerPackage;
	protected Label lPackage;
	protected Button bAddPackage;
	protected Button bRemovePackage;
	protected Label lModuleIsVariable;
	protected Button bModuleIsVariable;

	// Elements for details of Variability
	protected Label lVariabilityName;
	protected Text tVariabilityName;
	protected Label lVariabilityDescription;
	protected Text tVariabilityDescription;

	// Elements for details of Element
	protected Label lElementName;
	protected Text tElementName;
	protected Label lElementDescription;
	protected Text tElementDescription;
	protected Label lElementType;
	protected Combo cElementType;

	protected FormToolkit toolkit;
	protected Composite detailComposite;
	protected Section detailSection;
	protected Section detailResourcesDetailSection;
	protected Composite detailResourcesDetailComposite;
	protected Composite rightComposite;

	private IValidator nameValidator = null;
	protected IProject project;
	
	protected RemoveAction actionRemove;
	protected AddAction actionAdd;
	protected CreateCustomLineAction createCustomLine;

	public OverViewPagePOJO(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
		localDbConfiguration.setDataLocal(properties);
		refreshActionProperties(properties);
		
	}
	
	protected void refreshActionProperties(Properties properties){
		if(actionAdd != null){
			actionAdd.setProperties(properties);
		}
		
		if(actionRemove != null){
			actionRemove.setProperties(properties);
		}
		
		if(createCustomLine != null){
			createCustomLine.setProperties(properties);
		}
	}

	protected IValidator getNameValidator() {
		if (nameValidator == null) {
			nameValidator = new IValidator() {
				@Override
				public IStatus validate(Object value) {

					if (!value.toString().trim().equals("")) {
						return ValidationStatus.ok();

					}
					return ValidationStatus.error("Name musn't be empty!");
				}
			};
			return nameValidator;
		}

		return nameValidator;
	}
	
	protected void addDataBindingProductLine(ProductLine productLine) {

		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(getNameValidator());
		IObservableValue name = PojoProperties.value("name").observe(productLine);
		IObservableValue description = PojoProperties.value("description")
				.observe(productLine);
		IObservableValue targetName = WidgetProperties.text(SWT.Modify)
				.observe(tProductLineName);
		IObservableValue targetDescription = WidgetProperties.text(SWT.Modify)
				.observe(tProductLineDescription);

		Binding bindName = dataBindingContext.bindValue(targetName, name,
				strategy, null);
		ControlDecorationSupport.create(bindName, SWT.TOP | SWT.LEFT);
		dataBindingContext.bindValue(targetDescription, description);
	}

	protected void addDataBindingModule(Module module) {

		// create UpdateValueStrategy and assign
		// to the binding
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(getNameValidator());
		IObservableValue name = PojoProperties.value("name").observe(module);
		IObservableValue description = PojoProperties.value("description")
				.observe(module);
		IObservableValue targetName = WidgetProperties.text(SWT.Modify)
				.observe(tModuleName);
		IObservableValue targetDescription = WidgetProperties.text(SWT.Modify)
				.observe(tModuleDescription);
		IObservableValue isVariable = PojoProperties.value("variable").observe(
				module);

		Binding bindName = dataBindingContext.bindValue(targetName, name,
				strategy, null);
		ControlDecorationSupport.create(bindName, SWT.TOP | SWT.LEFT);
		dataBindingContext.bindValue(targetDescription, description);
		dataBindingContext.bindValue(
				SWTObservables.observeSelection(bModuleIsVariable), isVariable);
	}

	protected void addDataBindingVariable(Variability variability) {

		// create UpdateValueStrategy and assign
		// to the binding
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(getNameValidator());

		IObservableValue name = PojoProperties.value("name").observe(
				variability);
		IObservableValue description = PojoProperties.value("description")
				.observe(variability);
		IObservableValue targetName = WidgetProperties.text(SWT.Modify)
				.observe(tVariabilityName);
		IObservableValue targetDescription = WidgetProperties.text(SWT.Modify)
				.observe(tVariabilityDescription);

		Binding bindName = dataBindingContext.bindValue(targetName, name,
				strategy, null);
		ControlDecorationSupport.create(bindName, SWT.TOP | SWT.LEFT);
		dataBindingContext.bindValue(targetDescription, description);
	}

	protected void addDataBindingElement(Element element) {

		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(getNameValidator());

		IObservableValue name = PojoProperties.value("name").observe(element);
		IObservableValue description = PojoProperties.value("description")
				.observe(element);

		IObservableValue targetName = WidgetProperties.text(SWT.Modify)
				.observe(tElementName);
		IObservableValue targetDescription = WidgetProperties.text(SWT.Modify)
				.observe(tElementDescription);
		IObservableValue targetType = WidgetProperties.selection().observe(
				cElementType);

		Binding bindName = dataBindingContext.bindValue(targetName, name,
				strategy, null);
		ControlDecorationSupport.create(bindName, SWT.TOP | SWT.LEFT);
		dataBindingContext.bindValue(targetDescription, description);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	public void setDataBindingContext(DataBindingContext dataBindingContext) {
		this.dataBindingContext = dataBindingContext;
	}

	public void setPackageListInput(Set<PackageModule> packages) {
		if (currentSelectedObject instanceof Module) {
			/*Module m = (Module) currentSelectedObject;
			Set<PackageModule> packages = new HashSet<>();

			for (IPackageFragment pkg : elements) {
				PackageModule p = new PackageModule();
				p.setModule(m);
				p.setName(pkg.getElementName());
				packages.add(p);
			}*/
			((Module) currentSelectedObject).getPackages().addAll(packages);
		}
		listViewerPackage.setInput(((Module) currentSelectedObject)
				.getPackages());
	}
	
	public void refreshTree() {
		ProductLine p = loadData(true);
		if (p != null) {
			treeViewer.setInput(new Object[] { p });
		} else {
			treeViewer.setInput(new Object[] {});
		}
		treeViewer.expandAll();
	}
	
	protected void setDirtyState() {
		isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}
	
	protected void setTreeFilter(ViewerFilter filter, boolean force) {
		// currentFilter = filter;
		if (filter != null
				&& (force || (treeViewer.getFilters().length > 0 && treeViewer
						.getFilters()[0] != filter))) {
			treeViewer.addFilter(filter);
		}
	}

}
