package productline.plugin.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class Utils {

	public static Set<IPackageFragment> getPackageInJavaProject(IJavaProject javaProject)
			throws JavaModelException {
		Set<IPackageFragment> elements = new HashSet<>();

		final IPackageFragmentRoot[] packageFragmentRootArray = javaProject
				.getAllPackageFragmentRoots();

		for (final IPackageFragmentRoot packageFragmentRoot : packageFragmentRootArray) {
			if (!packageFragmentRoot.isArchive()) {
				for (final IJavaElement pkg : packageFragmentRoot.getChildren()) {
					if (pkg != null && !pkg.getElementName().equals("")
							&& pkg instanceof IPackageFragment) {
						if (!(pkg instanceof IFolder)) {
							elements.add((IPackageFragment) pkg);
						}
					}
				}
			}
		}
		return elements;
	}
}
