package productline.plugin.ui.providers;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WhereUsedContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Set<?>) {
			return ((Set<?>) inputElement).toArray();
		} else if(inputElement instanceof String){
			return new String[] { (String)inputElement };
		}
		else {
			return new Object[] {};
		}
	}

}
