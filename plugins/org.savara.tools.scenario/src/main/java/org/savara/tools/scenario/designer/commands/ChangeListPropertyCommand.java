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
package org.savara.tools.scenario.designer.commands;

import org.eclipse.ui.views.properties.*;

/**
 * This class implements the add to list command.
 */
public class ChangeListPropertyCommand
			extends org.eclipse.gef.commands.Command {
	
	private IPropertySource m_propertySource=null;
    private IPropertyDescriptor m_propertyDescriptor=null;
    private Object m_value=null;
    private Object m_oldValue=null;
    private int m_index=-1;
	
	public ChangeListPropertyCommand() {
	}
		
	public void execute() { 	
        @SuppressWarnings("unchecked")
		java.util.List<Object> list=(java.util.List<Object>)
        			m_propertySource.getPropertyValue(m_propertyDescriptor.getId());
        
        m_oldValue = list.get(m_index);
        list.set(m_index, m_value);
        
        // Apply list back as property, to ensure applied to both ends of link, if relevant
        m_propertySource.setPropertyValue(m_propertyDescriptor.getId(), list);
	}
	
	public void redo() {	
		execute();
	}
	
	public void setValue(Object value) {
		m_value = value;
	}
	
	public void setIndex(int index) {
		m_index = index;
	}
	
	/**
	 * The name of the property associated with the command.
	 * 
	 * @return The name of the property
	 */
	public String getPropertyName() {
		return(null);
	}
	
	public Object getValue() {
		return(m_value);
	}
	
	public int getIndex() {
		return(m_index);
	}
	
	public void setPropertySource(IPropertySource source) {
		m_propertySource = source;
	}
	
	public void setPropertyDescriptor(IPropertyDescriptor descriptor) {
		m_propertyDescriptor = descriptor;
	}
	
	public void undo() {
		if (m_index != -1) {
	        @SuppressWarnings("unchecked")
			java.util.List<Object> list=(java.util.List<Object>)
						m_propertySource.getPropertyValue(m_propertyDescriptor.getId());

	        list.set(m_index, m_oldValue);
	        
	        // Apply list back as property, to ensure applied to both ends of link, if relevant
	        m_propertySource.setPropertyValue(m_propertyDescriptor.getId(), list);
		}
	}
}
