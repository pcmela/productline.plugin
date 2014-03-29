package productline.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import diploma.productline.entity.BaseProductLineEntity;

public class RemoveAction extends Action {
	
	private TreeViewer treeViewer;
	
	public RemoveAction(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}
	
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
