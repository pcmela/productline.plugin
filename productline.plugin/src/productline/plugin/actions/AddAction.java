package productline.plugin.actions;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import productline.plugin.editor.OverviewPage;
import productline.plugin.internal.ElementSetTreeContainer;
import productline.plugin.internal.VariabilitySetTreeContainer;
import productline.plugin.ui.AddEntityDialog;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class AddAction extends Action {
	
	private TreeViewer treeViewer;
	private IProject project;
	private Properties properties;
	private OverviewPage editor;
	
	public AddAction(TreeViewer treeViewer, IProject project, Properties properties, OverviewPage editor){
		this.treeViewer = treeViewer;
		this.project = project;
		this.properties = properties;
		this.editor = editor;
	}
	
	@Override
	public void runWithEvent(Event event) {
		if (((TreeSelection) treeViewer.getSelection()).getFirstElement() instanceof BaseProductLineEntity) {
			BaseProductLineEntity entity = (BaseProductLineEntity) ((TreeSelection) treeViewer
					.getSelection()).getFirstElement();

			if (entity instanceof ProductLine) {
				AddEntityDialog dialog = new AddEntityDialog(new Shell(),
						entity, Module.class, project, properties);
				dialog.create();
				if (dialog.open() == Window.OK) {
					editor.refreshTree();
				}
			} else if (entity instanceof VariabilitySetTreeContainer) {
				AddEntityDialog dialog = new AddEntityDialog(new Shell(),
						entity, Variability.class, project, properties);
				dialog.create();
				if (dialog.open() == Window.OK) {
					editor.refreshTree();
				}
			} else if (entity instanceof ElementSetTreeContainer) {
				AddEntityDialog dialog = new AddEntityDialog(new Shell(),
						entity, Element.class, project, properties);
				dialog.create();
				if (dialog.open() == Window.OK) {
					editor.refreshTree();
				}
			}
			treeViewer.expandAll();
		} else {
			MessageDialog.openError(new Shell(), "Error",
					"Object doen't type of BaseProductLineEntity");
		}
	}
}