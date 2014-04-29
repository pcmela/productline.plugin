package productline.plugin.ui;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import productline.plugin.ui.providers.ProductLineListContentProvider;
import productline.plugin.ui.providers.ProductLineListLabelProvider;
import diploma.productline.entity.ProductLine;

public class ProductLineIdDialog extends Dialog{

	private Set<ProductLine> productLines;	
	private Text control;
	private ListViewer listViewer;
	private IStructuredSelection actualSelection;
	
	public ProductLineIdDialog(Shell parentShell, Set<ProductLine> productLines, Text control) {
		super(parentShell);
		this.productLines = productLines;
		this.control = control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		listViewer = new ListViewer(area, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setLabelProvider(new ProductLineListLabelProvider());
		listViewer.setContentProvider(new ProductLineListContentProvider());
		listViewer.setInput(productLines);	
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				actualSelection = (IStructuredSelection) event.getSelection();				
			}
		});
		
		return null;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		if(actualSelection != null){
			Object obj = actualSelection.getFirstElement();
			if(obj instanceof ProductLine){
				control.setText(String.valueOf(((ProductLine) obj).getId()));
			}
		}
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
