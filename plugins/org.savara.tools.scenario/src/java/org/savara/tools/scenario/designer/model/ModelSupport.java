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
 * 16 Feb 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.model;

import java.util.logging.Logger;
import org.savara.scenario.model.*;

public class ModelSupport {
	
	private static Logger logger = Logger.getLogger(ModelSupport.class.getName());

	public static java.util.List<Event> getChildren(Object component) {
		java.util.List<Event> ret=null;
		
		if (component instanceof Scenario) {
			ret = ((Scenario)component).getEvent();
		} else if (component instanceof Group) {
			ret = ((Group)component).getEvent();
		}
		
		return(ret);
	}
	
	public static Object getParent(Object component) {
		Object ret=null;
		
		// TODO: GPB: how to get parent???
		
		/*
		if (component instanceof org.eclipse.emf.ecore.EObject) {
			ret = ((org.eclipse.emf.ecore.EObject)component).eContainer();
		//} else if (component instanceof Participant) {
		//	ret = ((Participant)component).getDiagram();
		}
		*/
		
		return(ret);
	}
	
	public static int getChildIndex(Object parent, Object child) {
		int ret=0;
		
		if (child instanceof Role) {
			// TODO: GPB: Need to get scenario??
			//ret = ((Participant)child).getScenario().
			//		getParticipants().indexOf(child);
		} else {
			java.util.List children=getChildren(parent);
		
			ret = children.indexOf(child);
		}
		
		return(ret);
	}
	
	/**
	 * This method adds a new child to the parent.
	 * 
	 * @param parent The parent
	 * @param child The child
	 * @param index The index of the new child
	 */
	public static void addChild(Object parent, Object child,
						int index) {

		if (parent != null && child != null) {
			java.util.List list=null;
			
			if (child instanceof Role && parent instanceof Scenario) {
				list = ((Scenario)parent).getRole();
			} else {
				list = getChildren(parent);		
			}
	
			if (list != null) {
				
				if (index == -1 || index > list.size()) {
					list.add(child);
				} else {
					list.add(index, child);
				}
				
			} else {
				logger.severe("DON'T KNOW HOW TO ADD: child class="+child+
						" to parent="+parent);				
			}
		}
	}
	
	/**
	 * This method removes a child from the parent.
	 * 
	 * @param parent The parent
	 * @param child The child
	 */
	public static void removeChild(Object parent, Object child) {
		
		if (parent != null && child != null) {
			java.util.List list=null;
			
			if (parent instanceof Scenario &&
					child instanceof Role) {				
				((Scenario)parent).getRole().remove(child);
			} else {
				list = getChildren(parent);		
			}
			
			if (list != null) {
				list.remove(child);
								
			} else {
				logger.severe("DON'T KNOW HOW TO REMOVE: child class="+child+
						" to parent="+parent);				
			}
		}
	}
	
	public static boolean isValidTarget(Object child, Object parent) {
		boolean ret=false;
		
		if ((parent instanceof Scenario  ||
				parent instanceof Group) &&
				child instanceof Role) {
			ret = true;
		} else if ((parent instanceof Scenario ||
					parent instanceof Group) &&
					child instanceof Event) {
			ret = true;
		} else if ((parent instanceof Scenario || parent
					instanceof Group) &&
					child instanceof Link) {
			ret = true;
		}
		
		logger.info("Is valid target: parent="+parent+" child="+child+" ret="+ret);
		
		return(ret);
	}
	
	public static java.util.List getSourceConnections(Scenario scenario, Object component) {
		java.util.List ret=new java.util.Vector();
		
		if (component instanceof MessageEvent) {
			/* TODO: GPB: Need to get scenario???
			Scenario scenario = ((MessageEvent)component).getScenario();
			*/
			
			java.util.List links=scenario.getLink();
			
			for (int i=0; i < links.size(); i++) {
				Link link=(Link)links.get(i);
				
				if (link.getSource() == component) {
					ret.add(link);
				}
			}
			//*/
		}
		
		return(ret);
	}
	
	public static java.util.List getTargetConnections(Scenario scenario, Object component) {
		java.util.List ret=new java.util.Vector();
		
		if (component instanceof MessageEvent) {
			/* TODO: GPB: Need to get scenario???
			Scenario scenario = ((MessageEvent)component).getScenario();
			*/
			
			java.util.List links=scenario.getLink();
			
			for (int i=0; i < links.size(); i++) {
				Link link=(Link)links.get(i);
				
				if (link.getTarget() == component) {
					ret.add(link);
				}
			}
			//*/
		}
		
		return(ret);
	}
}
