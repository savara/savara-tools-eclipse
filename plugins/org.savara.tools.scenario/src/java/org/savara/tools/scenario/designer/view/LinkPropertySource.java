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
package org.savara.tools.scenario.designer.view;

import java.util.logging.Logger;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.DesignerDefinitions;

/**
 * This class implements the property source for a scenario
 * message link.
 */
public class LinkPropertySource implements IPropertySource {

	private static final String MESSAGE_PROPERTY = "MessageProperty";

	public LinkPropertySource(org.savara.scenario.model.Link element) {
		m_element = element;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return(m_element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] ret=new IPropertyDescriptor[] {};
		
		/*
		if (m_element.getSource() != null &&
				m_element.getTarget() != null) {
			ScenarioPropertySource sps=new ScenarioPropertySource(m_element.getSource());
			boolean f_businessView=DesignerDefinitions.isPreference(DesignerDefinitions.BUSINESS_VIEW);
			
			java.util.Vector pds=new java.util.Vector();
			
			String[] values=getMessageEntries();
			
			// Only display message combobox if messages available
			// to select
			if (values.length > 1 &&
					NamesUtil.isSet(m_element.getScenario().
							getChoreographyDescriptionURL())) {
				pds.add(new ComboBoxPropertyDescriptor(
						MESSAGE_PROPERTY,
						"Message",
						values));
			} else {
				pds.add(sps.createAttributePropertyDescriptor(
						ScenarioPackage.eINSTANCE.getMessageEvent_IsRequest()));
				pds.add(sps.createAttributePropertyDescriptor(
						ScenarioPackage.eINSTANCE.getMessageEvent_OperationName()));
				pds.add(sps.createAttributePropertyDescriptor(
						ScenarioPackage.eINSTANCE.getMessageEvent_FaultName()));
				pds.add(sps.createAttributePropertyDescriptor(
						ScenarioPackage.eINSTANCE.getMessageEvent_MessageType()));
			}

			pds.add(sps.createAttributePropertyDescriptor(
					ScenarioPackage.eINSTANCE.getScenarioObject_Description()));

			pds.add(sps.createAttributePropertyDescriptor(
					ScenarioPackage.eINSTANCE.getLifelineItem_CausesException()));

			pds.add(sps.createAttributePropertyDescriptor(
					ScenarioPackage.eINSTANCE.getMessageEvent_ValueURL()));
					
			ret = new IPropertyDescriptor[pds.size()];
			pds.copyInto(ret);
		} else {
			ret = new IPropertyDescriptor[] {};
		}
		*/
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (m_element.getSource() != null &&
				m_element.getTarget() != null) {
			
			/* TODO: GPB:
			if (id == MESSAGE_PROPERTY) {
				java.util.List list=ModelUtil.getMessageDefinitions(
						m_element.getSource(), m_element.getTarget());
				
				org.pi4soa.service.behavior.MessageDefinition mdef=
					ModelUtil.getMessageDefinition(m_element,
							list);

				if (mdef != null && list != null) {
					int index=list.indexOf(mdef);
					
					if (index != -1) {
						ret = new Integer(index+1);
					}
				}
				
				if (ret == null) {
					ret = new Integer(0);
				}
			} else {
				ScenarioPropertySource sps=new ScenarioPropertySource(m_element.getSource());
		
				ret = sps.getPropertyValue(id);
			}
			*/
		}
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySouce#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		
		if (m_element.getSource() != null &&
				m_element.getTarget() != null) {
			
			if (id == MESSAGE_PROPERTY) {
				if (value instanceof Integer) {
					/* TODO: GPB:
					java.util.List list=ModelUtil.getMessageDefinitions(m_element.getSource(),
										m_element.getTarget());
					int index=((Integer)value).intValue();
					
					// Deduct 1 from the index to fid the real list position
					// as we need to take into account the empty initial
					// entry representing no message definition
					index--;
					
					if (list != null && index != -1 && index < list.size()) {
						org.pi4soa.service.behavior.MessageDefinition mdef=
							(org.pi4soa.service.behavior.MessageDefinition)list.get(index);
					
						// Set the fields on both the source and target
						if (mdef.getOperationDefinition() != null) {
							m_element.getSource().setOperationName(mdef.getOperationDefinition().getName());
							m_element.getTarget().setOperationName(mdef.getOperationDefinition().getName());
						}
						
						if (mdef.getInformationType() != null) {
							if (NamesUtil.isSet(mdef.getInformationType().getFullyQualifiedType())) {
								m_element.getSource().setMessageType(mdef.getInformationType().getFullyQualifiedType());
								m_element.getTarget().setMessageType(mdef.getInformationType().getFullyQualifiedType());
							} else if (NamesUtil.isSet(mdef.getInformationType().getFullyQualifiedElement())) {
								m_element.getSource().setMessageType(mdef.getInformationType().getFullyQualifiedElement());
								m_element.getTarget().setMessageType(mdef.getInformationType().getFullyQualifiedElement());
							}
						}

						// 9/6/08 - removed service type
						//m_element.getSource().setServiceType(
						//		mdef.getOperationDefinition().getServiceType().getFullyQualifiedName());
						//m_element.getTarget().setServiceType(
						//		mdef.getOperationDefinition().getServiceType().getFullyQualifiedName());

						m_element.getSource().setFaultName(mdef.getFullyQualifiedFaultName());
						m_element.getTarget().setFaultName(mdef.getFullyQualifiedFaultName());

						m_element.getSource().setIsRequest(Boolean.valueOf(
								mdef.getClassification() ==
									org.pi4soa.service.behavior.MessageClassification.REQUEST));
						m_element.getTarget().setIsRequest(Boolean.valueOf(
								mdef.getClassification() ==
									org.pi4soa.service.behavior.MessageClassification.REQUEST));
					} else {
						// Clear the source and target fields
						m_element.getSource().setOperationName(null);
						m_element.getTarget().setOperationName(null);
						
						m_element.getSource().setMessageType(null);
						m_element.getTarget().setMessageType(null);

						//m_element.getSource().setServiceType(null);
						//m_element.getTarget().setServiceType(null);

						m_element.getSource().setFaultName(null);
						m_element.getTarget().setFaultName(null);

						m_element.getSource().setIsRequest(Boolean.TRUE);
						m_element.getTarget().setIsRequest(Boolean.TRUE);						
					}
					*/
				}
			} else {
				ScenarioPropertySource sps=new ScenarioPropertySource(m_element.getSource());
				//ScenarioPropertySource tps=new ScenarioPropertySource(m_element.getTarget());
			
				sps.setPropertyValue(id, value);
				//tps.setPropertyValue(id, value);
				
				// Copy value from source to target
				// TODO: Need to tidy up property sources so can
				// easily deal with delegating copy - but issue
				// is that the list of values may be different
				// for a particular field, due to other values in
				// the message events - so ideally need to just
				// set a value, rather than passing a list index
				
				/* TODO: GPB:
				EStructuralFeature feature=
					m_element.getSource().eClass().getEStructuralFeature(
								Integer.parseInt( (String)id ) );
	
				m_element.getTarget().eSet(feature,
						m_element.getSource().eGet(feature));
				*/
			}
		}
	}
	
	protected String[] getMessageEntries() {
		/* TODO: GPB:
		java.util.List list=ModelUtil.getMessageDefinitions(m_element.getSource(),
						m_element.getTarget());
		String[] ret=new String[list.size()+1];
		
		ret[0] = "";
		
		for (int i=0; i < list.size(); i++) {
			org.pi4soa.service.behavior.MessageDefinition mdef=
				(org.pi4soa.service.behavior.MessageDefinition)
						list.get(i);
			
			ret[i+1] = ModelUtil.getMessageDefinitionText(mdef);
		}
		
		return(ret);
		*/
		return(new String[0]);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.test.designer.view");	

    private org.savara.scenario.model.Link m_element=null;
}
