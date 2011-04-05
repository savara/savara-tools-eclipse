/*
 * Copyright 2005-8 Pi4 Technologies Ltd
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
 * 18 Jan, 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.commands;

import org.eclipse.ui.views.properties.*;

/**
 * This class implements the set property command.
 */
public class SetPropertyCommand
			extends org.eclipse.gef.commands.Command {
	
	public SetPropertyCommand() {
	}
		
	public void execute() { 	
		
        m_oldValue = m_property.getPropertyValue(
        			m_propertyDescriptor.getId());
        
        m_property.setPropertyValue(m_propertyDescriptor.getId(),
								m_value);
	}
	
	public void redo() {		
        m_property.setPropertyValue(m_propertyDescriptor.getId(),
				m_value);
	}
	
	public void setValue(Object value) {
		m_value = value;
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
	
	public void setPropertySource(IPropertySource source) {
		m_property = source;
	}
	
	public void setPropertyDescriptor(IPropertyDescriptor descriptor) {
		m_propertyDescriptor = descriptor;
	}
	
	public void undo() {
		
        m_property.setPropertyValue(m_propertyDescriptor.getId(),
				m_oldValue);
	}
		
	private IPropertySource m_property=null;
    private IPropertyDescriptor m_propertyDescriptor=null;
    private Object m_value=null;
    private Object m_oldValue=null;
}
