package productline.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import productline.plugin.view.WhereUsedView;

public class WhereUsedAction extends Action {
	
	private TreeViewer treeViewer;
	
	public WhereUsedAction(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void runWithEvent(Event event) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.showView("productline.plugin.viewWhereUsed");
			final IViewPart p = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView("productline.plugin.viewWhereUsed");
			if (p instanceof WhereUsedView) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						((WhereUsedView) p)
								.refresh(((IStructuredSelection) treeViewer
										.getSelection()).getFirstElement());
					}
				});
			}
			System.out.println(p);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
