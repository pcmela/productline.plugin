package productline.plugin.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import productline.plugin.editor.IPackageListViewer;
import diploma.productline.entity.PackageModule;

public class PackageListDialog extends Dialog {

	private Set<IPackageFragment> packages;
	private IProject project;
	private ListViewer listViewer;
	private IPackageListViewer parentDialog;
	private IStructuredSelection listSelection;
	private Set<?> storedElements;
	
	public void setStoredElements(Set<?> elements){
		storedElements = elements;
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PackageListDialog(Shell parentShell, IProject project, IPackageListViewer parentDialog) {
		super(parentShell);
		this.project = project;
		packages = new HashSet<>();
		this.parentDialog = parentDialog;
	}

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

		listViewer.setContentProvider(new PackageListContentProvider());
		initData();
		listViewer.setInput(packages);
		listViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return ((IPackageFragment) element).getElementName();
			}
		});
		
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			  @Override
			  public void selectionChanged(SelectionChangedEvent event) {
			    listSelection = (IStructuredSelection) event
			      .getSelection();
			  }
			}); 

		return null;
	}

	private void initData() {
		IJavaProject javaProject;
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				javaProject = (IJavaProject) JavaCore.create(project);
				final IPackageFragmentRoot[] packageFragmentRootArray = javaProject
						.getAllPackageFragmentRoots();

				for (final IPackageFragmentRoot packageFragmentRoot : packageFragmentRootArray) {
					if (!packageFragmentRoot.isArchive()) {
						for (final IJavaElement pkg : packageFragmentRoot
								.getChildren()) {
							if (pkg != null && !pkg.getElementName().equals("")
									&& pkg instanceof IPackageFragment) {
								if (!(pkg instanceof IFolder)) {
									if(storedElements == null || storedElements.size() == 0){
										packages.add((IPackageFragment) pkg);
									}else{
										boolean exist = false;
										for(Object p : storedElements){
											if(p instanceof String){
												if(pkg.getElementName().equals((String)p)){
													exist = true;
													break;
												}
											}else if(p instanceof IPackageFragment){
												if(pkg.getElementName().equals(((IPackageFragment)p).getElementName())){
													exist = true;
													break;
												}
											}else if(p instanceof PackageModule){
												if(pkg.getElementName().equals(((PackageModule)p).getName())){
													exist = true;
													break;
												}
											}
										}
										if(!exist){
											packages.add((IPackageFragment) pkg);
										}
									}
								}
							}
						}
					}
				}

			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		super.okPressed();

		Set<IPackageFragment> selectedObjects = new HashSet<>();
		
		if(listSelection != null){
			for (Iterator iterator = listSelection.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if (obj instanceof IPackageFragment) {
					selectedObjects.add((IPackageFragment) obj);
				}
			}
		}
		if(selectedObjects.size() > 0){
			parentDialog.setPackageListInput(selectedObjects);
		}
	}
	
	static Set<String> createSetOfPackageName(Set<?> set){
		Set<String> result = new HashSet<>();
		for(Object frg : set){
			if(frg instanceof IPackageFragment){
				result.add(((IPackageFragment)frg).getElementName());
			}else if(frg instanceof PackageModule){
				result.add(((PackageModule)frg).getName());
			}
		}
		
		return result;
	}

}
