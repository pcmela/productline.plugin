package productline.plugin.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import productline.plugin.internal.Utils;
import productline.plugin.internal.WhereUsedCode;
import productline.plugin.internal.WhereUsedCodeResolver;
import productline.plugin.view.WhereUsedCodeView;
import productline.plugin.view.WhereUsedView;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.Variability;

public class WhereUsedInCodeAction extends Action {

	private TreeViewer treeViewer;
	private String workspace;
	private IProject eclipseProject;
	private IJavaProject javaProject;

	public WhereUsedInCodeAction(TreeViewer treeViewer, String workspace,
			IProject eclipseProject) {
		this.treeViewer = treeViewer;
		this.workspace = workspace;
		this.eclipseProject = eclipseProject;
		try {
			if (eclipseProject.hasNature(JavaCore.NATURE_ID)) {
				javaProject = JavaCore.create(eclipseProject);
			} else {
				javaProject = null;
			}
		} catch (CoreException e) {
			javaProject = null;
			e.printStackTrace();
		}
	}

	@Override
	public void runWithEvent(Event event) {
		Object selection = ((IStructuredSelection) treeViewer.getSelection())
				.getFirstElement();

		try {
			final Set<WhereUsedCode> result = new HashSet<>();
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.showView("productline.plugin.viewWhereCodeUsed");

			final IViewPart p = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView("productline.plugin.viewWhereCodeUsed");
			

			if (selection instanceof Variability) {
				Variability v = (Variability) selection;
				try {
					WhereUsedCodeResolver resolver = new WhereUsedCodeResolver(
							workspace, getPackageFragments(this.javaProject, v
									.getModule().getPackages()), v.getName(), eclipseProject);
					try {
						result.addAll(resolver
								.getVariablesOccurences());
						System.out.println("done");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (p instanceof WhereUsedCodeView) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						((WhereUsedCodeView) p)
								.refresh(result);
					}
				});
			}
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private Set<IPackageFragment> getPackageFragments(IJavaProject project,
			Set<PackageModule> modulePackages) throws JavaModelException {
		Set<IPackageFragment> result = new HashSet<>();
		Set<IPackageFragment> packages = Utils
				.getPackageInJavaProject(javaProject);

		if (modulePackages != null) {
			for (PackageModule p : modulePackages) {
				for (IPackageFragment pkg : packages) {
					if (pkg.getElementName().equals(p.getName())) {
						result.add(pkg);
						break;
					}
				}
			}
		}

		return result;
	}
}
