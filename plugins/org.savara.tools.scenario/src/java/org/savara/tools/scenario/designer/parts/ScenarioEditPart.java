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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
//import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.figures.ScenarioFigure;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.policies.ScenarioContainerEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioContainerXYLayoutEditPolicy;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This edit part represents the complete choreography description.
 */
public class ScenarioEditPart extends ScenarioBaseEditPart
			implements ScenarioDiagram {

    /**
     * Creates a new WorkflowEditPart instance.
     * @param element
     */
    protected ScenarioEditPart(org.savara.scenario.model.Scenario scenario,
    		org.savara.tools.scenario.designer.simulate.ScenarioSimulation sim) {
        super(scenario);
        
        m_simulation = sim;
    }

    /**
     * This method returns the scenario diagram associated with
     * the edit part.
     * 
     * @return The scenario diagram
     */
    public ScenarioDiagram getScenarioDiagram() {
    	return(this);
    }
    
	
	public org.savara.scenario.model.Scenario getScenario() {
		return((org.savara.scenario.model.Scenario)getModel());
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    protected IFigure createFigure() {
        FreeformLayer layer = new ScenarioFigure();
        layer.setLayoutManager(new FreeformLayout());
        layer.setBorder(new LineBorder(1));

		//ViewSupport.setTooltip(layer, getModel());
        
    	ConnectionLayer cLayer = (ConnectionLayer) getLayer(org.eclipse.gef.LayerConstants.CONNECTION_LAYER);

        BendpointConnectionRouter router=new BendpointConnectionRouter();
    	cLayer.setConnectionRouter(router);
		
        return layer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONTAINER_ROLE,
				new ScenarioContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ScenarioContainerXYLayoutEditPolicy());
    	
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());    	
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    protected List<Object> getModelChildren() {
    	org.savara.scenario.model.Scenario scenario=
    		(org.savara.scenario.model.Scenario)getModel();
 
    	java.util.List<Object> ret=new java.util.Vector<Object>();
    	
    	ret.addAll(scenario.getRole());
    	
       	ret.addAll(scenario.getEvent());
       	
    	return(ret);
    }
        
    protected void refreshChildren() {
    	super.refreshChildren();
    	
    	Iterator<?> iter=getChildren().iterator();
    	while (iter.hasNext()) {
    		Object obj=iter.next();
    		if (obj instanceof ScenarioBaseEditPart) {
    			((ScenarioBaseEditPart)obj).refreshVisuals();
    			((ScenarioBaseEditPart)obj).refreshChildren();
    		}
    	}
    }
    
    protected void refreshVisuals() {
    	((ScenarioFigure)getFigure()).setName(getScenario().getName());
    	((ScenarioFigure)getFigure()).setAuthor(getScenario().getAuthor());
    	
    	super.refreshVisuals();
    }
    
    protected void refreshMessageLinks() {
    	
		// Need to update the text on all links
		for (int i=0; i < getScenario().getLink().size(); i++) {
			Link link=(Link)
					getScenario().getLink().get(i);
			
			Object ep=findEditPartForModel(link);
			
			if (ep instanceof LinkEditPart) {
				((LinkEditPart)ep).refresh();
			}
		}
    }

    public void update() {
    	refreshChildren();
    	
    	refreshMessageLinks();
    }
    
    public int getChildX(ScenarioBaseEditPart child) {
    	int ret=20;
    	
    	ret = org.savara.tools.scenario.designer.view.ViewSupport.getChildXPosition(getModel(),
    						child.getModel(), getScenarioDiagram());
    	
    	return(ret);
    }

    public int getChildY(ScenarioBaseEditPart child) {
    	int ret=20;
    	
    	ret = org.savara.tools.scenario.designer.view.ViewSupport.getChildYPosition(getModel(),
				child.getModel(), getScenarioDiagram());
      	
    	return(ret);
    }

    public int getHeight() {
    	int ret=ViewSupport.getHeight(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }
    
    public int getWidth() {
    	int ret=ViewSupport.getWidth(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
    /* TODO: GPB
	public void notifyChanged(Notification notification) {
		int type = notification.getEventType();
		
		switch( type ) {
			case Notification.ADD:
			case Notification.ADD_MANY:				
				refreshVisuals();
				refreshChildren();
				
				if (notification.getNewValue() instanceof Link) {
					// Need to update the text on all links
					/*
					for (int i=0; i < getScenario().getMessageLinks().size(); i++) {
						MessageLink link=(MessageLink)
								getScenario().getMessageLinks().get(i);
						
						Object ep=findEditPartForModel(link);
						
						if (ep instanceof MessageLinkEditPart) {
							((MessageLinkEditPart)ep).refresh();
						}
					}
					*/
    
    /*
					refreshMessageLinks();
				}
			
				// Refresh source and target connections on all children
				propagateConnectionRefresh(this);
			
				break;
				
			case Notification.REMOVE_MANY:
			case Notification.REMOVE:
				refreshVisuals();
				refreshChildren();
				
				if (notification.getOldValue() instanceof Link) {
					// Need to update the text on all links
					/*
					for (int i=0; i < getScenario().getMessageLinks().size(); i++) {
						MessageLink link=(MessageLink)
								getScenario().getMessageLinks().get(i);
						
						Object ep=findEditPartForModel(link);
						
						if (ep instanceof MessageLinkEditPart) {
							((MessageLinkEditPart)ep).refresh();
						}
					}
					*/
    /*
					refreshMessageLinks();
				}

				// Refresh source and target connections on all children
				propagateConnectionRefresh(this);
				
				break;
			
			case Notification.SET:
				refreshVisuals();
			
				refreshChildren();
				
				// Refresh source and target connections on all children
				propagateConnectionRefresh(this);
				break;
		}
	}
	*/
	
    /**
     * This method propagates the request up to an appropriate
     * edit part that can perform the getEditPartAt request.
     * 
     * @param loc
     * @param modelClass The optional model class to find
     * @return The editpart
     */
    public ScenarioBaseEditPart findEditPartAtLocation(Point loc,
    					Class modelClass) {
    	ScenarioBaseEditPart ret=null;
    	
		Point newpoint=loc.getTranslated(getBounds().x,
				getBounds().y);

		ret = getEditPartAt(newpoint, modelClass);
    	
    	return(ret);
    }
    	
	public org.savara.tools.scenario.designer.simulate.ScenarioSimulation getSimulation() {
		return(m_simulation);
	}
	
	public Rectangle getComponentBoundsWithoutIdentityDetails() {
		return(super.getComponentBounds());
	}
    
	public Rectangle getIdentityDetailsBounds() {
		Rectangle ret=new Rectangle(0, 0, 0, 0);
				
		return(ret);
	}
    
    public Rectangle getComponentBounds() {
    	Rectangle ret=super.getComponentBounds();
    	
    	// As ScenarioEditPart is the top level edit part,
    	// we also need to add additional height to take care of
    	// the information displayed below the actual diagram.
    	// This can be moved to a higher level editpart, if
    	// one is created into the future. However the diagram
    	// itself should not be considered to include this
    	// additional information in its dimensions.
    	
    	/* GPB: Not showing identity details
		if (getShowIdentityDetails()) {
			Rectangle idbounds=getIdentityDetailsBounds();
			
			ret.height += idbounds.height;
			if (idbounds.width > ret.width) {
				ret.width = idbounds.width;
			}
			
			/*
			java.util.List links=getScenario().getMessageLinks();
			
			for (int i=0; i < links.size(); i++) {
				MessageLinkInfo info=MessageLinkInfo.getInstance((MessageLink)
							links.get(i),
   						getScenarioDiagram());

	       		int height = ViewSupport.getHeight(info, this);
				
				int width = ViewSupport.getWidth(info, this)+
						ViewSupport.getChildXPosition(getScenario(),
									info, this);
				
				ret.height += height+5; // height + padding
				
				if (width > ret.width) {
					ret.width = width;
				}
			}
			*/
		//}
		
    	
    	return(ret);
    }
    
    private org.savara.tools.scenario.designer.simulate.ScenarioSimulation m_simulation=null;
    private boolean m_showIdentityDetails=false;
}
