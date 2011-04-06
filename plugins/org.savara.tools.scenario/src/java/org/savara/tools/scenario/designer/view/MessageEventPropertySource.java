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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario
 * message link.
 */
public class MessageEventPropertySource implements IPropertySource {

	private static final String OPERATION_ID = "Operation";
	private static final String FAULT_ID = "Fault";
	private static final String PARAMETERS_ID = "Parameters";
	private static final String ERROR_EXPECTED_ID = "ErrorExpected";

    private org.savara.scenario.model.MessageEvent m_element=null;

	public MessageEventPropertySource(org.savara.scenario.model.MessageEvent element) {
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
		
		//boolean f_businessView=DesignerDefinitions.isPreference(DesignerDefinitions.BUSINESS_VIEW);
		
		java.util.Vector<IPropertyDescriptor> descriptors=new java.util.Vector<IPropertyDescriptor>();
		
		descriptors.add(new TextPropertyDescriptor(
				OPERATION_ID,"Operation"));
		descriptors.add(new TextPropertyDescriptor(
				FAULT_ID,"Fault"));
		descriptors.add(new PropertyDescriptor(
				PARAMETERS_ID,"Parameters"));
		descriptors.add(new PropertyDescriptor(
				ERROR_EXPECTED_ID,"ErrorExpected"));
				
		ret = new IPropertyDescriptor[descriptors.size()];
		descriptors.copyInto(ret);
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (id == OPERATION_ID) {
			ret = getElement().getOperationName();
		} else if (id == FAULT_ID) {
			ret = getElement().getFaultName();
		} else if (id == PARAMETERS_ID) {
			ret = getElement().getParameter();
		} else if (id == ERROR_EXPECTED_ID) {
			ret = getElement().isErrorExpected();
		}

		if (ret == null) {
			ret = "";
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
	@SuppressWarnings("unchecked")
	public void setPropertyValue(Object id, Object value) {
		
		if (id == OPERATION_ID) {
			if (value instanceof String) {
				getElement().setOperationName((String)value);
			}
		} else if (id == FAULT_ID) {
			if (value instanceof String) {
				getElement().setFaultName((String)value);
			}			
		} else if (id == PARAMETERS_ID) {
			if (value instanceof java.util.List && value != getElement().getParameter()) {
				getElement().getParameter().clear();
				getElement().getParameter().addAll((java.util.List<Parameter>)value);
			}
		} else if (id == ERROR_EXPECTED_ID) {
			if (value instanceof Boolean) {
				getElement().setErrorExpected((Boolean)value);
			} else if (value instanceof String) {
				getElement().setErrorExpected(Boolean.valueOf((String)value));
			}
		}
	}
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	protected MessageEvent getElement() {
		return(m_element);
	}
}
