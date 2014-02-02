package productline.plugin.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class CreateWizardImportPagePOJO extends WizardPage{

	protected Composite container;
	
	protected final String BUTTON_DATA_KEY_ID = "ID";
	protected final String BUTTON_DATA_VALUE_IMPORT_FROM_WEB = "IMPORT_FROM_WEB";
	protected final String BUTTON_DATA_VALUE_IMPORT_FROM_FILE = "IMPORT_FROM_FILE";
	
	protected Label lWebUserName;
	protected Text tWebUserName;
	protected Label lWebPassword;
	protected Text tWebPassword;
	protected Label lWebUrl;
	protected Text tWebUrl;
	
	protected Label lFilePath;
	protected Text tFilePath;
	protected Button bFilePath;
	
	protected CreateWizardImportPagePOJO(String pageName) {
		super(pageName);
	}

	public Text gettWebUserName() {
		return tWebUserName;
	}

	public void settWebUserName(Text tWebUserName) {
		this.tWebUserName = tWebUserName;
	}

	public Text gettWebPassword() {
		return tWebPassword;
	}

	public void settWebPassword(Text tWebPassword) {
		this.tWebPassword = tWebPassword;
	}

	public Text gettWebUrl() {
		return tWebUrl;
	}

	public void settWebUrl(Text tWebUrl) {
		this.tWebUrl = tWebUrl;
	}

	public Text gettFilePath() {
		return tFilePath;
	}

	public void settFilePath(Text tFilePath) {
		this.tFilePath = tFilePath;
	}
	
}
