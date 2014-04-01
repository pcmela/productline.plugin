package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import productline.plugin.internal.WhereUsedCode;
import diploma.productline.dao.WhereUsedRecord;

public class WhereUsedLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof WhereUsedRecord) {
			WhereUsedRecord rec = (WhereUsedRecord) element;
			return rec.getName() + " - " + rec.getProductLine() + " ("
					+ rec.getType() + ")";
		}else if(element instanceof WhereUsedCode){
			WhereUsedCode e = (WhereUsedCode)element;
			return e.getRelativePath() + " - line: " + e.getLine();
		}else{
			return element.toString();
		}
	}
	
	/*@Override
	public void update(ViewerCell cell) {
		StyledString text = new StyledString();

		Object element = cell.getElement();

		if (element instanceof WhereUsedRecord) {
			WhereUsedRecord rec = (WhereUsedRecord) element;
			text.append(rec.getName()).append(" - ")
					.append(rec.getProductLine()).append(" (")
					.append(rec.getType()).append(")");
		} else if (element instanceof WhereUsedCode) {
			text.append(((WhereUsedCode) element).getRelativePath()).append(
					String.valueOf(((WhereUsedCode) element).getLine()),
					StyledString.DECORATIONS_STYLER);
		} else {
			text.append(element.toString());
		}

		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}*/
}


