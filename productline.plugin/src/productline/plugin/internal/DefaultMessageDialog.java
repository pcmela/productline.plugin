package productline.plugin.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class DefaultMessageDialog {

	public static void driversNotFoundDialog(String database) {
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Drivers not found",
				"There are no drivers for the " + database + " database");
	}
	
	public static void sqlExceptionDialog(String message){
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "SQL Exception",
				message);
	}
}
