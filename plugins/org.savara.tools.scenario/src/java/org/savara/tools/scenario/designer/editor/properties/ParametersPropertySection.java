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

import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.savara.scenario.model.Parameter;

public class ParametersPropertySection extends AbstractPropertySection {
	
	private ParameterListPropertySection m_parameters=
		new ParameterListPropertySection("parameter",
				"Parameters", "Parameters", 0, 100);

	public ParametersPropertySection() {
	}
	
    public void aboutToBeShown() {
    	super.aboutToBeShown();
    	
    	m_parameters.aboutToBeShown();
    }
    
    public void aboutToBeHidden() {
    	super.aboutToBeHidden();

    	m_parameters.aboutToBeHidden();
    }
    
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
	
        Composite composite = getWidgetFactory()
					.createFlatFormComposite(parent);

        m_parameters.setToolTip("The parameters");
        m_parameters.setTextGap(100);
        m_parameters.createControls(composite,
        		aTabbedPropertySheetPage);        
    }
    
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        
        m_parameters.setInput(part, selection);
    }
    
    public void refresh() {    	
    	super.refresh();
    	
    	m_parameters.refresh();
    }
    
    public int getMinimumHeight() {
    	return(400);
    }
    
    public void dispose() {    	
    	super.dispose();
    	
    	m_parameters.dispose();
    }
	
	public class ParameterListPropertySection extends AbstractListPropertySection {
		
		public ParameterListPropertySection(String property, String displayName,
				String label, int start, int end) {
			super(property, displayName, label, start, end);
		}

		protected Object createNewObject() {
			Parameter param=new Parameter();
			param.setType("PType"+System.currentTimeMillis());
			param.setValue("http://ptype"+System.currentTimeMillis());
			
			ParameterEditor pe=new ParameterEditor(getPart().getSite().getShell());
			
			pe.setParameter(param);
			
			return((Parameter)pe.open());
		}
		
	    protected String getDisplayValue(Object sourceValue) {
	    	String ret="";

	    	if (sourceValue instanceof Parameter) {
	    		Parameter p=(Parameter)sourceValue;
	    		
	    		if (p.getType() != null && p.getType().trim().length() > 0) {
	    			ret += p.getType();
	    		}

	    		ret += " [";
	    		
	    		if (p.getValue() != null && p.getValue().trim().length() > 0) {
	    			ret += p.getValue();
	    		}

	    		ret += "]";
	    	}
	    	
	    	return(ret);
	    }
	}
	
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);

		ParameterEditor pe=new ParameterEditor(shell);
		
		pe.open();
	}
	
	public static class ParameterEditor extends Dialog {
		private Parameter m_parameter=null;
		private Combo m_type=null;
		private Text m_value=null;
		private boolean m_ok=false;
		
        public ParameterEditor(Shell parent, int style) {
            super (parent, style);
	    }
        
	    public ParameterEditor(Shell parent) {
	    	this (parent, 0);
	    }
	    
	    public void setParameter(Parameter p) {
	    	m_parameter = p;
	    }
	    
	    public Object open() {
	    	Shell parent = getParent();
	    	
	    	final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	    	dialog.setText(getText());
	    	
	    	//Shell dialog = new Shell (dialog, SWT.DIALOG_TRIM);
	    	Label label1 = new Label (dialog, SWT.NONE);
	    	label1.setText("Type");
	    	
	    	m_type = new Combo(dialog, SWT.NONE);
	    	
	    	if (m_parameter != null && m_parameter.getType() != null &&
	    			m_parameter.getType().trim().length() > 0) {
	    		m_type.setText(m_parameter.getType());
	    	}
	    	
	    	Label label2 = new Label(dialog, SWT.NONE);
	    	label2.setText("Value");
	    	
	    	m_value = new Text(dialog, SWT.NONE);

	    	if (m_parameter != null && m_parameter.getValue() != null &&
	    			m_parameter.getValue().trim().length() > 0) {
	    		m_value.setText(m_parameter.getValue());
	    	}
	    	
	    	Button okButton = new Button (dialog, SWT.PUSH);
	    	okButton.setText ("&OK");
	    	Button cancelButton = new Button (dialog, SWT.PUSH);
	    	cancelButton.setText ("&Cancel");
	    	
	    	FormLayout form = new FormLayout();
	    	form.marginWidth = form.marginHeight = 8;
	    	dialog.setLayout(form);
	    	
	    	FormData label1Data = new FormData();
	    	label1Data.top = new FormAttachment(4);
	    	label1Data.width = 40;
	    	label1Data.height = 30;
	    	label1.setLayoutData(label1Data);
	    	
	    	FormData typeData = new FormData();
	    	typeData.left = new FormAttachment(label1, 8);
	    	typeData.width = 400;
	    	typeData.height = 30;
	    	m_type.setLayoutData(typeData);
	    	
	    	FormData label2Data = new FormData();
	    	label2Data.top = new FormAttachment(label1, 8);
	    	label2Data.width = 40;
	    	label2Data.height = 30;
	    	label2.setLayoutData(label2Data);
	    	
	    	FormData valueData = new FormData();
	    	valueData.left = new FormAttachment(label2, 8);
	    	valueData.top = new FormAttachment(m_type, 8);
	    	valueData.width = 400;
	    	valueData.height = 25;
	    	m_value.setLayoutData(valueData);
	    	
	    	FormData okData = new FormData();
	    	okData.top = new FormAttachment(m_value, 8);
	    	okData.left = new FormAttachment(0, 150);
	    	okData.width = 80;
	    	okButton.setLayoutData(okData);
	    	
	    	FormData cancelData = new FormData();
	    	cancelData.left = new FormAttachment(okButton, 8);
	    	cancelData.top = new FormAttachment(m_value, 8);
	    	cancelData.width = 80;
	    	cancelButton.setLayoutData(cancelData);
	    	
	    	dialog.setDefaultButton(okButton);
	    	dialog.pack();
	    	dialog.open();

	    	okButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}

				public void widgetSelected(SelectionEvent arg0) {
					m_parameter.setType(m_type.getText());
					m_parameter.setValue(m_value.getText());
					m_ok = true;
					dialog.dispose();
				}	    		
	    	});
	    	
	    	cancelButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}

				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
	    	});
	    	
	    	dialog.open();
	    	Display display = parent.getDisplay();
	    	while (!dialog.isDisposed()) {
	    		if (!display.readAndDispatch()) display.sleep();
	    	}
	    	
	    	return(m_ok ? m_parameter : null);
	    }
	}
}
