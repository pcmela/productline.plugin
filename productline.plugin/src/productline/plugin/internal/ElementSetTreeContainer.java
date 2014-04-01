package productline.plugin.internal;

import java.util.Set;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;

public class ElementSetTreeContainer extends BaseProductLineEntity {

	private final String name = "Elements";
	private Set<Element> elements;
	private Module parent;
	
	@Override
	public String getName() {
		return name;
	}

	public Set<Element> getElements() {
		return elements;
	}

	public void setElements(Set<Element> elements) {
		this.elements = elements;
	}

	public Module getParent() {
		return parent;
	}

	public void setParent(Module parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return getName();
	}

	
}
