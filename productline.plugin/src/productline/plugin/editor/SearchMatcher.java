package productline.plugin.editor;

import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;
import productline.plugin.internal.ElementSetTreeContainer;
import productline.plugin.internal.VariabilitySetTreeContainer;

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
		} else if (element instanceof VariabilitySetTreeContainer) {
			if (((VariabilitySetTreeContainer) element).getVariabilities() != null) {
				for (Variability v : ((VariabilitySetTreeContainer) element)
						.getVariabilities()) {
					result = (v.getName() != null && v.getName().indexOf(text) > -1);
					if (result)
						return result;
				}
			}
			return false;
		} else if (element instanceof ElementSetTreeContainer) {
			if (((ElementSetTreeContainer) element).getElements() != null) {
				for (Element e : ((ElementSetTreeContainer) element).getElements()) {
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
