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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class CreateProductLineWizard extends Wizard {

	CreateProductLineWizardPage1 page1;
	CreateProductLineWizardPage2 page2;

	private IProject project;
	private IPath workspaceLocation;
	private final String FOLDER = "/src/";
	
	private final String CONNECTION_URL_KEY = "connection_url";
	private final String USERNAME_KEY = "username";
	private final String PASSWORD_KEY = "password";
	private final String NAME_OF_CONFIG_FILE = "configuration.productline";

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
		
		Properties properties = new Properties();
		try {
			String p = workspaceLocation.toString()
					+ project.toString() + "/" + NAME_OF_CONFIG_FILE;
			IFile file = project.getFile("/" + NAME_OF_CONFIG_FILE);
			file.create(new ByteArrayInputStream("ahoj".getBytes()), IResource.NONE, null);
			
			
			File configuration = new File(workspaceLocation.toString()
					+ project.toString() + "/" + NAME_OF_CONFIG_FILE);
			
			configuration.createNewFile();
			properties.setProperty(CONNECTION_URL_KEY, "jdbc:h2:~/test2");
			properties.setProperty(USERNAME_KEY, page1.gettNewDbUserName().getText());
			properties.setProperty(PASSWORD_KEY, page1.gettNewDbPassword().getText());

			properties.store(new FileOutputStream(configuration), null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
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

}
