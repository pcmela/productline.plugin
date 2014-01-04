package productline.plugin.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CreateProductLineWizardPage1 extends WizardPage {

	protected CreateProductLineWizardPage1(String pageName) {
		super(pageName);
		setTitle("First Pagte");
		setDescription("First Page");
	}

	@Override
	public void createControl(Composite parent) {
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(layout);
		
		Button button = new Button (parent, SWT.TOGGLE);
		button.setText ("Create new DB");
		
		Button button2 = new Button (parent, SWT.TOGGLE);
		button2.setText ("Use existing DB");

	}

}
