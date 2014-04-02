package productline.plugin.view;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import productline.plugin.internal.WhereUsedCode;
import productline.plugin.ui.providers.WhereUsedContentProvider;
import productline.plugin.ui.providers.WhereUsedLabelProvider;

public class WhereUsedCodeView extends ViewPart {
	private ListViewer listViewer;
	private final String NO_DATA = "No data found";

	public WhereUsedCodeView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		listViewer = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		List list = listViewer.getList();
		listViewer.setContentProvider(new WhereUsedContentProvider());
		listViewer.setLabelProvider(new WhereUsedLabelProvider());
		listViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object obj = ((IStructuredSelection) listViewer.getSelection())
						.getFirstElement();

				if (obj instanceof WhereUsedCode) {
					openEditor((WhereUsedCode) obj);
				}
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void refresh(Set<WhereUsedCode> set) {
		setListViewerInput(set);
	}

	private void setListViewerInput(Set<?> set) {
		if (set.size() > 0) {
			listViewer.setInput(set);
		} else {
			listViewer.setInput(NO_DATA);
		}
	}

	private void openEditor(WhereUsedCode element) {
		IFile file = element.getFile();
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		HashMap map = new HashMap();
		map.put(IMarker.LINE_NUMBER, element.getLine());
		IMarker marker;
		try {
			marker = file.createMarker(IMarker.TEXT);

			marker.setAttributes(map);
			// page.openEditor(marker); //2.1 API
			IDE.openEditor(page, marker); // 3.0 API
			marker.delete();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
