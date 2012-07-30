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

public class ScenarioDetailsPropertySection extends AbstractPropertySection {
    
	private DefaultTextPropertySection m_description=
		new DefaultTextPropertySection("description",
				"Description", "Description");
	private DefaultTextPropertySection m_author=
		new DefaultTextPropertySection("author",
				"Author", "Author");

	public ScenarioDetailsPropertySection() {
	}
	
    public void aboutToBeShown() {
    	super.aboutToBeShown();
    	
    	m_description.aboutToBeShown();
    	m_author.aboutToBeShown();
    }
    
    public void aboutToBeHidden() {
    	super.aboutToBeHidden();

    	m_description.aboutToBeHidden();
    	m_author.aboutToBeHidden();
    }
    
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
	
        Composite composite = getWidgetFactory()
					.createFlatFormComposite(parent);

        m_author.setToolTip("The author of the scenario");
        m_author.setTextGap(100);
        m_author.setStartPercentage(0);
        m_author.setEndPercentage(38);
        m_author.createControls(composite,
        		aTabbedPropertySheetPage);
        
        m_description.setTopPercentage(50);
        m_description.setToolTip("The description of the scenario");
        m_description.setTextGap(100);
        m_description.setStartPercentage(0);
        m_description.setEndPercentage(38);
        m_description.setExpandable(true);

        m_description.createControls(composite,
        		aTabbedPropertySheetPage);

    }
    
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        
        m_description.setInput(part, selection);
        m_author.setInput(part, selection);
    }
    
    public void refresh() {    	
    	super.refresh();
    	
    	m_description.refresh();
    	m_author.refresh();
    }
    
    public void dispose() {    	
    	super.dispose();
    	
    	m_description.dispose();
    	m_author.dispose();
    }
}
