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
	
	public static class ParameterListPropertySection extends AbstractListPropertySection {
		
		public ParameterListPropertySection(String property, String displayName,
				String label, int start, int end) {
			super(property, displayName, label, start, end);
		}

		protected Object createNewObject() {
			Parameter ret=new Parameter();
			ret.setType("PType"+System.currentTimeMillis());
			ret.setValue("http://ptype"+System.currentTimeMillis());
			return(ret);
		}
		
	    protected String getDisplayValue(Object sourceValue) {
	    	String ret="";

	    	if (sourceValue instanceof Parameter) {
	    		Parameter p=(Parameter)sourceValue;
	    		
	    		if (p.getType() != null && p.getType().trim().length() > 0) {
	    			ret += p.getType();
	    		}

	    		ret += " [";
	    		
	    		if (p.getType() != null && p.getType().trim().length() > 0) {
	    			ret += p.getType();
	    		}

	    		ret += "]";
	    	}
	    	
	    	return(ret);
	    }
	}
}
