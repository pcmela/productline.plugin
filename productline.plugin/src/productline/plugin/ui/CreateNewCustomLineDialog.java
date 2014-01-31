package productline.plugin.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import productline.plugin.internal.CreateCustomeLine;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class CreateNewCustomLineDialog extends TitleAreaDialog {

	private ProductLine productLine;
	private String destinationPath;
	private IProject project;
	private CheckboxTreeViewer checkboxTreeViewer;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CreateNewCustomLineDialog(Shell parentShell,
			ProductLine productLine, String destinationPath, IProject project) {
		super(parentShell);
		this.productLine = productLine;
		this.destinationPath = destinationPath;
		this.project = project;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Product Line: " + this.productLine.getName());

		Button b = new Button(container, SWT.NONE);
		b.setText("Call get Elements");
		b.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ProductLine productLine = (ProductLine) checkboxTreeViewer
						.getInput();
				productLine.getModules().clear();

				Set<Module> modules = new HashSet<>();
				Object[] elements = checkboxTreeViewer.getCheckedElements();

				for (Object o : elements) {
					if (o instanceof Module) {
						Module m = (Module) o;
						m.getVariabilities().clear();
						m.getElements().clear();
						modules.add(m);
					}
				}

				for (Object o : elements) {
					if (o instanceof Variability) {
						Variability v = (Variability) o;
						for (Module m : modules) {
							if (m.equals(v.getModule())) {
								m.getVariabilities().add(v);
								break;
							}
						}
					} else if (o instanceof Element) {
						Element e = (Element) o;
						for (Module m : modules) {
							if (m.equals(e.getModule())) {
								m.getElements().add(e);
								break;
							}
						}
					}
				}

				productLine.getModules().addAll(modules);

				CreateCustomeLine custom = new CreateCustomeLine(productLine,
						project, "C:\\Users\\IBM_ADMIN\\Desktop\\customeLine");
				try {
					custom.create();
				} catch (JavaModelException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(container, SWT.NONE);

		checkboxTreeViewer = new CheckboxTreeViewer(container, SWT.BORDER);
		Tree tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				// If the item is checked . . .
				if (event.getChecked()) {
					// . . . check all its children
					checkboxTreeViewer.setSubtreeChecked(event.getElement(),
							true);
				}
			}
		});

		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				// If the item is checked . . .
				if (!event.getChecked()) {
					// . . . check all its children
					checkboxTreeViewer.setSubtreeChecked(event.getElement(),
							false);
				}
			}
		});

		checkboxTreeViewer
				.setContentProvider(new ProductLineTreeContentProvider());
		checkboxTreeViewer.setLabelProvider(new ProductLineTreeLabelProvider());
		checkboxTreeViewer.setInput(this.productLine);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
