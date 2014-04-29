package productline.plugin.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.internal.databinding.BindingStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;

import productline.plugin.internal.Configuration;
import productline.plugin.internal.DefaultMessageDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ElementDAO;
import diploma.productline.dao.ModuleDAO;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.dao.VariabilityDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class ProductLineConfigurationEditor extends FormEditor {

	private TextEditor editor;
	private IProject project;
	private IFile file;
	private OverviewPage overviewPage;
	private ConfigurationPage configPage;

	private IResourceChangeListener configurationFileDeleted = new IResourceChangeListener() {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {

			if (!file.exists()) {
				for (final IWorkbenchPage page : getSite().getWorkbenchWindow()
						.getPages()) {
					final FileEditorInput editorInput = (FileEditorInput) ProductLineConfigurationEditor.this
							.getEditorInput();
					// ProductLineConfigurationEditor.this.getActiveEditor().getSite().getShell().getDi
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							page.closeEditor(page.findEditor(editorInput), true);
						}
					});

				}
				cleanUpDatasourceAndFiles();
			}
			event.getDelta().getAffectedChildren();
		}
	};

	private void cleanUpDatasourceAndFiles() {
		String path = project.getLocation().toString();
		File projectDirectory = new File(path);
		File[] filesToBeDeleted = projectDirectory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				} else {
					String path = file.getAbsolutePath().toLowerCase();

					if (path.endsWith("database.h2.db")
							|| path.endsWith("database.trace.db")) {
						return true;
					}

				}
				return false;
			}
		});

		for (File file : filesToBeDeleted) {
			boolean deleted = false;
			try {
				deleted = file.delete();
				if (!deleted) {
					MessageDialog.openError(new Shell(), "Cannot remove file",
							"Cannot remove file: " + file.getName());
				}
			} catch (SecurityException e) {
				if (!deleted) {
					MessageDialog.openError(new Shell(), "Cannot remove file",
							"Cannot remove file: " + file.getName()
									+ " because: " + e.getMessage());
				}
			}
		}
	}

	@Override
	protected void addPages() {
		final IEditorInput input = getEditorInput();
		if (input instanceof FileEditorInput) {
			file = ((FileEditorInput) input).getFile();
			this.project = file.getProject();
		}

		try {
			addPage(overviewPage = new OverviewPage(this,
					"productline.plugin.editor.overview", "Overview", project));
			addPage(configPage = new ConfigurationPage(this,
					"productline.plugin.editor.config", "Configuration",
					project));
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				configurationFileDeleted, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IObservableList observableList = overviewPage.getDataBindingContext()
				.getValidationStatusProviders();
		for (Object o : observableList) {
			ValidationStatusProvider provider = (ValidationStatusProvider) o;
			WritableValue writableValud = (WritableValue) provider
					.getValidationStatus();
			BindingStatus bindingStatus = (BindingStatus) writableValud
					.getValue();
			IStatus[] status = bindingStatus.getChildren();
			for (IStatus s : status) {
				MessageDialog.openError(new Shell(), "Validation error",
						s.getMessage());
				return;
			}

		}
		
		try {
			refreshConfigFile();
		} catch (IOException e2) {
			DefaultMessageDialog.ioException(e2.getMessage());
			e2.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[] input = (Object[]) overviewPage.getTreeViewer().getInput();
		if (input.length > 0) {
			ProductLine productLine = (ProductLine) ((Object[]) overviewPage
					.getTreeViewer().getInput())[0];

			try (Connection con = DaoUtil.connect(overviewPage.getProperties())) {

				if (productLine.isDirty()) {
					if (!checkNameValue(productLine, "Productline")) {
						return;
					}
					ProductLineDAO pDao = new ProductLineDAO();
					pDao.update(productLine, con);
					overviewPage.getTreeViewer().refresh();
				}

				if (productLine.getModules() != null) {
					ModuleDAO mDao = new ModuleDAO();
					for (Module m : productLine.getModules()) {
						if (m.isDirty()) {
							if (!checkNameValue(m, "Module")) {
								return;
							}
							mDao.update(m, con);
							overviewPage.getTreeViewer().refresh();
						}

						if (m.getVariabilities() != null) {
							VariabilityDAO vDao = new VariabilityDAO();
							for (Variability v : m.getVariabilities()) {
								if (v.isDirty()) {
									if (!checkNameValue(v, "Variability")) {
										return;
									}
									vDao.update(v, con);
									overviewPage.getTreeViewer().refresh();
								}
							}
						}

						if (m.getElements() != null) {
							ElementDAO eDao = new ElementDAO();
							for (Element e : m.getElements()) {
								if (e.isDirty()) {
									if (!checkNameValue(e, "Element")) {
										return;
									}
									eDao.update(e, con);
									overviewPage.getTreeViewer().refresh();
								}
							}
						}
					}
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				DefaultMessageDialog.sqlExceptionDialog(e1.getMessage());
				e1.printStackTrace();
			}
		}
		overviewPage.setDirty(false);
		configPage.setDirty(false);
		firePropertyChange(IEditorPart.PROP_DIRTY);
		overviewPage.getTreeViewer().expandAll();
	}

	private void refreshConfigFile() throws IOException, CoreException {
		Configuration local = configPage.getLocalDbConfiguration();
		if (local.isDirty()) {
			Properties properties = Configuration.getProperties(local);
			String content = getPropertyAsString(properties);

			file.setContents(new ByteArrayInputStream(content.getBytes()),
					IFile.FORCE, null);

			local.setDirty(false);
			overviewPage.setProperties(properties);
			configPage.setProperties(properties);
			
			overviewPage.refreshTree();
		}
	}

	private boolean checkNameValue(BaseProductLineEntity obj, String type) {
		if (obj.getName().trim().equals("")) {
			MessageDialog.openError(new Shell(), "Name cannot be empty!",
					"Please enter the name for " + type);
			return false;
		}

		return true;
	}

	private String getPropertyAsString(Properties prop) throws IOException {
		StringWriter writer = new StringWriter();
		prop.store(writer, null);
		return writer.getBuffer().toString();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				configurationFileDeleted);
	}

}
