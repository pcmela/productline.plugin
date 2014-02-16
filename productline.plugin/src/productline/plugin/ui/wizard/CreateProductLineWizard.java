package productline.plugin.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import productline.plugin.internal.ConfigurationKeys;
import productline.plugin.internal.DefaultMessageDialog;
import diploma.productline.DaoUtil;
import diploma.productline.configuration.YamlExtractor;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.ProductLine;

public class CreateProductLineWizard extends Wizard implements IWorkbenchWizard {

	CreateWizardOverview page1;
	CreateWizardImportPage page2;

	private IProject project;
	private IPath workspaceLocation;
	private final String FOLDER = "/src/";

	@Override
	public void addPages() {
		if (checkIfConfigExist(project)) {
			MessageDialog
					.openError(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"Config already exist",
							"Configuration file for product line already exist in your project. If you want to create new file, delete the old first.");
			dispose();
			return;
		}

		page1 = new CreateWizardOverview("Create database");
		page1.setProjectLoacation(project.getLocation());
		addPage(page1);
		page2 = new CreateWizardImportPage("Import");
		addPage(page2);
	};

	@Override
	public boolean performFinish() {

		int existingId;
		String username;
		String password;
		String connectionString;
		String productLineName;
		String productLineDescription;
		IFile configurationFile;

		ProductLineDAO pDao = new ProductLineDAO();
		try {
			if (page1.getbCreateDb().getSelection()) {
				username = page1.gettNewDbUserName().getText();
				password = page1.gettNewDbPassword().getText();
				productLineName = page1.gettProductLineName().getText();
				productLineDescription = page1.gettProductLineDescription()
						.getText();
				connectionString = "jdbc:h2:"
						+ project.getLocation().toString() + "/database";

				try (Connection con = DaoUtil.connect(username, password,
						connectionString)) {
					ProductLineDAO
							.createDatabaseStructure(loadDdlScript(), con);
					if (page2.getbImportData().getSelection()) {
						// TODO import Data

						if (page2.getbImportFromYAML().getSelection()) {
							existingId = saveDataFromYaml(productLineName,
									page2.gettFilePath().getText(), con, pDao);
						} else {
							existingId = saveDataFromDB(pDao, username, password, connectionString);
						}
						configurationFile = createIFile(existingId, username,
								password, connectionString);
					} else {
						existingId = createNewProductLine(productLineName,
								productLineDescription, con, pDao);
						configurationFile = createIFile(existingId, username,
								password, connectionString);
					}

				}

			} else {
				username = page1.gettExistingDbUserName().getText();
				password = page1.gettExistingDbPassword().getText();
				connectionString = page1.gettExistingDbPath().getText();
				if (page1.getbUseExistingProductLine().getSelection()) {
					existingId = Integer.parseInt(page1.gettExistingId()
							.getText());
				}
				existingId = Integer.parseInt(page1.gettExistingId().getText());

				try (Connection con = DaoUtil.connect(username, password,
						connectionString)) {
					configurationFile = createIFile(existingId, username,
							password, connectionString);
				}
			}
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			DefaultMessageDialog.ioException(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			DefaultMessageDialog.ioException(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	private IFile createIFile(int productLineId, String username,
			String password, String connectionString) throws CoreException {
		IFile file = project.getFile("/"
				+ ConfigurationKeys.NAME_OF_CONFIG_FILE);
		String configContent = createConfigurationContent(
				String.valueOf(productLineId), username, password,
				connectionString);
		file.create(new ByteArrayInputStream(configContent.getBytes()),
				IResource.NONE, null);
		return file;
	}

	private int createNewProductLine(String productLineName,
			String productLineDescription, Connection con, ProductLineDAO pDao)
			throws ClassNotFoundException, SQLException {
		ProductLine p = new ProductLine();
		p.setName(productLineName);
		p.setDescription(productLineDescription);
		return pDao.save(p, con);
	}

	private int saveDataFromYaml(String productLineName, String path,
			Connection con, ProductLineDAO pDao) throws ClassNotFoundException,
			SQLException {
		File yamlFile = new File(path);
		if (yamlFile.exists()) {
			if (yamlFile.isFile()) {
				ProductLine p = YamlExtractor.extract(path);
				if (!resolveNameImportFromYaml(p, productLineName)) {
					String[] names = new String[] { p.getName(),
							page1.gettProductLineName().getText() };
					MessageDialog dialog = new MessageDialog(new Shell(),
							"Conflict in name", null,
							"Please choose which name you want to persist.",
							MessageDialog.INFORMATION, names, 0);
					int result = dialog.open();
					p.setName(names[result]);
				}
				if (p != null) {
					return pDao.createAll(p, con);
				} else {
					DefaultMessageDialog.yamlIsEmptyException(path);
				}
			} else {
				DefaultMessageDialog.yamlIsNotFileException(path);
			}
		} else {
			DefaultMessageDialog.fileNotFoundException(path);
		}

		return 0;
	}

	private int saveDataFromDB(ProductLineDAO pDao, String username, String password, String connectionString)
			throws ClassNotFoundException, SQLException {
		String sourceUsername = page2.gettWebUserName().getText();
		String sourcePassword = page2.gettWebPassword().getText();
		String sourceConnectionString = page2.gettWebUrl().getText();
		int existingId = Integer.parseInt(page2.gettExistingId().getText());

		ProductLine p;
		try (Connection con = DaoUtil.connect(sourceUsername, sourcePassword,
				sourceConnectionString)) {
			p = pDao.getProductLineWithChilds(existingId, con);
		}
		p.setParent(null);
		try(Connection con = DaoUtil.connect(username, password, connectionString)){
			return pDao.createAll(p, con);
		}
	}

	private boolean resolveNameImportFromYaml(ProductLine yamlResult,
			String name) {
		if (yamlResult.getName().equals(name)) {
			return true;
		}
		return false;
	}

	private File loadDdlScript() {
		URL url;
		try {
			url = new URL(
					"platform:/plugin/productline.plugin/resources/DDL.sql");
			return new File(FileLocator.resolve(url).toURI());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String createConfigurationContent(String productLineName,
			String username, String password, String connectionString) {
		StringBuilder result = new StringBuilder();
		result.append(ConfigurationKeys.CONNECTION_URL_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(connectionString)
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.USERNAME_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(username)
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PASSWORD_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(password)
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PRODUCTLINE_ID_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(productLineName)
				.append(ConfigurationKeys.NEW_LINE)
				// sync properties
				.append(ConfigurationKeys.SYNC_URL_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page2.gettWebUrl().getText())
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.SYNC_USERNAME_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page2.gettWebUserName().getText())
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.SYNC_PASSWORD_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page2.gettWebPassword())
				.append(ConfigurationKeys.NEW_LINE);

		return result.toString();
	}

	private String getPropertyAsString(Properties prop) throws IOException {
		StringWriter writer = new StringWriter();
		prop.store(writer, null);
		return writer.getBuffer().toString();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IPath getWorkspaceLocation() {
		return workspaceLocation;
	}

	public void setWorkspaceLocation(IPath workspaceLocation) {
		this.workspaceLocation = workspaceLocation;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IJavaElement) {
			project = ((IJavaElement) element).getJavaProject().getProject();
		} else if (element instanceof IFile) {
			project = ((IFile) element).getProject();
		}

		workspaceLocation = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation();
	}

	private boolean checkIfConfigExist(IProject project) {
		IFile file = project.getFile("/"
				+ ConfigurationKeys.NAME_OF_CONFIG_FILE);
		if (file.exists()) {
			return true;
		}
		return false;
	}
}
