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

import java.util.EventObject;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.commands.UnexecutableCommand;

/**
 * This class implements the delegating command stack.
 */
public class DelegatingCommandStack extends CommandStack
			implements CommandStackListener {

    /**
     * Returns the current <code>CommandStack</code>.
     * @return the current <code>CommandStack</code>
     */
    public CommandStack getCurrentCommandStack()
    {
        return currentCommandStack;
    }

    /**
     * Sets the current <code>CommandStack</code>.
     * @param stack the <code>CommandStack</code> to set
     */
    public void setCurrentCommandStack(CommandStack stack) {
        if (currentCommandStack == stack) {
            return;
        }

        // remove from old command stack
        if (null != currentCommandStack) {
            currentCommandStack.removeCommandStackListener(this);
        }

        // set new command stack
        currentCommandStack = stack;

        // watch new command stack
        currentCommandStack.addCommandStackListener(this);

        // the command stack changed
        notifyListeners();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#canRedo()
     */
    public boolean canRedo() {
        if (null == currentCommandStack) {
            return false;
        }

        return currentCommandStack.canRedo();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#canUndo()
     */
    public boolean canUndo() {
        if (null == currentCommandStack) {
            return false;
        }

        return currentCommandStack.canUndo();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#dispose()
     */
    public void dispose() {
        if (null != currentCommandStack) {
            currentCommandStack.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#execute(org.eclipse.gef.commands.Command)
     */
    public void execute(Command command) {
        if (null != currentCommandStack) {
            currentCommandStack.execute(command);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#flush()
     */
    public void flush() {
        if (null != currentCommandStack) {
            currentCommandStack.flush();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#getCommands()
     */
    public Object[] getCommands() {
        if (null == currentCommandStack) {
            return EMPTY_OBJECT_ARRAY;
        }

        return currentCommandStack.getCommands();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#getRedoCommand()
     */
    public Command getRedoCommand() {
        if (null == currentCommandStack) {
            return UnexecutableCommand.INSTANCE;
        }

        return currentCommandStack.getRedoCommand();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#getUndoCommand()
     */
    public Command getUndoCommand() {
        if (null == currentCommandStack) {
            return UnexecutableCommand.INSTANCE;
        }

        return currentCommandStack.getUndoCommand();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#getUndoLimit()
     */
    public int getUndoLimit() {
        if (null == currentCommandStack) {
            return -1;
        }

        return currentCommandStack.getUndoLimit();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#isDirty()
     */
    public boolean isDirty() {
        if (null == currentCommandStack) {
            return false;
        }

        return currentCommandStack.isDirty();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#markSaveLocation()
     */
    public void markSaveLocation() {
        if (null != currentCommandStack) {
            currentCommandStack.markSaveLocation();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#redo()
     */
    public void redo() {
        if (null != currentCommandStack) {
            currentCommandStack.redo();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#setUndoLimit(int)
     */
    public void setUndoLimit(int undoLimit) {
        if (null != currentCommandStack) {
            currentCommandStack.setUndoLimit(undoLimit);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStack#undo()
     */
    public void undo() {
        if (null != currentCommandStack) {
            currentCommandStack.undo();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "DelegatingCommandStack(" + currentCommandStack + ")";
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
     */
    public void commandStackChanged(EventObject event) {
        notifyListeners();
    }

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
    private CommandStack currentCommandStack;
}
