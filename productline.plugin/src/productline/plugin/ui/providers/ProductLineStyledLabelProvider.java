package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyledText;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;

public class ProductLineStyledLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		StyledString text = new StyledString();

		Object element = cell.getElement();

		if (element instanceof BaseProductLineEntity) {
			if (element instanceof Module) {
				Module m = (Module) element;
				if (!m.isVariable()) {
					text.append(m.getName());
					text.append(" [mandatory]", StyledString.DECORATIONS_STYLER);
				}else{
					text.append(m.getName());
				}
			}else if (element instanceof Element) {
				Element e = (Element) element;
				text.append(e.getName())
						.append(" [", StyledString.DECORATIONS_STYLER)
						.append(e.getType().getName(),
								StyledString.DECORATIONS_STYLER)
						.append("]", StyledString.DECORATIONS_STYLER);
			}else{
				text.append(element.toString());
			}
		}else{
			text.append(element.toString());
		}

		// return "Error - invalid object type in LabelProvider";
		cell.setText(text.toString());
	    cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}

}
