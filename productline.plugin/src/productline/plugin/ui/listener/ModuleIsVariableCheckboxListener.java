package productline.plugin.ui.listener;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import productline.plugin.editor.OverviewPage;
import diploma.productline.entity.BaseProductLineEntity;

public class ModuleIsVariableCheckboxListener implements Listener {

	private OverviewPage page;
	private TreeViewer treeViewer;
	
	public ModuleIsVariableCheckboxListener(OverviewPage page, TreeViewer treeViewer){
		this.page=page;
		this.treeViewer=treeViewer;
	}
	
	@Override
	public void handleEvent(Event event) {
		BaseProductLineEntity o = (BaseProductLineEntity) ((IStructuredSelection) treeViewer
				.getSelection()).getFirstElement();

		if (!o.isDirty()) {
			o.setDirty(true);
		}
		if (!page.isDirty()) {
			page.setDirtyAndfirePropertyChange(true);
		}
		treeViewer.refresh();
		treeViewer.expandAll();
	}
}
