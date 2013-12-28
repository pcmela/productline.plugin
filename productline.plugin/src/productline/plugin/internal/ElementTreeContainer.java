package productline.plugin.internal;

import java.util.List;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;

public class ElementTreeContainer implements BaseProductLineEntity {

	private final String name = "Elements";
	private List<Element> elements;
	
	@Override
	public String getName() {
		return name;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	
}
