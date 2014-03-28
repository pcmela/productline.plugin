package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;

public class ProductLineTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof BaseProductLineEntity) {
			if (element instanceof Module) {
				Module m = (Module) element;
				if (!m.isVariable()) {
					return m.getName() + " [mandatory]";
				}
			}

			if (element instanceof Element) {
				Element e = (Element) element;
				return new StringBuilder().append(e.getName()).append(" [")
						.append(e.getType().getName()).append("]").toString();
			}
			return ((BaseProductLineEntity) element).getName();
		}

		return "Error - invalid object type in LabelProvider";
	}
}
