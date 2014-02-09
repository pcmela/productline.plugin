package productline.plugin.ui;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Module;

public class ProductLineTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof BaseProductLineEntity){
			if(element instanceof Module){
				Module m = (Module) element;
				if(m.isVariable()){
					return m.getName() + " [mandatory]";
				}
			}
			return ((BaseProductLineEntity) element).getName();
		}
		
		return "Error - invalid object type in LabelProvider";
	}
}
