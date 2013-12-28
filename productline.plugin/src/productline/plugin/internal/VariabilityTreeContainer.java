package productline.plugin.internal;

import java.util.List;

import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Variability;

public class VariabilityTreeContainer implements BaseProductLineEntity {

	private final String name = "Variabilities";
	private List<Variability> variabilities;
	
	@Override
	public String getName() {
		return this.name;
	}

	public List<Variability> getVariabilities() {
		return variabilities;
	}

	public void setVariabilities(List<Variability> variabilities) {
		this.variabilities = variabilities;
	}
	
	

}
