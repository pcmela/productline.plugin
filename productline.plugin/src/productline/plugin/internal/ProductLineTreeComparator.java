package productline.plugin.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import diploma.productline.entity.BaseProductLineEntity;

public class ProductLineTreeComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		if(e1 instanceof BaseProductLineEntity && e2 instanceof BaseProductLineEntity){
			return ((BaseProductLineEntity)e1).getName().compareTo(((BaseProductLineEntity)e2).getName());
		}
		
		return 0;
	}
	
}
