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
 * 23 Jan 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * This is a derived 'advanced' property sheet section, with
 * the added command stack support to enable changes to be
 * undone.
 */
public class AllPropertySheetSection
	extends org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection {

	public AllPropertySheetSection() {
	}
	
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
    	super.createControls(parent, aTabbedPropertySheetPage);

        if (aTabbedPropertySheetPage instanceof DesignerTabbedPropertySheetPage) {
        	m_commandStack = ((DesignerTabbedPropertySheetPage)aTabbedPropertySheetPage).getCommandStack();
        }

        page.setRootEntry(new org.eclipse.gef.ui.properties.UndoablePropertySheetEntry(
                m_commandStack));
    }
    
	private CommandStack m_commandStack=null;
}
