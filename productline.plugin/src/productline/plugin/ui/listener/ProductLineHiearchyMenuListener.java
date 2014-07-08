package productline.plugin.ui.listener;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import productline.plugin.actions.AddAction;
import productline.plugin.actions.CreateCustomLineAction;
import productline.plugin.actions.RemoveAction;
import productline.plugin.actions.ViewChildAction;
import productline.plugin.actions.WhereUsedAction;
import productline.plugin.actions.WhereUsedInCodeAction;
import productline.plugin.internal.ElementSetTreeContainer;
import productline.plugin.internal.VariabilitySetTreeContainer;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class ProductLineHiearchyMenuListener implements IMenuListener {

	protected MenuManager mgr;

	protected RemoveAction actionRemove;
	protected AddAction actionAdd;
	protected CreateCustomLineAction createCustomLine;
	protected TreeViewer treeViewer;
	protected ViewChildAction viewChilrenAction;
	protected WhereUsedAction whereUsedAction;
	protected WhereUsedInCodeAction whereUsedCodeAction;

	public ProductLineHiearchyMenuListener(MenuManager mgr,
			RemoveAction actionRemove, AddAction actionAdd,
			CreateCustomLineAction createCustomLine, TreeViewer treeViewer,
			ViewChildAction viewChilrenAction, WhereUsedAction whereUsedAction,
			WhereUsedInCodeAction whereUsedCodeAction) {

		this.mgr = mgr;
		this.actionRemove = actionRemove;
		this.createCustomLine = createCustomLine;
		this.treeViewer = treeViewer;
		this.viewChilrenAction = viewChilrenAction;
		this.whereUsedAction = whereUsedAction;
		this.whereUsedCodeAction = whereUsedCodeAction;
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		TreeSelection selection = ((TreeSelection) treeViewer.getSelection());
		if (!selection.isEmpty()) {
			Object o = selection.getFirstElement();
			if (o instanceof ProductLine) {
				mgr.add(actionAdd);
				mgr.add(createCustomLine);
				mgr.add(viewChilrenAction);
				mgr.add(actionRemove);
			} else if (o instanceof Module) {
				mgr.add(whereUsedAction);
				mgr.add(whereUsedCodeAction);
				mgr.add(actionRemove);
			} else if (o instanceof VariabilitySetTreeContainer
					|| o instanceof ElementSetTreeContainer) {
				mgr.add(actionAdd);
			} else if (o instanceof Variability || o instanceof Element
					|| o instanceof Module) {
				mgr.add(whereUsedAction);
				if (o instanceof Variability) {
					mgr.add(whereUsedCodeAction);
				}
				mgr.add(actionRemove);
			}
		}
	}
}
