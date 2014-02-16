package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.entity.ProductLine;

public class ProductLineListLabelProvider extends LabelProvider {

	public String getText(Object element) {
		ProductLine p = (ProductLine)element;
		if(p.getParent() == null){
			return p.getName() + " [parent]";
		}else{
			return p.getName() + " [child, parent:" + p.getParent().getName() +"]";
		}
	}
}
