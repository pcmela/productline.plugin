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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import productline.plugin.ProductLineUtils;
import productline.plugin.internal.CreateCustomeLine;
import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.internal.ProductLineTreeComparator;
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
	private Text tNewName;
	private String newName = "";
	private Button okButton;
	private Text tPath;
	private Button bPath;

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
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblParentNameStatic = new Label(container, SWT.NONE);
		lblParentNameStatic.setText("Product Line:");

		Label lblParentName = new Label(container, SWT.NONE);
		lblParentName.setText(this.productLine.getName());
		GridData dParentName = new GridData();
		dParentName.horizontalSpan = 2;
		lblParentName.setLayoutData(dParentName);

		Label lblNewName = new Label(container, SWT.NONE);
		lblNewName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewName.setText("Name of custome line:");

		tNewName = new Text(container, SWT.BORDER);
		tNewName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		tNewName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateForm();
			}
		});
		new Label(container, SWT.NONE);
		tPath = new Text(container, SWT.BORDER);
		tPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		tPath.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateForm();
			}
		});

		bPath = new Button(container, SWT.PUSH);
		bPath.setText("Browse");
		bPath.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog dirDialog = new DirectoryDialog(new Shell());
				dirDialog.setText("Select directory for Custome Line");
				String selectedDir = dirDialog.open();
				tPath.setText(selectedDir);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		GridData dSpace = new GridData();
		dSpace.horizontalSpan = 3;
		new Label(container, SWT.NONE).setLayoutData(dSpace);

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
		checkboxTreeViewer.setComparator(new ProductLineTreeComparator());
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

	private void validateForm() {
		newName = tNewName.getText();
		boolean valid = false;
		if (tNewName.getText().trim().equals("")) {
			valid = false;
		} else {
			valid = true;
		}

		if (tPath.getText().trim().equals("")) {
			valid = false;
		} else {
			valid = true;
		}

		okButton.setEnabled(valid);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 600);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {

		try {
			Set<String> existingNames = ProductLineDAO.getNamesOfChild(
					DaoUtil.connect(properties), productLine.getId());
			if (existingNames.contains(tNewName.getText())) {
				MessageDialog.openError(new Shell(), "Duplicate name",
						"Name \"" + tNewName.getText()
								+ "\" of Custome Line already exist.");
				return;
			}
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			e.printStackTrace();
		} catch (SQLException e) {
			DefaultMessageDialog.ioException(e.getMessage());
			e.printStackTrace();
		}

		Job job = createCustomeLineJob();

		job.setUser(true);
		job.schedule();

		super.okPressed();
	}

	private Job createCustomeLineJob() {
		final String path = tPath.getText();
		final ProductLine productLine = SerializationUtils
				.clone((ProductLine) checkboxTreeViewer.getInput());
		productLine.setName(newName);
		productLine.setParent((ProductLine) checkboxTreeViewer.getInput());
		productLine.getModules().clear();

		final Set<Module> modules = new HashSet<>();
		final Object[] elements = checkboxTreeViewer.getCheckedElements();

		Job job = new Job("Creating new custome line...") {
			protected IStatus run(IProgressMonitor monitor) {

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

				ProductLine refreshedProductLine = ProductLineUtils
						.refreshRelations(productLine);
				CreateCustomeLine custom = new CreateCustomeLine(
						refreshedProductLine, project, path);
				try {
					custom.create();
					ProductLineDAO pDao = new ProductLineDAO();
					try (Connection con = DaoUtil.connect(properties)) {
						pDao.createAll(refreshedProductLine, con);
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

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(null,
								"Created Custome Line",
								"Creation of new Custome Line is done!");
					}
				});
				return Status.OK_STATUS;
			}
		};

		return job;
	}

}
