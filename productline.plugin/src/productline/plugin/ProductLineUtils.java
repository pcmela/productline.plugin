package productline.plugin;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;


public class ProductLineUtils {
	
	/**
	 * Set parent object to the child object
	 * @param productLine
	 * @return ProductLine object with relation from children to parent object
	 */
	public static ProductLine refreshRelations(ProductLine productLine){
		for(Module m : productLine.getModules()){
			m.setProductLine(productLine);
			for(Variability v : m.getVariabilities()){
				v.setModule(m);
			}
			for(Element e : m.getElements()){
				e.setModule(m);
			}
		}
		return productLine;
	}
}
