package productline.plugin.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import productline.plugin.internal.Utils;
import productline.plugin.internal.WhereUsedCode;
import productline.plugin.internal.WhereUsedCodeResolver;
import productline.plugin.view.WhereUsedCodeView;
import productline.plugin.view.WhereUsedView;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
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
					//monitor.beginTask("task", 100);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

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
								((WhereUsedCodeView) p).refresh(result);
							}
						});
					}
					return Status.OK_STATUS;
				}
			};

			/*IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			Shell shell = win != null ? win.getShell() : null;
			new ProgressMonitorDialog(shell).run(true, true, op);*/
			op.setUser(true);
			op.schedule();
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} /*catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
