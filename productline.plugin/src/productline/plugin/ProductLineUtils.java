package productline.plugin;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;


public class ProductLineUtils {
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
