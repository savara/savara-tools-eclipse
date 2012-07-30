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
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.commands.ConnectionCommand;
import org.savara.tools.scenario.designer.commands.PasteComponentCommand;
import org.savara.tools.scenario.designer.model.ModelSupport;
import org.savara.tools.scenario.designer.tools.*;

/**
 * This class provides the paste action implementation.
 *
 */
public class PasteAction extends org.eclipse.gef.ui.actions.SelectionAction {

	/**
	 * Creates a <code>SelectionAction</code> and associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public PasteAction(IWorkbenchPart part) {
		super(part);
	}
	
	/**
	 * Initializes this action.
	 */
	protected void init() {
		setId(org.eclipse.ui.actions.ActionFactory.PASTE.getId());
		setText("Paste");
	}
	
	/**
	 * Calculates and returns the enabled state of this action.  
	 * @return <code>true</code> if the action is enabled
	 */
	protected boolean calculateEnabled() {
		boolean ret=false;
		
		if (getSelectedObjects() != null &&
				getSelectedObjects().size() == 1 &&
				getSelectedObjects().get(0) instanceof EditPart) {
        	EditPart ep=(EditPart)getSelectedObjects().get(0);
        	
        	// Check if the copied model object is suitable to be
        	// pasted into the currently selected object
        	if (ModelSupport.isValidTarget(CopyAction.getCurrentTarget(),
        			ep.getModel())) {
        		ret = true;
        	}
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
        	Object source=CopyAction.getCurrentTarget();
        	
        	if (source instanceof Link) {
        		Link link=new Link();
       		
        		ConnectionCommand command=new ConnectionCommand();
        		command.setLink(link);
        		
        		// TODO: GPB: Need to consider whether copy is necessary
        		command.setSource((MessageEvent)//EcoreUtil.copy(
        				((Link)source).getSource()); //);
        		command.setTarget((MessageEvent)//EcoreUtil.copy(
        				((Link)source).getTarget()); //);
        		
        		command.setPasteParent(ep.getModel());
        		
        		execute(command);
        		
        	} else if (source != null) {
        		// Make a copy of the source
        		//source = EcoreUtil.copy((EObject)source);
        		
            	// Build create component command
    			PasteComponentCommand command=new PasteComponentCommand();
    			
    			command.setChild(source);
    			command.setParent(ep.getModel());
    			
    			execute(command);
           	}        	
         }
    }
}
