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

import org.eclipse.ui.IWorkbenchPart;

import org.savara.tools.scenario.designer.simulate.*;

/**
 * This class provides the 'reset simulation' action implementation.
 *
 */
public class SimulationEntityFocusAction extends org.eclipse.gef.ui.actions.SelectionAction {

	public static final String ID="org.savara.tools.scenario.designer.editor.SimulationEntityFocusActionID";
	
	/**
	 * Creates a <code>CreateMessageLinksAction</code> and 
	 * associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public SimulationEntityFocusAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
	}
	
	protected void handleSelectionChanged() {
		String text="<Select message event or event group>";
		
		if (getSelectedObjects().size() == 1) {
			Object obj=getSelectedObjects().get(0);
			
			if (obj instanceof SimulationEntity) {
				SimulationEntity se=(SimulationEntity)obj;
				
				text=((ScenarioDesigner)getWorkbenchPart()).
							getScenarioSimulation().getLogEntry(
									se.getLogStartPosition(),
									se.getLogEndPosition());
				
				if (text != null && text.trim().length() == 0) {
					text = "<No simulation log information available>";
				}
			}
		}
		
		if (text != null) {
			((ScenarioDesigner)getWorkbenchPart()).
						getSimulationLogPage().setText(text);
		}
	}
	
	public boolean calculateEnabled() {
		return(true);
	}
}