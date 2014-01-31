package productline.plugin.editor;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;

import diploma.productline.HibernateUtil;

public class ProductLineConfigurationEditor extends FormEditor {

	private TextEditor editor;
	private IProject project;
	private IFile file;

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
		HibernateUtil.removeSessionFactory();
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
		
		for(File file : filesToBeDeleted){
			boolean deleted = false;
			try{
				deleted = file.delete();
				if(!deleted){
					MessageDialog.openError(new Shell(), "Cannot remove file", "Cannot remove file: " + file.getName());
				}
			}catch(SecurityException e){
				if(!deleted){
					MessageDialog.openError(new Shell(), "Cannot remove file", "Cannot remove file: " + file.getName() + " because: " + e.getMessage());
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
			addPage(new OverviewPage(this, "productline.plugin.v1", "Overview",
					project));
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "Source");
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
		// TODO Auto-generated method stub

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
