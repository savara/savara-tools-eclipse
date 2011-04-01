/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * This is an abstract property section implementation used for
 * boolean based properties.
 */
public class DefaultCheckBoxPropertySection 
				extends AbstractDesignerPropertySection {

	private Button m_checkBox=null;
	
	public DefaultCheckBoxPropertySection(String propName,
			String displayName, String label) {
		super(propName, displayName, label, 0, 50, 0, 0);
	}
	
	public DefaultCheckBoxPropertySection(String propName,
			String displayName, String label, int start, int end) {
		super(propName, displayName, label, start, end, 0, 0);
	}

    private SelectionListener m_listener = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			widgetSelected(arg0);
		}

		public void widgetSelected(SelectionEvent arg0) {
        	SetPropertyCommand command=
    			new SetPropertyCommand();
	    	command.setPropertySource(getPropertySource());
	    	command.setPropertyDescriptor(getPropertyDescriptor());
	    	command.setValue(m_checkBox.getSelection());
    	
	    	getCommandStack().execute(command);
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

       	m_checkBox = getWidgetFactory().createButton(composite, getLabel(), SWT.CHECK); //$NON-NLS-1$
        
        if (getToolTip() != null) {
        	m_checkBox.setToolTipText(getToolTip());
        }
        
        data = new FormData();
        
        data.width = 200;
        data.left = new FormAttachment(getStartPercentage(), getTextGap());
       	data.right = new FormAttachment(getEndPercentage(), 0);        	
        data.top = new FormAttachment(getTopPercentage(), ITabbedPropertyConstants.VSPACE);
        
        m_checkBox.setLayoutData(data);
        m_checkBox.addSelectionListener(m_listener);
    }
    
    /**
     * This method returns the principle widget used for obtaining
     * the value of the property.
     * 
     * @return The widget
     */
    protected org.eclipse.swt.widgets.Control getWidget() {
    	return(m_checkBox);
    }
    
    public void dispose() {
    	super.dispose();
    	
    	if (m_checkBox != null && m_checkBox.isDisposed() == false) {
    		m_checkBox.removeSelectionListener(m_listener);
    	}
    }
    
    public void refresh() {
    	super.refresh();
    	
		Object val=getPropertySource().getPropertyValue(
						getPropertyDescriptor().getId());
		
		if (val instanceof Boolean &&
				(m_checkBox.getSelection() != ((Boolean)val).booleanValue())) {
			m_checkBox.removeSelectionListener(m_listener);
			m_checkBox.setSelection(((Boolean)val).booleanValue());
			m_checkBox.addSelectionListener(m_listener);
		}
    }    
}
