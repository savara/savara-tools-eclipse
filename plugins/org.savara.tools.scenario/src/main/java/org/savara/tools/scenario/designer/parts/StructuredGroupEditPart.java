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
package org.savara.tools.scenario.designer.parts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.draw2d.Label;
import org.savara.tools.scenario.designer.figures.GroupingFigure;
import org.savara.tools.scenario.designer.model.ModelSupport;
import org.savara.tools.scenario.designer.policies.ScenarioComponentEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioContainerEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioContainerHighlightEditPolicy;
import org.savara.tools.scenario.designer.policies.ScenarioContainerXYLayoutEditPolicy;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This edit part represents a structured group of other parts.
 */
public abstract class StructuredGroupEditPart extends ScenarioBaseEditPart {

	public StructuredGroupEditPart(Object elem) {
		super(elem);
	}
	
	/**
	 * @see org.eclipse.gef.examples.flow.parts.ActivityPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ScenarioComponentEditPolicy());
		installEditPolicy(
			EditPolicy.SELECTION_FEEDBACK_ROLE,
			new ScenarioContainerHighlightEditPolicy());
	
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ScenarioContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ScenarioContainerXYLayoutEditPolicy());
	}

	private boolean directEditHitTest(Point requestLoc) {
		/*
		IFigure header = ((GroupFigure)getFigure()).getHeader();
		header.translateToRelative(requestLoc);
		if (header.containsPoint(requestLoc))
			return true;
			*/
		return false;
	}

	/**
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			if (request instanceof DirectEditRequest
				&& !directEditHitTest(((DirectEditRequest) request)
					.getLocation()
					.getCopy()))
				return;
			performDirectEdit();
		}
	}

	int getAnchorOffset() {
		return -1;
	}

	public IFigure getContentPane() {
		if (getFigure() instanceof GroupingFigure)
			return ((GroupingFigure)getFigure()).getContents();
		return getFigure();
	}

	protected List getModelChildren() {
		return(ModelSupport.getChildren(getModel()));
	}

	/**
	 * @see org.eclipse.gef.examples.flow.parts.ActivityPart#performDirectEdit()
	 */
	protected void performDirectEdit() {
		/*
		if (manager == null) {
			Label l = ((Label)((SubgraphFigure) getFigure()).getHeader());
			manager =
				new ActivityDirectEditManager(
					this,
					TextCellEditor.class,
					new ActivityCellEditorLocator(l),l);
		}
		manager.show();
	*/
	}
	
    /**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		((Label)((GroupingFigure)getFigure()).getHeader()).setText(
					ViewSupport.getName(getModel(), getScenarioDiagram()));

		ViewSupport.setTooltip(getFigure(), getModel());

		super.refreshVisuals();
	}

    public int getHeight() {
    	int ret=ViewSupport.getHeight(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }
    
    public int getWidth() {
    	int ret=ViewSupport.getWidth(getModel(), getScenarioDiagram());
    	
    	return(ret);
    }
}
