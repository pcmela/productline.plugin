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
import productline.plugin.ui.providers.WhereUsedContentProvider;
import productline.plugin.ui.providers.WhereUsedLabelProvider;

public class WhereUsedView extends ViewPart {
	private WhereUsedDAO wDao = new WhereUsedDAO();
	ListViewer listViewer;
	
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
				listViewer.setInput(records);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(obj instanceof Variability){
			
		}else if(obj instanceof Element){
			
		}
	}

}
