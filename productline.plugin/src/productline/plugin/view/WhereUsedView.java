package productline.plugin.view;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;

import diploma.productline.DaoUtil;
import diploma.productline.dao.WhereUsedDAO;
import diploma.productline.dao.WhereUsedRecord;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.Variability;
import productline.plugin.internal.DefaultMessageDialog;
import productline.plugin.ui.providers.WhereUsedContentProvider;
import productline.plugin.ui.providers.WhereUsedLabelProvider;

public class WhereUsedView extends ViewPart {
	private WhereUsedDAO wDao = new WhereUsedDAO();
	private ListViewer listViewer;
	private final String NO_DATA = "No data found";
	
	public WhereUsedView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
		listViewer = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		List list = listViewer.getList();
		listViewer.setContentProvider(new WhereUsedContentProvider());
		listViewer.setLabelProvider(new WhereUsedLabelProvider());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void refresh(Object obj){
		if(obj instanceof Module){
			Module m = (Module)obj;
			try(Connection con = DaoUtil.connect(m.getProductLine().getDatabaseProperties())){
				Set<WhereUsedRecord> records = wDao.getModules(m.getProductLine(), m, con);
				setListViewerInput(records);
			} catch (ClassNotFoundException e) {
				DefaultMessageDialog.driversNotFoundDialog("H2");
				e.printStackTrace();
			} catch (SQLException e) {
				DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
				e.printStackTrace();
			}
		}else if(obj instanceof Variability){
			Variability v = (Variability)obj;
			try(Connection con = DaoUtil.connect(v.getModule().getProductLine().getDatabaseProperties())){
				Set<WhereUsedRecord> records = wDao.getVariabilities(v.getModule().getProductLine(), v, con);
				setListViewerInput(records);
			} catch (ClassNotFoundException e) {
				DefaultMessageDialog.driversNotFoundDialog("H2");
				e.printStackTrace();
			} catch (SQLException e) {
				DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
				e.printStackTrace();
			}
		}else if(obj instanceof Element){
			Element el = (Element)obj;
			try(Connection con = DaoUtil.connect(el.getModule().getProductLine().getDatabaseProperties())){
				Set<WhereUsedRecord> records = wDao.getElements(el.getModule().getProductLine(), el, con);
				setListViewerInput(records);
			} catch (ClassNotFoundException e) {
				DefaultMessageDialog.driversNotFoundDialog("H2");
				e.printStackTrace();
			} catch (SQLException e) {
				DefaultMessageDialog.sqlExceptionDialog(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void setListViewerInput(Set<WhereUsedRecord> set){
		if(set.size() > 0){
			listViewer.setInput(set);
		}else{
			listViewer.setInput(NO_DATA);
		}
	}

}
