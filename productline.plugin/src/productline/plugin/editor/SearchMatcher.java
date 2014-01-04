package productline.plugin.editor;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;
import productline.plugin.internal.ElementTreeContainer;
import productline.plugin.internal.VariabilityTreeContainer;

public class SearchMatcher {

	private final SearchControl searchControl;

	public SearchMatcher(SearchControl searchControl) {
		this.searchControl = searchControl;
	}

	public boolean isMatchingArtifact(Object element) {
		String text = searchControl.getSearchText().getText();
		String name;
		boolean result = false;

		if (element == null)
			return false;

		if (element instanceof ProductLine) {
			return true;
		} else if (element instanceof Module) {
			name = ((Module) element).getName();
			result = (name != null && name.indexOf(text) > -1);
			if (result)
				return result;

			if (((Module) element).getVariabilities() != null) {
				for (Variability v : ((Module) element).getVariabilities()) {
					result = (v.getName() != null && v.getName().indexOf(text) > -1);
					if (result)
						return result;
				}
			}

			if (((Module) element).getElements() != null) {
				for (Element e : ((Module) element).getElements()) {
					result = (e.getName() != null && e.getName().indexOf(text) > -1);
					if (result)
						return result;
				}
			}

			return false;
		} else if (element instanceof VariabilityTreeContainer) {
			if (((VariabilityTreeContainer) element).getVariabilities() != null) {
				for (Variability v : ((VariabilityTreeContainer) element)
						.getVariabilities()) {
					result = (v.getName() != null && v.getName().indexOf(text) > -1);
					if (result)
						return result;
				}
			}
			return false;
		} else if (element instanceof ElementTreeContainer) {
			if (((ElementTreeContainer) element).getElements() != null) {
				for (Element e : ((ElementTreeContainer) element).getElements()) {
					result = (e.getName() != null && e.getName().indexOf(text) > -1);
					if (result)
						return result;
				}
			}
			return false;
		} else if (element instanceof Variability) {
			name = ((Variability) element).getName();
			return (name != null && name.indexOf(text) > -1);
		} else if (element instanceof Element) {
			name = ((Element) element).getName();
			return (name != null && name.indexOf(text) > -1);
		} else {
			return false;
		}

	}

	public boolean isEmpty() {
		if (searchControl.getSearchText() == null)
			return true;

		return searchControl.getSearchText().getText() == null //
				|| searchControl.getSearchText().getText().trim().length() == 0;
	}

}
