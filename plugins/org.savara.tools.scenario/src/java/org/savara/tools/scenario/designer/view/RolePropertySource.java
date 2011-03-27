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
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario
 * participant.
 */
public class RolePropertySource implements IPropertySource {

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
		Collection	descriptors = new Vector();

		/* TODO: GPB:
		java.util.List list=ItemProviderSupport.getChoiceOfValues(
						m_element.getScenario(),
				ScenarioPackage.eINSTANCE.getParticipant_Type());
		
		String[] values=new String[0];
		
		if (list != null && list.size() > 0) {
			values = new String[list.size()];
			list.toArray(values);
		}
		
		descriptors.add(new ComboBoxPropertyDescriptor(
				TYPE_ID, "Type",
				values));
				*/

		descriptors.add(new TextPropertyDescriptor(
				INSTANCE_ID,"Instance"));

		return (IPropertyDescriptor[])descriptors.toArray( new IPropertyDescriptor[] {} );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		/* TODO: GPB:
		if (id == TYPE_ID) {
			ret = getElement().getType();

			java.util.List list=ItemProviderSupport.getChoiceOfValues(
					m_element.getScenario(),
					ScenarioPackage.eINSTANCE.getParticipant_Type());
	
			if (list != null) {
				int index=list.indexOf(ret);
				
				if (index != -1) {
					ret = new Integer(index);
				} else {
					ret = new Integer(0);
				}
			} else {
				ret = new Integer(0);
			}
		} else if (id == INSTANCE_ID) {
			ret = getElement().getInstance();
		}
		*/
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
		
		/* TODO: GPB:
		if (id == TYPE_ID) {
			
			if (value instanceof Integer) {
				java.util.List list=ItemProviderSupport.getChoiceOfValues(
						m_element.getScenario(),
				ScenarioPackage.eINSTANCE.getParticipant_Type());
		
				if (((Integer)value).intValue() != -1) {
					getElement().setType((String)
							list.get(((Integer)value).intValue()));					
				}
			}
		} else if (id == INSTANCE_ID) {
			if (value instanceof String) {
				getElement().setInstance((String)value);
			}
		}
		*/
	}
	
	/**
	 * This method determines if the supplied attribute can be
	 * provided as a text region.
	 * 
	 * @param attr The attribute
	 * @return Whether it can be provided as a text region
	 */
	/* TODO: GPB:
	public boolean isTextRegion(EAttribute attr) {
		boolean ret=false;
		
		return(ret);
	}
	*/
	
	/**
	 * This method determines if the supplied attribute can be
	 * provided as an editable list.
	 * 
	 * @param attr The attribute
	 * @return Whether it can be provided as an editable list
	 */
	/* TODO: GPB:
	public boolean isEditableList(EAttribute attr) {
		boolean ret=false;
		
		return(ret);
	}
	*/
	
	/**
	 * This method provides the list of values that can be
	 * provided in an editable property field with associated
	 * list.
	 * 
	 * @param attr The attribute
	 * @return The list of values
	 */
	/* TODO: GPB:
	public java.util.List getStringValues(EAttribute attr) {
		java.util.List ret=new java.util.Vector();
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values appropriate for
	 * the reference.
	 * 
	 * @param ref The reference
	 * @return The list of values
	 */
	/* TODO: GPB:
	public String[] getStringValues(EReference ref) {
		String[] ret=new String[0];
		
		java.util.List list=getValues(m_element, ref);
		if (list != null) {
			ret = new String[list.size()+1];
			ret[0] = "";
			
			for (int i=0; i < list.size(); i++) {
				ret[i+1] = ViewSupport.getName(list.get(i),
									null);
			}
		}
	
		return(ret);
	}
	*/
		
	/**
	 * This method returns the selected value associated with
	 * the reference and the index.
	 * 
	 * @param ref The reference
	 * @param index The index
	 * @return The selected value
	 */
	/* TODO: GPB:
	public Object getSelectedValue(EReference ref, int index) {
		Object ret=null;
		
		// Subtract 1 from the index to account for the initial
		// empty slot
		index--;
		
		// Check for known referenced class types
		java.util.List list=getValues(m_element, ref);
		
		if (list != null && index >= 0 &&
					index < list.size()) {
			ret = list.get(index);
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values relevant for the
	 * supplied reference.
	 * 
	 * @param src The source object
	 * @param ref The reference
	 * @return The list of values
	 */
	/* TODO: GPB:
	public java.util.List getValues(Object src, EReference ref) {
		return(null);
	}
	*/
		
	/**
	 * This method returns a displayable version of the supplied
	 * property name.
	 * 
	 * @param propName The property name
	 * @return A displayable version
	 */
	public String getPropertyName(String propName) {
		StringBuffer ret=new StringBuffer();
		
		for (int i=0; i < propName.length(); i++) {
			char ch=propName.charAt(i);
			
			if (i == 0) {
				ret.append(Character.toUpperCase(ch));
			} else {
				if (Character.isUpperCase(ch)) {
					ret.append(' ');
				}
				ret.append(ch);
			}
		}
		
		return(ret.toString());
	}
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	protected Role getElement() {
		return(m_element);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.scenario.designer.view");	

	private static final String INSTANCE_ID = "instance";
	private static final String TYPE_ID = "type";

	private static final int COMBO_SIZE_LIMIT = 15;

    private Role m_element=null;
}
