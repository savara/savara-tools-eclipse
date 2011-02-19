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
 * Feb 23, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * The editor's context menu.
 */
public class EditorContextMenuProvider extends ContextMenuProvider {

    /**
     * Creates a new WorkflowEditorContextMenuProvider instance.
     * @param viewer
     */
    public EditorContextMenuProvider(EditPartViewer viewer,
    			ActionRegistry actionRegistry) {
        super(viewer);
        m_actionRegistry = actionRegistry;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void buildContextMenu(IMenuManager menuManager) {
        GEFActionConstants.addStandardActionGroups(menuManager);

        appendActionToUndoGroup(menuManager, org.eclipse.ui.actions.ActionFactory.UNDO.getId());
        appendActionToUndoGroup(menuManager, org.eclipse.ui.actions.ActionFactory.REDO.getId());

        appendActionToEditGroup(menuManager, org.eclipse.ui.actions.ActionFactory.COPY.getId());
        appendActionToEditGroup(menuManager, org.eclipse.ui.actions.ActionFactory.PASTE.getId());
        appendActionToEditGroup(menuManager, org.eclipse.ui.actions.ActionFactory.DELETE.getId());
        appendActionToEditGroup(menuManager, GEFActionConstants.DIRECT_EDIT);

        appendScenarioSubmenu(menuManager);

        appendActionToMenu(
            menuManager,
            org.eclipse.ui.actions.ActionFactory.SAVE.getId(),
            GEFActionConstants.GROUP_SAVE);
    }

    /**
     * Appends the alignment subment.
     * @param menuManager
     */
    private void appendScenarioSubmenu(IMenuManager menuManager) {
    	
        // Scenario Actions
        MenuManager submenu = new MenuManager("Scenario");

        IAction action = getActionRegistry().getAction(org.savara.tools.scenario.designer.editor.SimulateScenarioAction.ID);
        if (null != action && action.isEnabled())
            submenu.add(action);

        action = getActionRegistry().getAction(org.savara.tools.scenario.designer.editor.ResetSimulationAction.ID);
        if (null != action && action.isEnabled())
            submenu.add(action);

        action = getActionRegistry().getAction(org.savara.tools.scenario.designer.editor.GenerateImageAction.ID);
        if (null != action && action.isEnabled()) {
            submenu.add(new Separator());
            
            submenu.add(action);
        }
        
        submenu.add(new Separator());
        
        action = getActionRegistry().getAction(org.savara.tools.scenario.designer.editor.CreateLinksAction.ID);
        if (null != action && action.isEnabled())
            submenu.add(action);

        submenu.add(new Separator());
        
        /*
        action = getActionRegistry().getAction(org.savara.tools.scenario.designer.editor.ShowIdentityDetailsAction.ID);
        if (null != action && action.isEnabled())
            submenu.add(action);
         */
        
        if (!submenu.isEmpty())
            menuManager.appendToGroup(GEFActionConstants.GROUP_REST, submenu);
    }

    /**
     * Returns the action registry.
     * @return the action registry
     */
    protected ActionRegistry getActionRegistry() {
        return m_actionRegistry;
    }

    /**
     * Appends the specified action to the specified menu group
     * @param actionId
     * @param menuGroup
     */
    private void appendActionToMenu(IMenuManager menu,
    				String actionId, String menuGroup) {
        IAction action = getActionRegistry().getAction(actionId);
        if (null != action && action.isEnabled()) {
            menu.appendToGroup(menuGroup, action);
        }
    }

    /**
     * Appends the specified action to the specified menu group
     * @param actionId
     * @param menuGroup
     */
    private void appendActionToUndoGroup(IMenuManager menu, String actionId) {
        IAction action = getActionRegistry().getAction(actionId);
        if (null != action && action.isEnabled()) {
            menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        }
    }

    /**
     * Appends the specified action to the specified menu group
     * @param actionId
     * @param menuGroup
     */
    private void appendActionToEditGroup(IMenuManager menu, String actionId) {
        IAction action = getActionRegistry().getAction(actionId);
        if (null != action && action.isEnabled()) {
            menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        }
    }

    /** the action registry */
    private ActionRegistry m_actionRegistry=null;
}
