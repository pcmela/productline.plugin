package productline.plugin.actions;

import java.util.Properties;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

import productline.plugin.editor.OverviewPage;
import productline.plugin.ui.CreateNewCustomLineDialog;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class CreateCustomLineAction extends Action {
	
	private TreeViewer treeViewer;
	private Properties properties;
	private IProject project;
	
	public CreateCustomLineAction(TreeViewer treeViewer, Properties properties, IProject project){
		this.treeViewer = treeViewer;
		this.properties = properties;
		this.project = project;
	}

	@Override
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
		Object input = treeViewer.getInput();
		ProductLine productLine = null;
		if (input instanceof Object[]) {
			if (((Object[]) input)[0] instanceof ProductLine) {
				productLine = (ProductLine) ((Object[]) input)[0];

				for (Module m : productLine.getModules()) {
					System.out.println(m);
					for (Variability v : m.getVariabilities()) {
						System.out.println(v);
					}
					for (Element e : m.getElements()) {
						System.out.println(e);
					}
				}
			}
		} else {
			productLine = (ProductLine) input;
		}
		ProductLine newCustomeLine = SerializationUtils.clone(productLine);
		CreateNewCustomLineDialog dialog = new CreateNewCustomLineDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), newCustomeLine, "", project,
				properties);
		dialog.open();
	}

}
