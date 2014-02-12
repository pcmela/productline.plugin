package productline.plugin.ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import productline.plugin.ProductLineUtils;
import productline.plugin.internal.CreateCustomeLine;
import productline.plugin.ui.providers.ProductLineTreeContentProvider;
import productline.plugin.ui.providers.ProductLineTreeLabelProvider;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.BaseProductLineEntity;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;

public class CreateNewCustomLineDialog extends TitleAreaDialog {

	private ProductLine productLine;
	private String destinationPath;
	private IProject project;
	private CheckboxTreeViewer checkboxTreeViewer;
	private Properties properties;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CreateNewCustomLineDialog(Shell parentShell,
			ProductLine productLine, String destinationPath, IProject project,
			Properties properties) {
		super(parentShell);
		this.productLine = productLine;
		this.destinationPath = destinationPath;
		this.project = project;
		this.properties = properties;
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
				ProductLine productLine = SerializationUtils
						.clone((ProductLine) checkboxTreeViewer.getInput());
				productLine.setParent((ProductLine) checkboxTreeViewer
						.getInput());
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

				productLine = ProductLineUtils.refreshRelations(productLine);
				CreateCustomeLine custom = new CreateCustomeLine(productLine,
						project, "C:\\Users\\IBM_ADMIN\\Desktop\\customeLine");
				try {
					custom.create();
					ProductLineDAO pDao = new ProductLineDAO();
					try (Connection con = DaoUtil.connect(properties)) {
						pDao.createAll(productLine, con);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (JavaModelException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(container, SWT.NONE);

		checkboxTreeViewer = new CheckboxTreeViewer(container, SWT.BORDER);

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
		checkboxTreeViewer.expandAll();
		final Tree tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		checkboxTreeViewer.getTree().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					if ((event.item.getData() instanceof Module)
							&& !(((Module) event.item.getData())).isVariable()) {
						event.detail = SWT.NONE;
						event.type = SWT.None;
						event.doit = false;
						try {
							tree.setRedraw(false);
							TreeItem item = (TreeItem) event.item;
							item.setChecked(!item.getChecked());
						} finally {
							tree.setRedraw(true);
						}
					} else {
						// ITreeSelection selection =
						// ((ITreeSelection)event.item);
						ITreeContentProvider tcp = (ITreeContentProvider) checkboxTreeViewer
								.getContentProvider();
						Object child = event.item.getData();
						Object parent = tcp.getParent(child);
						if (parent != null) {
							checkboxTreeViewer.setChecked(parent, true);
							parent = tcp.getParent(parent);
							if (parent != null) {
								checkboxTreeViewer.setChecked(parent, true);
							}
						}
					}
				}

			}
		});

		setBackgroundAndCheckedForMandatoryFields(tree);

		return area;
	}

	private void setBackgroundAndCheckedForMandatoryFields(Tree tree) {
		List<TreeItem> allItems = new ArrayList<TreeItem>();

		getAllItemsFromTree(tree, allItems);
		for (Module m : productLine.getModules()) {
			for (TreeItem i : allItems) {
				BaseProductLineEntity obj = (BaseProductLineEntity) i.getData();
				if (obj instanceof Module) {
					if (obj.getName().equals(m.getName()) && !m.isVariable()) {
						i.setBackground(Display.getDefault().getSystemColor(
								SWT.COLOR_GRAY));
					}
				}
			}
		}

		for (Module m : productLine.getModules()) {
			if (!m.isVariable()) {
				checkboxTreeViewer.setChecked(m, true);
			}
		}
	}

	private void getAllItemsFromTree(Tree tree, List<TreeItem> allItems) {
		for (TreeItem item : tree.getItems()) {
			allItems.add(item);
			getAllItemsFromTree(item, allItems);
		}
	}

	private void getAllItemsFromTree(TreeItem currentItem,
			List<TreeItem> allItems) {
		TreeItem[] children = currentItem.getItems();

		for (int i = 0; i < children.length; i++) {
			allItems.add(children[i]);

			getAllItemsFromTree(children[i], allItems);
		}
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
