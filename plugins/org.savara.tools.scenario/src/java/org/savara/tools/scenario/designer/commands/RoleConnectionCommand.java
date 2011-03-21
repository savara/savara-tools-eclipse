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
public class RoleConnectionCommand extends Command {

	/**
	 * Creates a ConnectionCommand
	 */
	public RoleConnectionCommand() {
		super(ConnectionCommand_Label);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {

		// Source and target must be pointing to some 
		// real connection point
		if (source == null) {
			return false;
		}
		if (target == null) {
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// It is a delete connection command
		/*
		if (source == null && target == null) {

			// Deletion of the connection is actually handled by
			// the component policy, but left in as a placeholder
			// in case required
			if (m_messageLink != null &&
					m_messageLink.getScenario() != null) {
				
				m_messageLink.getScenario().
						getMessageLinks().remove(m_messageLink);
			}
			
		}
		*/
		
		if (m_sourceEvent == null && source instanceof Role) {
			m_sourceEvent=new SendEvent();
			m_sourceEvent.setRole((Role)source);
			
			m_link.setSource(m_sourceEvent);
		} else {
			m_link.setSource(m_sourceEvent);
		}

		if (m_targetEvent == null) {
			m_targetEvent=new ReceiveEvent();
			m_targetEvent.setRole(target);

			if (source instanceof MessageEvent) {
				m_targetEvent.setErrorExpected(
						((MessageEvent)source).isErrorExpected());
				m_targetEvent.setFaultName(
						((MessageEvent)source).getFaultName());
				m_targetEvent.setOperationName(
						((MessageEvent)source).getOperationName());
				
				for (Parameter p : ((MessageEvent)source).getParameter()) {
					Parameter tp=new Parameter();
					tp.setType(p.getType());
					tp.setValue(p.getValue());
					m_targetEvent.getParameter().add(tp);
				}
			}
			
			m_link.setTarget(m_targetEvent);
		}
	
		/* TODO: GPB: How to get scenario?
		if (source instanceof Role) {
			((Role)source).getScenario().
				getMessageLinks().add(m_link);
		} else if (source instanceof MessageEvent) {
			((MessageEvent)source).getScenario().
					getMessageLinks().add(m_link);
		}
		*/
		
		if (source instanceof Role) {
			ModelSupport.addChild(m_sourceParent, m_sourceEvent, m_sourceIndex);
		}
		ModelSupport.addChild(m_targetParent, m_targetEvent, m_targetIndex);
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
	public Object getSource() {
		return(source);
	}

	/**
	 * Returns the target.
	 * @return
	 */
	public Role getTarget() {
		return(target);
	}

	public void setSourceIndex(int index) {
		m_sourceIndex = index;
	}
	
	public void setTargetIndex(int index) {
		m_targetIndex = index;
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

	/**
	 * Sets the source.
	 * @param newSource
	 */
	public void setSource(Object newSource) {
		source = newSource;
		
		if (newSource instanceof MessageEvent) {
			m_sourceEvent = (MessageEvent)newSource;
		}
	}
	
	public void setSourceParent(Object parent) {
		m_sourceParent = parent;
	}

	public Object getSourceParent() {
		return(m_sourceParent);
	}

	/**
	 * Sets the target.
	 * @param newTarget
	 */
	public void setTarget(Role newTarget) {
		target = newTarget;
	}

	public void setTargetParent(Object parent) {
		m_targetParent = parent;
	}
	
	public Object getTargetParent() {
		return(m_targetParent);
	}

	/**
	 * Sets the edge.
	 * @param edge
	 */
	public void setLink(Link rel) {
		this.m_link = rel;
		/*
		oldSource = m_messageLink.getSource();
		oldTarget = m_messageLink.getTarget();
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		/*
		// It is a delete connection command
		if (source == null && target == null) {
			// Deletion currently handled by component command
			// policy
			m_messageLink = ScenarioFactory.eINSTANCE.createMessageLink();
			m_messageLink.setSource(oldSource);
			m_messageLink.setTarget(oldTarget);
			
			oldSource.getScenario().
					getMessageLinks().add(m_messageLink);
		}

		// It was a reconnect source command 
		if (oldSource != null && source != null) {		
			m_messageLink.setSource(oldSource);
		}
		
		// It was a reconnect target command
		if (oldTarget != null && target != null) {
			m_messageLink.setTarget(oldTarget);
		}

		// It was a connection create command
		if (oldSource == null && oldTarget == null) {

			m_messageLink.getScenario().
					getMessageLinks().remove(m_messageLink);
		}
		*/
		
		/* TODO: GPB: How to obtain scenario?
		if (source instanceof Role) {
			((Role)source).getScenario().
				getMessageLinks().remove(m_link);
		} else if (source instanceof MessageEvent) {
			((MessageEvent)source).getScenario().
					getMessageLinks().remove(m_link);
		}
		*/
		
		if (source instanceof Role) {
			ModelSupport.removeChild(m_sourceParent, m_sourceEvent);
		}
		ModelSupport.removeChild(m_targetParent, m_targetEvent);
	}
	
    private static final String ConnectionCommand_Label = "connect participants";
	private static final String ConnectionCommand_Description =
		"participant connection command";
			
	// Connection are made from an output to an input port
	protected Object source;
	private Object m_sourceParent=null;
	protected Role target;
	private Object m_targetParent=null;
	
	private int m_sourceIndex=-1;
	private int m_targetIndex=-1;
	
	private MessageEvent m_sourceEvent=null;
	private MessageEvent m_targetEvent=null;
	
	// Selected edge. It can be given to the command or 
	// created by the command itself.
	protected Link m_link=null;
}
