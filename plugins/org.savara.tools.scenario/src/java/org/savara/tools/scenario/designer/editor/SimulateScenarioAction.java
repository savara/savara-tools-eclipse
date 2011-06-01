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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IWorkbenchPart;
import org.savara.tools.scenario.designer.simulate.*;

/**
 * This class provides the 'create message links' action implementation.
 *
 */
public class SimulateScenarioAction extends org.eclipse.gef.ui.actions.SelectionAction {

	public static final String ID = "org.pi4soa.service.test.designer.editor.SimulateScenarioID";
    
    private static Logger logger = Logger.getLogger(SimulateScenarioAction.class.getName());	

	/**
	 * Creates a <code>CreateMessageLinksAction</code> and 
	 * associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public SimulateScenarioAction(IWorkbenchPart part) {
		super(part);
	}
	
	/**
	 * Initializes this action.
	 */
	protected void init() {
		setId(ID);
		setText("Simulate");
		
		setImageDescriptor(org.savara.tools.scenario.designer.DesignerImages.getImageDescriptor("Simulate.png"));
	}
	
	/**
	 * Calculates and returns the enabled state of this action.  
	 * @return <code>true</code> if the action is enabled
	 */
	protected boolean calculateEnabled() {
		boolean ret=false;
		
		if (getWorkbenchPart() instanceof ScenarioDesigner) { //&&
				//ResourceUtil.hasErrors(((ScenarioDesigner)getWorkbenchPart()).getFile()) == false) {
			ScenarioSimulation view=((ScenarioDesigner)getWorkbenchPart()).getScenarioSimulation();
			
			if (view.getScenario() != null &&
					//org.pi4soa.common.util.NamesUtil.isSet(
					//		view.getScenario().getChoreographyDescriptionURL()) &&
					view.getScenario().getEvent().size() > 0) {
				ret = !view.isSimulationRunning();
			}
		}
		
		return(ret);
	}

    /**
     * Perform this action.
     * 
     */
    public void run() {
        
		ScenarioSimulationDialog ssd=
			new ScenarioSimulationDialog(getWorkbenchPart().getSite().getShell());
	
		try {
			ssd.initializeScenario(((ScenarioDesigner)getWorkbenchPart()).getFile());
			
			ssd.setScenarioDesigner((ScenarioDesigner)getWorkbenchPart());
	
			ssd.open();

	    	((ScenarioDesigner)getWorkbenchPart()).updateEditPartActions();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to initialize simulation", e);
		}
    }
}
