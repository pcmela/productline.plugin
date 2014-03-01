package productline.plugin.editor;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import productline.plugin.internal.Configuration;
import diploma.productline.entity.Variability;

public class ConfigurationPagePOJO extends ProductLineFormPage {

	protected FormToolkit toolkit;
	protected IProject project;

	protected Label lProductLineId;
	protected Text tProductLineId;
	protected Button bProductLineId;

	protected Label lLocalConnectionString;
	protected Text tLocalConnectionString;
	protected Label lLocalUsername;
	protected Text tLocalUsername;
	protected Label lLocalPassword;
	protected Text tLocalPassword;

	protected Label lRemoteConnectionString;
	protected Text tRemoteConnectionString;
	protected Label lRemoteUsername;
	protected Text tRemoteUsername;
	protected Label lRemotePassword;
	protected Text tRemotePassword;

	public ConfigurationPagePOJO(FormEditor editor, String id, String title,
			IProject project) {
		super(editor, id, title);
		// TODO Auto-generated constructor stub
	}

	protected void addDataBindingLocalDb(Configuration config) {

		IObservableValue id = PojoProperties.value("id").observe(config);
		IObservableValue connectionString = PojoProperties.value(
				"connectionString").observe(config);
		IObservableValue username = PojoProperties.value("username").observe(
				config);
		IObservableValue password = PojoProperties.value("password").observe(
				config);

		IObservableValue targetId = WidgetProperties.text(SWT.Modify).observe(
				tProductLineId);
		IObservableValue targetConnectionString = WidgetProperties.text(
				SWT.Modify).observe(tLocalConnectionString);
		IObservableValue targetUsername = WidgetProperties.text(SWT.Modify)
				.observe(tLocalUsername);
		IObservableValue targetPassword = WidgetProperties.text(SWT.Modify)
				.observe(tLocalPassword);

		dataBindingContext.bindValue(targetId, id);
		dataBindingContext.bindValue(targetConnectionString, connectionString);
		dataBindingContext.bindValue(targetUsername, username);
		dataBindingContext.bindValue(targetPassword, password);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public ConfigurationPagePOJO(FormEditor editor, String id, String title) {
		super(editor, id, title);
		// TODO Auto-generated constructor stub
	}

	public Text gettLocalConnectionString() {
		return tLocalConnectionString;
	}

	public void settLocalConnectionString(Text tLocalConnectionString) {
		this.tLocalConnectionString = tLocalConnectionString;
	}

	public Text gettLocalUsername() {
		return tLocalUsername;
	}

	public void settLocalUsername(Text tLocalUsername) {
		this.tLocalUsername = tLocalUsername;
	}

	public Text gettLocalPassword() {
		return tLocalPassword;
	}

	public void settLocalPassword(Text tLocalPassword) {
		this.tLocalPassword = tLocalPassword;
	}

	public Text gettRemoteConnectionString() {
		return tRemoteConnectionString;
	}

	public void settRemoteConnectionString(Text tRemoteConnectionString) {
		this.tRemoteConnectionString = tRemoteConnectionString;
	}

	public Text gettRemoteUsername() {
		return tRemoteUsername;
	}

	public void settRemoteUsername(Text tRemoteUsername) {
		this.tRemoteUsername = tRemoteUsername;
	}

	public Text gettRemotePassword() {
		return tRemotePassword;
	}

	public void settRemotePassword(Text tRemotePassword) {
		this.tRemotePassword = tRemotePassword;
	}

	public Text gettProductLineId() {
		return tProductLineId;
	}

	public void settProductLineId(Text tProductLineId) {
		this.tProductLineId = tProductLineId;
	}

}
