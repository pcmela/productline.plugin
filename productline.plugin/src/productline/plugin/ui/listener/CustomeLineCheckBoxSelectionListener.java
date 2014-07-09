package productline.plugin.ui.listener;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import diploma.productline.entity.Module;

public class CustomeLineCheckBoxSelectionListener implements Listener {

	private CheckboxTreeViewer checkboxTreeViewer;
	
	public CustomeLineCheckBoxSelectionListener(
			CheckboxTreeViewer checkboxTreeViewer) {
		super();
		this.checkboxTreeViewer = checkboxTreeViewer;
	}



	@Override
	public void handleEvent(Event event) {
		if (event.detail == SWT.CHECK) {
			if ((event.item.getData() instanceof Module)
					&& !(((Module) event.item.getData())).isVariable()) {
				event.detail = SWT.NONE;
				event.type = SWT.None;
				event.doit = false;
				try {
					checkboxTreeViewer.getTree().setRedraw(false);
					TreeItem item = (TreeItem) event.item;
					item.setChecked(!item.getChecked());
				} finally {
					checkboxTreeViewer.getTree().setRedraw(true);
				}
			} else {
				ITreeContentProvider tcp = (ITreeContentProvider) checkboxTreeViewer
						.getContentProvider();
				Object child = event.item.getData();
				Object parent = tcp.getParent(child);
				if (parent != null) {
					checkboxTreeViewer.setChecked(parent, true);
					parent = tcp.getParent(parent);
					if (parent != null) {
						checkboxTreeViewer.setChecked(parent, true);
					}
				}
			}
		}

	}
}
