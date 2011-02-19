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
 * 30 Jan 2007 : Initial version created by gary
 */
package org.savara.tools.bpmn.generation.gmf;

import org.eclipse.gmf.runtime.notation.*;
import org.savara.tools.bpmn.generation.BPMNGenerationException;
import org.savara.tools.bpmn.generation.BPMNModelFactory;
import org.savara.tools.bpmn.generation.BPMNNotationFactory;

public class GMFBPMNNotationFactoryImpl implements BPMNNotationFactory {

	public String getFileExtension() {
		return("bpmn_diagram");
	}
	
	public void saveNotation(String modelFileName, Object diagramModel,
			String notationFileName, Object diagramNotation)
						throws BPMNGenerationException {
		try {
			int pos=notationFileName.lastIndexOf(java.io.File.separator);
			
			((Diagram)diagramNotation).setName(notationFileName.substring(pos+1));
									
			org.eclipse.emf.ecore.resource.ResourceSet resourceSet =
						new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

//			 Register XML resource factory
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn_diagram", 
					new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn", 
					new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
			
			resourceSet.getPackageRegistry().put(NotationPackage.eINSTANCE.getNsURI(), NotationPackage.eINSTANCE);
			resourceSet.getPackageRegistry().put(org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE.getNsURI(), org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE);

			org.eclipse.emf.ecore.resource.Resource modelResource =
				resourceSet.createResource(org.eclipse.emf.common.util.URI.createFileURI(modelFileName));
			
			if (modelResource instanceof org.eclipse.emf.ecore.xmi.XMLResource) {
				setXMIId((org.eclipse.emf.ecore.xmi.XMLResource)modelResource,
							(org.eclipse.stp.bpmn.BpmnDiagram)diagramModel);
			}
			
			modelResource.getContents().add((org.eclipse.emf.ecore.EObject)diagramModel);
			
			org.eclipse.emf.ecore.resource.Resource notationResource =
				resourceSet.createResource(org.eclipse.emf.common.util.URI.createFileURI(notationFileName));
			
			if (notationResource instanceof org.eclipse.emf.ecore.xmi.XMLResource) {
				setXMIId((org.eclipse.emf.ecore.xmi.XMLResource)notationResource,
							(org.eclipse.emf.ecore.EObject)diagramNotation);
			}
			
			notationResource.getContents().add((org.eclipse.emf.ecore.EObject)diagramNotation);
			
			/*
			org.eclipse.emf.ecore.resource.Resource resource =
					((Diagram)diagramNotation).eResource();
			resource.setURI(org.eclipse.emf.common.util.URI.createFileURI(fileName));
			*/
			
			org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl xmi =
				new org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl();
			java.util.Map options=xmi.getDefaultSaveOptions();
			
			options.put(org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
			//options.put(org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);
			notationResource.save(options);
			modelResource.save(options);

			
/*			
			// Output the UML2 model to the supplied stream
			final org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl xmi =
				new org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl();
			xmi.setURI(org.eclipse.emf.common.util.URI.createFileURI(fileName));
			
			xmi.getResourceSet();
			
			org.eclipse.emf.ecore.resource.impl.ResourceSetImpl set=
				new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();
			
			org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE.getActivity();
			
			System.out.println("Registry for '"+org.eclipse.stp.bpmn.BpmnPackage.eNS_URI
					+": "+xmi.getResourceSet().getPackageRegistry().getEPackage(org.eclipse.stp.bpmn.BpmnPackage.eNS_URI));

			xmi.getContents().add(diagramNotation);
			
			xmi.doSave(ostream, xmi.getDefaultLoadOptions());
*/			
			//ostream.close();
			
		} catch(Exception e) {
			throw new BPMNGenerationException("Failed to convert to XMI", e);
		}
	}

	public Object createDiagram(BPMNModelFactory factory, Object diagramModel,
			int x, int y, int width, int height) {
		Diagram ret=NotationFactory.eINSTANCE.createDiagram();
		
		ret.setType("Bpmn");
		ret.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);
		ret.setElement((org.eclipse.emf.ecore.EObject)diagramModel);
	
		ret.getStyles().add(NotationFactory.eINSTANCE.createPageStyle());
		ret.getStyles().add(NotationFactory.eINSTANCE.createGuideStyle());
		ret.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
		
		/*
		try {
		org.eclipse.emf.ecore.resource.ResourceSet resourceSet =
			new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

// Register XML resource factory
resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn_diagram", 
						new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());

resourceSet.getPackageRegistry().put(NotationPackage.eINSTANCE.getNsURI(), NotationPackage.eINSTANCE);
resourceSet.getPackageRegistry().put(org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE.getNsURI(), org.eclipse.stp.bpmn.BpmnPackage.eINSTANCE);

org.eclipse.emf.ecore.resource.Resource resource =
	resourceSet.createResource(org.eclipse.emf.common.util.URI.createFileURI("test.xmi"));
// add the root object to the resource
resource.getContents().add(ret);
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/

		return(ret);
	}

	public Object createPool(BPMNModelFactory factory, Object poolModel,
			Object diagramNotation, int x, int y, int width, int height) {
		Node ret=NotationFactory.eINSTANCE.createNode();
		
		ret.setElement((org.eclipse.emf.ecore.EObject)poolModel);
		ret.setType("1001");
		
		Bounds bounds=NotationFactory.eINSTANCE.createBounds();
		bounds.setHeight(height);
		bounds.setWidth(width+100);
		bounds.setX(x);
		bounds.setY(y);
		
		ret.setLayoutConstraint(bounds);
		
		FontStyle font=NotationFactory.eINSTANCE.createFontStyle();
		font.setFontName("Ariel");
		ret.getStyles().add(font);
		ret.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
		
		FillStyle fill=NotationFactory.eINSTANCE.createFillStyle();
		fill.setFillColor(16771304);
		ret.getStyles().add(fill);
		
		LineStyle line=NotationFactory.eINSTANCE.createLineStyle();
		line.setLineColor(11119017);
		ret.getStyles().add(line);
		
		Node nameNode=NotationFactory.eINSTANCE.createNode();
		nameNode.setType("4008");
		ret.insertChild(nameNode);
		
		Node lineNode=NotationFactory.eINSTANCE.createNode();
		lineNode.setType("5001");
		ret.getStyles().add(NotationFactory.eINSTANCE.createDrawerStyle());
		ret.getStyles().add(NotationFactory.eINSTANCE.createSortingStyle());
		ret.getStyles().add(NotationFactory.eINSTANCE.createFilteringStyle());
		ret.insertChild(lineNode);
		
		((Diagram)diagramNotation).insertChild(ret);	
		
		return(lineNode);
	}

	public Object createTask(BPMNModelFactory factory,
			Object taskModel, Object parentNotation,
					int x, int y, int width, int height) {
		Node ret=NotationFactory.eINSTANCE.createNode();
		
		ret.setElement((org.eclipse.emf.ecore.EObject)taskModel);
		ret.setType("2001");
		
		Bounds bounds=NotationFactory.eINSTANCE.createBounds();
		bounds.setHeight(height);
		bounds.setWidth(width);
		bounds.setX(x);
		bounds.setY(y);
		
		ret.setLayoutConstraint(bounds);
		
		FontStyle font=NotationFactory.eINSTANCE.createFontStyle();
		font.setFontName("Ariel");
		ret.getStyles().add(font);
		ret.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
		
		FillStyle fill=NotationFactory.eINSTANCE.createFillStyle();
		fill.setFillColor(16771304);
		ret.getStyles().add(fill);
		
		LineStyle line=NotationFactory.eINSTANCE.createLineStyle();
		line.setLineColor(11119017);
		ret.getStyles().add(line);
		
		Node nameNode=NotationFactory.eINSTANCE.createNode();
		nameNode.setType("4001");
		ret.insertChild(nameNode);
		
		((Node)parentNotation).insertChild(ret);	
		
		m_taskViews.put(taskModel, ret);
		
		return(ret);
	}
	

	public Object createJunction(BPMNModelFactory factory,
			Object junctionModel, Object parentNotation,
					int x, int y, int width, int height) {
		Node ret=NotationFactory.eINSTANCE.createNode();
		
		ret.setElement((org.eclipse.emf.ecore.EObject)junctionModel);
		ret.setType("2001");
		
		Bounds bounds=NotationFactory.eINSTANCE.createBounds();
		bounds.setHeight(height);
		bounds.setWidth(width);
		bounds.setX(x);
		bounds.setY(y);
		
		ret.setLayoutConstraint(bounds);
		
		FontStyle font=NotationFactory.eINSTANCE.createFontStyle();
		font.setFontName("Ariel");
		ret.getStyles().add(font);
		ret.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
		
		FillStyle fill=NotationFactory.eINSTANCE.createFillStyle();
		fill.setFillColor(16771304);
		ret.getStyles().add(fill);
		
		LineStyle line=NotationFactory.eINSTANCE.createLineStyle();
		line.setLineColor(11119017);
		ret.getStyles().add(line);
		
		Node nameNode=NotationFactory.eINSTANCE.createNode();
		nameNode.setType("4001");
		ret.insertChild(nameNode);
		
		((Node)parentNotation).insertChild(ret);	
		
		return(ret);
	}
	
	public Object createMessageLink(BPMNModelFactory factory,
						Object linkModel, Object diagramNotation) {
		Edge ret=NotationFactory.eINSTANCE.createEdge();
		
		Object source=factory.getSource(linkModel);
		if (source != null) {
			ret.setSource(getTaskView(source));
		}
		
		Object target=factory.getTarget(linkModel);
		if (target != null) {
			ret.setTarget(getTaskView(target));
		}
		
		ret.setElement((org.eclipse.emf.ecore.EObject)linkModel);
		ret.setType("3002");
		
		Node nameNode=NotationFactory.eINSTANCE.createNode();
		nameNode.setType("4007");
		ret.insertChild(nameNode);

		Location layout=NotationFactory.eINSTANCE.createLocation();
		layout.setY(40);
		nameNode.setLayoutConstraint(layout);
				
		ret.getStyles().add(NotationFactory.eINSTANCE.createRoutingStyle());
		
		FontStyle font=NotationFactory.eINSTANCE.createFontStyle();
		font.setFontName("Ariel");
		ret.getStyles().add(font);
		ret.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
		
		RelativeBendpoints bendpoints=NotationFactory.eINSTANCE.createRelativeBendpoints();
		//bendpoints.setPoints(arg0);
		ret.setBendpoints(bendpoints);
		
		((Diagram)diagramNotation).insertEdge(ret);	

		return(ret);
	}
	
	protected View getTaskView(Object modelObject) {
		View ret=(View)m_taskViews.get(modelObject);
		
		return(ret);
	}
	
	protected void setXMIId(org.eclipse.emf.ecore.xmi.XMLResource res,
					org.eclipse.emf.ecore.EObject eobj) {
		
		if (eobj instanceof org.eclipse.stp.bpmn.Identifiable) {
			res.setID((org.eclipse.stp.bpmn.Identifiable)eobj,
					((org.eclipse.stp.bpmn.Identifiable)eobj).getID());
		} else if (eobj instanceof org.eclipse.gmf.runtime.notation.View) {
			res.setID((org.eclipse.gmf.runtime.notation.View)eobj,
					"_"+System.currentTimeMillis()+"_"+((org.eclipse.gmf.runtime.notation.View)eobj).hashCode());
		}
		
		java.util.List children=eobj.eContents();
		
		for (int i=0; i < children.size(); i++) {
			setXMIId(res, (org.eclipse.emf.ecore.EObject)children.get(i));
		}
	}
	
	private java.util.Hashtable m_taskViews=new java.util.Hashtable();
}
