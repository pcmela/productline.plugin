package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import diploma.productline.dao.WhereUsedRecord;

public class WhereUsedLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		WhereUsedRecord rec = (WhereUsedRecord) element;
		return rec.getName() + " - " + rec.getProductLine() + " (" + rec.getType() + ")";
	}
}
