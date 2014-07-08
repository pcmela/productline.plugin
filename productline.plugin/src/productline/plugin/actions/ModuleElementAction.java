package productline.plugin.actions;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import productline.plugin.editor.OverviewPage;
import productline.plugin.ui.AddEntityDialog;
import productline.plugin.ui.listener.ProductLineHiearchyMenuListener;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;

public class ModuleElementAction extends Action {

	private ProductLine productLine;
	private IProject project;
	private OverviewPage page;
	private TreeViewer treeViewer;
	private Properties properties;

	public ModuleElementAction(String text, ImageDescriptor image,
			ProductLine productLine, IProject project, OverviewPage page,
			TreeViewer treeViewer, Properties properties) {

		super(text, image);

		this.productLine = productLine;
		this.project = project;
		this.page = page;
		this.treeViewer = treeViewer;
		this.properties = properties;
	}

	@Override
	public void run() {
		AddEntityDialog dialog = new AddEntityDialog(new Shell(), productLine,
				Module.class, project, properties);
		dialog.create();
		if (dialog.open() == Window.OK) {
			System.out.println("Module Created");
			productLine = page.loadData(false);
			if (productLine != null) {
				treeViewer.setInput(new Object[] { productLine });
			} else {
				treeViewer.setInput(new Object[] {});
			}
			treeViewer.expandAll();
		}
	}
}
