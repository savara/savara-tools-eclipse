/*
 * Copyright 2005 Pi4 Technologies Ltd
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
 * Jul 8, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.view;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario
 * participant.
 */
public class RolePropertySource implements IPropertySource {
	
	private static final String NAME_ID = "name";

    private Role m_element=null;

	public RolePropertySource(Role element) {
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
		Collection<IPropertyDescriptor>	descriptors = new Vector<IPropertyDescriptor>();

		descriptors.add(new TextPropertyDescriptor(
				NAME_ID,"Name"));

		return (IPropertyDescriptor[])descriptors.toArray( new IPropertyDescriptor[] {} );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (id == NAME_ID) {
			ret = getElement().getName();
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
	public void setPropertyValue(Object id, Object value) {
		
		if (id == NAME_ID) {
			if (value instanceof String) {
				getElement().setName((String)value);
			}
		}
	}
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	protected Role getElement() {
		return(m_element);
	}
}
