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
 * Feb 14, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Adds commands to the action bar.
 */
public class DesignerActionBarContributor extends ActionBarContributor {

    /* (non-Javadoc)
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
     */
    protected void buildActions() {
        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());
        addRetargetAction(new DeleteRetargetAction());
        
        IWorkbenchWindow iww = getPage().getWorkbenchWindow();
        addRetargetAction((org.eclipse.ui.actions.RetargetAction)
        		org.eclipse.ui.actions.ActionFactory.COPY.create(iww));
        addRetargetAction((org.eclipse.ui.actions.RetargetAction)
        		org.eclipse.ui.actions.ActionFactory.PASTE.create(iww));
    
        addRetargetAction(new ZoomInRetargetAction());
        addRetargetAction(new ZoomOutRetargetAction());
        
        org.eclipse.ui.actions.RetargetAction act=
        	new org.eclipse.ui.actions.RetargetAction(
        		SimulateScenarioAction.ID, "Simulate Scenario");
        act.setImageDescriptor(org.savara.tools.scenario.designer.DesignerImages.getImageDescriptor("Simulate.png"));
        addRetargetAction(act);
        
        act = new org.eclipse.ui.actions.RetargetAction(
        		ResetSimulationAction.ID, "Reset Simulation");
        act.setImageDescriptor(org.savara.tools.scenario.designer.DesignerImages.getImageDescriptor("ResetSimulation.png"));
        addRetargetAction(act);
        
        act = new org.eclipse.ui.actions.RetargetAction(
        		GenerateImageAction.ID, "Generate Image");
        act.setImageDescriptor(org.savara.tools.scenario.designer.DesignerImages.getImageDescriptor("GenerateImage.gif"));
        addRetargetAction(act);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
     */
    protected void declareGlobalActionKeys() {
        addGlobalActionKey(org.eclipse.ui.actions.ActionFactory.PRINT.getId());
		addGlobalActionKey(org.eclipse.ui.actions.ActionFactory.DELETE.getId());
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void contributeToMenu(IMenuManager menuManager) {
        super.contributeToMenu(menuManager);

        // add a "View" menu after "Edit"
        MenuManager viewMenu = new MenuManager("View");
        viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
        viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
        
        menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    public void contributeToToolBar(IToolBarManager toolBarManager) {
        super.contributeToToolBar(toolBarManager);

        toolBarManager.add(getAction(org.eclipse.ui.actions.ActionFactory.UNDO.getId()));
        toolBarManager.add(getAction(org.eclipse.ui.actions.ActionFactory.REDO.getId()));
        
        toolBarManager.add(getAction(org.eclipse.ui.actions.ActionFactory.COPY.getId()));
        toolBarManager.add(getAction(org.eclipse.ui.actions.ActionFactory.PASTE.getId()));
    
        toolBarManager.add(new Separator());
        toolBarManager.add(new ZoomComboContributionItem(getPage()));

        toolBarManager.add(new Separator());
        toolBarManager.add(getAction(org.savara.tools.scenario.designer.editor.SimulateScenarioAction.ID));
        toolBarManager.add(getAction(org.savara.tools.scenario.designer.editor.ResetSimulationAction.ID));
        toolBarManager.add(getAction(org.savara.tools.scenario.designer.editor.GenerateImageAction.ID));
    }	
}
