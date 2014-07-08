package productline.plugin.ui.listener;

import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import productline.plugin.editor.OverviewPage;
import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.ui.PackageListDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.PackageDAO;
import diploma.productline.entity.Module;

public class ModuleAddButtonListener implements Listener {

	private static Logger LOG = LoggerFactory.getLogger(ModuleAddButtonListener.class); 
	
	private Module module;
	private IProject project;
	private Properties properties;
	private OverviewPage page;

	public ModuleAddButtonListener(Module module, IProject project,
			Properties properties, OverviewPage page) {
		this.module = module;
		this.project = project;
		this.properties = properties;
		this.page = page;
	}

	@Override
	public void handleEvent(Event event) {
		PackageListDialog dialog = new PackageListDialog(new Shell(), project,
				page, module, properties);
		try {
			dialog.setStoredElements(PackageDAO.getStoredPackages(DaoUtil
					.connect(properties), module.getProductLine().getId()));
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			LOG.error(e.getMessage());
		} catch (SQLException e2) {
			DefaultMessageDialog.sqlExceptionDialog(e2.getMessage());
			LOG.error(e2.getMessage());
		}
		dialog.open();

	}
}
