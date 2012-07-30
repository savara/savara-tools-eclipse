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
 * Jul 5, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * This is the factory class for the Graphical edit parts.
 */
public class ScenarioEditPartsFactory implements EditPartFactory {

	public ScenarioEditPartsFactory(org.savara.tools.scenario.designer.simulate.ScenarioSimulation sim) {
		m_simulation = sim;
	}
	
	/**
	 * This method returns the relevant edit part for the supplied
	 * model component.
	 * 
	 * @param context The Edit Part context
	 * @param model The model object
	 * @return The edit part
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart ret=null;
		
		if (model instanceof org.savara.scenario.model.Scenario) {
			ret = new ScenarioEditPart((org.savara.scenario.model.Scenario)model,
								m_simulation);
		} else if (model instanceof org.savara.scenario.model.Group) {
			ret = new GroupEditPart(model);
		} else if (model instanceof org.savara.scenario.model.TimeElapsedEvent) {
			ret = new TimeElapsedEventEditPart(model);
		} else if (model instanceof org.savara.scenario.model.Import) {
			ret = new ImportEditPart(model);
		} else if (model instanceof org.savara.scenario.model.MessageEvent) {
			ret = new MessageEventEditPart(model);
		} else if (model instanceof org.savara.scenario.model.Link) {
			ret = new LinkEditPart((org.savara.scenario.model.Link)model);
		} else if (model instanceof org.savara.scenario.model.Role) {
			ret = new RoleEditPart(model);
		}
		
		if (ret == null) {
			System.out.println("NOT RETURNING PART FOR MODEL = "+model);
		}
		
		return(ret);
	}
	
	public org.savara.tools.scenario.designer.simulate.ScenarioSimulation getSimulation() {
		return(m_simulation);
	}

	private org.savara.tools.scenario.designer.simulate.ScenarioSimulation m_simulation=null;
}
