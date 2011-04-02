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
 * Jul 14, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.commands.DeleteLinkCommand;
import org.savara.tools.scenario.designer.parts.LinkEditPart;

/**
 * This is the component edit policy.
 */
public class LinkComponentEditPolicy extends ComponentEditPolicy {

	/**
	 * @see ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeleteLinkCommand deleteCmd = new DeleteLinkCommand();
		
		Object child=(Object)(getHost().getModel());
		
		if (child instanceof Link) {
			deleteCmd.setChild((Link)child);
			
			if (getHost() instanceof LinkEditPart) {
				deleteCmd.setParent(((LinkEditPart)getHost()).getScenarioDiagram().getScenario());
			}
			
			// TODO: GPB - how to find scenario
			//deleteCmd.setParent(((Link)child).getScenario());
		}
		
		return deleteCmd;
	}
}
