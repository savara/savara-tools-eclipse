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
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario.
 */
public class ScenarioPropertySource implements IPropertySource {

	private static final String NAME_ID = "Name";
	private static final String DESCRIPTION_ID = "Description";
	private static final String AUTHOR_ID = "Author";

    private org.savara.scenario.model.Scenario m_element=null;

	public ScenarioPropertySource(org.savara.scenario.model.Scenario element) {
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
				NAME_ID,"Name"));
		descriptors.add(new TextPropertyDescriptor(
				DESCRIPTION_ID,"Description"));
		descriptors.add(new PropertyDescriptor(
				AUTHOR_ID,"Author"));
				
		ret = new IPropertyDescriptor[descriptors.size()];
		descriptors.copyInto(ret);
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (id == NAME_ID) {
			ret = getElement().getName();
		} else if (id == DESCRIPTION_ID) {
			if (getElement().getDescription() != null) {
				for (Object content : getElement().getDescription().getContent()) {
					if (content instanceof String) {
						ret = content;
						break;
					}
				}
			}
		} else if (id == AUTHOR_ID) {
			ret = getElement().getAuthor();
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
		} else if (id == DESCRIPTION_ID) {
			if (value instanceof String) {
				if (getElement().getDescription() == null) {
					getElement().setDescription(new Scenario.Description());
				}
				getElement().getDescription().getContent().clear();
				getElement().getDescription().getContent().add((String)value);
			}			
		} else if (id == AUTHOR_ID) {
			if (value instanceof String) {
				getElement().setAuthor((String)value);
			}			
		}
	}
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	protected Scenario getElement() {
		return(m_element);
	}
}
