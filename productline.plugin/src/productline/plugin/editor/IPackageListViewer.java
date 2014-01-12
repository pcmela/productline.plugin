package productline.plugin.editor;

import java.util.Set;

import org.eclipse.jdt.core.IPackageFragment;

public interface IPackageListViewer {
	public void setPackageListInput(Set<IPackageFragment> elements);
}
