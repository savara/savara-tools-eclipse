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
 * 18 Jan 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.ui.views.properties.tabbed.*;
import org.eclipse.gef.commands.*;

/**
 * This class provides the designer specific implementation
 * of the tabbed property sheet page, supporting an undo
 * command stack.
 */
public class DesignerTabbedPropertySheetPage extends TabbedPropertySheetPage {

	/**
	 * This is the constructor for the designer tabbed
	 * property page.
	 * 
	 * @param tabbedPropertySheetPageContributor
	 */
	public DesignerTabbedPropertySheetPage(
			ITabbedPropertySheetPageContributor contributor,
			CommandStack commandStack) {
		super(contributor);
		
		m_commandStack = commandStack;
		m_contributor = contributor;
	}
	
	/**
	 * This method returns the command stack.
	 * 
	 * @return The command stack
	 */
	public CommandStack getCommandStack() {
		return(m_commandStack);
	}
	
	/**
	 * This method returns the resource associated with the
	 * tabbed properties sheet.
	 * 
	 * @return The resource
	 */
	public org.eclipse.core.resources.IResource getResource() {
		org.eclipse.core.resources.IResource ret=null;
		
		if (m_contributor instanceof org.eclipse.ui.part.MultiPageEditorPart) {
			ret = (org.eclipse.core.resources.IResource)
				((org.eclipse.ui.part.MultiPageEditorPart)m_contributor).
						getEditorInput().getAdapter(
						org.eclipse.core.resources.IResource.class);
		}
		
		return(ret);
	}
	
	private CommandStack m_commandStack=null;
	private ITabbedPropertySheetPageContributor m_contributor=null;
}
