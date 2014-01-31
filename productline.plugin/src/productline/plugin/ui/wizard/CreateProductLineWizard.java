package productline.plugin.ui.wizard;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.hibernate.Session;

import diploma.productline.HibernateUtil;
import diploma.productline.entity.ProductLine;
import productline.plugin.internal.ConfigurationKeys;
import productline.plugin.internal.DatabaseUtil;

public class CreateProductLineWizard extends Wizard implements IWorkbenchWizard {

	CreateProductLineWizardPage1 page1;
	CreateProductLineWizardPage2 page2;

	private IProject project;
	private IPath workspaceLocation;
	private final String FOLDER = "/src/";

	@Override
	public void addPages() {
		page1 = new CreateProductLineWizardPage1("Create database");
		page1.setProjectLoacation(project.getLocation());
		addPage(page1);
		page2 = new CreateProductLineWizardPage2("Import");
		addPage(page2);
	};

	@Override
	public boolean performFinish() {

		try {

			String p = workspaceLocation.toString();
			IFile file = project.getFile("/" + ConfigurationKeys.NAME_OF_CONFIG_FILE);
			file.create(new ByteArrayInputStream(createConfigurationContent().getBytes()), IResource.NONE, null);

			Properties properties = new Properties();
			properties.load(file.getContents());
			Properties hibernateProp = DatabaseUtil.getHibernateProperties(properties);
			HibernateUtil.initSessionFactory(hibernateProp);
			
			ProductLine productLine = new ProductLine();
			productLine.setName(page1.gettProductLineName().getText());
			productLine.setDescription(page1.gettProductLineDescription().getText());
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			session.save(productLine);
			session.getTransaction().commit();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private String createConfigurationContent() {
		StringBuilder result = new StringBuilder();
		result.append(ConfigurationKeys.CONNECTION_URL_KEY).append(ConfigurationKeys.EQUAL)
					.append("jdbc:h2:"+project.getLocation().toString()+"/database").append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.USERNAME_KEY).append(ConfigurationKeys.EQUAL)
					.append(page1.gettNewDbUserName().getText()).append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PASSWORD_KEY).append(ConfigurationKeys.EQUAL)
					.append(page1.gettNewDbPassword().getText()).append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.PRODUCTLINE_ID_KEY).append(ConfigurationKeys.EQUAL)
					.append(page1.gettProductLineName().getText()).append(ConfigurationKeys.NEW_LINE)
				//sync properties
				.append(ConfigurationKeys.SYNC_URL_KEY).append(ConfigurationKeys.EQUAL)
					.append(page2.gettWebUrl().getText()).append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.SYNC_USERNAME_KEY).append(ConfigurationKeys.EQUAL)
					.append(page2.gettWebUserName().getText()).append(ConfigurationKeys.NEW_LINE)
				.append(ConfigurationKeys.SYNC_PASSWORD_KEY).append(ConfigurationKeys.EQUAL)
					.append(page2.gettWebPassword()).append(ConfigurationKeys.NEW_LINE);
		
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
		if(element instanceof IJavaElement){
			project = ((IJavaElement)element).getJavaProject().getProject();
		}else if(element instanceof IFile){
			project = ((IFile)element).getProject();
		}
		
		workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		/*else if(element instanceof IPackageFragment){
			project = ((IPackageFragment)selection.getFirstElement()).getJavaProject().getProject();
		}else if(element instanceof IPackageFragmentRoot){
			project = ((IPackageFragmentRoot)selection.getFirstElement()).getJavaProject().getProject();
		}else if(element instanceof ICompilationUnit){
			project = ((ICompilationUnit)element).getJavaProject().getProject();
		}*/
		
	}

}
