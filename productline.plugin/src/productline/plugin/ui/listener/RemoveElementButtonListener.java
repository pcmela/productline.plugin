package productline.plugin.ui.listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import productline.plugin.internal.DefaultMessageDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ResourceDao;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Resource;

public class RemoveElementButtonListener implements Listener {

	private BaseProductLineEntity currentSelectedObject;
	private Properties properties;
	private ListViewer listViewerPackage;
	
	public RemoveElementButtonListener(
			BaseProductLineEntity currentSelectedObject, Properties properties,
			ListViewer listViewerPackage) {
		super();
		this.currentSelectedObject = currentSelectedObject;
		this.properties = properties;
		this.listViewerPackage = listViewerPackage;
	}



	@Override
	public void handleEvent(Event event) {
		Iterator it = ((StructuredSelection) listViewerPackage
				.getSelection()).iterator();
		boolean ok = MessageDialog
				.openConfirm(new Shell(), "Remove package",
						"Are you sure that you want to remove selected packages?");
		if (ok) {
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof Resource) {
					Resource r = (Resource) o;
					ResourceDao rDao = new ResourceDao();
					try (Connection con = DaoUtil.connect(properties)) {
						rDao.delete(r.getId(), con);
						Set<Resource> resources = rDao
								.getResourceWhithChildsByElement(
										(Element) currentSelectedObject,
										con);
						((Element) currentSelectedObject)
								.setResources(resources);
						listViewerPackage.setInput(resources);
					} catch (ClassNotFoundException e) {
						DefaultMessageDialog
								.driversNotFoundDialog("H2");
						e.printStackTrace();
					} catch (SQLException e) {
						DefaultMessageDialog.sqlExceptionDialog(e
								.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
