package productline.plugin.editor;

import java.io.IOException;
import java.sql.Connection;
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
import diploma.productline.DaoUtil;
import diploma.productline.HibernateUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.ProductLine;

public class ProductLineFormPage extends FormPage {

	protected IFile source;
	protected FormEditor editor;
	protected BaseProductLineEntity currentSelectedObject;
	
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
			if(!c.isDisposed()){
				c.dispose();
			}
		}

	}
	
	protected ProductLine loadData(final boolean initial) {
		ProductLine productLine;
		Properties properties = new Properties();
		try {
			properties.load(source.getContents());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (Connection con = DaoUtil.connect(properties);){

			Properties hibernateProp = DatabaseUtil.getHibernateProperties(properties);

			/*if (HibernateUtil.getSessionFactory() == null || initial) {
				HibernateUtil.initSessionFactory(hibernateProp);
			}*/
			String id = properties
					.getProperty(ConfigurationKeys.PRODUCTLINE_ID_KEY);
			if (id != null && !id.equals("")) {
				/*Session session = HibernateUtil.getSessionFactory()
						.getCurrentSession();
				session.beginTransaction();
				productLine = (ProductLine) session.get(
						ProductLine.class, id);
				session.getTransaction().commit();*/
				ProductLineDAO plDao = new ProductLineDAO(properties);
				productLine = plDao.getProductLineWithChilds(id,con);
				

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
	
	protected void resetCurrentSelectedObject(){
		currentSelectedObject = null;
	}

}
