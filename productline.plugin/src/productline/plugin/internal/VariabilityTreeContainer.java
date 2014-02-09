package productline.plugin.internal;

import java.util.Set;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Module;
import diploma.productline.entity.Variability;

public class VariabilityTreeContainer extends BaseProductLineEntity {

	private final String name = "Variabilities";
	private Set<Variability> variabilities;
	private Module parent;
	
	@Override
	public String getName() {
		return this.name;
	}

	public Set<Variability> getVariabilities() {
		return variabilities;
	}

	public void setVariabilities(Set<Variability> variabilities) {
		this.variabilities = variabilities;
	}

	public Module getParent() {
		return parent;
	}

	public void setParent(Module parent) {
		this.parent = parent;
	}
	
	
	

}
