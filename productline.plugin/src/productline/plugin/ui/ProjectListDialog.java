package productline.plugin.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import productline.plugin.ui.wizard.CreateWizardOverview;

public class ProjectListDialog extends Dialog {

	
	public ProjectListDialog(Shell parentShell, Text textInput, CreateWizardOverview wizard) {
		super(parentShell);
		this.textInput = textInput;
		this.wizard = wizard;
	}

	private ListViewer listViewer;
	private IStructuredSelection listSelection;
	private Text textInput;
	private CreateWizardOverview wizard;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	/*public ProjectListDialog() {
	}*/

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout(SWT.HORIZONTAL));


		listViewer = new ListViewer(area, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listViewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if(inputElement instanceof IProject[]){
					return (IProject[])inputElement;
				}
				return new Object[]{};
			}
		});
		listViewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());

		listViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return ((IProject) element).getName();
			}
		});

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				listSelection = (IStructuredSelection) listViewer.getSelection();
			}
		});
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
				
			}
		});
		return null;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(250, 300);
	}

	@Override
	protected void okPressed() {
		
		if (listSelection != null) {
			if(listSelection.getFirstElement() instanceof IProject){
				IProject p = (IProject)listSelection.getFirstElement();
				
				IFile f = p.getFile("configuration.productline");
				if(f != null){
					MessageDialog.openError(new Shell(), "Product line", "In selected project product line configuration already exist.");
					return;
				}
				
				wizard.setProjectLoacation(p.getLocation());
				textInput.setText(p.getLocation().toString());
			}
		}

		super.okPressed();
	}

}
