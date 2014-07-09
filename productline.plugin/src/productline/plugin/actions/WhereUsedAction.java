package productline.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import productline.plugin.view.WhereUsedView;

public class WhereUsedAction extends Action {
	
	private static Logger LOG = LoggerFactory.getLogger(WhereUsedAction.class);
	
	private TreeViewer treeViewer;
	
	public WhereUsedAction(TreeViewer treeViewer){
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void runWithEvent(Event event) {
		try {
			
			//open view viewWhereUsed
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.showView("productline.plugin.viewWhereUsed");
			
			//get instance of view viewWhereUsed
			final IViewPart p = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView("productline.plugin.viewWhereUsed");
			if (p instanceof WhereUsedView) {
				
				//refresh view with new data from actual selected item
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						((WhereUsedView) p)
								.refresh(((IStructuredSelection) treeViewer
										.getSelection()).getFirstElement());
					}
				});
			}
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
		}
	}
}
