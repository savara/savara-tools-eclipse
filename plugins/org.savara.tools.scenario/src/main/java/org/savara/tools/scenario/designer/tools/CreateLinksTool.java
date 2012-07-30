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
 * 23 Feb 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.tools;

import org.savara.scenario.model.*;

public class CreateLinksTool {

	public CreateLinksTool(Scenario scenario) {
		m_scenario = scenario;
	}
	
	public void run() {
		run(m_scenario.getEvent());
	}
	
	public void run(java.util.List list) {
		scanEvents(list);
	}
	
	protected void scanEvents(java.util.List list) {
		
		// Scan for sends that are not associated with an
		// existing message link
		for (int i=0; i < list.size(); i++) {
			Event obj=(Event)list.get(i);
			
			if (obj instanceof SendEvent) {
				
				if (getLinks((MessageEvent)obj).size() == 0) {
					
					// Search for an appropriate receive message
					// event within the same group
					MessageEvent recv=findReceive((MessageEvent)obj,
							list, i+1);
					
					if (recv != null) {
						Link link=new Link();
						
						link.setSource((MessageEvent)obj);
						link.setTarget(recv);
						
						m_scenario.getLink().add(link);
						
						m_changed = true;
					}
				}
				
			} else if (obj instanceof Group) {
				scanEvents(((Group)obj).getEvent());
			}
		}
	}
	
	/**
	 * This method attempts to locate a compatible receive for
	 * the supplied send message event, within the supplied list
	 * starting at the specified index.
	 * 
	 * @param send The send message event
	 * @param list The list
	 * @param index The starting index
	 * @return The receive message event, or null if not found
	 */
	protected MessageEvent findReceive(MessageEvent send,
			java.util.List list, int index) {
		MessageEvent ret=null;
		
		for (int i=index; ret == null && i < list.size(); i++) {
			if (list.get(i) instanceof MessageEvent) {
				MessageEvent me=(MessageEvent)list.get(i);
				
				if (me instanceof ReceiveEvent &&
						getLinks(me).size() == 0) {
					if (isSame(me.getOperationName(),
							send.getOperationName()) &&
						isSame(me.getFaultName(),
							send.getFaultName())) {
						
						boolean f_matchParameters=(me.getParameter().size() == 
									send.getParameter().size());
						
						for (int j=0; f_matchParameters &&
									j < me.getParameter().size(); j++) {
							f_matchParameters = me.getParameter().get(j).getType().equals(
									send.getParameter().get(j).getType());
						}
						
						if (f_matchParameters) {
							ret = me;
						}
					}
				}
			}
		}
		
		return(ret);
	}
	
	protected boolean isSame(String val1, String val2) {
		boolean ret=false;
		
		if (val1 != null &&
			val2 != null &&
				val1.equals(val2)) {
			ret = true;
		} else if (val1 == null &&
				val2 == null) {
			ret = true;
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the links that are associated with the
	 * supplied message event.
	 * 
	 * @param me The message event
	 * @return The list of message links
	 */
	protected java.util.List getLinks(MessageEvent me) {
		java.util.Vector ret=new java.util.Vector();
		
		for (int i=0; i < m_scenario.getLink().size(); i++) {
			Link link=(Link)
					m_scenario.getLink().get(i);
			
			if (link.getSource() == me || link.getTarget() == me) {
				ret.add(link);
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method determines whether the scenario has been
	 * changed.
	 * 
	 * @return Whether the scenario has been changed
	 */
	public boolean isScenarioChanged() {
		return(m_changed);
	}
	
	private Scenario m_scenario=null;
	private boolean m_changed=false;
}
