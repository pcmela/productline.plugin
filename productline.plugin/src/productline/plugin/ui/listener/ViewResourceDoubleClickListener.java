package productline.plugin.ui.listener;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diploma.productline.entity.Resource;

public class ViewResourceDoubleClickListener implements IDoubleClickListener {

	private static Logger LOG = LoggerFactory.getLogger(ViewResourceDoubleClickListener.class);
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		Object o = ((StructuredSelection) event.getSelection())
				.getFirstElement();
		if (o instanceof Resource) {
			Resource r = (Resource) o;

			File fileToOpen = new File(r.getFullPath());

			if (fileToOpen.exists() && fileToOpen.isFile()) {
				IFileStore fileStore = EFS.getLocalFileSystem()
						.getStore(fileToOpen.toURI());
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				try {
					IDE.openEditorOnFileStore(page, fileStore);
				} catch (PartInitException e) {
					LOG.error(e.getMessage());
				}
			}
		}
	}
	
}
