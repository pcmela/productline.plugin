package productline.plugin.view;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.ui.providers.ProductLineStyledLabelProvider;
import productline.plugin.ui.providers.ProductLineTreeContentProvider;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.ProductLine;

public class BrowseProductLineView extends ViewPart {

	private static Logger LOG = LoggerFactory.getLogger(BrowseProductLineView.class);
	
	private TreeViewer treeViewer;
	private ProductLineDAO plDao;
	private ProductLine parentProductLine;
	private final String NO_DATA = "No data found";

	public BrowseProductLineView() {
		super();
		plDao = new ProductLineDAO();
	}

	ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection sel) {
			if (!(sel instanceof IStructuredSelection))
				return;
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object o = ss.getFirstElement();
			if (o instanceof ProductLine) {
				// if (parentProductLine == null) {
				parentProductLine = (ProductLine) o;
				ProductLine[] p = getProductLineChildren((ProductLine) o);
				if(p != null && p.length > 0){
					treeViewer.setInput(p);
				}else{
					treeViewer.setInput(NO_DATA);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(new ProductLineTreeContentProvider());
		treeViewer.setLabelProvider(new ProductLineStyledLabelProvider());
		treeViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return e1.toString().compareTo(e2.toString());
			};
		});

		ISelectionService service = getSite().getWorkbenchWindow()
				.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection();
		if (structured instanceof TreeSelection) {
			Object selected = structured.getFirstElement();
			if (selected instanceof ProductLine) {
				ProductLine productLine = (ProductLine) selected;
				parentProductLine = productLine;
				ProductLine[] p = getProductLineChildren(productLine);
				
				if(p != null && p.length > 0){
					treeViewer.setInput(p);
				}else{
					treeViewer.setInput(NO_DATA);
				}
			}
		}
		System.out.println("ahoj");
		getSite().getPage().addSelectionListener(listener);
	}

	private ProductLine[] getProductLineChildren(ProductLine productLine) {
		try (Connection con = DaoUtil.connect(productLine
				.getDatabaseProperties())) {
			return plDao.getProductLineByParent(productLine.getId(), con);
		} catch (ClassNotFoundException e) {
			DefaultMessageDialog.driversNotFoundDialog("H2");
			LOG.error(e.getMessage());
		} catch (SQLException e) {
			DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
			LOG.error(e.getMessage());
		}
		return new ProductLine[0];
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(listener);
	}

}
