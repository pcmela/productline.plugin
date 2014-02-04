package productline.plugin.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

import productline.plugin.internal.ConfigurationKeys;
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
		page1 = new CreateWizardOverview("Create database");
		page1.setProjectLoacation(project.getLocation());
		addPage(page1);
		page2 = new CreateWizardImportPage("Import");
		addPage(page2);
	};

	@Override
	public boolean performFinish() {

		try {

			String p = workspaceLocation.toString();
			IFile file = project.getFile("/"
					+ ConfigurationKeys.NAME_OF_CONFIG_FILE);
			file.create(new ByteArrayInputStream(createConfigurationContent()
					.getBytes()), IResource.NONE, null);

			Properties properties = new Properties();
			properties.load(file.getContents());
			ProductLineDAO plDao = new ProductLineDAO(properties);
			try (Connection con = DaoUtil.connect(properties)) {
				ProductLineDAO.createDatabaseStructure(properties,
						loadDdlScript(), con);

				if (page2.getbImportData().getSelection()) {
					if (page2.getbImportFromYAML().getSelection()) {
						File f = new File(page2.gettFilePath().getText());
						if (f.exists()) {
							String pathToYaml = page2.gettFilePath().getText();
							ProductLine plInsert = YamlExtractor
									.extract(pathToYaml);
							plDao.createAll(plInsert, con);
						} else {
							MessageDialog.openError(new Shell(),
									"ProductLine cannot be save", "File "
											+ page2.gettFilePath().getText()
											+ " does not exists.");
						}
					}
				}
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
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

	private String createConfigurationContent() {
		StringBuilder result = new StringBuilder();
		result.append(ConfigurationKeys.CONNECTION_URL_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append("jdbc:h2:" + project.getLocation().toString()
						+ "/database")
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.USERNAME_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page1.gettNewDbUserName().getText())
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PASSWORD_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page1.gettNewDbPassword().getText())
				.append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PRODUCTLINE_ID_KEY)
				.append(ConfigurationKeys.EQUAL)
				.append(page1.gettProductLineName().getText())
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
		/*
		 * else if(element instanceof IPackageFragment){ project =
		 * ((IPackageFragment
		 * )selection.getFirstElement()).getJavaProject().getProject(); }else
		 * if(element instanceof IPackageFragmentRoot){ project =
		 * ((IPackageFragmentRoot
		 * )selection.getFirstElement()).getJavaProject().getProject(); }else
		 * if(element instanceof ICompilationUnit){ project =
		 * ((ICompilationUnit)element).getJavaProject().getProject(); }
		 */

	}

}
