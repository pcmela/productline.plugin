package productline.plugin.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ProductLineConfigurationEditor extends FormEditor {

	private ConfigurationTextEditor sourcePage;
	private TextEditor editor;
	private IProject project;
	private IFile file;
	
	@Override
	protected void addPages() {
		final IEditorInput input = getEditorInput();  
        if (input instanceof FileEditorInput){ 
            file = ((FileEditorInput) input).getFile(); 
            this.project = file.getProject();
        }
        
		try {
			addPage(new OverviewPage(this, "productline.plugin.v1", "Overview", project));
			sourcePage = new ConfigurationTextEditor();
			//addPage(sourcePage);
			createPage0();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void createPage0() {
		try {
			
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
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


}
