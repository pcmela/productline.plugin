package productline.plugin.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PackageListLabelProvider extends LabelProvider {
	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}
}
