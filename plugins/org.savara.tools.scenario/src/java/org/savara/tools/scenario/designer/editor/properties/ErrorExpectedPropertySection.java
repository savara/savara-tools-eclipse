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

public class ErrorExpectedPropertySection extends AbstractPropertySection {
    
	private DefaultCheckBoxPropertySection m_errorExpected=
		new DefaultCheckBoxPropertySection("errorExpected",
				"ErrorExpected", "Error Expected");

	public ErrorExpectedPropertySection() {
	}
	
    public void aboutToBeShown() {
    	super.aboutToBeShown();
    	
    	m_errorExpected.aboutToBeShown();
    }
    
    public void aboutToBeHidden() {
    	super.aboutToBeHidden();

    	m_errorExpected.aboutToBeHidden();
    }
    
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
	
        Composite composite = getWidgetFactory()
					.createFlatFormComposite(parent);

        m_errorExpected.setToolTip("Whether this event is expected to result in an error");
       // m_errorExpected.setTextGap(130);
        m_errorExpected.setStartPercentage(0);
        m_errorExpected.setEndPercentage(68);
        m_errorExpected.createControls(composite,
        		aTabbedPropertySheetPage);
    }
    
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        
        m_errorExpected.setInput(part, selection);
    }
    
    public void refresh() {    	
    	super.refresh();
    	
    	m_errorExpected.refresh();
    }
    
    public void dispose() {    	
    	super.dispose();
    	
    	m_errorExpected.dispose();
    }
}
