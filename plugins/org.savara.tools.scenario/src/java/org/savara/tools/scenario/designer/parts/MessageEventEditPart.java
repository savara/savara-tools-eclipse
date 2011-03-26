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
 * This class provides a simple type part.
 */
public class MessageEventEditPart extends ScenarioBaseEditPart 
					implements org.eclipse.gef.NodeEditPart {

	public MessageEventEditPart(Object elem) {
		super(elem);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure ret=null;
		org.savara.scenario.model.MessageEvent me=
			(org.savara.scenario.model.MessageEvent)getModel();
		
		if (me instanceof SendEvent) {
			ret = new SendMessageEventFigure();
		} else {
			ret = new ReceiveMessageEventFigure();
		}
		
		return(ret);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ScenarioComponentEditPolicy());

		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ScenarioContainerXYLayoutEditPolicy());
		
		installEditPolicy(org.eclipse.gef.EditPolicy.GRAPHICAL_NODE_ROLE,
				new ConnectableMessageEventEditPolicy());
		
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
	
	/* TODO: GPB: Way to handle notification
    public void notifyChanged(Notification notification) {
        int type = notification.getEventType();
        
        if (type != Notification.SET) {
        	super.notifyChanged(notification);
        } else {
        	getFigure().invalidateTree();
        	refresh();
        	getFigure().repaint();
        }
    }
    */

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		//((Label)getFigure()).setText(ViewSupport.getName(getModel()));
		
		//((Label)getFigure()).setIcon(ViewSupport.getImage(getModel()));
		((MessageEventFigure)getFigure()).setErrorExpected(
				((MessageEvent)getModel()).isErrorExpected());
		
		for (int i=0; i < getSourceConnections().size(); i++) {
			if (getSourceConnections().get(i) instanceof LinkEditPart) {
				((LinkEditPart)getSourceConnections().get(i)).refreshBendpoints();
			}
		}
		
		ViewSupport.setTooltip(getFigure(), getModel());

		super.refreshVisuals();
	}

    public int getHeight() {
    	int ret=20; //ViewSupport.getHeight(getModel());
    	
    	return(ret);
    }
    
    public int getWidth() {
    	int ret=30; //ViewSupport.getWidth(getModel());
    	
    	return(ret);
    }
    
    public org.eclipse.gef.EditPart findEditPartForModel(Object model) {
    	org.eclipse.gef.EditPart ret=null;
    	
    	if (model instanceof Link) {
    		// Check the source and target links for this
    		// message event
    		java.util.List list=getSourceConnections();
    		
    		for (int i=0; ret == null && i < list.size(); i++) {
    			ret = (org.eclipse.gef.EditPart)list.get(i);
    			
    			if (ret.getModel() != model) {
    				ret = null;
    			}
    		}

    		list=getTargetConnections();
    		
    		for (int i=0; ret == null && i < list.size(); i++) {
    			ret = (org.eclipse.gef.EditPart)list.get(i);
    			
    			if (ret.getModel() != model) {
    				ret = null;
    			}
    		}
    	}
    	
    	if (ret == null) {
    		ret = super.findEditPartForModel(model);
    	}
    	
    	return(ret);
    }
    
   /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
     */
    protected java.util.List getModelSourceConnections() {
        return(ModelSupport.getSourceConnections(getScenarioDiagram().getScenario(), getModel()));
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     */
    protected java.util.List getModelTargetConnections() {
        return(ModelSupport.getTargetConnections(getScenarioDiagram().getScenario(), getModel()));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return(((MessageEventFigure)getFigure()).getConnectionAnchor());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
    	ConnectionAnchor ret=null;
    	
    	if (((org.savara.scenario.model.MessageEvent)getModel()) instanceof SendEvent) {
	        Point pt = new Point(((DropRequest) request).getLocation());
	 
	        // TODO: ISSUE - if figure is outside normal scroll region,
	        // then this 'contains' check will not work, as the point
	        // is based on the location within the viewport
	        if (getFigure() != null &&
	        		getFigure().getBounds().contains(pt)) {
	        	ret = ((MessageEventFigure)getFigure()).getConnectionAnchor();
	        }
    	}
        
        return(ret);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getSourceConnectionAnchor() {
    	ConnectionAnchor ret=((MessageEventFigure)getFigure()).getConnectionAnchor();
        return(ret);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return(((MessageEventFigure)getFigure()).getConnectionAnchor());
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
        	return(((MessageEventFigure)getFigure()).getConnectionAnchor());
        }
        return(null);
    }
    
    public ConnectionAnchor getTargetConnectionAnchor() {
        return(((MessageEventFigure)getFigure()).getConnectionAnchor());
    }
    
    protected org.eclipse.gef.ConnectionEditPart createConnection(Object model) {
    	org.eclipse.gef.ConnectionEditPart ret=super.createConnection(model);
    	
    	// Highlight this connection
    	getViewer().select(ret);
    	
    	return(ret);
    }
    
    public void reset() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_RESET);
    	
    	refreshLinks();
		
		super.reset();
    }
    
	public void processing() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_PROCESSING);
	}
	
    public void successful() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_SUCCESSFUL);
    	
    	refreshLinks();
    }
    
    public void unsuccessful() {
    	((MessageEventFigure)getFigure()).setState(MessageEventFigure.STATE_UNSUCCESSFUL);
    	
    	refreshLinks();
    }
    
	public boolean isUnsuccessful() {
		return(((MessageEventFigure)getFigure()).getState() ==
				MessageEventFigure.STATE_UNSUCCESSFUL);
	}
	
	protected void refreshLinks() {
    	
    	// Refresh any message links
    	java.util.List links=null;
    	
    	/* Not at present, as this may cause the link info
    	 * to be derived twice - after both the source and target
    	 * message events have been set
    	links = getSourceConnections();
    	for (int i=0; i < links.size(); i++) {
    		MessageLinkEditPart ep=(MessageLinkEditPart)links.get(i);

    		org.pi4soa.service.test.designer.model.MessageLinkInfo info=
    			(org.pi4soa.service.test.designer.model.MessageLinkInfo)
    			org.pi4soa.scenario.ScenarioManager.getCachedInformation(
    				org.pi4soa.service.test.designer.model.MessageLinkInfo.class, ep.getModel());
    		
    		if (info != null) {
    			info.refresh();
    		}
    	}
    	 */
    	
    	/* GPB: Commented out as not using message link info now
    	boolean f_update=false;

    	links = getTargetConnections();
    	for (int i=0; i < links.size(); i++) {
    		LinkEditPart ep=(LinkEditPart)links.get(i);

    		org.savara.tools.scenario.designer.model.MessageLinkInfo info=
    			(org.savara.tools.scenario.designer.model.MessageLinkInfo)
    			org.pi4soa.scenario.ScenarioManager.getCachedInformation(
    				org.savara.tools.scenario.designer.model.MessageLinkInfo.class, ep.getModel());
    		
    		if (info != null) {
    			info.refresh();
    			f_update = true;
    		}
    	}
    	
    	if (f_update) {
    		getScenarioDiagram().update();
    	}
    	*/
	}
}
