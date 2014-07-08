package productline.plugin.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import productline.plugin.internal.Utils;
import productline.plugin.internal.WhereUsedCode;
import productline.plugin.internal.WhereUsedCodeResolver;
import productline.plugin.view.WhereUsedCodeView;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.Variability;

public class WhereUsedInCodeAction extends Action {

	private static Logger LOG = LoggerFactory.getLogger(WhereUsedInCodeAction.class);
	
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
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void runWithEvent(Event event) {
		final Object selection = ((IStructuredSelection) treeViewer
				.getSelection()).getFirstElement();

		try {
			final Set<WhereUsedCode> result = new HashSet<>();

			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.showView("productline.plugin.viewWhereCodeUsed");

			final IViewPart p = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findView("productline.plugin.viewWhereCodeUsed");

			Job op = new Job("Searching") {

				@Override
				public IStatus run(IProgressMonitor monitor) {
					monitor.setTaskName("Finding where used in source code...");

					if (selection instanceof Variability) {
						Variability v = (Variability) selection;
						try {
							WhereUsedCodeResolver resolver = new WhereUsedCodeResolver(
									workspace,
									getPackageFragments(
											WhereUsedInCodeAction.this.javaProject,
											v.getModule().getPackages()),
									v.getName(), eclipseProject);
							try {
								result.addAll(resolver.getVariablesOccurences());
								System.out.println("done");
							} catch (FileNotFoundException e) {
								LOG.error(e.getMessage());
							} catch (IOException e) {
								LOG.error(e.getMessage());
							}
						} catch (JavaModelException e) {
							LOG.error(e.getMessage());
						}
					} else if (selection instanceof Module) {
						Module v = (Module) selection;
						try {
							WhereUsedCodeResolver resolver = new WhereUsedCodeResolver(
									workspace,
									getPackageFragments(
											WhereUsedInCodeAction.this.javaProject,
											null), v.getName(), eclipseProject);
							try {
								result.addAll(resolver.getModulesOccurences());
								System.out.println("done");
							} catch (FileNotFoundException e) {
								LOG.error(e.getMessage());
							} catch (IOException e) {
								LOG.error(e.getMessage());
							}
						} catch (JavaModelException e) {
							LOG.error(e.getMessage());
						}
					}

					if (p instanceof WhereUsedCodeView) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								((WhereUsedCodeView) p).refresh(result);
							}
						});
					}
					return Status.OK_STATUS;
				}
			};

			op.setUser(true);
			op.schedule();
		} catch (PartInitException e1) {
			LOG.error(e1.getMessage());
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
		} else {
			return Utils.getPackageInJavaProject(javaProject);
		}

		return result;
	}
}
