package productline.plugin.view;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import productline.plugin.ui.providers.ProductLineTreeContentProvider;
import productline.plugin.ui.providers.ProductLineTreeLabelProvider;
import diploma.productline.DaoUtil;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.entity.ProductLine;

public class BrowseProductLineView extends ViewPart {

	private TreeViewer treeViewer;
	private ProductLineDAO plDao;
	private ProductLine parentProductLine;

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
				if (parentProductLine == null) {
					parentProductLine = (ProductLine) o;
					ProductLine[] p = getProductLineChildren((ProductLine) o);
					treeViewer.setInput(p);
				} else if (parentProductLine.getId() != ((ProductLine) o)
						.getId()) {
					parentProductLine = (ProductLine) o;
					ProductLine[] p = getProductLineChildren((ProductLine) o);
					treeViewer.setInput(p);
				}
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(new ProductLineTreeContentProvider());
		treeViewer.setLabelProvider(new ProductLineTreeLabelProvider());

		ISelectionService service = getSite().getWorkbenchWindow()
				.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection();
		if (structured instanceof TreeSelection) {
			Object selected = structured.getFirstElement();
			if (selected instanceof ProductLine) {
				ProductLine productLine = (ProductLine) selected;
				parentProductLine = productLine;
				treeViewer.setInput(getProductLineChildren(productLine));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
