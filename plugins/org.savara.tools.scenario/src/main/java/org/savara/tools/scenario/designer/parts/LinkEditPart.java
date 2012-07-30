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
 * Feb 21, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.ScenarioDiagram;
import org.savara.tools.scenario.designer.policies.LinkComponentEditPolicy;
import org.savara.tools.scenario.designer.policies.LinkConnectionEditPolicy;
import org.savara.tools.scenario.designer.policies.LinkEndpointEditPolicy;
import org.savara.tools.scenario.designer.policies.LinkSelectionHandlesEditPolicy;
import org.savara.tools.scenario.designer.simulate.*;
import org.savara.tools.scenario.designer.view.ViewSupport;
import org.savara.tools.scenario.designer.view.GraphicalComponent;

/**
 * This class represents the edit part for the message
 * link connection.
 */
public class LinkEditPart
			extends AbstractConnectionEditPart
			implements SimulationEntity {

    /**
     * @param element
     */
    public LinkEditPart(Link element) {
        super();

        setModel(element);
    }

    /**
     * This method returns the scenario diagram associated with
     * the edit part.
     * 
     * @return The scenario diagram
     */
    public ScenarioDiagram getScenarioDiagram() {
    	ScenarioDiagram ret=null;
    	
    	if (getParent() != null) {
    		java.util.List children=getParent().getChildren();
    		
    		for (int i=0; ret == null && i < children.size(); i++) {
    			Object child=children.get(i);
    	    	if (child instanceof ScenarioBaseEditPart) {
    	    		ret = ((ScenarioBaseEditPart)child).getScenarioDiagram();
    	    	}	
    		}
    	}
    	
    	return(ret);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    protected IFigure createFigure() {
        PolylineConnection connection = new PolylineConnection();
        PolygonDecoration arrow = new PolygonDecoration();
        arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
        arrow.setScale(10, 5);
        connection.setTargetDecoration(arrow);
        
        m_label = new Label("");
        m_label.setOpaque(true);
        m_label.setBackgroundColor(org.eclipse.draw2d.ColorConstants.buttonLightest);
        m_label.setBorder(new org.eclipse.draw2d.LineBorder());
        connection.add(m_label, new org.eclipse.draw2d.MidpointLocator(connection, 0));

		//ViewSupport.setTooltip(connection, getModel());
		
        return(connection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
						new LinkComponentEditPolicy());
        installEditPolicy(
            EditPolicy.CONNECTION_ENDPOINTS_ROLE,
            new LinkEndpointEditPolicy());
        installEditPolicy(
            EditPolicy.CONNECTION_BENDPOINTS_ROLE,
            new LinkSelectionHandlesEditPolicy());
        installEditPolicy(EditPolicy.CONNECTION_ROLE,
        		new LinkConnectionEditPolicy());
    }

    public void setText(String text) {
    	m_label.setText(text);
    }

    /**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		setText(ViewSupport.getName(getModel(), getScenarioDiagram()));

		if (getTarget() != null && getSource() != null) {
			PolylineConnection connection=(PolylineConnection)getFigure();
			
			connection.remove(m_label);
			
			int diffy=((MessageEventEditPart)getTarget()).getComponentBounds().y -
				((MessageEventEditPart)getSource()).getComponentBounds().y;
			int diffx=((MessageEventEditPart)getTarget()).getComponentBounds().x -
				((MessageEventEditPart)getSource()).getComponentBounds().x;
			//boolean backlink=(diff<0);
		
			
			diffy = java.lang.Math.abs(diffy);
			
			if (diffx == 0 || diffy > 50) {
				int vdist=0;//(backlink ? -10:10);
				int udist=20;
				
				if (diffx == 0) {
					udist = 40;
				}
				
		        ConnectionEndpointLocator sourceEndpointLocator = 
		            new ConnectionEndpointLocator(connection, false);
				sourceEndpointLocator.setVDistance(vdist);
				sourceEndpointLocator.setUDistance(udist);
				connection.add(m_label, sourceEndpointLocator);
			} else {
				connection.add(m_label, new org.eclipse.draw2d.MidpointLocator(connection, 0));
			}
		}
		
		refreshBendpoints();

		super.refreshVisuals();
	}
	
	public Rectangle getEditPartBounds() {
		return(getFigure().getBounds());
	}

    public GraphicalComponent getEditPartParent() {
    	return(null);
    }

	/* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#activate()
     */
    public void activate() {
    	/*
    	((Link)getModel()).eAdapters().add(m_adapter);
    	
    	if (((Link)getModel()).getTarget() != null) {
    		((Link)getModel()).getTarget().eAdapters().add(m_adapter);
    	}
    	
    	if (((MessageLink)getModel()).getSource() != null) {
    		((MessageLink)getModel()).getSource().eAdapters().add(m_adapter);
    	}
    	*/
    	
        super.activate();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#deactivate()
     */
    public void deactivate() {
    	/*
    	((MessageLink)getModel()).eAdapters().remove(m_adapter);

    	if (((MessageLink)getModel()).getTarget() != null) {
    		((MessageLink)getModel()).getTarget().eAdapters().remove(m_adapter);
    	}
    	
    	if (((MessageLink)getModel()).getSource() != null) {
    		((MessageLink)getModel()).getSource().eAdapters().remove(m_adapter);
    	}
    	*/
    	
    	super.deactivate();
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
     */
    /*
    public void setTarget(Notifier newTarget) {
    }
    */

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class key)
    {
        /* override the default behavior defined in AbstractEditPart
        *  which would expect the model to be a property sourced. 
        *  instead the editpart can provide a property source
        */
        if (IPropertySource.class == key)
        {
            return getPropertySource();
        }
        return super.getAdapter(key);
    }

    /* (non-Javadoc)
     * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
     */
    protected IPropertySource getPropertySource() {
        if (propertySource == null) {
           	propertySource = new org.savara.tools.scenario.designer.view.LinkPropertySource(
           					(org.savara.scenario.model.Link)getModel());
        }
        
        return propertySource;
    }
    

	public int getLogEndPosition() {
		if (getTarget() == null) {
			return(-1);
		}
		return(((SimulationEntity)getTarget()).getLogEndPosition());
	}

	public int getLogStartPosition() {
		if (getSource() == null) {
			return(-1);
		}
		return(((SimulationEntity)getSource()).getLogStartPosition());
	}

	public boolean isUnsuccessful() {
		return false;
	}

	public void processing() {
	}

	public void reset() {
	}

	public void setLogEndPosition(int pos) {
	}

	public void setLogStartPosition(int pos) {
	}

	public void successful() {
	}

	public void unsuccessful() {
	}

	protected void refreshBendpoints() {
		java.util.List figureConstraint = new java.util.ArrayList();

		if (getTarget() != null && getSource() != null) {
			if (((MessageEventEditPart)getTarget()).getComponentBounds().x ==
					((MessageEventEditPart)getSource()).getComponentBounds().x) {
				int height=((MessageEventEditPart)getTarget()).getComponentBounds().y -
					((MessageEventEditPart)getSource()).getComponentBounds().y;
				int inset=60;
					
				RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
				rbp.setRelativeDimensions(new org.eclipse.draw2d.geometry.Dimension(inset, 0),
						new org.eclipse.draw2d.geometry.Dimension(inset, -height));
				//rbp.setWeight((i+1) / ((float)modelConstraint.size()+1));
				rbp.setWeight(1);
				figureConstraint.add(rbp);
				
				rbp = new RelativeBendpoint(getConnectionFigure());
				rbp.setRelativeDimensions(new org.eclipse.draw2d.geometry.Dimension(inset, height),
						new org.eclipse.draw2d.geometry.Dimension(inset, 0));
				//rbp.setWeight((i+1) / ((float)modelConstraint.size()+1));
				rbp.setWeight(1);
				figureConstraint.add(rbp);
			}
		}
		
		getConnectionFigure().setRoutingConstraint(figureConstraint);
		
		getConnectionFigure().repaint();
		getConnectionFigure().revalidate();
	}

	private IPropertySource propertySource = null;
    private Label m_label=null;
    
    /* TODO: GPB: Need way to setup a list for change
    private MessageLinkAdapter m_adapter=new MessageLinkAdapter();
    
    private class MessageLinkAdapter implements Adapter {
        Notifier newTarget;

        public Notifier getTarget()
        {
            return newTarget;
        }

        public boolean isAdapterForType(Object type)
        {
            return getModel().getClass() == type;
        }

        public void notifyChanged(Notification notification) {
            if (notification.getEventType() == Notification.SET) {
/*            	
                int featureId =
                    notification.getFeatureID(WorkflowPackage.class);

                // TODO: need to handle edge reattachments here?
                switch (featureId)
                {
                    case WorkflowPackage.EDGE__SOURCE :
                        break;
                    case WorkflowPackage.EDGE__TARGET :
                        break;
                }
               */
    /*
            	refreshVisuals();
            }
        }

        public void setTarget(Notifier newTarget)
        {
            this.newTarget = newTarget;
        }
    }
    */
}
