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
 * Feb 20, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.commands;

import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.ModelSupport;

/**
 * This class implements the activity deletion command.
 */
public class DeleteLinkCommand
			extends org.eclipse.gef.commands.Command {
	
	public DeleteLinkCommand() {
	}
	
	public boolean canExecute() {
		boolean ret=false;
		
		if (m_parent != null && m_child != null) {
			ret = true;
		}
		
		return(ret);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		m_sourceParent = ModelSupport.getParent((Scenario)getParent(), m_sourceEvent);
		m_targetParent = ModelSupport.getParent((Scenario)getParent(), m_targetEvent);
		
		m_index = m_parent.getLink().indexOf(m_child);
		
		if (m_sourceParent != null && m_sourceEvent != null &&
				ModelSupport.getSourceConnections((Scenario)getParent(), m_sourceEvent).size() == 1) {
			m_sourceEventIndex = ModelSupport.getChildIndex(m_sourceParent, m_sourceEvent);
			
			ModelSupport.removeChild(m_sourceParent, m_sourceEvent);		
		}
		
		if (m_targetParent != null && m_targetEvent != null &&
				ModelSupport.getTargetConnections((Scenario)getParent(), m_targetEvent).size() == 1) {
			m_targetEventIndex = ModelSupport.getChildIndex(m_targetParent, m_targetEvent);
			
			ModelSupport.removeChild(m_targetParent, m_targetEvent);		
		}
		
		m_parent.getLink().remove(m_child);
		
		m_child.setSource(null);
		m_child.setTarget(null);	
	}
	
	public Object getParent() {
		return(m_parent);
	}
	
	public void redo() {
		execute();
	}
	
	public void setChild(Link newNode) {
		m_child = newNode;
		
		m_sourceEvent = (MessageEvent)m_child.getSource();
		m_targetEvent = (MessageEvent)m_child.getTarget();
	}
	
	public void setParent(Scenario newParent) {
		m_parent = newParent;
	}
	
	public void undo() {
		if (m_index != -1) {

			if (m_targetParent != null && m_targetEvent != null &&
					m_targetEventIndex != -1) {
				
				ModelSupport.addChild(m_targetParent, m_targetEvent,
						m_targetEventIndex);		
			}
			
			if (m_sourceParent != null && m_sourceEvent != null &&
					m_sourceEventIndex != -1) {
					
				ModelSupport.addChild(m_sourceParent, m_sourceEvent,
							m_sourceEventIndex);		
			}
			
			m_child.setTarget(m_targetEvent);				
			m_child.setSource(m_sourceEvent);
			
			m_parent.getLink().add(m_index, m_child);

			m_targetEventIndex = -1;
			m_sourceEventIndex = -1;
		}
	}

	private Link m_child=null;
	private Object m_sourceParent=null;
	private Object m_targetParent=null;
	private MessageEvent m_sourceEvent=null;
	private MessageEvent m_targetEvent=null;
	private int m_sourceEventIndex=-1;
	private int m_targetEventIndex=-1;
	private Scenario m_parent=null;
	private int m_index = -1;
}
