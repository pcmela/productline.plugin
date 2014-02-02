package productline.plugin.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import productline.plugin.ui.wizard.CreateProductLineWizard;

public class CreateConfigurationCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    Object firstElement = selection.getFirstElement();
	    
	    if(firstElement instanceof IJavaProject){
	    	IJavaProject project = (IJavaProject)firstElement;
	    	System.out.println(project.getPath());
	    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    	//IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	    	System.out.println(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
	    	
	    	CreateProductLineWizard customWizard = new CreateProductLineWizard();
	    	customWizard.setProject(project.getProject());
	    	customWizard.setWorkspaceLocation(ResourcesPlugin.getWorkspace().getRoot().getLocation());
	    	
	    	WizardDialog wizardDialog = new WizardDialog(new Shell(),
					customWizard);
	    	
			if (wizardDialog.open() == Window.OK) {
				System.out.println("Ok pressed");
			} else {
				System.out.println("Cancel pressed");
			}
	    }
		
		

		return null;
	}

}
