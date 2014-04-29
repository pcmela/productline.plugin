package productline.plugin.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.ui.ProductLineIdDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.ProductLine;

public class ConfigurationPage extends ConfigurationPagePOJO {

	public ConfigurationPage(FormEditor editor, String id, String title, IProject project) {
		super(editor, id, title, project);
		this.editor = editor;
		this.project = project;
	}
	
	private ModifyListener localModify = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			if(!localDbConfiguration.isDirty()){
				localDbConfiguration.setDirty(true);
			}
			
			if (!isDirty) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();
			}
		}
	};
	
private ModifyListener remoteModify = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			if(!localDbConfiguration.isDirty()){
				localDbConfiguration.setDirty(true);
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();
			}
		}
	};

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Product Line: Configuration");

		form.setExpandHorizontal(true);
		form.setExpandVertical(true);

		Composite body = form.getBody();
		body.setLayout(new FillLayout());

		SashForm sashForm = new SashForm(body, SWT.NONE);
		toolkit.adapt(sashForm);
		toolkit.adapt(sashForm, true, true);
		
		Composite localComposite = toolkit.createComposite(sashForm);
		localComposite.setLayout(new GridLayout(1, false));
		
		createProductLineSection(localComposite, toolkit);
		createLocalDbSection(localComposite, toolkit);
		//createRemoteDbSection(localComposite, toolkit);
	}
	
	private void createProductLineSection(Composite localComposite,
			FormToolkit formToolkit){
		
		Section detailSection = formToolkit.createSection(localComposite,
				ExpandableComposite.TITLE_BAR);
		detailSection.marginHeight = 1;
		GridData gd_detailSection = new GridData(SWT.FILL, SWT.TOP, true, false);
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Product line");
		toolkit.paintBordersFor(detailSection);
		
		Composite detailComposite = formToolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(3, false));
		detailSection.setClient(detailComposite);
		
		lProductLineId = toolkit.createLabel(detailComposite, "Id:");
		tProductLineId = toolkit.createText(detailComposite, "");
		GridData tdTextId = new GridData(/*SWT.FILL, SWT.TOP, true, false*/);
		//tdTextId.horizontalSpan = 3;
		tdTextId.widthHint = 100;
		tProductLineId.setLayoutData(tdTextId);
		tProductLineId.setEnabled(false);
		bProductLineId = toolkit.createButton(detailComposite, "Get id", SWT.PUSH);
		bProductLineId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openExistingProductLinesDialog(tProductLineId);
			}
		});
	}
	
	private void createLocalDbSection(Composite localComposite,
			FormToolkit formToolkit) {

		
		Section detailSection = formToolkit.createSection(localComposite,
				ExpandableComposite.TITLE_BAR);
		detailSection.marginHeight = 1;
		GridData gd_detailSection = new GridData(SWT.FILL, SWT.TOP, true, false);
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Local DB");
		toolkit.paintBordersFor(detailSection);
		
		Composite detailComposite = formToolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(2, false));
		detailSection.setClient(detailComposite);
		
		
		lLocalConnectionString = toolkit.createLabel(detailComposite, "Connection string:", SWT.NONE);
		tLocalConnectionString = toolkit.createText(detailComposite, "");
		GridData tdConString = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdConString.horizontalSpan = 3;
		tLocalConnectionString.setLayoutData(tdConString);
		
		lLocalUsername = toolkit.createLabel(detailComposite, "Username:");
		tLocalUsername = toolkit.createText(detailComposite, "");
		GridData tdUsername = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdUsername.horizontalSpan = 3;
		tLocalUsername.setLayoutData(tdUsername);
		
		lLocalPassword = toolkit.createLabel(detailComposite, "Password");
		tLocalPassword = toolkit.createText(detailComposite, "");
		GridData tdPassword = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdPassword.horizontalSpan = 3;
		tLocalPassword.setLayoutData(tdPassword);
		
		addDataBindingLocalDb(localDbConfiguration);
		addModifyListenerLocal();
	}
	
	/*private void createRemoteDbSection(Composite localComposite,
			FormToolkit formToolkit) {
		
		Section detailSection = formToolkit.createSection(localComposite,
				ExpandableComposite.TITLE_BAR);
		detailSection.marginHeight = 1;
		GridData gd_detailSection = new GridData(SWT.FILL, SWT.TOP, true, false);
		detailSection.setLayoutData(gd_detailSection);
		detailSection.setText("Remote DB");
		toolkit.paintBordersFor(detailSection);
		
		Composite detailComposite = formToolkit.createComposite(detailSection, SWT.NONE);
		detailComposite.setLayout(new GridLayout(2, false));
		detailSection.setClient(detailComposite);
		
		lRemoteConnectionString = toolkit.createLabel(detailComposite, "Connection string:");
		tRemoteConnectionString = toolkit.createText(detailComposite, "");
		GridData tdConString = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdConString.horizontalSpan = 3;
		tRemoteConnectionString.setLayoutData(tdConString);
		
		lRemoteUsername = toolkit.createLabel(detailComposite, "Username:");
		tRemoteUsername = toolkit.createText(detailComposite, "");
		GridData tdUsername = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdUsername.horizontalSpan = 3;
		tRemoteUsername.setLayoutData(tdUsername);
		
		lRemotePassword = toolkit.createLabel(detailComposite, "Password");
		tRemotePassword = toolkit.createText(detailComposite, "");
		GridData tdPassword = new GridData(SWT.FILL, SWT.TOP, true, false);
		//tdPassword.horizontalSpan = 3;
		tRemotePassword.setLayoutData(tdPassword);
		
		addDataBindingRemoteDb(remoteDbConfiguration);
		addModifyListenerRemote();
	}*/
	
	private void addModifyListenerLocal(){
		tProductLineId.addModifyListener(localModify);
		tLocalConnectionString.addModifyListener(localModify);
		tLocalUsername.addModifyListener(localModify);
		tLocalPassword.addModifyListener(localModify);
	}
	
	private void addModifyListenerRemote(){
		tRemoteConnectionString.addModifyListener(remoteModify);
		tRemoteUsername.addModifyListener(remoteModify);
		tRemotePassword.addModifyListener(remoteModify);
	}
	
	protected int openExistingProductLinesDialog(Text control){
		
		ProductLineDAO pDao = new ProductLineDAO();
		
		try(Connection con = DaoUtil.connect(properties)){
			Set<ProductLine> productLines = pDao.getProductLine(con);
			ProductLineIdDialog dialog = new ProductLineIdDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), productLines, control);
			dialog.open();
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
		} catch (SQLException e) {
			DefaultMessageDialog.sqlExceptionDialog("Connection failed!\nPlease enter the valid username, password and connection string and try it again.");
			e.printStackTrace();
		}
		return 0;
	}
	
}
