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

public class ImportPropertySection extends AbstractPropertySection {
    
	private DefaultTextPropertySection m_url=
		new DefaultTextPropertySection("url",
				"URL", "URL");
	private DefaultTextPropertySection m_group=
		new DefaultTextPropertySection("group",
				"Group", "Group");

	public ImportPropertySection() {
	}
	
    public void aboutToBeShown() {
    	super.aboutToBeShown();
    	
    	m_group.aboutToBeShown();
    	m_url.aboutToBeShown();
    }
    
    public void aboutToBeHidden() {
    	super.aboutToBeHidden();

    	m_group.aboutToBeHidden();
    	m_url.aboutToBeHidden();
    }
    
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
	
        Composite composite = getWidgetFactory()
					.createFlatFormComposite(parent);

        m_url.setToolTip("The URL for the scenario to be imported");
        m_url.setTextGap(100);
        m_url.setStartPercentage(0);
        m_url.setEndPercentage(38);
        m_url.createControls(composite,
        		aTabbedPropertySheetPage);
        
        m_group.setTopPercentage(50);
        m_group.setToolTip("The optional group within the scenario");
        m_group.setTextGap(100);
        m_group.setStartPercentage(0);
        m_group.setEndPercentage(38);
        m_group.createControls(composite,
        		aTabbedPropertySheetPage);

    }
    
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        
        m_group.setInput(part, selection);
        m_url.setInput(part, selection);
    }
    
    public void refresh() {    	
    	super.refresh();
    	
    	m_group.refresh();
    	m_url.refresh();
    }
    
    public void dispose() {    	
    	super.dispose();
    	
    	m_group.dispose();
    	m_url.dispose();
    }
}
