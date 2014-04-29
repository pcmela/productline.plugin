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
	
	public static void ioException(String message){
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "IO Exception",
				message);
	}
	
	public static void fileNotFoundException(String path){
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"File not found.", "YAML configuration file "
						+ path
						+ " does not exists.");
	}
	
	public static void yamlIsNotFileException(String path){
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"File is not a file.", "YAML configuration file "
						+ path
						+ " is not a file.");
	}
	
	public static void yamlIsEmptyException(String path){
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"No content available.", "YAML configuration file "
						+ path
						+ " is empty.");
	}
}
