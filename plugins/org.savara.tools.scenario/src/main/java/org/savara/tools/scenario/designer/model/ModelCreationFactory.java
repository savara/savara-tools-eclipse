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
 * Feb 14, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.model;

import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.gef.requests.CreationFactory;
import org.savara.scenario.model.Event;
import org.savara.scenario.model.Role;

/**
 * This class is responsible for creating new instances of the
 * Test Scenario model.
 */
public class ModelCreationFactory implements CreationFactory {

	private static Logger logger = Logger.getLogger("org.pi4soa.service.test.designer.view");	

	private Class<?> m_targetClass=null;

	public ModelCreationFactory(Class<?> targetClass) {
		m_targetClass = targetClass;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		Object ret=null;
		
		try {
			ret = m_targetClass.newInstance();
			
			if (ret instanceof Event) {
				((Event)ret).setId(UUID.randomUUID().toString());
			} else if (ret instanceof Role) {
				((Role)ret).setId(UUID.randomUUID().toString());				
			}
		} catch(Exception e) {
			logger.severe("Failed to create new object of type '"+
					m_targetClass+"': "+e);
		}
		
		return(ret);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return(m_targetClass);
	}
}
