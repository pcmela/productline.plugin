package productline.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewChildAction extends Action {
	
	private static Logger LOG = LoggerFactory.getLogger(ViewChildAction.class);
	
	@Override
	public void runWithEvent(Event event) {
		try {
			//show new view with the children of product line
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.showView("productline.plugin.browseView");
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
		}
	}
}