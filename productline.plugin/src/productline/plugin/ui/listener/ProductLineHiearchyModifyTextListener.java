package productline.plugin.ui.listener;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import productline.plugin.editor.OverviewPage;
import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import diploma.productline.entity.BaseProductLineEntity;

public class ProductLineHiearchyModifyTextListener implements ModifyListener {

	private Object selection;
	private OverviewPage page;
	
	public ProductLineHiearchyModifyTextListener(Object selection, OverviewPage page){
		this.selection = selection;
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		BaseProductLineEntity o = (BaseProductLineEntity) selection;
		if (o instanceof ElementTreeContainer) {
			o = ((ElementTreeContainer) o).getSource();
		} else if (o instanceof VariabilityTreeContainer) {
			o = ((VariabilityTreeContainer) o).getSource();
		}
		if (!o.isDirty()) {
			o.setDirty(true);
		}
		if (!page.isDirty()) {
			page.setDirtyAndfirePropertyChange(true);
		}
	}

}
