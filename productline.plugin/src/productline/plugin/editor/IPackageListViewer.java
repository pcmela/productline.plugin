package productline.plugin.editor;

import java.util.Set;

import org.eclipse.jdt.core.IPackageFragment;

import diploma.productline.entity.PackageModule;

public interface IPackageListViewer {
	public void setPackageListInput(Set<PackageModule> elements);
}
