package productline.plugin.internal;

import java.util.List;
import java.util.Set;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Variability;

public class VariabilityTreeContainer implements BaseProductLineEntity {

	private final String name = "Variabilities";
	private Set<Variability> variabilities;
	
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
	
	

}