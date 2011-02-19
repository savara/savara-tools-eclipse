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
 * 26 Jan 2007 : Initial version created by gary
 */
package org.savara.tools.bpmn.generation.stp;

import org.eclipse.stp.bpmn.*;
import org.savara.tools.bpmn.generation.BPMNGenerationException;

public class STPBPMNModelFactoryImpl implements org.savara.tools.bpmn.generation.BPMNModelFactory {

	public String getFileExtension() {
		return("bpmn");
	}
	
	public void saveModel(String fileName, Object diagram)
						throws BPMNGenerationException {
		
		/*
		try {
			org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE.getActivity();
			
			java.io.FileOutputStream ostream=
				new java.io.FileOutputStream(fileName);
						
			// Output the UML2 model to the supplied stream
			final org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl xmi =
				new org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl();
			xmi.setURI(org.eclipse.emf.common.util.URI.createFileURI(fileName));
			xmi.getContents().add(diagram);
			
			xmi.doSave(ostream, xmi.getDefaultLoadOptions());
			
			ostream.close();
			
		} catch(Exception e) {
			throw new BPMNGenerationException("Failed to convert to XMI", e);
		}
		*/
	}
	
	public Object createDiagram() {
		BpmnDiagram ret=BpmnFactory.eINSTANCE.createBpmnDiagram();
		return(ret);
	}
	
	public Object createPool(Object diagram, String name) {
		Pool ret = BpmnFactory.eINSTANCE.createPool();
		ret.setName(name);
		
		if (diagram instanceof BpmnDiagram) {
			((BpmnDiagram)diagram).getPools().add(ret);
		}
		
		return(ret);
	}
	
