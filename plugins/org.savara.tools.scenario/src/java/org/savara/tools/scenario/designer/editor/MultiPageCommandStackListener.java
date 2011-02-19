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
 * Jul 5, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;

/**
 * This class listeners for command stacks associated with multiple
 * pages.
 */
class MultiPageCommandStackListener implements CommandStackListener {

	public MultiPageCommandStackListener(Editor editor) {
		m_editor = editor;
	}
	
    /**
     * Adds a <code>CommandStack</code> to observe.
     * @param commandStack
     */
    public void addCommandStack(CommandStack commandStack) {
        commandStacks.add(commandStack);
        commandStack.addCommandStackListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
     */
    public void commandStackChanged(java.util.EventObject event) {
        if (((CommandStack) event.getSource()).isDirty()) {
            // at least one command stack is dirty, 
            // so the multi page editor is dirty too
            m_editor.setDirty(true);
        } else {
            // probably a save, we have to check all command stacks
            boolean oneIsDirty = false;
            for (java.util.Iterator stacks = commandStacks.iterator();
                	stacks.hasNext();) {
                CommandStack stack = (CommandStack) stacks.next();
                if (stack.isDirty()) {
                    oneIsDirty = true;
                    break;
                }
            }
            m_editor.setDirty(oneIsDirty);
        }
    }

    /**
     * Disposed the listener
     */
    public void dispose() {
        for (java.util.Iterator stacks = commandStacks.iterator();
        					stacks.hasNext();) {
            ((CommandStack) stacks.next()).removeCommandStackListener(this);
        }
        commandStacks.clear();
    }

    /**
     * Marks every observed command stack beeing saved.
     * This method should be called whenever the editor/model
     * was saved.
     */
    public void markSaveLocations() {
        for (java.util.Iterator stacks = commandStacks.iterator();
        				stacks.hasNext();) {
            CommandStack stack = (CommandStack) stacks.next();
            stack.markSaveLocation();
        }
    }
    
    /** the observed command stacks */
    private java.util.List commandStacks = new java.util.ArrayList();
    private Editor m_editor=null;
}
