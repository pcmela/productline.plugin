package productline.plugin.ui;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.entity.BaseProductLineEntity;

public class ProductLineTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof BaseProductLineEntity){
			return ((BaseProductLineEntity) element).getName();
		}
		
		return "Error - invalid object type in LabelProvider";
	}
}
