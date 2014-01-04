package productline.plugin.editor;

/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;



/**
 * @author Eugene Kuleshov
 */
public class SearchControl extends ControlContribution {
  private final IManagedForm managedForm;

  Text searchText;

  public SearchControl(String id, IManagedForm managedForm) {
    super(id);
    this.managedForm = managedForm;
  }

  public Text getSearchText() {
    return searchText;
  }

  private boolean isMac() {
    String os = System.getProperty("os.name"); //$NON-NLS-1$
    return os != null && os.startsWith("Mac"); //$NON-NLS-1$
  }

  protected Control createControl(Composite parent) {
    if(parent instanceof ToolBar) {
      // the FormHeading class sets the toolbar cursor to hand for some reason,
      // we change it back so the input control can use a proper I-beam cursor
      parent.setCursor(null);
    }

    FormToolkit toolkit = managedForm.getToolkit();
    Composite composite = toolkit.createComposite(parent);

    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    //gross, but on the Mac the search controls are cut off on the bottom, 
    //so they need to be bumped up  a little. other OSs are fine.
    if(isMac()) {
      layout.marginHeight = -1;
    }
    layout.verticalSpacing = 0;
    composite.setLayout(layout);
    composite.setBackground(null);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    Control label = toolkit.createLabel(composite, "Filter");
    label.setBackground(null);

    searchText = toolkit.createText(composite, "", SWT.FLAT | SWT.SEARCH); //$NON-NLS-1$
    searchText.setData(FormToolkit.TEXT_BORDER, Boolean.TRUE);

    searchText.setLayoutData(new GridData(200, -1));
    ToolBar cancelBar = new ToolBar(composite, SWT.FLAT);

    final ToolItem clearToolItem = new ToolItem(cancelBar, SWT.NONE);
    clearToolItem.setEnabled(false);
    clearToolItem.setImage(PlatformUI.getWorkbench().getSharedImages()
    		.getImage(ISharedImages.IMG_ETOOL_CLEAR));
    clearToolItem.setDisabledImage(PlatformUI.getWorkbench()
    		.getSharedImages().getImage(ISharedImages.IMG_ETOOL_CLEAR_DISABLED));
    clearToolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        searchText.setText(""); //$NON-NLS-1$
      }
    });

    searchText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        clearToolItem.setEnabled(searchText.getText().length() > 0);
      }
    });

    toolkit.paintBordersFor(composite);

    return composite;
  }

}
