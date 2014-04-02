package productline.plugin.ui.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class CreateWizardOverviewPOJO extends CreateWizardPageBase{

	protected CreateWizardOverviewPOJO(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	protected final String BUTTON_DATA_KEY_ID = "ID";
	protected final String BUTTON_DATA_VALUE_NEWDB = "NEW_DB";
	protected final String BUTTON_DATA_VALUE_EXISTINGDB = "EXISTING_DB";

	protected IPath projectLoacation;
	
	protected Composite container;

	protected Label lDefaultPath;
	protected Text tDefaultPath;
	protected Button bDefaultPath;
	
	protected Label lProductLineName;
	protected Text tProductLineName;
	protected Label lProductLineDescription;
	protected Text tProductLineDescription;
	
	protected Button bCreateDb;
	protected Button bExistingDb;
	protected Label lNewDbUserName;
	protected Text tNewDbUserName;
	protected Label lNewDbPassword;
	protected Text tNewDbPassword;
	protected Label lNewDbPasswordConfirm;
	protected Text tNewDbPasswordConfirm;

	protected Label lExistingDbUserName;
	protected Text tExistingDbUserName;
	protected Label lExistingDbPassword;
	protected Text tExistingDbPassword;
	protected Label lExistingDbPath;
	protected Text tExistingDbPath;
	protected Button bExistingDbPath;
	protected Label lUseExistingProductLine;
	protected Button bUseExistingProductLine;
	protected Label lExistingId;
	protected Text tExistingId;
	protected Button bExistingProductLines;
	
	protected boolean createNewDB = true;
	
	
	protected void validateForm(){
		if(tDefaultPath.getText().trim().equals("")){
			setDescription("Choose a project");
			setPageComplete(false);
			return;
		}
		
		if(tProductLineName.getText().equals("")){
			setDescription("Enter a product line name");
			setPageComplete(false);
			return;
		}
		
		if(createNewDB){
			if(tNewDbUserName.getText().equals("")){
				setDescription("Enter username for DB");
				setPageComplete(false);
				return;
			}
			if(tNewDbPassword.getText().equals("")){
				setDescription("Enter password for username");
				setPageComplete(false);
				return;
			}
			if(!tNewDbPasswordConfirm.getText().equals(tNewDbPassword.getText())){
				setDescription("Confirm password must be same as password");
				setPageComplete(false);
				return;
			}
		}else{
			if(tExistingDbUserName.getText().equals("")){
				setDescription("Enter username");
				setPageComplete(false);
				return;
			}
			
			if(tExistingDbPassword.getText().equals("")){
				setDescription("Enter password");
				setPageComplete(false);
				return;
			}
			
			if(tExistingDbPath.getText().equals("")){
				setDescription("Enter path");
				setPageComplete(false);
				return;
			}
		}
		
		setDescription("Create productline configuration files");
		setPageComplete(true);
	}	
	
	public Text gettNewDbUserName() {
		return tNewDbUserName;
	}

	public void settNewDbUserName(Text tNewDbUserName) {
		this.tNewDbUserName = tNewDbUserName;
	}

	public Text gettNewDbPassword() {
		return tNewDbPassword;
	}

	public void settNewDbPassword(Text tNewDbPassword) {
		this.tNewDbPassword = tNewDbPassword;
	}

	public Text gettNewDbPasswordConfirm() {
		return tNewDbPasswordConfirm;
	}

	public void settNewDbPasswordConfirm(Text tNewDbPasswordConfirm) {
		this.tNewDbPasswordConfirm = tNewDbPasswordConfirm;
	}

	public Text gettExistingDbUserName() {
		return tExistingDbUserName;
	}

	public void settExistingDbUserName(Text tExistingDbUserName) {
		this.tExistingDbUserName = tExistingDbUserName;
	}

	public Text gettExistingDbPassword() {
		return tExistingDbPassword;
	}

	public void settExistingDbPassword(Text tExistingDbPassword) {
		this.tExistingDbPassword = tExistingDbPassword;
	}

	public Text gettExistingDbPath() {
		return tExistingDbPath;
	}

	public void settExistingDbPath(Text tExistingDbPath) {
		this.tExistingDbPath = tExistingDbPath;
	}

	public boolean isCreateNewDB() {
		return createNewDB;
	}

	public void setCreateNewDB(boolean createNewDB) {
		this.createNewDB = createNewDB;
	}

	public IPath getProjectLoacation() {
		return projectLoacation;
	}

	public void setProjectLoacation(IPath projectLoacation) {
		this.projectLoacation = projectLoacation;
	}

	public Text gettProductLineName() {
		return tProductLineName;
	}

	public void settProductLineName(Text tProductLineName) {
		this.tProductLineName = tProductLineName;
	}

	public Text gettProductLineDescription() {
		return tProductLineDescription;
	}

	public void settProductLineDescription(Text tProductLineDescription) {
		this.tProductLineDescription = tProductLineDescription;
	}

	public Button getbUseExistingProductLine() {
		return bUseExistingProductLine;
	}

	public void setbUseExistingProductLine(Button bUseExistingProductLine) {
		this.bUseExistingProductLine = bUseExistingProductLine;
	}

	public Text gettExistingId() {
		return tExistingId;
	}

	public void settExistingId(Text tExistingId) {
		this.tExistingId = tExistingId;
	}

	public Button getbCreateDb() {
		return bCreateDb;
	}

	public void setbCreateDb(Button bCreateOrExistingDb) {
		this.bCreateDb = bCreateOrExistingDb;
	}

	public Button getbExistingDb() {
		return bExistingDb;
	}

	public void setbExistingDb(Button bExistingDb) {
		this.bExistingDb = bExistingDb;
	}
	
	
	

}
