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
 * Mar 21, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.parts;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.figures.*;
import org.savara.tools.scenario.designer.model.ModelSupport;
import org.savara.tools.scenario.designer.policies.ConnectableMessageEventEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioComponentEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioContainerXYLayoutEditPolicy;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class provides a time elapse event edit part.
 */
public class TimeElapsedEventEditPart extends ScenarioBaseEditPart {

	public TimeElapsedEventEditPart(Object elem) {
		super(elem);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure ret=null;
		
		ret = new TimeElapsedEventFigure(getScenarioDiagram());
		
		return(ret);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ScenarioComponentEditPolicy());

		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ScenarioContainerXYLayoutEditPolicy());
		
		//installEditPolicy(org.eclipse.gef.EditPolicy.GRAPHICAL_NODE_ROLE,
		//		new ConnectableMessageEventEditPolicy());
		
		//installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ActivityNodeEditPolicy());
		//installEditPolicy(EditPolicy.CONTAINER_ROLE, new ActivitySourceEditPolicy());
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new ActivityEditPolicy());
		//installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ActivityDirectEditPolicy());
	}
	
	/*
	int getAnchorOffset() {
		return 9;
	}
	*/

	/*
	protected void performDirectEdit() {
		if (manager == null) {
			Label l = (Label)getFigure();
			manager =
				new ActivityDirectEditManager(
					this,
					TextCellEditor.class,
					new ActivityCellEditorLocator(l), l);
		}
		manager.show();
	}
	*/

	public int getXOffset() {
		return(-10);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		
		((TimeElapsedEventFigure)getFigure()).setElapsedTime(
				((TimeElapsedEvent)getModel()).getDuration());
		
		ViewSupport.setTooltip(getFigure(), getModel());

		super.refreshVisuals();
	}

    public int getHeight() {
    	int ret=20; //ViewSupport.getHeight(getModel());
    	
    	return(ret);
    }
    
    public int getWidth() {
    	int ret=ViewSupport.getWidth(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }
    
    /*
    public void reset() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_RESET);
		
		super.reset();
    }
    
	public void processing() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_PROCESSING);
	}
	
    public void successful() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_SUCCESSFUL);
    }
    
    public void unsuccessful() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_UNSUCCESSFUL);
    }
    
	public boolean isUnsuccessful() {
		return(((MessageEventFigure)getFigure()).getState() ==
				MessageEventFigure.STATE_UNSUCCESSFUL);
	}
	*/
}
