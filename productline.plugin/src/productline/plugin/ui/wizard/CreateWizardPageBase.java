package productline.plugin.ui.wizard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import productline.plugin.internal.ConfigurationKeys;
import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.ui.ProductLineIdDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.ProductLine;

public abstract class CreateWizardPageBase extends WizardPage {

	protected CreateWizardPageBase(String pageName) {
		super(pageName);
	}

	protected int openExistingProductLinesDialog(String username, String password, String connectionString, Text control){
		Properties properties = new Properties();
		properties.setProperty(ConfigurationKeys.CONNECTION_URL_KEY, connectionString);
		properties.setProperty(ConfigurationKeys.USERNAME_KEY, username);
		properties.setProperty(ConfigurationKeys.PASSWORD_KEY, password);
		
		ProductLineDAO pDao = new ProductLineDAO();
		
		try(Connection con = DaoUtil.connect(properties)){
			Set<ProductLine> productLines = pDao.getProductLine(con);
			ProductLineIdDialog dialog = new ProductLineIdDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), productLines, control);
			dialog.open();
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
		} catch (SQLException e) {
			DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

}
