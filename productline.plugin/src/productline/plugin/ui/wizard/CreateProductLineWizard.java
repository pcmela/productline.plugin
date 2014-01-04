package productline.plugin.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class CreateProductLineWizard extends Wizard implements IWorkbenchWizard {

	CreateProductLineWizardPage1 page1;
	
	@Override
	public void addPages() {
		page1 = new CreateProductLineWizardPage1("First Page");
		addPage(page1);
	};
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

}
