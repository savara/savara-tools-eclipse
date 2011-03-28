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

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.views.properties.IPropertySource;
import org.savara.tools.scenario.designer.figures.*;
import org.savara.tools.scenario.designer.simulate.*;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class represents a sequentially order grouping construct's
 * edit part.
 */
public class GroupEditPart extends StructuredGroupEditPart {

	private IPropertySource propertySource = null;

	/**
	 * This is the constructor.
	 * 
	 * @param cdlType
	 */
	public GroupEditPart(Object elem) {
		super(elem);
	}
		
    public int getChildX(ScenarioBaseEditPart child) {
    	int ret=0;
    	
    	ret = ViewSupport.getChildXPosition(getModel(),
						child.getModel(), getScenarioDiagram());
	
    	return(ret);
    }

    public int getChildY(ScenarioBaseEditPart child) {
    	int ret=0;

    	ret = ViewSupport.getChildYPosition(getModel(),
				child.getModel(), getScenarioDiagram());
    	
		return(ret);
    }
    
    protected void refreshChildren() {
    	super.refreshChildren();
    	
    	Iterator iter=getChildren().iterator();
    	while (iter.hasNext()) {
    		Object obj=iter.next();
    		if (obj instanceof ScenarioBaseEditPart) {
    			((ScenarioBaseEditPart)obj).refreshVisuals();
    			((ScenarioBaseEditPart)obj).refreshChildren();
    		}
    	}
    }
    
    /* (non-Javadoc)
     * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
     */
    protected IPropertySource getPropertySource() {
        if (propertySource == null) {
           	propertySource = new org.savara.tools.scenario.designer.view.GroupPropertySource(
           					(org.savara.scenario.model.Group)getModel());
        }
        
        return propertySource;
    }
    
	/**
	 * @see org.eclipse.gef.examples.flow.parts.StructuredActivityPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure f=new GroupFigure(null);

		return(f);
	}

	public void reset() {
		((GroupFigure)getFigure()).setState(GroupFigure.STATE_RESET);
		
		super.reset();
	}
	
	public void processing() {
		((GroupFigure)getFigure()).setState(GroupFigure.STATE_PROCESSING);
	}
	
	public void successful() {
		boolean unsuccessful=false;
		
    	Iterator iter=getChildren().iterator();
    	while (unsuccessful == false && iter.hasNext()) {
    		Object obj=iter.next();
    		if (obj instanceof SimulationEntity) {
    			unsuccessful = ((SimulationEntity)obj).isUnsuccessful();
    		}
    	}
		
    	if (unsuccessful) {
    		unsuccessful();
    	} else {
    		((GroupFigure)getFigure()).setState(GroupFigure.STATE_SUCCESSFUL);
    	}
	}
	
	public void unsuccessful() {
		((GroupFigure)getFigure()).setState(GroupFigure.STATE_UNSUCCESSFUL);
	}
	
	public boolean isUnsuccessful() {
		return(((GroupFigure)getFigure()).getState() == 
					GroupFigure.STATE_UNSUCCESSFUL);
	}
}
