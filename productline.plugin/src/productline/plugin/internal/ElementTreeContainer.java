package productline.plugin.internal;

import java.util.List;
import java.util.Set;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;

public class ElementTreeContainer implements BaseProductLineEntity {

	private final String name = "Elements";
	private Set<Element> elements;
	
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

	
}
