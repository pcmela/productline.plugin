package productline.plugin.editor;

import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ConfigurationTextEditor extends AbstractTextEditor {
	public ConfigurationTextEditor() {
		setDocumentProvider(new TextFileDocumentProvider());
	}
}
