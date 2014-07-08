package productline.plugin.ui.listener;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import productline.plugin.internal.DefaultMessageDialog;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ResourceDao;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Resource;

public class AddPackageButtonListener implements Listener {

	private IProject project;
	private Properties properties;
	private ListViewer listViewerPackage;
	private BaseProductLineEntity currentSelectedObject;

	public AddPackageButtonListener(IProject project, Properties properties,
			ListViewer listViewerPackage,
			BaseProductLineEntity currentSelectedObject) {
		super();
		this.project = project;
		this.properties = properties;
		this.listViewerPackage = listViewerPackage;
		this.currentSelectedObject = currentSelectedObject;
	}

	@Override
	public void handleEvent(Event event) {
		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.MULTI);
		String workspaceLoc = project.getWorkspace().getRoot().getLocation()
				.toOSString();
		String projectLoc = project.getFullPath().toOSString();
		fileDialog.setFilterPath(workspaceLoc + projectLoc);

		Set<String> existingFiles = new HashSet<>();

		if (fileDialog.open() != null) {
			try (Connection con = DaoUtil.connect(properties)) {
				ResourceDao rDao = new ResourceDao();

				String[] names = fileDialog.getFileNames();
				Element element = (Element) currentSelectedObject;
				Set<Resource> resources = element.getResources();

				for (int i = 0, n = names.length; i < n; i++) {
					StringBuffer buf = new StringBuffer(
							fileDialog.getFilterPath());
					if (buf.charAt(buf.length() - 1) != File.separatorChar)
						buf.append(File.separatorChar);
					buf.append(names[i]);
					Resource r = new Resource();
					r.setName(names[i]);
					CharSequence projectLocation = workspaceLoc + projectLoc;
					r.setRelativePath(buf.toString().replace(projectLocation,
							""));
					r.setFullPath(buf.toString());
					r.setElement(element);
					if (resources == null) {
						resources = new HashSet<Resource>();
					}
					int rId = rDao.save(r, con);
					if (rId == -1) {
						existingFiles.add(r.getRelativePath());
					} else {
						r.setId(rId);
						resources.add(r);
					}

				}
				if (existingFiles.size() > 0) {
					StringBuffer message = new StringBuffer(
							"This files are already added:\n");
					for (String f : existingFiles) {
						message.append(f).append("\n");
					}
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Existing files", message.toString());
				}
				listViewerPackage.setInput(element.getResources());
			} catch (ClassNotFoundException e) {
				DefaultMessageDialog.driversNotFoundDialog("H2");
				e.printStackTrace();
			} catch (SQLException e) {
				DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
				e.printStackTrace();
			}
		}

	}

}
