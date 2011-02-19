/*
 * Copyright 2005-7 Pi4 Technologies Ltd
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
 * Feb 23, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This class provides the 'create message links' action implementation.
 *
 */
public class CreateLinksAction extends org.eclipse.gef.ui.actions.SelectionAction {

	public static final String ID = "org.pi4soa.service.test.designer.editor.CreateMessageLinksID";

	/**
	 * Creates a <code>CreateMessageLinksAction</code> and 
	 * associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public CreateLinksAction(IWorkbenchPart part) {
		super(part);
	}
	
	/**
	 * Initializes this action.
	 */
	protected void init() {
		setId(ID);
		setText("Create Message Links");
	}
	
	/**
	 * Calculates and returns the enabled state of this action.  
	 * @return <code>true</code> if the action is enabled
	 */
	protected boolean calculateEnabled() {
		return(true);
	}

    /**
     * Perform this action.
     * 
     */
    public void run() {
        
    	org.savara.scenario.model.Scenario scenario=
    			((ScenarioDesigner)getWorkbenchPart()).getScenario();

    	org.savara.tools.scenario.designer.tools.CreateLinksTool tool=
    		new org.savara.tools.scenario.designer.tools.CreateLinksTool(scenario);
    	
    	tool.run();
    	
    	if (tool.isScenarioChanged()) {
    		((ScenarioDesigner)getWorkbenchPart()).setDirty(true);
    	}
    }
}
