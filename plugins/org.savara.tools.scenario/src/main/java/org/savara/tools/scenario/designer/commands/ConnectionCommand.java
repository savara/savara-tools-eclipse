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
 * Feb 21, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.commands;

import org.eclipse.gef.commands.Command;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.*;

/**
 * This class provides the connection command for the message link.
 */
public class ConnectionCommand extends Command {

	/**
	 * Creates a ConnectionCommand
	 */
	public ConnectionCommand() {
		super(ConnectionCommand_Label);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {

		if (oldSource == null && oldTarget == null) {
			// It is a connection create command 
            
			// Source and target must be pointing to some 
			// real connection point
			if (source == null) {
				return false;
			}
			if (target == null) {
				return false;
			}
			// Avoid loop on a node
			if (source == target) {
				return false;
			}
			
			if ((source instanceof SendEvent) == false) {
				return false;
			}
			
			if ((target instanceof ReceiveEvent) == false) {
				return false;
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// It is a delete connection command
		if (source == null && target == null) {

			// Deletion of the connection is actually handled by
			// the component policy, but left in as a placeholder
			// in case required
			
			/* TODO: GPB: need scenario
			if (m_link != null &&
					m_link.getScenario() != null) {
				
				m_link.getScenario().
						getMessageLinks().remove(m_link);
			}
			*/
			
		}
		// It is a reconnect source command 
		if (oldSource != null && source != null) {
			// The edge is still linked to the oldSource
			/* TODO: GPB May need to check for existing value and
			 * do relevant update
			if (m_relationshipType.getFirstRole() != null) {
				.....
			}
			 */
			
			m_link.setSource(source);
		}
		// It is a reconnect target command
		if (oldTarget != null && target != null) {
			// The target is still linked to the oldTarget
			/* same as above
 			if (m_relationshipType.getTarget() != null) {
				....
			}
			*/
			
			m_link.setTarget(target);
		}

		// It is a connection create command
		if (oldSource == null && oldTarget == null) {			
			m_link.setSource(source);
			m_link.setTarget(target);
						
			if (m_pasteParent != null) {
				ModelSupport.addChild(m_pasteParent, source, -1);
				ModelSupport.addChild(m_pasteParent, target, -1);
			}
			
			/* TODO: GPB: need scenario
			source.getScenario().
					getLinks().add(m_link);
			*/
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	public String getLabel() {
		return ConnectionCommand_Description;
	}

	/**
	 * Returns the source
	 * @return
	 */
	public MessageEvent getSource() {
		return(source);
	}

	/**
	 * Returns the target.
	 * @return
	 */
	public MessageEvent getTarget() {
		return(target);
	}

	/**
	 * Returns the edge.
	 * @return
	 */
	public Link getLink() {
		return m_link;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}

	public void setPasteParent(Object parent) {
		m_pasteParent = parent;
	}
	
	/**
	 * Sets the source.
	 * @param newSource
	 */
	public void setSource(MessageEvent newSource) {
		source = newSource;
	}

	/**
	 * Sets the target.
	 * @param newTarget
	 */
	public void setTarget(MessageEvent newTarget) {
		target = newTarget;
	}

	/**
	 * Sets the edge.
	 * @param edge
	 */
	public void setLink(Link rel) {
		this.m_link = rel;
		oldSource = (MessageEvent)m_link.getSource();
		oldTarget = (MessageEvent)m_link.getTarget();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		// It is a delete connection command
		if (source == null && target == null) {
			// Deletion currently handled by component command
			// policy
			m_link = new Link();
			m_link.setSource(oldSource);
			m_link.setTarget(oldTarget);
			
			/* TODO: GPB: need scenario
			oldSource.getScenario().
					getLinks().add(m_link);
			*/
		}

		// It was a reconnect source command 
		if (oldSource != null && source != null) {		
			m_link.setSource(oldSource);
		}
		
		// It was a reconnect target command
		if (oldTarget != null && target != null) {
			m_link.setTarget(oldTarget);
		}

		// It was a connection create command
		if (oldSource == null && oldTarget == null) {

			/* TODO: GPB: need scenario
			m_link.getScenario().
					getLinks().remove(m_link);
			*/
			
			if (m_pasteParent != null) {
				ModelSupport.removeChild(m_pasteParent, source);
				ModelSupport.removeChild(m_pasteParent, target);
			}
		}
	}
	
    /**
     * Sets the old source (for reconnecting)
     * @param port
     */
    public void setOldSource(MessageEvent source) {
        oldSource = source;
    }

    /**
     * Sets the old target (for reconnecting)
     * @param port
     */
    public void setOldTarget(MessageEvent target) {
        oldTarget = target;
    }

    private static final String ConnectionCommand_Label = "connect message events";
	private static final String ConnectionCommand_Description =
		"message event connection command";
			
	// Connection are made from an output to an input port
	protected MessageEvent source;
	protected MessageEvent target;
	
	// Old source and target
	protected MessageEvent oldSource;
	protected MessageEvent oldTarget;
	
	private Object m_pasteParent=null;
	
	// Selected edge. It can be given to the command or 
	// created by the command itself.
	protected Link m_link=null;
}
