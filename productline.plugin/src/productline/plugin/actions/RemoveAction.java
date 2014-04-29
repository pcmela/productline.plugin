package productline.plugin.actions;

import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import productline.plugin.editor.OverviewPage;
import productline.plugin.internal.DefaultMessageDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ElementDAO;
import diploma.productline.dao.ModuleDAO;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.dao.VariabilityDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class RemoveAction extends Action {
	
	private TreeViewer treeViewer;
	private Properties properties;
	private OverviewPage overviewPage;
	
	public RemoveAction(TreeViewer treeViewer, Properties properties, OverviewPage overviewPage){
		this.treeViewer = treeViewer;
		this.properties = properties;
		this.overviewPage = overviewPage;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
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
				if(entity instanceof ProductLine){
					ProductLineDAO pDao = new ProductLineDAO();
					ProductLine e = (ProductLine)entity;
					try {
						pDao.delete(e, DaoUtil.connect(properties));
					} catch (ClassNotFoundException e1) {
						DefaultMessageDialog.driversNotFoundDialog("H2");
						e1.printStackTrace();
					} catch (SQLException e1) {
						DefaultMessageDialog.sqlExceptionDialog(e1.getMessage());
						e1.printStackTrace();
					}
					overviewPage.refreshTree();
				}else if (entity instanceof Module){
					ModuleDAO mDao = new ModuleDAO();
					Module e = (Module)entity;
					try {
						mDao.delete(e, DaoUtil.connect(properties));
					} catch (ClassNotFoundException e1) {
						DefaultMessageDialog.driversNotFoundDialog("H2");
						e1.printStackTrace();
					} catch (SQLException e1) {
						DefaultMessageDialog.sqlExceptionDialog(e1.getMessage());
						e1.printStackTrace();
					}
					overviewPage.refreshTree();
				}else if(entity instanceof Variability){
					VariabilityDAO vDao = new VariabilityDAO();
					Variability e = (Variability)entity;
					try {
						vDao.delete(e.getId(), DaoUtil.connect(properties));
					} catch (ClassNotFoundException e1) {
						DefaultMessageDialog.driversNotFoundDialog("H2");
						e1.printStackTrace();
					} catch (SQLException e1) {
						DefaultMessageDialog.sqlExceptionDialog(e1.getMessage());
						e1.printStackTrace();
					}
					overviewPage.refreshTree();
				}else if(entity instanceof Element){
					ElementDAO eDao = new ElementDAO();
					Element e = (Element)entity;
					try {
						eDao.delete(e, DaoUtil.connect(properties));
					} catch (ClassNotFoundException e1) {
						DefaultMessageDialog.driversNotFoundDialog("H2");
						e1.printStackTrace();
					} catch (SQLException e1) {
						DefaultMessageDialog.sqlExceptionDialog(e1.getMessage());
						e1.printStackTrace();
					}
					overviewPage.refreshTree();
				}
			} else {
				
			}
		} else {

		}
	}
}