	public Object createInitialNode(Object container) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.EVENT_START_EMPTY_LITERAL);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public Object createSimpleTask(Object container,
			org.pi4soa.service.behavior.ActivityType activity) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.TASK_LITERAL);
		
		String label=activity.getName();
		
		Class[] intf=activity.getClass().getInterfaces();
		for (int i=0; i < intf.length; i++) {
			if (intf[i].getName().startsWith("org.pi4soa.service.behavior")) {
				int index=intf[i].getName().lastIndexOf('.');
				label = intf[i].getName().substring(index+1);
				
				if (activity instanceof org.pi4soa.service.behavior.Perform) {
					label += " "+((org.pi4soa.service.behavior.Perform)
							activity).getBehaviorDescription().getName();
				} else if (activity.getName() != null) {
					label += " "+activity.getName();
				}
			}
		}

		ret.setName(label);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public Object createControlLink(Object container,
			Object fromNode, Object toNode,
						String conditionalExpression) {
		org.eclipse.stp.bpmn.SequenceEdge ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createSequenceEdge();
		
		if (fromNode instanceof Vertex) {
			ret.setSource((Vertex)fromNode);
		}
		
		if (toNode instanceof Vertex) {
			ret.setTarget((Vertex)toNode);
		}
		
		if (conditionalExpression != null) {
			ret.setName(conditionalExpression);
		}
		
		if (container instanceof Graph) {
			((Graph)container).getSequenceEdges().add(ret);
		}
		
		return(ret);
	}

	public Object createMessageLink(Object container,
			Object fromNode, Object toNode,
			org.pi4soa.service.behavior.Receive receive) {
		MessagingEdge ret=BpmnFactory.eINSTANCE.createMessagingEdge();
		
		if (fromNode instanceof Activity) {
			ret.setSource((Activity)fromNode);
		}
		
		if (toNode instanceof Activity) {
			ret.setTarget((Activity)toNode);
		}
		
		String name=receive.getOperationName();
		String mesgType=null;
		
		if (receive.getMessageDefinition() != null &&
				receive.getMessageDefinition().getInformationType() != null) {
			mesgType = receive.getMessageDefinition().getInformationType().getType();			
			
			if (mesgType == null) {
				mesgType = receive.getMessageDefinition().getInformationType().getElement();
			}
		}
		
		if (mesgType != null) {
			mesgType = org.pi4soa.common.xml.XMLUtils.getLocalname(mesgType);
		}

		if (name == null) {
			name = mesgType;
		} else if (mesgType != null) {
			name += "("+mesgType+")";
		} else {
			name += "()";
		}
		
		if (name != null) {
			ret.setName(name);
		}
		
		if (container instanceof BpmnDiagram) {
			((BpmnDiagram)container).getMessages().add(ret);
		} else if (container instanceof Pool) {
			((Pool)container).getBpmnDiagram().getMessages().add(ret);
		}
		
		return(ret);
	}

	public Object setLinkExpression(Object link, String expression) {
		
		if (expression != null &&
				link instanceof org.eclipse.stp.bpmn.SequenceEdge) {
			org.eclipse.stp.bpmn.SequenceEdge edge=
				(org.eclipse.stp.bpmn.SequenceEdge)link;
			
			edge.setName(expression);
		}
		
		return(link);
	}
	
	public Object createDataBasedXORGateway(Object container) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.GATEWAY_DATA_BASED_EXCLUSIVE_LITERAL);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public Object createEventBasedXORGateway(Object container) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.GATEWAY_EVENT_BASED_EXCLUSIVE_LITERAL);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public Object createANDGateway(Object container) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.GATEWAY_PARALLEL_LITERAL);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public Object createFinalNode(Object container) {
		org.eclipse.stp.bpmn.Activity ret=
			org.eclipse.stp.bpmn.BpmnFactory.eINSTANCE.createActivity();
		
		ret.setActivityType(ActivityType.EVENT_END_EMPTY_LITERAL);
		
		if (container instanceof Graph) {
			((Graph)container).getVertices().add(ret);
		}
		
		return(ret);
	}
	
	public boolean isDecision(Object node) {
		boolean ret=false;
		
		if (node instanceof org.eclipse.stp.bpmn.Activity &&
				(((org.eclipse.stp.bpmn.Activity)node).getActivityType()
					== ActivityType.GATEWAY_DATA_BASED_EXCLUSIVE_LITERAL ||
					((org.eclipse.stp.bpmn.Activity)node).getActivityType()
					== ActivityType.GATEWAY_EVENT_BASED_EXCLUSIVE_LITERAL)) {
			ret = true;
		}
		
		return(ret);
	}
	
	/**
	 * This method determines if the supplied node is a join. This
	 * is true, if the node is a data or event based gateway,
	 * and as incoming edges. This is based on the fact that only
	 * a join gateway, at the point this method is invoked, would
	 * have incoming links, otherwise any other gateway would be
	 * assumed to be the initial gateway in a conditional grouping
	 * construct.
	 * 
	 * @param node
	 * @return Whether the node is a join
	 */
	public boolean isJoin(Object node) {
		boolean ret=false;
		
		if (node instanceof org.eclipse.stp.bpmn.Activity &&
				(((org.eclipse.stp.bpmn.Activity)node).getActivityType()
					== ActivityType.GATEWAY_DATA_BASED_EXCLUSIVE_LITERAL ||
					((org.eclipse.stp.bpmn.Activity)node).getActivityType()
					== ActivityType.GATEWAY_EVENT_BASED_EXCLUSIVE_LITERAL) &&
				((org.eclipse.stp.bpmn.Activity)node).getIncomingEdges().size() > 0) {
			ret = true;
		}
		
		return(ret);
	}
	
	public boolean isTerminal(Object node) {
		boolean ret=false;
		
		if (node instanceof org.eclipse.stp.bpmn.Activity &&
				((org.eclipse.stp.bpmn.Activity)node).getActivityType()
					== ActivityType.EVENT_END_EMPTY_LITERAL) {
			ret = true;
		}
		
		return(ret);
	}
	
	public void setLabel(Object entity, String label) {
		if (entity instanceof Activity) {
			((Activity)entity).setName(label);
		}
	}
	
	public Object getSource(Object link) {
		Object ret=null;
		
		if (link instanceof SequenceEdge) {
			ret = ((SequenceEdge)link).getSource();
		} else if (link instanceof MessagingEdge) {
			ret = ((MessagingEdge)link).getSource();
		}
		
		return(ret);
	}
	
	public void setSource(Object link, Object node) {
		if (link instanceof SequenceEdge && node instanceof Vertex) {
			((SequenceEdge)link).setSource((Vertex)node);
		} else if (link instanceof MessagingEdge &&
						node instanceof Activity) {
			((MessagingEdge)link).setSource((Activity)node);
		}
	}
	
	public Object getTarget(Object link) {
		Object ret=null;
		
		if (link instanceof SequenceEdge) {
			ret = ((SequenceEdge)link).getTarget();
		} else if (link instanceof MessagingEdge) {
			ret = ((MessagingEdge)link).getTarget();
		}
		
		return(ret);
	}
	
	public void setTarget(Object link, Object node) {
		if (link instanceof SequenceEdge && node instanceof Vertex) {
			((SequenceEdge)link).setTarget((Vertex)node);
		} else if (link instanceof MessagingEdge &&
						node instanceof Activity) {
			((MessagingEdge)link).setTarget((Activity)node);
		}
	}
	
	public java.util.List getInboundControlLinks(Object node) {
		java.util.List ret=null;
		
		if (node instanceof Vertex) {
			ret = ((Vertex)node).getIncomingEdges();
		}
		
		return(ret);
	}
	
	public java.util.List getOutboundControlLinks(Object node) {
		java.util.List ret=null;
		
		if (node instanceof Vertex) {
			ret = ((Vertex)node).getOutgoingEdges();
		}
		
		return(ret);
	}
	
	public java.util.List getInboundMessageLinks(Object node) {
		java.util.List ret=null;
		
		if (node instanceof Activity) {
			ret = ((Activity)node).getIncomingMessages();
		}
		
		return(ret);
	}
	
	public java.util.List getOutboundMessageLinks(Object node) {
		java.util.List ret=null;
		
		if (node instanceof Activity) {
			ret = ((Activity)node).getOutgoingMessages();
		}
		
		return(ret);
	}
	
	public void delete(Object entity) {
//System.out.println("DELETE: "+entity);
		
		if (entity instanceof Vertex) {
			Vertex node=(Vertex)entity;
			
			if (node.getGraph() != null) {
				node.getGraph().getVertices().remove(node);
			}
			
			for (int i=node.getIncomingEdges().size()-1; i >= 0; i--) {
				SequenceEdge edge=(SequenceEdge)node.getIncomingEdges().get(i);
				delete(edge);
			}
			
			for (int i=node.getOutgoingEdges().size()-1; i >= 0; i--) {
				SequenceEdge edge=(SequenceEdge)node.getOutgoingEdges().get(i);
				delete(edge);
			}
		} else if (entity instanceof SequenceEdge) {
			SequenceEdge edge=(SequenceEdge)entity;
			
			edge.setSource(null);
			edge.setTarget(null);
			
			if (edge.getGraph() != null) {
				edge.getGraph().getSequenceEdges().remove(edge);
			}
		} else if (entity instanceof Pool) {
			Pool pool=(Pool)entity;
			
			if (pool.getBpmnDiagram() != null) {
				pool.getBpmnDiagram().getPools().remove(pool);
			}
		}
	}

	public boolean isDeleted(Object entity) {
		boolean ret=false;
		
		if (entity instanceof Vertex) {
			Vertex node=(Vertex)entity;
			
			if (node.getGraph() == null) {
				ret = true;
			}
		} else if (entity instanceof SequenceEdge) {
			SequenceEdge edge=(SequenceEdge)entity;
			
			if (edge.getGraph() == null) {
				ret = true;
			}
		} else if (entity instanceof Pool) {
			Pool pool=(Pool)entity;
			
			if (pool.getBpmnDiagram() != null) {
				ret = true;
			}
		}
		
		return(ret);
	}
}
