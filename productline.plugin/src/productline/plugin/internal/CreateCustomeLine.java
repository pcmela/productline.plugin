package productline.plugin.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;

public class CreateCustomeLine {

	private ProductLine productLine;
	private IProject project;
	private String destinationPath;
	private IJavaProject javaProject;
	private Set<IPackageFragment> packageElements;
	private HashMap<String, String> mapOfDirectories;

	public CreateCustomeLine(ProductLine productLine, IProject project,
			String destinationPath) {
		this.productLine = productLine;
		this.project = project;
		this.destinationPath = destinationPath;
		this.javaProject = JavaCore.create(project);
		mapOfDirectories = new HashMap<>();
	}

	public boolean create() throws IOException, JavaModelException {
		this.packageElements = getPackageInJavaProject();
		createDestinationFolders();
		copyDefaultProjectFiles();
		createPackageStructure(project.getLocation().toOSString()
				.replace(javaProject.getPath().toOSString(), ""));
		copyFiles();
		return true;
	}

	private void copyDefaultProjectFiles() throws IOException {
		File classPathFile = new File(project.getLocation().toString()
				+ "/.classpath");
		if (classPathFile.exists() && classPathFile.isFile()) {
			System.out.println(destinationPath);
			System.out.println(classPathFile.getAbsolutePath());
			System.out.println(project.getLocation().toOSString());
			Files.copy(
					Paths.get(project.getLocation().toString() + "/.classpath"),
					Paths.get(destinationPath + "/.classpath"),
					StandardCopyOption.REPLACE_EXISTING);
		}

	}

	private void createDestinationFolders() {
		File destionation = new File(destinationPath);

		if (!destionation.exists()) {
			if (destionation.isDirectory()) {
				destionation.mkdirs();
			} else {
				File parent = destionation.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
			}
		}
	}

	private void createPackageStructure(String workspacePath)
			throws JavaModelException {
		for (Module m : productLine.getModules()) {
			for (PackageModule pkgModule : m.getPackages()) {
				for (IPackageFragment pkg : packageElements) {
					if (pkg.getElementName().equals(pkgModule.getName())) {
						System.out.println(pkg.getPath().toString());
						String destinationDirectory = destinationPath
								+ pkg.getPath()
										.toOSString()
										.replace(
												javaProject.getPath()
														.toOSString(), "");
						mapOfDirectories.put(workspacePath
								+ pkg.getPath().toOSString(),
								destinationDirectory);
						File f = new File(destinationDirectory);
						if (!f.exists()) {
							f.mkdirs();
						}
					}
				}
			}
		}
	}

	private Set<IPackageFragment> getPackageInJavaProject()
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

	private void copyFiles() throws IOException {
		Iterator<Entry<String, String>> it = mapOfDirectories.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();
			File source = new File(pairs.getKey());
			for (File f : source.listFiles()) {
				if (f.isFile()) {
					System.out.println(f.getPath().toString());
					System.out.println(f.getAbsolutePath().replace(
							pairs.getKey(), pairs.getValue()));
					Files.copy(
							Paths.get(f.getAbsolutePath()),
							Paths.get(f.getAbsolutePath().replace(
									pairs.getKey(), pairs.getValue())),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			// it.next();
		}
	}
}
