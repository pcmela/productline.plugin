package productline.plugin.ui.listener.model;

import org.eclipse.swt.events.ModifyListener;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public interface ProductLineEventListener {

	public void createDetailProductLine(ProductLine productLine,
			ModifyListener modifyListener, ModifyListener modifyListenerOther);
	
	public void createDetailModule(final Module module,
			ModifyListener modifyListener, ModifyListener modifyListenerOther);
	
	public void createDetailVariability(Variability variability,
			ModifyListener modifyListener, ModifyListener modifyListenerOther);
	
	public void createDetailElement(Element element,
			ModifyListener modifyListener, ModifyListener modifyListenerOther);
}
