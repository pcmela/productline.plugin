package productline.plugin.editor;

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.part.FileEditorInput;

import productline.plugin.internal.Configuration;
import productline.plugin.internal.ConfigurationKeys;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.ProductLine;

public class ProductLineFormPage extends FormPage {

	protected IFile source;
	protected FormEditor editor;
	protected BaseProductLineEntity currentSelectedObject;
	protected Properties properties;
	protected Configuration localDbConfiguration;
	
	protected boolean isDirty;
	protected DataBindingContext dataBindingContext;

	public ProductLineFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		IEditorInput e = editor.getEditorInput();
		source = ((FileEditorInput) e).getFile();
		properties = getProperties();
		isDirty = false;
		dataBindingContext = new DataBindingContext();
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
			if (!c.isDisposed()) {
				c.dispose();
			}
		}
		dataBindingContext.dispose();

	}

	protected ProductLine loadData(final boolean initial) {
		ProductLine productLine = null;

		try (Connection con = DaoUtil.connect(properties)) {

			/*
			 * if (HibernateUtil.getSessionFactory() == null || initial) {
			 * HibernateUtil.initSessionFactory(hibernateProp); }
			 */
			String stringId = properties
					.getProperty(ConfigurationKeys.PRODUCTLINE_ID_KEY);
			int id;
			try {
				id = Integer.parseInt(stringId);
			} catch (NumberFormatException e) {
				MessageDialog.openError(this.getSite().getShell(),
						"Synchronization error",
						"Product line id have not correct format! Id must be number. Actual id is: "
								+ stringId);
				e.printStackTrace();
				return null;
			}

			ProductLineDAO plDao = new ProductLineDAO();
			productLine = plDao.getProductLineWithChilds(id, con);
			if(productLine != null){
				productLine.setDatabaseProperties(properties);
			}

			if (initial == false) {
				if (productLine == null) {
					MessageDialog.openInformation(this.getSite().getShell(),
							"Product Line does not exist!",
							"You must create product line with id: " + id);
				}
			}

			return productLine;

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

	protected void resetCurrentSelectedObject() {
		currentSelectedObject = null;
	}

	public Properties getProperties() {
		if (properties == null) {
			return propertiesFactory(false);
		}

		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
		localDbConfiguration.setDataLocal(properties);
	}

	private Properties propertiesFactory(boolean changed) {
		Properties properties = new Properties();
		try {
			properties.load(source.getContents());
			localDbConfiguration = new Configuration();
			localDbConfiguration.setDataLocal(properties);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return properties;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public Configuration getLocalDbConfiguration() {
		return localDbConfiguration;
	}
	
	
}
