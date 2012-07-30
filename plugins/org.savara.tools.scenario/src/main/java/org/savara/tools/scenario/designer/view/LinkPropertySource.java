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
import org.savara.scenario.model.*;

/**
 * This class implements the property source for a scenario
 * message link.
 */
public class LinkPropertySource implements IPropertySource {

    private org.savara.scenario.model.Link m_element=null;

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
		
		if (m_element.getSource() instanceof MessageEvent &&
				m_element.getTarget() instanceof MessageEvent) {
			MessageEventPropertySource meps=new MessageEventPropertySource((MessageEvent)m_element.getSource());
					
			ret = meps.getPropertyDescriptors();

		} else {
			ret = new IPropertyDescriptor[] {};
		}
		
		return(ret);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object ret=null;
		
		if (m_element.getSource() instanceof MessageEvent &&
				m_element.getTarget() instanceof MessageEvent) {
			MessageEventPropertySource meps=new MessageEventPropertySource((MessageEvent)m_element.getSource());

			ret = meps.getPropertyValue(id);
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
		
		if (m_element.getSource() instanceof MessageEvent &&
				m_element.getTarget() instanceof MessageEvent) {
			MessageEventPropertySource smeps=new MessageEventPropertySource((MessageEvent)m_element.getSource());
			MessageEventPropertySource tmeps=new MessageEventPropertySource((MessageEvent)m_element.getTarget());

			smeps.setPropertyValue(id, value);
			tmeps.setPropertyValue(id, value);
		}
	}
}
