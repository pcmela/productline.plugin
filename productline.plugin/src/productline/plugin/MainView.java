package productline.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import diploma.productline.configuration.YamlExtractor;
import diploma.productline.entity.Element;
import diploma.productline.entity.Module;
import diploma.productline.entity.ProductLine;
import diploma.productline.entity.Variability;
import productline.plugin.ui.ProductLineTreeContentProvider;
import productline.plugin.ui.ProductLineTreeLabelProvider;

public class MainView {

private TreeViewer viewer;
	
	private ExpandBar treeExpandBar;
	private ExpandBar detailExpandBar;

	@Inject
    public void createPartControl(Composite parent) {
		
        FillLayout mainLayout = new FillLayout();        
        parent.setLayout(mainLayout);
        
        createTreePanel(parent);
        createDetailPanel(parent);
        
                
        viewer = new TreeViewer((Composite)treeExpandBar.getItem(0).getControl(), SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ProductLineTreeContentProvider());
        viewer.setLabelProvider(new ProductLineTreeLabelProvider());
        
        String path = "C:\\Users\\IBM_ADMIN\\Desktop\\Neon.yaml";
        ProductLine productLine = YamlExtractor.extract(path);
        
        /*ProductLine productLine = new ProductLine();
        Module module1 = new Module();
        Module module2 = new Module();
        Variability var1 = new Variability();
        Variability var2 = new Variability();
        Element el = new Element();
        
        productLine.setName("Prod1");
        productLine.setId(1l);
        
        module1.setName("Module1");
        module1.setId("1");
        module2.setName("Module2");
        module2.setId("2");
        List<Module> m = new ArrayList<Module>();
        m.add(module1);
        m.add(module2);
        
        var1.setId("1");
        var1.setName("Variability1");
        var2.setId("2");
        var2.setName("Variability2");
        List<Variability> v = new ArrayList<Variability>();
        v.add(var1);
        v.add(var2);
        
        
        el.setId("1");
        el.setName("Element1");
        List<Element> e = new ArrayList<Element>();
        e.add(el);
        
        productLine.setModules(m);
        module1.setVariabilities(v);
        module1.setElements(e);*/
        
        viewer.setInput(new Object[] {productLine});
        viewer.expandAll();
        
        
        
    }
	
	@Focus
	public void setFocus(){
		
	}
	
	private void createTreePanel(Composite parent){
		treeExpandBar = new ExpandBar(parent, SWT.NONE);
		treeExpandBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        Composite composite = new Composite(treeExpandBar, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        
        ExpandItem item = new ExpandItem(treeExpandBar, SWT.NONE, 0);
        item.setText("Product Line");
        //item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        item.setControl(composite);
        item.setHeight(200);
        item.setExpanded(true);
	}
	
	
	private void createDetailPanel(Composite parent){
		detailExpandBar = new ExpandBar(parent, SWT.NONE);
		detailExpandBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        Composite expComp = new Composite(detailExpandBar, SWT.NONE);
        GridLayout expLayout = new GridLayout();
        expLayout.marginLeft = expLayout.marginTop = expLayout.marginRight = expLayout.marginBottom = 10;
        expLayout.verticalSpacing = 10;
        expComp.setLayout(expLayout);
        
        ExpandItem expItem = new ExpandItem(detailExpandBar, SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
        expItem.setControl(expComp);
        expItem.setText("Details");
        expItem.setHeight(expComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        expItem.setExpanded(true);
	}
}
