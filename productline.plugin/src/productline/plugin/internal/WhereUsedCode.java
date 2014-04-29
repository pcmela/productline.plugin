package productline.plugin.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class WhereUsedCode {

	private String relativePath;
	private int line;
	private IProject project;
	private String workspace;

	public WhereUsedCode() {
	}

	public WhereUsedCode(String relativePath, int line, IProject project) {
		super();
		this.relativePath = relativePath.substring(project.getName().length()+1);
		this.line = line;
		this.project = project;
		String w = project.getLocation().toOSString();
		workspace = w.substring(0,w.length()-(project.getName().length()+1));		
	}

	public IFile getFile() {
		return project.getFile(relativePath);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

}
