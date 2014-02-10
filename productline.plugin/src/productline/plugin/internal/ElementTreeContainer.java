package productline.plugin.internal;

import diploma.productline.entity.Element;

public class ElementTreeContainer extends Element implements ITreeElement {

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
