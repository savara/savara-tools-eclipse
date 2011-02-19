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

import java.util.logging.Logger;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.ui.IWorkbenchPart;
import org.savara.tools.scenario.designer.simulate.*;
import org.savara.tools.scenario.simulation.ScenarioSimulationLaunchConfigurationConstants;


/**
 * This class provides the 'create message links' action implementation.
 *
 */
public class SimulateScenarioAction extends org.eclipse.gef.ui.actions.SelectionAction {

	public static final String ID = "org.pi4soa.service.test.designer.editor.SimulateScenarioID";

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
        
    	org.savara.scenario.model.Scenario scenario=
    			((ScenarioDesigner)getWorkbenchPart()).getScenario();

    	org.savara.tools.scenario.designer.simulate.ScenarioDesignerSimulationLauncher launcher=
    		new org.savara.tools.scenario.designer.simulate.ScenarioDesignerSimulationLauncher(
    				getWorkbenchPart().getSite().getShell().getDisplay(),
    				scenario, ((ScenarioDesigner)getWorkbenchPart()).getScenarioSimulation());
    	
		try {
			ILaunchManager manager =
				DebugPlugin.getDefault().getLaunchManager();
			
			ILaunchConfigurationType type =
				manager.getLaunchConfigurationType(
			      	ScenarioSimulationLaunchConfigurationConstants.LAUNCH_CONFIG_TYPE);
			ILaunchConfiguration[] configurations =
			      manager.getLaunchConfigurations(type);
			
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(PI4SOA_SCENARIO_TEST)) {
					configuration.delete();
					break;
				}
			}
						
			ILaunchConfigurationWorkingCopy workingCopy =
			      type.newInstance(null, PI4SOA_SCENARIO_TEST);

			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					((ScenarioDesigner)getWorkbenchPart()).getFile().getProject().getName());
			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_SCENARIO,
					((ScenarioDesigner)getWorkbenchPart()).getFile().getProjectRelativePath().toString());
			
			/*
			String services=getServiceList();
			if (services != null) {
				workingCopy.setAttribute(ScenarioTestLaunchConfigurationConstants.ATTR_EXECUTE_SERVICES,
						services);
			}
			*/
				
			ILaunchConfiguration configuration=workingCopy.doSave();
		

			Launch launch=new Launch(configuration, LAUNCH_MODE, null);
			
			launcher.launch(configuration, LAUNCH_MODE, launch, null);

			ScenarioSimulation view=((ScenarioDesigner)getWorkbenchPart()).getScenarioSimulation();	
			view.startSimulation();
			
			((ScenarioDesigner)getWorkbenchPart()).updateEditPartActions();
			
		} catch(Exception e) {
			logger.severe("Failed to launch scenario tester: "+e);
			
			e.printStackTrace();
		}
    	/*
    	org.pi4soa.service.test.designer.tools.CreateMessageLinksTool tool=
    		new org.pi4soa.service.test.designer.tools.CreateMessageLinksTool(scenario);
    	
    	tool.run();
    	
    	if (tool.isScenarioChanged()) {
    		((ScenarioEditor)getWorkbenchPart()).setDirty(true);
    	}
    	*/
    }
    
    private static Logger logger = Logger.getLogger("org.pi4soa.service.test.designer.editor");	
   
	private static final String LAUNCH_MODE = "run";
	private static final String PI4SOA_SCENARIO_TEST = "Pi4SOA Scenario Test";
}
