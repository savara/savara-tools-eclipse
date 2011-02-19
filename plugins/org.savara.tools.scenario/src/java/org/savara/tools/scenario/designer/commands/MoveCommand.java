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
 * Feb 16, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.commands;

import org.eclipse.gef.commands.Command;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.ModelSupport;

/**
 * This class provides the move command.
 */
public class MoveCommand extends Command {

	/**
	 * Creates a move command
	 */
	public MoveCommand() {
		super(MoveCommand_Label);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		
		if (m_container == null ||
				m_component == null) {
			return(false);
		}
		
		if (m_container == m_component) {
			return(false);
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		java.util.List list=ModelSupport.getChildren(m_container);
		
		m_oldIndex = list.indexOf(m_component);

		// Check if child is a scenario event
		if (m_component instanceof Event &&
				m_participant != null) {
			m_oldParticipant = (Role)((Event)m_component).getRole();
			
			((Event)m_component).setRole(m_participant);
		}
		
		// Remove from old container
		ModelSupport.removeChild(m_container, m_component);
		
		// Add to new container
		int index=m_index;
		
		if (index > m_oldIndex) {
			index--;
		}
		ModelSupport.addChild(m_container, m_component, index);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	public String getLabel() {
		return MoveCommand_Description;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		
		// Remove from new container
		ModelSupport.removeChild(m_container, m_component);
		
		// Check if child is a scenario event
		if (m_component instanceof Event &&
				m_participant != null) {
			((Event)m_component).setRole(m_oldParticipant);
		}
		
		// Add to old container
		ModelSupport.addChild(m_container, m_component, m_oldIndex);
	}
	
	public void setContainer(Object container) {
		m_container = container;
	}
	
	public void setComponent(Object component) {
		m_component = component;
	}
	
	public void setRole(Role participant) {
		m_participant = participant;
	}
	
	public void setIndex(int index) {
		m_index = index;
	}
	
	public Object getContainer() {
		return(m_container);
	}
	
	public Object getComponent() {
		return(m_component);
	}
	
	private Object m_container=null;
	private Object m_component=null;
	private int m_index=-1;
	private int m_oldIndex=-1;
	private Role m_participant=null;
	private Role m_oldParticipant=null;

    private static final String MoveCommand_Label = "move";
	private static final String MoveCommand_Description =
		"move command";
}
