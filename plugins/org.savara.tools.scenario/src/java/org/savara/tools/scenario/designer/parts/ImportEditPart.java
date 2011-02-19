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
 * Mar 29, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.parts;

import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.figures.*;
import org.savara.tools.scenario.designer.policies.*;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This class provides a time elapse event edit part.
 */
public class ImportEditPart extends ScenarioBaseEditPart {

	public ImportEditPart(Object elem) {
		super(elem);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure ret=null;
		
		ret = new ImportFigure(getScenarioDiagram());
		
		return(ret);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ScenarioComponentEditPolicy());

		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ScenarioContainerXYLayoutEditPolicy());
	}
	
	public void performRequest(org.eclipse.gef.Request request) {
		
		org.eclipse.gef.EditPartViewer viewer=getRoot().getViewer();
		
		// GPB: Not the ideal way to get to the workbench, but
		// quickest for now, using the scenario simulation interface
		// but need to tidy up and find how an editpart can 
		// more easily navigate to the workbench
		if (viewer.getEditPartFactory() instanceof ScenarioEditPartsFactory) {
			ScenarioEditPartsFactory factory=(ScenarioEditPartsFactory)
					viewer.getEditPartFactory();
			
			factory.getSimulation().focus(
					((Import)getModel()).getUrl(),
					((Import)getModel()).getGroup());
		}
	}
	
	/*
	}
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
		String text=((Import)getModel()).getUrl();
		
		if (((Import)getModel()).getGroup() != null) {
			text += "["+((Import)getModel()).getGroup()+"]";
		}
		
		((ImportFigure)getFigure()).setURL(text);
		
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
    
    public void reset() {
    	((ImportFigure)getFigure()).setState(ImportFigure.STATE_RESET);
		
		super.reset();
    }
    
	public void processing() {
    	((ImportFigure)getFigure()).setState(ImportFigure.STATE_PROCESSING);
	}
	
    public void successful() {
    	((ImportFigure)getFigure()).setState(ImportFigure.STATE_SUCCESSFUL);
    }
    
    public void unsuccessful() {
    	((ImportFigure)getFigure()).setState(ImportFigure.STATE_UNSUCCESSFUL);
    }
    
	public boolean isUnsuccessful() {
		return(((ImportFigure)getFigure()).getState() ==
			ImportFigure.STATE_UNSUCCESSFUL);
	}
}
