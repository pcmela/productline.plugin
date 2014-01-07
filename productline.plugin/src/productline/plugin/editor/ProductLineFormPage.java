package productline.plugin.editor;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.hibernate.Session;

import productline.plugin.internal.ConfigurationKeys;
import productline.plugin.internal.DatabaseUtil;
import diploma.productline.HibernateUtil;
import diploma.productline.entity.ProductLine;

public class ProductLineFormPage extends FormPage {

	protected IFile source;
	protected FormEditor editor;
	
	public ProductLineFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	static class DependencyFilter extends ViewerFilter {
		protected SearchMatcher matcher;

		public DependencyFilter(SearchMatcher matcher) {
			this.matcher = matcher;
		}

		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (matcher != null && !matcher.isEmpty()) {
				return matcher.isMatchingArtifact(element);
			}
			return true;
		}

	}

	protected void disposeActiveElements(Control[] active) {
		for (Control c : active) {
			c.dispose();
		}

	}
	
	protected ProductLine loadData(final boolean initial) {
		ProductLine productLine;

		try {
			Properties properties = new Properties();
			properties.load(source.getContents());

			Properties hibernateProp = DatabaseUtil.getHibernateProperties(properties);

			if (HibernateUtil.getSessionFactory() == null) {
				HibernateUtil.initSessionFactory(hibernateProp);
			}

			String id = properties
					.getProperty(ConfigurationKeys.PRODUCTLINE_ID_KEY);
			if (id != null && !id.equals("")) {
				Session session = HibernateUtil.getSessionFactory()
						.getCurrentSession();
				session.beginTransaction();
				productLine = (ProductLine) session.get(
						ProductLine.class, id);
				session.getTransaction().commit();
				

				if (initial == false) {
					if (productLine == null) {
						MessageDialog.openInformation(
								this.getSite().getShell(),
								"Product Line does not exist!",
								"You must create product line with id: " + id);
					}
				}
				
				return productLine;
			} else {
				MessageDialog.openError(this.getSite().getShell(),
						"Synchronization error",
						"You must set the product line ID!");
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(),
					"Synchronization error", e.getMessage());
		}

		/*
		 * String path = "C:\\Users\\IBM_ADMIN\\Desktop\\Neon.yaml"; ProductLine
		 * productLine = YamlExtractor.extract(path);
		 */
		return null;

	}

}
