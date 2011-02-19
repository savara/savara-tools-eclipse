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
 * Jan 5, 2006 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * This interface represents an editor within the pi4soa designer.
 *
 */
public interface Editor {

    /**
     * This method returns the dirty status of the edited content.
     * 
     * @return Whether the content is dirty.
     * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
     */
    public boolean isDirty();
    
    /**
	 * This method indicates that the edited content is dirty.
	 * 
	 * @param dirty The dirty status
	 */
	public void setDirty(boolean dirty);
	
    /**
     * This method determines if the editor is currently in
     * the process of saving its modified content.
     * 
     * @return Whether the editor is currently saving
     */
    public boolean isEditorSaving();
    
    /**
     * Closes this editor.
     * @param save
     */
    public void closeEditor(final boolean save);
    
    /**
     * This method returns the editor input.
     * 
     * @return The editor input
     */
    public IEditorInput getEditorInput();
    
    /**
     * This method sets the editor input.
     * 
     * @param input The editor input
     */
    public void setInput(IEditorInput input);
    
    /**
     * This method returns the editor site.
     * 
     * @return The editor site
     */
    public IWorkbenchPartSite getSite();
    
    /**
     * This method return the primary description being
     * presented by the editor.
     * 
     * @return The description
     */
    public Object getDescription();
    
    /**
     * This method saves the content.
     * 
     * @param monitor Progress monitor
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor);
    
    /**
     * This method enables the user to select where the choreography
     * description should be saved to.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs();

    /**
     * This method determines whether the contents can be saved.
     * 
     * @return Whether the contents can be saved
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed();

    /**
     * Returns the default <code>PaletteRoot</code> for this editor and all
     * its pages.
     * @return the default <code>PaletteRoot</code>
     */
    public PaletteRoot getPaletteRoot();
   
    /**
     * Returns the selection syncronizer object. 
     * The synchronizer can be used to sync the selection of 2 or more
     * EditPartViewers.
     * @return the syncrhonizer
     */
    public SelectionSynchronizer getSelectionSynchronizer();
    
    /**
     * Returns the action registry of this editor.
     * @return the action registry
     */
    public ActionRegistry getActionRegistry();

    /**
     * This method returns the shared key handler.
     * 
     * @return The shared key handler
     */
    public KeyHandler getSharedKeyHandler();

}
