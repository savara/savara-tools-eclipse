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
package org.savara.tools.scenario.designer.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.ui.views.properties.IPropertySource;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.figures.*;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.policies.*;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class provides a simple type part.
 */
public class RoleEditPart extends ScenarioBaseEditPart  
					implements org.eclipse.gef.NodeEditPart {
    
	private IPropertySource propertySource = null;

	public RoleEditPart(Object elem) {
		super(elem);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure ret=null;

		ret = new RoleFigure();
		
		((RoleFigure)ret).setText(ViewSupport.getName(getModel(), getScenarioDiagram()));

		return(ret);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ScenarioComponentEditPolicy());
		//installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ActivityNodeEditPolicy());
		//installEditPolicy(EditPolicy.CONTAINER_ROLE, new ActivitySourceEditPolicy());
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new ActivityEditPolicy());
		//installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ActivityDirectEditPolicy());

		installEditPolicy(org.eclipse.gef.EditPolicy.GRAPHICAL_NODE_ROLE,
				new ConnectableRoleEditPolicy());
	}
	
	int getAnchorOffset() {
		return 9;
	}

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
		//return(7);
		int ret=-((getWidth() - 10)/2);
		
		return(ret);
	}
	
    /**
     * This is the property change notification method.
     * 
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	super.propertyChange(evt);

    	int index=ViewSupport.getNearestParticipantIndex(getX(),
    					getScenarioDiagram());
    	
    	// Recursively find all message event objects and update
    	// the values associated with the objects in this column
    	ScenarioBaseEditPart ep=(ScenarioBaseEditPart)getParent();
    	
    	Role participant=(Role)getModel();
    	
    	updateParticipant(ep, index, participant);
    }
    
    protected void updateParticipant(ScenarioBaseEditPart ep, int index,
    						Role participant) {
    	java.util.List children=ep.getChildren();
    	
    	for (int i=0; i < children.size(); i++) {
    		if (children.get(i) instanceof ScenarioBaseEditPart) {
    			ScenarioBaseEditPart subep=
    				(ScenarioBaseEditPart)children.get(i);
    			
    			if (subep instanceof MessageEventEditPart) {
    				org.savara.scenario.model.MessageEvent me=
    					(org.savara.scenario.model.MessageEvent)
    							((MessageEventEditPart)subep).getModel();
    				
    				// Use last known location, rather than
    				// subep.getX(), as this method return potential
    				// an incorrect value based on the new location
    				// which may not be valid, if the participant
    				// instance is being renamed.
    				if (ViewSupport.getNearestParticipantIndex(subep.getFigure().getBounds().x,
    						getScenarioDiagram()) == index) {
    					me.setRole(participant);
     				}
    				
    			} else {
    				updateParticipant(subep, index, participant);
    			}
    		}
    	}
    }
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {

		((RoleFigure)getFigure()).setText(
				ViewSupport.getName(getModel(), getScenarioDiagram()));
		
		// Update the participant header width according to the
		// width of the participant (governed by the text
		// associated with the participant).
		org.eclipse.draw2d.geometry.Dimension dim=
			((RoleFigure)getFigure()).getPreferredSize();
		
		dim.width = getWidth();
		
		((RoleFigure)getFigure()).setPreferredSize(dim);
		
		/*
		((ParticipantFigure)getFigure()).setText(ViewSupport.getName(getModel()));
		((Label)((ParticipantFigure)getFigure()).getHeader()).setPreferredSize(100, 40);
		((Label)((ParticipantFigure)getFigure()).getHeader()).setSize(100, 40);
		((Label)((ParticipantFigure)getFigure()).getHeader()).setBounds(new org.eclipse.draw2d.geometry.Rectangle(0, 0, 100, 40));
		((Label)((ParticipantFigure)getFigure()).getHeader()).setLabelAlignment(org.eclipse.draw2d.PositionConstants.CENTER);
		*/
		//((ParticipantFigure)getFigure()).setBounds(new org.eclipse.draw2d.geometry.Rectangle(0, 0, 100, 40));
		super.refreshVisuals();
	}

    public int getHeight() {
    	int ret=getScenarioDiagram().getHeight();
    	
    	ret -= ViewSupport.PARTICIPANT_PADDING_Y*2 +
    				ViewSupport.getHeaderPadding(getScenarioDiagram().getScenario());
    	
    	return(ret);
    }
    
    public int getWidth() {
    	//int ret=100; //ViewSupport.getWidth(getModel());
    	int ret=ViewSupport.getWidth(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }

    /* (non-Javadoc)
     * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
     */
    protected IPropertySource getPropertySource() {
        if (propertySource == null) {
           	propertySource = new org.savara.tools.scenario.designer.view.RolePropertySource(
           					(org.savara.scenario.model.Role)getModel());
        }
        
        return propertySource;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return(((RoleFigure)getFigure()).getConnectionAnchor());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
    	ConnectionAnchor ret=null;
    	
    	//if (((Participant)getModel()).getDirection()
    	//					== org.pi4soa.scenario.MessageDirection.SEND) {
	        Point pt = new Point(((DropRequest) request).getLocation());
	 
	        // TODO: ISSUE - if figure is outside normal scroll region,
	        // then this 'contains' check will not work, as the point
	        // is based on the location within the viewport
	        if (getFigure() != null &&
	        		getFigure().getBounds().contains(pt)) {
	        	pt.x = getFigure().getBounds().x +
	        				(getWidth()/2);
	        	//ret = ((ParticipantFigure)getFigure()).getConnectionAnchor();
	        	ret = new org.eclipse.draw2d.XYAnchor(pt);
	        }
    	//}
        
        return(ret);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getSourceConnectionAnchor() {
    	ConnectionAnchor ret=((RoleFigure)getFigure()).getConnectionAnchor();
        return(ret);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return(((RoleFigure)getFigure()).getConnectionAnchor());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        Point pt = new Point(((DropRequest) request).getLocation());

        // TODO: ISSUE - if figure is outside normal scroll region,
        // then this 'contains' check will not work, as the point
        // is based on the location within the viewport
        if (getFigure() != null &&
        		getFigure().getBounds().contains(pt)) {
//        	return(((ParticipantFigure)getFigure()).getConnectionAnchor());
        	return(new org.eclipse.draw2d.XYAnchor(pt));
        }
        
        return(null);
    }
    
    public ConnectionAnchor getTargetConnectionAnchor() {
        return(((RoleFigure)getFigure()).getConnectionAnchor());
    }
}
