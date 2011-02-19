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
 * Jul 7, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.commands;

import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.tools.CreateLinksTool;

/**
 * This class implements the activity creation command.
 */
public class PasteComponentCommand
			extends org.eclipse.gef.commands.Command {
	
	public PasteComponentCommand() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		
		// Check if child is a scenario event
		if (m_child instanceof Event &&
				m_role != null) {
			((Event)m_child).setRole(m_role);
		}
		
		// note that the model adds the ports to the node in this call
		ModelSupport.addChild(m_parent, m_child, m_index);
		
		// Check if source is an event group. If so, then
		// create message links
		if (m_child instanceof Group) {
			/* TODO: GPB: need scenario
			CreateLinksTool tool=
				new CreateLinksTool(((Group)m_child).getScenario());
			
			int numlinks=((Group)m_child).getScenario().getLinks().size();
			
			tool.run(((Group)m_child).getEvents());
			
			for (int i=numlinks; i <
					((Group)m_child).getScenario().getLinks().size(); i++) {
				m_additionalLinks.add(((Group)m_child).getScenario().getLinks().get(i));
			}
			*/
		}
	}
	
	public Object getParent() {
		return(m_parent);
	}
	
	public void setRole(Role role) {
		m_role = role;
	}
	
	public void redo() {
		ModelSupport.addChild(m_parent, m_child, m_index);
	}
	
	public void setChild(Object newNode) {
		m_child = newNode;
	}
	
	public void setIndex(int index) {
		m_index = index;
	}
	
	public void setParent(Object newParent) {
		m_parent = newParent;
	}
	
	public void undo() {
		for (int i=0; i < m_additionalLinks.size(); i++) {
			/* TODO: GPB: need scenario
			((Group)m_child).getScenario().getMessageLinks().remove(
						m_additionalLinks.get(i));
				*/
		}
		
		m_additionalLinks.clear();

		ModelSupport.removeChild(m_parent, m_child);
	}

	private Object m_child=null;
	private Object m_parent=null;
	private Role m_role=null;
	private int m_index = -1;
	private java.util.Vector m_additionalLinks=new java.util.Vector();
}
