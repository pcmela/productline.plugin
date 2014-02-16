package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.entity.ProductLine;

public class ProductLineListLabelProvider extends LabelProvider {

	public String getText(Object element) {
		return ((ProductLine) element).getName();
	}
}
