package productline.plugin.internal;

import diploma.productline.entity.Variability;

public class VariabilityTreeContainer extends Variability implements ITreeElement{

	private Object parent;
	
	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public void setParent(Object parent) {
		this.parent = parent;
	}

}
