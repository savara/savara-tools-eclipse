/*
 * Copyright 2005 Pi4 Technologies Ltd
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
 * Aug 10, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This class provides the copy action implementation.
 *
 */
public class CopyAction extends org.eclipse.gef.ui.actions.SelectionAction {

	/**
	 * Creates a <code>SelectionAction</code> and associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public CopyAction(IWorkbenchPart part) {
		super(part);
	}
	
	/**
	 * Initializes this action.
	 */
	protected void init() {
		setId(org.eclipse.ui.actions.ActionFactory.COPY.getId());
		setText("Copy");
	}
	
	/**
	 * Calculates and returns the enabled state of this action.  
	 * @return <code>true</code> if the action is enabled
	 */
	protected boolean calculateEnabled() {
		boolean ret=false;
		
		if (getSelectedObjects() != null &&
				getSelectedObjects().size() > 0) {
			ret = true;
		}
		
		return(ret);
	}

    /**
     * Perform this action.
     * 
     */
    public void run() {
         
        if (getSelectedObjects().size() == 1 &&
        		getSelectedObjects().get(0) instanceof EditPart) {
        	EditPart ep=(EditPart)getSelectedObjects().get(0);
        	
            m_currentTarget = ep.getModel();
        }
    }

	public static Object getCurrentTarget() {
		return(m_currentTarget);
	}
	
	private static Object m_currentTarget=null;
}
