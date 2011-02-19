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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.savara.scenario.model.Role;
import org.savara.tools.scenario.designer.commands.AddCommand;
import org.savara.tools.scenario.designer.commands.MoveCommand;
import org.savara.tools.scenario.designer.model.ModelSupport;
import org.savara.tools.scenario.designer.parts.ScenarioBaseEditPart;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class provides the XY layout implementation for the generic
 * container.
 */
public class ScenarioContainerXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createAddCommand(EditPart child, Object constraint) {
		
		AddCommand ret=null;
		ScenarioBaseEditPart comppart=null;
		if (child instanceof ScenarioBaseEditPart) {
			comppart = (ScenarioBaseEditPart)child;
		}
		if (comppart != null && getHost() instanceof ScenarioBaseEditPart
				&& ModelSupport.isValidTarget(comppart.getModel(),
						((ScenarioBaseEditPart)getHost()).getModel())) {
			Object oldParent=ModelSupport.getParent(comppart.getModel());
			
			ret = new AddCommand();
			
			if (constraint instanceof org.eclipse.draw2d.geometry.Rectangle) {
				
				FigureCanvas canvas=(FigureCanvas)
						getHost().getViewer().getControl();

				Viewport port = canvas.getViewport();
		
				// TODO: The rect is the region being moved, so
				// need to locate its real position within the
				// diagram as opposed to just the containing host
				org.eclipse.draw2d.geometry.Rectangle rect=
						(org.eclipse.draw2d.geometry.Rectangle)constraint;
					
				int x=port.getClientArea().x + rect.x;
		
				Role participant=ViewSupport.getNearestRole(x,
						comppart.getScenarioDiagram());
		
				ret.setRole(participant);
				
				// Calculate the index position
				int y = port.getClientArea().y + rect.y -
					ViewSupport.getHeaderPadding(((ScenarioBaseEditPart)getHost()).getModel());
		
				int index=0;
				java.util.List list=ModelSupport.getChildren(((ScenarioBaseEditPart)getHost()).getModel());
				
				for (int i=0; y > 0 && i < list.size(); i++) {
					index++;
					
					y -= ViewSupport.getHeight(list.get(i),
							((ScenarioBaseEditPart)getHost()).getScenarioDiagram());
					
    				y -= ViewSupport.getPadding(list, i);
				}
				
				ret.setIndex(index);
			}

			ret.setNewParent(((ScenarioBaseEditPart)getHost()).getModel());
			ret.setOldParent(oldParent);
			ret.setChild(comppart.getModel());
		}
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand( EditPart child,
													 Object constraint) {
		return(null);
	}

	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, 
            EditPart child, Object constraint) {
		return(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Command	createCommand = null;
		
		return createCommand;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {		
		return super.createChildEditPolicy( child );
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.XYLayoutEditPolicy#getMinimumSizeFor(org.eclipse.gef.GraphicalEditPart)
	 */
	protected Dimension getMinimumSizeFor(GraphicalEditPart child) {
		return child.getContentPane().getMinimumSize();
	}
	
	
	protected Command getMoveChildrenCommand(Request request) {
		Command ret=null;
		
		ChangeBoundsRequest req=(ChangeBoundsRequest)request;
		
		ScenarioBaseEditPart comppart=null;
		if (req.getEditParts().size() == 1 &&
				req.getEditParts().get(0) instanceof ScenarioBaseEditPart) {
			comppart = (ScenarioBaseEditPart)req.getEditParts().get(0);
		}
		
		if (getHost() instanceof ScenarioBaseEditPart) {

			ret = new MoveCommand();
			
			((MoveCommand)ret).setComponent(comppart.getModel());
			((MoveCommand)ret).setContainer(((ScenarioBaseEditPart)getHost()).getModel());	
		
			FigureCanvas canvas=(FigureCanvas)
					getHost().getViewer().getControl();

			Viewport port = canvas.getViewport();

			int x=port.getClientArea().x + req.getLocation().x;
			
			Role participant=ViewSupport.getNearestRole(x,
					comppart.getScenarioDiagram());
		
			((MoveCommand)ret).setRole(participant);
	
			// Calculate the index position
			int y=port.getClientArea().y + req.getLocation().y
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
			
			((MoveCommand)ret).setIndex(index);
		}
		
		return(ret);
	}
	
	protected Command getOrphanChildrenCommand(Request request) {
		return(null);
	}
	
	protected Command getAddCommand(Request generic) {
		Command ret=null;
		try {
			ret = super.getAddCommand(generic);
		} catch(NullPointerException e) {
			// Ignore - thrown for inappropriate containers
			// based on lack of XYlayout origin. Tried setting
			// this, but it moved the problem, so instead
			// taking the pragmatic approach of ignoring it.
		}
		return(ret);
	}
}
