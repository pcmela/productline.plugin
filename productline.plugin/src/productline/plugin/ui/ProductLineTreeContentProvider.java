package productline.plugin.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;

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
			VariabilityTreeContainer variabilityContainer = new VariabilityTreeContainer();
			ElementTreeContainer elementContainer = new ElementTreeContainer();
			
			variabilityContainer.setVariabilities(((Module)parentElement).getVariabilities());
			elementContainer.setElements(((Module)parentElement).getElements());
			
			return new Object[] { variabilityContainer, elementContainer };
		}else if(parentElement instanceof VariabilityTreeContainer){
			return ((VariabilityTreeContainer)parentElement).getVariabilities().toArray();
		}else if(parentElement instanceof ElementTreeContainer){
			return ((ElementTreeContainer)parentElement).getElements().toArray();
		}else{
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
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
			Module m = ((Module)element);
			boolean hasVariables = (m.getVariabilities() != null) ? m.getVariabilities().size() > 0 : false;
			boolean hasElements = (m.getElements() != null) ? m.getElements().size() > 0 : false;
			
			return hasElements || hasVariables;
		}else if(element instanceof VariabilityTreeContainer){
			VariabilityTreeContainer variabilityContainer = (VariabilityTreeContainer)element;
			if(variabilityContainer.getVariabilities() != null){
				return variabilityContainer.getVariabilities().size() > 0;
			}else{
				return false;
			}
		}else if(element instanceof ElementTreeContainer){
			ElementTreeContainer elementContainer = (ElementTreeContainer)element;
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

}
