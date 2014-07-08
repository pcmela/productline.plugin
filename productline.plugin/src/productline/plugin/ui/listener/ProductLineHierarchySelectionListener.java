package productline.plugin.ui.listener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.ModifyListener;

import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import productline.plugin.ui.listener.model.ProductLineEventListener;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;

public class ProductLineHierarchySelectionListener implements
		ISelectionChangedListener {

	private ProductLineEventListener listener;
	
	public ProductLineHierarchySelectionListener(
			ProductLineEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		final Object selection = ((TreeSelection) listener.getTreeViewer().getSelection())
				.getFirstElement();

		ModifyListener modifyListenerName = new ProductLineHiearchyModifyTextListener(
				selection, listener);
		ModifyListener modifyListenerOther = new ProductLineHiearchyModifyTextListener(
				selection, listener);

		if (selection instanceof ProductLine) {
			listener.createDetailProductLine((ProductLine) selection,
					modifyListenerName, modifyListenerOther);
		}
		if (selection instanceof Module) {
			listener.createDetailModule((Module) selection, modifyListenerName,
					modifyListenerOther);
		} else if (selection instanceof VariabilityTreeContainer) {
			listener.createDetailVariability(
					((VariabilityTreeContainer) selection).getSource(),
					modifyListenerName, modifyListenerOther);
		} else if (selection instanceof ElementTreeContainer) {
			listener.createDetailElement(((ElementTreeContainer) selection).getSource(),
					modifyListenerName, modifyListenerOther);
		} else {
			return;
		}
	}
}
