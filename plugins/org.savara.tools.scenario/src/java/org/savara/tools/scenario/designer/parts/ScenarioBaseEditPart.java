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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.ScenarioDiagram;
import org.savara.tools.scenario.designer.simulate.*;
import org.savara.tools.scenario.designer.view.GraphicalComponent;

/**
 * The edit part for the generic scenario component.
 */
public abstract class ScenarioBaseEditPart extends AbstractGraphicalEditPart
						implements java.beans.PropertyChangeListener,
							GraphicalComponent, SimulationEntity {
	
    private IPropertySource propertySource = null;
    //private Notifier target;
    private int m_logStartPosition=0;
    private int m_logEndPosition=0;

    /**
     * This is the default constructor.
     * 
     * @param element The scenario component
     */
    protected ScenarioBaseEditPart(Object element) {
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
    	
    	if (getParent() instanceof ScenarioBaseEditPart) {
    		ret = ((ScenarioBaseEditPart)getParent()).getScenarioDiagram();
    	}
    	
    	return(ret);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
     */
    public void activate() {
        if (isActive())
            return;

        // start listening for changes in the model
        hookIntoScenarioObject(getModel());

        super.activate();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
     */
    public void deactivate() {
        if (!isActive())
            return;

        // stop listening for changes in the model
        unhookFromScenarioObject(getModel());

        super.deactivate();
    }
    
    protected boolean isContained(Point loc) {
    	boolean ret=false;
     	
    	Rectangle r=getBounds();
    	if (r.width == -1) {
    		r = this.getFigure().getBounds();
    	}
    	if (r.contains(loc)) {
    		ret = true;
    	}

    	return(ret);
    }
    
    protected ScenarioBaseEditPart getEditPartAt(Point loc,
    					Class modelClass) {
    	ScenarioBaseEditPart ret=null;
		Point newpoint=loc.getTranslated(-getBounds().x,
				-getBounds().y);
    	    	
    	if (isContained(loc)) {
    		
    		// Check if one of the children contains the point
    		java.util.Iterator iter=getChildren().iterator();
    		
    		while (ret == null && iter.hasNext()) {
    			ScenarioBaseEditPart part=(ScenarioBaseEditPart)iter.next();
    			
    			//Point newpoint=loc.getTranslated(-getChildX(part),
    			//		-getChildY(part));
    			//Point newpoint=loc.getTranslated(-part.getBounds().x,
    			//		-part.getBounds().y);
    			
    			ret = part.getEditPartAt(newpoint, modelClass);
    		}
    		
    		if (ret == null && (modelClass == null ||
    				modelClass.isAssignableFrom(getModel().getClass()))) {
    			ret = this;
    		}
    	}
    	
    	return(ret);
    }
    
    /**
     * This method propagates the request up to an appropriate
     * edit part that can perform the getEditPartAt request.
     * 
     * @param loc
     * @return The editpart
     */
    /*
    public ScenarioBaseEditPart findEditPartAtLocation(Point loc) {
    	ScenarioBaseEditPart ret=null;
    	
    	if (getParent() instanceof ScenarioBaseEditPart) {
    		ret = ((ScenarioBaseEditPart)getParent()).
					findEditPartAtLocation(loc);
    	}
    	
    	return(ret);
    }
    */

    public org.eclipse.gef.EditPart findEditPartForModel(Object model) {
    	org.eclipse.gef.EditPart ret=null;
    	
    	if (getModel() == model) {
    		ret = this;
    	} else {
    		java.util.List list=getChildren();
    		
    		for (int i=0; ret == null && i < list.size(); i++) {
    			if (list.get(i) instanceof ScenarioBaseEditPart) {
    				ret = ((ScenarioBaseEditPart)list.get(i)).findEditPartForModel(model);
    			}
    		}
    	}
    	
    	return(ret);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Adapter#getTarget()
     */
    /*
    public Notifier getTarget() {
        return target;
    }
    */

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
     */
    public boolean isAdapterForType(Object type) {
        return type.equals(getModel().getClass());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
     */
    /*
    public void setTarget(Notifier newTarget) {
        target = newTarget;
    }
    */

    protected void refreshVisuals() {
    	Rectangle r=getComponentBounds();
    	    	
        ((GraphicalEditPart) getParent()).setLayoutConstraint(
        		this, getFigure(), r);
    }
    
    protected void refreshChildren() {
    	super.refreshChildren();
    }
    
    public int getWidth() {
    	return(-1);
    }
    
    public int getHeight() {
     	return(-1);
    }
    
    public int getXOffset() {
    	return(0);
    }
    
    protected boolean useLocalCoordinates() {
    	return(true);
    }
    
    public int getX() {
    	int ret=20;
    	
    	if (getParent() instanceof ScenarioBaseEditPart) {
    		ret = ((ScenarioBaseEditPart)getParent()).getChildX(this);
    	}
    	
    	ret += getXOffset();
  
    	return(ret);
    }
    
    public int getY() {
    	int ret=20;
    	
    	if (getParent() instanceof ScenarioBaseEditPart) {
    		ret = ((ScenarioBaseEditPart)getParent()).getChildY(this);
    	}
    	
    	return(ret);
    }
    
    public int getChildX(ScenarioBaseEditPart child) {
    	return(20);
    }

    public int getChildY(ScenarioBaseEditPart child) {
    	return(20);
    }

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

    protected abstract IPropertySource getPropertySource();
    /*
    protected IPropertySource getPropertySource() {
        if (propertySource == null) {
           	propertySource = new org.savara.tools.scenario.designer.view.ScenarioPropertySource(getModel());
        }
        
        return propertySource;
    }
    */

    /* TODO: GPB notification
    public void notifyChanged(Notification notification) {
        int type = notification.getEventType();

        switch (type) {
            case Notification.ADD :
            case Notification.ADD_MANY :
            case Notification.REMOVE :
            case Notification.REMOVE_MANY :

            	// Refresh parent parts
            	ScenarioBaseEditPart part=this;
            	while (part != null) {
            		
                	part.refreshChildren();
                	part.refreshVisuals();
                	
                	// Refresh visuals for immediate children
                	java.util.Iterator iter=part.getChildren().iterator();
                	while (iter.hasNext()) {
                		Object obj=iter.next();
                		
                		if (obj instanceof ScenarioBaseEditPart) {
                			((ScenarioBaseEditPart)obj).refreshVisuals();
                		}
                	}
                	
            		if (part.getParent() instanceof ScenarioBaseEditPart) {
            			part = (ScenarioBaseEditPart)part.getParent();
               		} else {
            			part = null;
            		}
            	}
                break;

            case Notification.SET :
                refreshVisuals();
                break;
        }  
    }
    */

    /**
     * This is the property change notification method.
     * 
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	refreshVisuals();
    }

    /**
     * Registers this edit part as a listener for change notifications
     * to the specified workflow element.
     * 
     * @param element the worklfow element that should be observed
     * for change notifications
     */
    protected void hookIntoScenarioObject(Object element) {
    	/* TODO: GPB: How to listen for changes?
        if (element instanceof org.eclipse.emf.ecore.EObject) {
            ((org.eclipse.emf.ecore.EObject)element).eAdapters().add(this);
        } else if (element instanceof org.savara.tools.scenario.designer.model.Bean) {
        	((org.savara.tools.scenario.designer.model.Bean)element).addPropertyChangeListener(this);
        }
        */
    }

    /**
     * Removes this edit part from the specified workflow element.
     * Thus, it will no longe receive change notifications.
     * 
     * @param element the worklfow element that should not be observed
     * any more
     */
    protected void unhookFromScenarioObject(Object element) {
    	/* TODO: GPB: How to listen for changes?
        if (element instanceof org.eclipse.emf.ecore.EObject) {
            ((org.eclipse.emf.ecore.EObject)element).eAdapters().remove(this);
        }
         */
   }
    
    public Rectangle getComponentBounds() {
    	Rectangle ret=new Rectangle(getX(), getY(), getWidth(),
    					getHeight());
    	
    	return(ret);
    }
    
    public Rectangle getBounds() {
    	return(getComponentBounds());
    }
    
    public GraphicalComponent getComponentParent() {
    	GraphicalComponent ret=null;
    	
    	if (getParent() instanceof GraphicalComponent) {
    		ret = (GraphicalComponent)getParent();
    	}
    	
    	return(ret);
    }
    
	protected void propagateConnectionRefresh(ScenarioBaseEditPart part) {
		refreshSourceConnections();
		refreshTargetConnections();
		
		java.util.Iterator iter=getChildren().iterator();
		while (iter.hasNext()) {
			Object obj=iter.next();
			
			if (obj instanceof ScenarioBaseEditPart) {
				((ScenarioBaseEditPart)part).propagateConnectionRefresh((ScenarioBaseEditPart)obj);
			}
		}
	}
	
	public void reset() {
		m_logStartPosition = 0;
		m_logEndPosition = 0;
		
		java.util.Iterator iter=getChildren().iterator();
		while (iter.hasNext()) {
			Object obj=iter.next();
			
			if (obj instanceof SimulationEntity) {
				((SimulationEntity)obj).reset();
			}
		}
	}
	
	public void processing() {
	}
	
	public void successful() {
	}
	
	public void unsuccessful() {
	}
	
	public boolean isUnsuccessful() {
		return(false);
	}
	
	public void setLogStartPosition(int pos) {
		m_logStartPosition = pos;
	}
	
	public void setLogEndPosition(int pos) {
		m_logEndPosition = pos;
	}
	
	public int getLogStartPosition() {
		return(m_logStartPosition);
	}
	
	public int getLogEndPosition() {
		return(m_logEndPosition);
	}
}
