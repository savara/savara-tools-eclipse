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
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.ui.views.properties.*;

/**
 * This class implements the remove from list command.
 */
public class RemoveFromListCommand
			extends org.eclipse.gef.commands.Command {
	
	private IPropertySource m_propertySource=null;
    private IPropertyDescriptor m_propertyDescriptor=null;
    private Object m_value=null;
    private int m_removeIndex=-1;
	
	public RemoveFromListCommand() {
	}
		
	public void execute() { 	
        java.util.List<Object> list=(java.util.List<Object>)
        			m_propertySource.getPropertyValue(m_propertyDescriptor.getId());
        
        m_value = list.remove(m_removeIndex);
	}
	
	public void redo() {	
		execute();
	}
	
	public void setIndex(int index) {
		m_removeIndex = index;
	}
	
	/**
	 * The name of the property associated with the command.
	 * 
	 * @return The name of the property
	 */
	public String getPropertyName() {
		return(null);
	}
	
	public void setPropertySource(IPropertySource source) {
		m_propertySource = source;
	}
	
	public void setPropertyDescriptor(IPropertyDescriptor descriptor) {
		m_propertyDescriptor = descriptor;
	}
	
	public void undo() {
		if (m_removeIndex != -1 && m_value != null) {
	        java.util.List<Object> list=(java.util.List<Object>)
						m_propertySource.getPropertyValue(m_propertyDescriptor.getId());

	        list.add(m_removeIndex, m_value);
	        
	        m_removeIndex = -1;
		}
	}
}
