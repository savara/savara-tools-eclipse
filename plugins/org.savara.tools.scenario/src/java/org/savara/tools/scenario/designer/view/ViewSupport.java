/*
 * Copyright 2005-6 Pi4 Technologies Ltd
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
 * 14 Feb 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.view;

import java.util.logging.Logger;

import org.eclipse.swt.graphics.Image;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.DesignerImages;
import org.savara.tools.scenario.designer.model.*;

public class ViewSupport {

	/**
	 * This method returns the appropriate image for this scenario
	 * component on the main display area.
	 * 
	 * @param component The scenario component
	 * @return The image
	 */
	public static Image getImage(Object component, ScenarioDiagram diagram) {
		Image ret=null;
		
		String imageName=component.getClass().getName();
		
		int index=imageName.lastIndexOf('.');
		if (index != -1) {
			imageName = imageName.substring(index+1);
		}
		
		if (imageName != null) {
			try {	
				ret = DesignerImages.getImage(imageName+".gif");
				
			} catch(Exception e) {
				logger.severe("Image for '"+imageName+
						"' could not be found");
			}
		}
		
		return(ret);
	}
	
	public static String getName(Object component, ScenarioDiagram diagram) {
		String ret=null;
		
		if (component instanceof Group) {
			ret = ((Group)component).getName();
			
		} else if (component instanceof Role) {
			ret = ((Role)component).getName();
			
		} else if (component instanceof Link) {			
			ret = getLinkText((Link)component);
		}
		
		if (ret == null) {
			ret = "<unknown>";
		}
		
		return(ret);
	}
	
	protected static String getLinkText(Link link) {
		String ret=null;
		
		if (ret == null && link.getSource() != null) {
			String operation=((MessageEvent)link.getSource()).getOperationName();
			String messageType=((MessageEvent)link.getSource()).getType();
			String faultName=((MessageEvent)link.getSource()).getFaultName();
			
			String label="";
			
			if (operation != null) {
				label += operation;
			}
			
			if (messageType != null) {
				label += "("+messageType+")";
			}
			
			if (faultName != null) {
				label += " fault "+faultName;
			}
			
			if (label.length() > 0) {
				ret = label;
			}
		}
		
		return(ret);
	}
	
	public static String getFullDescription(Object model) {
		String ret=null;
		
		/* TODO: GPB:
		if (model instanceof ScenarioObject) {
			ret = ((ScenarioObject)model).getDescription();
		}
		*/
		
		return(ret);
	}

	/**
	 * This method sets the tooltip for the supplied figure,
	 * associated with the supplied model.
	 * 
	 * @param figure The figure
	 * @param model The model
	 */
	public static void setTooltip(org.eclipse.draw2d.IFigure figure,
								Object model) {
		
		org.eclipse.draw2d.ScrollPane tooltip=new org.eclipse.draw2d.ScrollPane();
		
		org.eclipse.draw2d.text.FlowPage page=new org.eclipse.draw2d.text.FlowPage();
		tooltip.setContents(page);
		
		String description=getFullDescription(model);
		
		if (description != null) {
			org.eclipse.draw2d.text.BlockFlow block1=new org.eclipse.draw2d.text.BlockFlow();
			page.add(block1);
			
			org.eclipse.draw2d.text.TextFlow text1=new org.eclipse.draw2d.text.TextFlow();
			block1.add(text1);
			text1.setText(description);
			
			figure.setToolTip(tooltip);
		}
	}
		
	public static int getHeight(Object component, ScenarioDiagram diagram) {
		int ret=0;
		java.util.List children=ModelSupport.getChildren(component);
				
		if (children != null && children.size() > 0) {
			ret += 40;
			
			for (int i=0; i < children.size(); i++) {
				ret += getHeight(children.get(i), diagram);
				
				ret += getPadding(children, i); // padding
			}
		}
		
		if (ret == 0) {
			
			if (component instanceof org.savara.scenario.model.Group) {
				ret = 70;
			} else {
				ret = 30;
			}
		}
		
		if (component instanceof Scenario) {
			ret += getHeaderPadding(component);
			
			ret += 40; // Additional padding
			
			// If test scenario is less than 300 high, make
			// it 300
			if (ret < 300) {
				ret = 300;
			}
		}
		
		return(ret);
	}
	
	public static int getWidth(Object component, ScenarioDiagram diagram) {
		int ret=0;
		
		if (component instanceof Role) {
			String name=((Role)component).getName();
			
			if (name != null) {
				ret = (int)(name.length()*6.5);
			}
			
			if (ret < 100) {
				ret = 100;
			} else if (ret > 180) {
				ret = 180;
			}
			
		} else if (component instanceof TimeElapsedEvent) {
			/* TODO: GPB: Need to get parent of the event

			ret = getWidth(((TimeElapsedEvent)component).eContainer(),
					diagram);
			
			ret -= 10;
			*/
		} else {
			java.util.List children=ModelSupport.getChildren(component);
			boolean f_foundSubGroup = false;

			if (children != null) {
				for (int i=0; i < children.size(); i++) {
					if (children.get(i) instanceof
							org.savara.scenario.model.Group) {
						int wid=getWidth(children.get(i), diagram);
						
						if (ret < (wid+15)) {
							ret = wid + 15; // added padding
						}
						
						f_foundSubGroup = true;
					}
				}
			}
			
			if (f_foundSubGroup == false) {
				// Need to add width for participants
				ret = getSidebarWidth(diagram)+
						(diagram.getScenario().getRole().size()*
								ROLE_PADDING_X-
								(int)(ROLE_PADDING_X*0.6));
			}
		}
		
		return(ret);
	}
	
	public static int getChildYPosition(Object parent, Object child,
						ScenarioDiagram diagram) {
		int ret=0;
		
		if (child instanceof Role) {
			ret = getHeaderPadding(child);
			
		} else {			
			java.util.List children=ModelSupport.getChildren(parent);
			
			if (children != null) {
				int pos=children.indexOf(child);
				
				ret = getHeaderPadding(parent);
	    		
		    	if (pos > 0) {
		    		
		    		for (int i=0; i < pos; i++) {
		    			ret += getHeight(children.get(i), diagram);
		    			
		    			ret += getPadding(children, i);
		    		}
		    	}
			}
		}
		
		/*
		if (parent instanceof Scenario) {
			ret += getTopBorderHeight(diagram);
		}
		*/
		
		return(ret);
	}
	
	/**
	 * This method determines how much padding should be included
	 * after the specified child index in the list of children.
	 * 
	 * @param children The list of children
	 * @param index The index of the child prior to the padding
	 * @return The amount of padding
	 */
	public static int getPadding(java.util.List children,
						int index) {
		int ret=PADDING_Y;
		
		if (index < children.size()-1) {
			Object child1=children.get(index);
			Object child2=children.get(index+1);
			
			if (child1 instanceof org.savara.scenario.model.MessageEvent &&
					child2 instanceof org.savara.scenario.model.MessageEvent) {
				
				// Is there a message link between these children
				
				/* TODO: GPB: need to get scenario
				java.util.List links=
					((org.savara.scenario.model.MessageEvent)child1).getScenario().getMessageLinks();
				boolean f_found=false;
				
				for (int i=0; f_found == false &&
								i < links.size(); i++) {
					org.savara.scenario.Link link=
						(org.savara.scenario.Link)
								links.get(i);
					
					if ((link.getSource() == child1 &&
							link.getTarget() == child2) ||
							(link.getSource() == child2 &&
									link.getTarget() == child1)) {
						f_found = true;
					}
				}
				
				if (f_found == false) {
					ret = 0;
				}
				*/
				
			} else {
				ret = 10;
			}
		}
		
		return(ret);
	}
	
	public static int getChildXPosition(Object parent, Object child,
					ScenarioDiagram diagram) {
		int ret=0;
		
		if (child instanceof Role) {
			int pos=diagram.getScenario().getRole().indexOf(child);
			//int pos=diagram.getParticipantIndex((Participant)child);
			
			if (pos != -1) {
				ret = getSidebarWidth(diagram) + (pos * ROLE_PADDING_X);
			} else {
				ret = ROLE_PADDING_X;
			}
			
		} else if (child instanceof Event) {
			Event me=(Event)child;
			
			Role participant=getRoleForEvent(
					diagram.getScenario().getRole(), me);
			
			if (participant != null) {
				ret = getChildXPosition(null, participant, diagram);
				
				// For each contained event group, we need to adjust
				// the X position
				Object cur=ModelSupport.getParent(me);
				while (cur != null && (cur instanceof Scenario) == false) {
					ret -= EVENT_GROUP_PADDING_X;
					cur=ModelSupport.getParent(cur);
				}
			} else {
				ret = 50;
			}
		} else if (child instanceof TimeElapsedEvent) {
			ret = 5;
		} else if (child instanceof Import) {
			ret = 5;
		}
		
		return(ret);
	}	
	
	/**
	 * This method returns the participant associated with the
	 * supplied scenario event. If a participant type is not
	 * defined, then a participant will not be returned.
	 * 
	 * @param list The list of participants
	 * @param evt The scenario event
	 * @return The participant, or null if not found
	 */
	public static Role getRoleForEvent(java.util.List list,
					Event evt) {
		Role ret=(Role)evt.getRole();
		
		/* 9/6/08 - phased out participant type name/instance
		if (evt.getParticipant() == null &&
				org.pi4soa.common.util.NamesUtil.isSet(evt.getParticipantTypeName())) {
			
			for (int i=0; ret == null && i < list.size(); i++) {
				ret = (Participant)list.get(i);
				
				if ((evt.getParticipantTypeName() == null &&
						ret.getType() != null) ||
					(evt.getParticipantTypeName() != null &&
							evt.getParticipantTypeName().equals(ret.getType()) == false)) {
					ret = null;
				} else if ((evt.getParticipantInstance() == null &&
						ret.getInstance() != null) ||
						(evt.getParticipantInstance() != null &&
					evt.getParticipantInstance().equals(ret.getInstance()) == false)) {
					ret = null;
				}
			}
			
			if (ret != null) {
				evt.setParticipant(ret);
			}
		}
		*/
		
		return(ret);
	}
	
	public static int getNewParticipantIndex(int x,
						ScenarioDiagram diagram) {
		int start=getSidebarWidth(diagram) - ROLE_PADDING_X;
		int pos=(x - start) / ROLE_PADDING_X;
		
		return(pos);
	}
	
	public static int getNearestParticipantIndex(int x,
						ScenarioDiagram diagram) {
		int pos=(x - getSidebarWidth(diagram) + (ROLE_PADDING_X/2)) /
					ROLE_PADDING_X;
		
		return(pos);
	}
	
	public static Role getNearestRole(int x, ScenarioDiagram diagram) {
		int pos=getNearestParticipantIndex(x, diagram);
		Role ret=null;
		
		java.util.List roles=diagram.getScenario().getRole();
		
		if (roles.size() > 0) {
			if (pos < 0) {
				ret = (Role)roles.get(0);
			} else if (pos >= roles.size()) {
				ret = (Role)roles.get(roles.size()-1);
			} else {
				ret = (Role)roles.get(pos);
			}
		}
		
		return(ret);
	}
	
	public static int getHeaderPadding(Object parent) {
		int ret=EVENT_GROUP_PADDING_Y;
		
		if (parent instanceof Scenario) {
			ret = HEADER_HEIGHT;
			
			ret += 30; // For the name in the header
			
			if (((Scenario)parent).getAuthor() != null) {
				ret += 30;
			}
		} else if (parent instanceof Role) {
			ret = PARTICIPANT_PADDING_Y;
			
			ret += 30; // For the name in the header
			
			/* TODO: GPB: Need to find scenario
			if (((Role)parent).getScenario().getAuthor() != null) {
				ret += 30;
			}
			*/
		}
		
		return(ret);
	}
	
	/**
	 * This method calculates an appropriate width for the
	 * left hand side bar.
	 * 
	 * @param diagram The diagram
	 * @return The width
	 */
	public static int getSidebarWidth(ScenarioDiagram diagram) {
		// This can become dynamic, if necessary, based on the
		// depth of the event groups
		return(100);
	}
	
	private static Logger logger = Logger.getLogger("org.pi4soa.service.test.designer.view");
	
	public static int TYPES_INITIAL_YPADDING=26;

	public static final int EVENT_GROUP_PADDING_X=8;
	public static final int EVENT_GROUP_PADDING_Y=35;
	public static final int PARTICIPANT_PADDING_Y=10;
	public static final int HEADER_HEIGHT=60;
	public static final int PADDING_Y=10;
	//public static final int SIDEBAR_WIDTH=300;
	public static final int ROLE_PADDING_X=200;
}
