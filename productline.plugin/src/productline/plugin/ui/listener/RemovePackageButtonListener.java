package productline.plugin.ui.listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diploma.productline.DaoUtil;
import diploma.productline.dao.PackageDAO;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;

public class RemovePackageButtonListener implements Listener {

	private static Logger LOG = LoggerFactory
			.getLogger(RemovePackageButtonListener.class);

	private Module module;
	private TreeViewer treeViewer;
	private Properties properties;
	private ListViewer listViewerPackage;

	public RemovePackageButtonListener(Module module, TreeViewer treeViewer,
			Properties properties, ListViewer listViewerPackage) {
		this.module=module;
		this.treeViewer=treeViewer;
		this.properties=properties;
		this.listViewerPackage=listViewerPackage;
	}

	@Override
	public void handleEvent(Event event) {
		Iterator<PackageModule> it = ((IStructuredSelection) listViewerPackage
				.getSelection()).iterator();
		boolean ok = MessageDialog.openConfirm(new Shell(), "Remove package",
				"Are you sure that you want to remove package?");
		if (ok) {
			try (Connection con = DaoUtil.connect(properties)) {
				PackageDAO pDao = new PackageDAO();
				while (it.hasNext()) {
					PackageModule pkg = it.next();
					pDao.delete(pkg.getId(), con);
				}
				Set<PackageModule> packages = pDao
						.getPackagesWhithChildsByModule(module, con);
				listViewerPackage.setInput(packages);
				((Module) ((IStructuredSelection) treeViewer.getSelection())
						.getFirstElement()).setPackages(packages);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage());
			} catch (SQLException e) {
				LOG.error(e.getMessage());
			}
		}
	}

}
