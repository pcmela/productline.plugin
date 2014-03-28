package productline.plugin.ui.providers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import productline.plugin.internal.ElementSetTreeContainer;
import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilitySetTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class ProductLineTreeContentProvider implements ITreeContentProvider {

	/**
	 * Returns the elements to display in the viewer when its input is set to the given element.
	 * @param Object element
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Object[]){
			return (Object[])inputElement;
		}else if(inputElement instanceof ProductLine){
			return ((ProductLine) inputElement).getModules().toArray();
		}else {
			return new Object[0];
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ProductLine){
			return ((ProductLine) parentElement).getModules().toArray();
		}else if(parentElement instanceof Module){
			VariabilitySetTreeContainer variabilityContainer = new VariabilitySetTreeContainer();
			variabilityContainer.setParent((Module)parentElement);
			ElementSetTreeContainer elementContainer = new ElementSetTreeContainer();
			elementContainer.setParent((Module)parentElement);
			
			variabilityContainer.setVariabilities(((Module)parentElement).getVariabilities());
			elementContainer.setElements(((Module)parentElement).getElements());
			
			return new Object[] { variabilityContainer, elementContainer };
		}else if(parentElement instanceof VariabilitySetTreeContainer){
			VariabilitySetTreeContainer cont = (VariabilitySetTreeContainer)parentElement;
			return makeVariabilityTreeContainer(cont.getVariabilities(), parentElement).toArray();
		}else if(parentElement instanceof ElementSetTreeContainer){
			ElementSetTreeContainer cont = (ElementSetTreeContainer)parentElement;
			return makeElementTreeContainer(cont.getElements(), parentElement).toArray();
			//return ((ElementSetTreeContainer)parentElement).getElements().toArray();
		}else{
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof Module){
			return null;
		}else if(element instanceof VariabilitySetTreeContainer){
			return ((VariabilitySetTreeContainer)element).getParent();
		}else if(element instanceof ElementSetTreeContainer){
			return ((ElementSetTreeContainer)element).getParent();
		}else if(element instanceof VariabilityTreeContainer){
			return ((VariabilityTreeContainer)element).getParent();
		}else if(element instanceof ElementTreeContainer){
			return ((ElementTreeContainer)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element == null){
			return false;
		}
		
		if(element instanceof ProductLine){
			ProductLine p = ((ProductLine) element);
			if(p.getModules() != null){
				return p.getModules().size() > 0;
			}else{
				return false;
			}
		}else if(element instanceof Module){
			return true;
		}else if(element instanceof VariabilitySetTreeContainer){
			VariabilitySetTreeContainer variabilityContainer = (VariabilitySetTreeContainer)element;
			if(variabilityContainer.getVariabilities() != null){
				return variabilityContainer.getVariabilities().size() > 0;
			}else{
				return false;
			}
		}else if(element instanceof ElementSetTreeContainer){
			ElementSetTreeContainer elementContainer = (ElementSetTreeContainer)element;
			if(elementContainer.getElements() != null){
				return elementContainer.getElements().size() > 0;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
	
	private Set<VariabilityTreeContainer> makeVariabilityTreeContainer(Set<Variability> variabilities, Object parent){
		Set<VariabilityTreeContainer> cont = new HashSet<>();
		for(Variability v : variabilities){
			VariabilityTreeContainer c = new VariabilityTreeContainer();
			c.setId(v.getId()); 
			c.setName(v.getName());
			c.setDescription(v.getDescription());
			c.setModule(v.getModule());
			c.setParent(parent);
			cont.add(c);
		}
		
		return cont;
	}
	
	private Set<ElementTreeContainer> makeElementTreeContainer(Set<Element> elements, Object parent){
		Set<ElementTreeContainer> cont = new HashSet<>();
		for(Element v : elements){
			ElementTreeContainer c = new ElementTreeContainer();
			c.setId(v.getId()); 
			c.setName(v.getName());
			c.setDescription(v.getDescription());
			c.setModule(v.getModule());
			c.setResources(v.getResources());
			c.setParent(parent);
			c.setType(v.getType());
			c.setSource(v);
			cont.add(c);
		}
		
		return cont;
	}

}
