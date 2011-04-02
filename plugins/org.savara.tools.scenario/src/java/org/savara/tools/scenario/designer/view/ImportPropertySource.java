/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.scenario.designer.view;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario.
 */
public class ImportPropertySource implements IPropertySource {

	private static final String URL_ID = "URL";
	private static final String GROUP_ID = "Group";

    private org.savara.scenario.model.Import m_element=null;

	public ImportPropertySource(org.savara.scenario.model.Import element) {
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
		
		java.util.Vector<IPropertyDescriptor> descriptors=new java.util.Vector<IPropertyDescriptor>();
		
		descriptors.add(new TextPropertyDescriptor(
				URL_ID,"URL"));
		descriptors.add(new TextPropertyDescriptor(
				GROUP_ID,"Group"));
				
		ret = new IPropertyDescriptor[descriptors.size()];
		descriptors.copyInto(ret);
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (id == URL_ID) {
			ret = getElement().getUrl();
		} else if (id == GROUP_ID) {
			ret = getElement().getGroup();
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
		
		if (id == URL_ID) {
			if (value instanceof String) {
				getElement().setUrl((String)value);
			}
		} else if (id == GROUP_ID) {
			if (value instanceof String) {
				getElement().setGroup((String)value);
			}			
		}
	}
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	protected Import getElement() {
		return(m_element);
	}
}
