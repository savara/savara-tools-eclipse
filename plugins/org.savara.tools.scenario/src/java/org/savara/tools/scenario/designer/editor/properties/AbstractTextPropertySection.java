/*
 * Copyright 2005-8 Pi4 Technologies Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Change History:
 * 16 Jan 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * This is an abstract property section implementation used for
 * text based properties. This property section supports optional
 * expandable dialog window for editing a larger amount of text.
 */
public abstract class AbstractTextPropertySection 
				extends AbstractDesignerPropertySection {

	public AbstractTextPropertySection(String propName,
			String displayName, String label) {
		super(propName, displayName, label, 0, 50, 0, 100);
	}
	
	public AbstractTextPropertySection(String propName,
			String displayName, String label, int start, int end) {
		super(propName, displayName, label, start, end, 0, 100);
	}

    private ModifyListener m_listener = new ModifyListener() {

        public synchronized void modifyText(ModifyEvent arg0) {
        	
        	SetPropertyCommand command=
    			new SetPropertyCommand();
	    	command.setPropertySource(getPropertySource());
	    	command.setPropertyDescriptor(getPropertyDescriptor());
	    	command.setValue(m_text.getText());
    	
	    	getCommandStack().execute(command);
	    	
            //getPropertySource().setPropertyValue(
				//getPropertyDescriptor().getId(), m_text.getText());
        }
    };
    
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
        Composite composite = parent;
        
        if (isCreateForm()) {
        	composite = getWidgetFactory().createFlatFormComposite(parent);
        }

        FormData data;

        if (isExpandable()) {
        	m_text = getWidgetFactory().createText(composite, "", SWT.MULTI); //$NON-NLS-1$
        } else {
        	m_text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
        }
        
        if (getToolTip() != null) {
        	m_text.setToolTipText(getToolTip());
        }
        
        data = new FormData();
        
        data.width = 60;
        
        data.left = new FormAttachment(getStartPercentage(), getTextGap());
        
        if (isExpandable()) {
            data.height = 50;
        	data.right = new FormAttachment(80, 0);
        } else {
        	data.right = new FormAttachment(getEndPercentage(), 0);        	
        }
        data.top = new FormAttachment(getTopPercentage(), ITabbedPropertyConstants.VSPACE);
        
        m_text.setLayoutData(data);
        m_text.addModifyListener(m_listener);

        CLabel labelLabel = getWidgetFactory()
            .createCLabel(composite, getLabel()+":"); //$NON-NLS-1$
        
        if (getToolTip() != null) {
        	labelLabel.setToolTipText(getToolTip());
        }
        
        data = new FormData();
        data.left = new FormAttachment(getStartPercentage(), 0);
        data.right = new FormAttachment(m_text,
            -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(m_text, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        
        if (isExpandable()) {
	        Button button=
	        	getWidgetFactory().createButton(composite, "...", SWT.PUSH);
	        data = new FormData();
	        data.left = new FormAttachment(82, 0);
	        data.right = new FormAttachment(85, 0);
	        data.top = new FormAttachment(getTopPercentage(), ITabbedPropertyConstants.VSPACE);
	        button.setLayoutData(data);
	        
	        button.addSelectionListener(new SelectionAdapter() {
	            /* (non-Javadoc)
	             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            public void widgetSelected(SelectionEvent event) {
	            	// Remove the button's focus listener since it's guaranteed
	            	// to lose focus when the dialog opens
	            	//button.removeFocusListener(getButtonFocusListener());
	                
	            	String newValue = openDialogBox();
	            	
	            	if (newValue != null) {
	            		m_text.setText(newValue);
	            	}
	            }
	        });	        
        }
    }
    
    /**
     * This method returns the principle widget used for obtaining
     * the value of the property.
     * 
     * @return The widget
     */
    protected org.eclipse.swt.widgets.Control getWidget() {
    	return(m_text);
    }
    
    public boolean isExpandable() {
    	return(m_expandable);
    }
    
    public void setExpandable(boolean expand) {
    	m_expandable = expand;
    }
    
    public void dispose() {
    	super.dispose();
    	
    	if (m_text != null && m_text.isDisposed() == false) {
    		m_text.removeModifyListener(m_listener);
    	}
    }
    
    public void refresh() {
    	super.refresh();
    	
		Object val=getPropertySource().getPropertyValue(
						getPropertyDescriptor().getId());
		
		if (val instanceof String &&
				m_text.getText().equals(val) == false) {
			m_text.removeModifyListener(m_listener);
			m_text.setText((String)val);
			m_text.addModifyListener(m_listener);
		}
    }
    
	/**
	 * Opens a dialog box under the given parent control and returns the
	 * dialog's value when it closes, or <code>null</code> if the dialog
	 * was cancelled or no selection was made in the dialog.
	 * <p>
	 * This framework method must be implemented by concrete subclasses.
	 * It is called when the user has pressed the button and the dialog
	 * box must pop up.
	 * </p>
	 *
	 * @param cellEditorWindow the parent control cell editor's window
	 *   so that a subclass can adjust the dialog box accordingly
	 * @return the selected value, or <code>null</code> if the dialog was 
	 *   cancelled or no selection was made in the dialog
	 */
	protected String openDialogBox() {
		String ret=null;
		
		try {
			TextRegionDialog dialog=new TextRegionDialog(m_text.getShell());
	        
			dialog.setTitle(getPropertyName());
			dialog.setText(m_text.getText());
			
			// Required to cause initialization from the content provider
			//dialog.setInput(this);
			
			if (dialog.open() == Window.OK) {
				
				ret = dialog.getText();
				
				//if (dialog.getResult().length > 0) {
				//	ret = dialog.getResult()[0];
				//}
			}
		
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
		return(ret);
	}

    private boolean m_expandable=false;
    private Text m_text; 
    
    public class TextRegionDialog extends Dialog {
    	
    	public TextRegionDialog(Shell parentShell) {
    		super(parentShell);
    	}
    	
        /*
         * (non-Javadoc) Method declared in Window.
         */
        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            if (m_title != null) {
                shell.setText(m_title);
            }
        }

    	protected Control createDialogArea(Composite parent) {
    		Composite composite = (Composite) super.createDialogArea(parent);
    		
    		m_textRegion=new Text(composite,
    				SWT.MULTI|SWT.V_SCROLL|SWT.WRAP);
    		
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.heightHint = convertHeightInCharsToPixels(heightInChars);
            gd.widthHint = convertWidthInCharsToPixels(widthInChars);

            m_textRegion.setLayoutData(gd);
            
            if (m_text != null) {
            	m_textRegion.setText(m_text);
            }
            
            m_textRegion.addModifyListener(new ModifyListener() {
            	
            	public void modifyText(ModifyEvent me) {
            		m_text = m_textRegion.getText();
            	}
            });
            
    		return(composite);
    	}
    	
    	public void setTitle(String title) {
    		m_title = title;
    	}
    	
    	public void setText(String text) {
    		m_text = text;
    		if (m_textRegion != null) {
    			m_textRegion.setText(text);
    		}
    	}
    	
    	public String getText() {
    		return(m_text);
    	}
    	
    	private String m_title=null;
    	private String m_text=null;
        private int widthInChars = 75;
        private int heightInChars = 20;
        private Text m_textRegion;
    }    
}
