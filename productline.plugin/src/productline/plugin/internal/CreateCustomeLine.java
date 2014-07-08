package productline.plugin.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.PackageModule;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Resource;
import diploma.productline.entity.Variability;

public class CreateCustomeLine {

	private static Logger LOG = LoggerFactory.getLogger(CreateCustomeLine.class);
	
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
		String workspace = project.getLocation().toOSString()
				.replace(javaProject.getPath().toOSString(), "");
		this.packageElements = Utils.getPackageInJavaProject(javaProject);
		createDestinationFolders();
		copyDefaultProjectFiles();
		createPackageStructure(workspace);
		copyElementResources(project.getLocation().toOSString());
		return true;
	}

	private void copyDefaultProjectFiles() throws IOException {
		File classPathFile = new File(project.getLocation().toString()
				+ "/.classpath");
		if (classPathFile.exists() && classPathFile.isFile()) {
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
					createPakcage(pkgModule, pkg, workspacePath, m);
				}
			}
		}
	}
	
	private void createPakcage(PackageModule pkgModule, IPackageFragment pkg, String workspacePath, Module m){
		if (pkg.getElementName().equals(pkgModule.getName())) {
			String destinationDirectory = destinationPath
					+ pkg.getPath()
							.toOSString()
							.substring(
									javaProject.getPath()
											.toOSString().length());
			for (File f : new File(workspacePath + pkg.getPath())
					.listFiles()) {
				if(!f.isFile()){
					continue;
				}
				File createFile = new File(destinationDirectory
						+ "\\" + f.getName());
				createFile.getParentFile().mkdirs();
				try {
					createFile.createNewFile();

					try (BufferedReader br = new BufferedReader(
							new FileReader(workspacePath
									+ pkg.getPath() + "\\"
									+ f.getName()));
							BufferedWriter bw = new BufferedWriter(
									new FileWriter(
											destinationDirectory
													+ "\\"
													+ f.getName()))) {
						ResolveArtifacts ra = new ResolveArtifacts(
								m.getName(),
								getNamesOfVariabilities(m));
						String line;
						while ((line = br.readLine()) != null) {
							line = ra.processLine(line);
							bw.write(line + "\n");
						}
					}
				} catch (FileNotFoundException e) {
					LOG.error(e.getMessage());
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
			}
			mapOfDirectories.put(workspacePath
					+ pkg.getPath().toOSString(),
					destinationDirectory);
		}
	}

	

	private Set<String> getNamesOfVariabilities(Module module) {
		Set<String> result = new HashSet<>();
		for (Variability v : module.getVariabilities()) {
			result.add(v.getName());
		}
		return result;
	}
	
	
	private void copyElementResources(String workspace) throws IOException{
		for(Module m : productLine.getModules()){
			for(Element e : m.getElements()){
				for(Resource r : e.getResources()){
					File f = new File(workspace + r.getRelativePath());
					File newFile = new File(destinationPath + r.getRelativePath());
					
					if(!newFile.exists()){
						File parent = newFile.getParentFile();
						if(!parent.exists()){
							parent.mkdirs();
						}
						
						if(!f.toString().toLowerCase().endsWith(".xml")){
							copyResourceFile(f, newFile);
						}else{
							copyResourceXmlFile(f, newFile, m);
						}
					}					
				}
			}
		}
	}
	
	private void copyResourceFile(File f, File newFile) throws IOException{			
			Files.copy(
					f.toPath(),
					newFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
	}
	
	private void copyResourceXmlFile(File f, File newFile, Module m) throws IOException{
		newFile.createNewFile();

		try (BufferedReader br = new BufferedReader(
				new FileReader(f));
				BufferedWriter bw = new BufferedWriter(
						new FileWriter(newFile.getAbsolutePath()))) {
					ResolveArtifactsXml ra = new ResolveArtifactsXml(
					m.getName(),
					getNamesOfVariabilities(m));
			String line;
			while ((line = br.readLine()) != null) {
				line = ra.processLine(line);
				if(line != null){
					bw.write(line + "\n");
				}
			}
		}
	}
}
