package productline.plugin.ui.providers;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PackageListFilter extends ViewerFilter {

	private String pattern = "";
	
	public void setPattern(String pattern){
		this.pattern = pattern.toLowerCase();
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		if(element instanceof IPackageFragment){
			IPackageFragment e = (IPackageFragment)element;
			
			if(e.getElementName().toLowerCase().contains(pattern)){
				return true;
			}else{
				return false;
			}
		}
		
		return true;
	}

}
