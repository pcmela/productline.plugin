package productline.plugin.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;

public class WhereUsedCodeResolver {

	private final String START_VARIABILITY = "(\\s*+)(\\*)(\\s*+)@variability\\s";
	private final String START_MODULE = "(\\s*+)(\\*)(\\s*+)@module\\s";

	private Set<IPackageFragment> packages;
	private String workspace;
	private String nameOfSearchItem;
	private IProject project;

	public WhereUsedCodeResolver(String workspace,
			Set<IPackageFragment> packages, String nameOfSearchItem,
			IProject project) {
		this.workspace = workspace;
		this.packages = packages;
		this.nameOfSearchItem = nameOfSearchItem;
		this.project = project;
	}

	/**
	 * Return all occurrences of selected variability in source code
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Set<WhereUsedCode> getVariablesOccurences()
			throws FileNotFoundException, IOException {
		Set<WhereUsedCode> result = new HashSet<>();
		if (packages != null) {
			for (IPackageFragment pkg : packages) {
				File f = new File(workspace + pkg.getPath().toOSString());
				result.addAll(readFileAndGetVariabilityOccurences(f,
						this.nameOfSearchItem));
			}
		}
		return result;
	}

	/**
	 * Return all occurrences of selected module in source code 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Set<WhereUsedCode> getModulesOccurences()
			throws FileNotFoundException, IOException {
		Set<WhereUsedCode> result = new HashSet<>();
		if (packages != null) {
			//browse all java packages in project
			for (IPackageFragment pkg : packages) {
				File f = new File(workspace + pkg.getPath().toOSString());
				result.addAll(readFileAndGetModuleOccurences(f,
						this.nameOfSearchItem));
			}
		}
		return result;
	}

	private Set<WhereUsedCode> readFileAndGetVariabilityOccurences(File file,
			String nameOfSearchItem) throws FileNotFoundException, IOException {
		Set<WhereUsedCode> result = new HashSet<>();
		String search = START_VARIABILITY + nameOfSearchItem;

		// if the file is not the folder with classes return empty result
		if (!file.isDirectory()) {
			return result;
		}
		
		// browse the package folder and search occurrences in all java classes
		for (File f : file.listFiles()) {
			if (!f.isFile()) {
				continue;
			}
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {

				String lineText;
				int lineCount = 0;
				while ((lineText = br.readLine()) != null) {
					lineCount++;
					if (lineText.trim().matches(search)) {
						result.add(new WhereUsedCode(f.getAbsolutePath()
								.replace(this.workspace, ""), lineCount,
								project));
					}
				}
			}

		}

		return result;
	}

	private Set<WhereUsedCode> readFileAndGetModuleOccurences(File file,
			String nameOfSearchItem) throws FileNotFoundException, IOException {
		Set<WhereUsedCode> result = new HashSet<>();
		String search = START_MODULE + nameOfSearchItem;

		if (!file.isDirectory()) {
			return result;
		}
		
		//search module occurrences in all java classes
		for (File f : file.listFiles()) {
			if (f.isFile()) {
				continue;
			}
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {

				String lineText;
				int lineCount = 0;
				while ((lineText = br.readLine()) != null) {
					lineCount++;
					if (lineText.trim().matches(search)) {
						result.add(new WhereUsedCode(f.getAbsolutePath()
								.replace(this.workspace, ""), lineCount,
								project));
					}
				}
			}

		}

		return result;
	}
}
