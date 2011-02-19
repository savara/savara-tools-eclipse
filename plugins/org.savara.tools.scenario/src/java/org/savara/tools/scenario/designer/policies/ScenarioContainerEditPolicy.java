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
 * Jul 6, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.policies;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.commands.CreateComponentCommand;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.parts.ScenarioBaseEditPart;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class represents the edit policy for the generic container
 * edit part.
 */
public class ScenarioContainerEditPolicy extends ContainerEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		CreateComponentCommand ret=null;
		
		if (getHost() instanceof ScenarioBaseEditPart &&
				ModelSupport.isValidTarget(request.getNewObject(),
						((ScenarioBaseEditPart)getHost()).getModel())) {
			ScenarioBaseEditPart part=(ScenarioBaseEditPart)getHost();
	
			FigureCanvas canvas=(FigureCanvas)
					getHost().getViewer().getControl();

			Viewport port = canvas.getViewport();
			
			ret = new CreateComponentCommand();
			
			ret.setChild(request.getNewObject());
			ret.setParent(part.getModel());

			if (request.getNewObject() instanceof Event) { //ScenarioObject) {
				int x=port.getClientArea().x + request.getLocation().x;
				
				Role participant=ViewSupport.getNearestRole(x, part.getScenarioDiagram());
	
				// Calculate the index position
				int y=port.getClientArea().y + request.getLocation().y
							- ViewSupport.getHeaderPadding(((ScenarioBaseEditPart)getHost()).getModel()); /* -
							ViewSupport.INITIAL_YPADDING
								- ViewSupport.YPADDING;*/
				y -= ((ScenarioBaseEditPart)getHost()).getFigure().getBounds().y;
			
				int index=0;
				java.util.List list=ModelSupport.getChildren(((ScenarioBaseEditPart)getHost()).getModel());
				
				for (int i=0; y > 0 && i < list.size(); i++) {
					index++;
					
					y -= ViewSupport.getHeight(list.get(i),
							((ScenarioBaseEditPart)getHost()).getScenarioDiagram());
					
    				y -= ViewSupport.getPadding(list, i);
				}
				
				ret.setRole(participant);
				ret.setIndex(index);
				
			} else if (request.getNewObject() instanceof Role) {
				int x=port.getClientArea().x + request.getLocation().x;
				int index=ViewSupport.getNewParticipantIndex(x,
							part.getScenarioDiagram());
				
				ret.setIndex(index);
				
				//((Participant)request.getNewObject()).setDiagram(part.getScenarioDiagram());
			}
		}
		
		return(ret);
	}
}
