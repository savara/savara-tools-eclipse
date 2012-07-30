/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.bpmn2.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.savara.tools.bpmn2.osgi.Activator;

/**
 * This class provides access to the BPMN2 specific Eclipse properties.
 *
 */
public class BPMN2Properties {

	private static final Logger LOG=Logger.getLogger(BPMN2Properties.class.getName());
	
	public static final String GENERATE_MESSAGE_BASED_INVOCATION="savara.bpmn2.genmbi";
	
	/**
	 * This method determines if message based invocation should be used
	 * when generating BPMN2.
	 * 
	 * @param res The resource
	 * @return Whether to use message based invocation
	 */
	public static boolean isGenerateMessageBasedInvocation(IResource res) {
		boolean ret=false;
		
		try {
			if (res.exists()) {
				String val=res.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
								BPMN2Properties.GENERATE_MESSAGE_BASED_INVOCATION));
				
				if (val != null) {
					ret = Boolean.parseBoolean(val);
				}
			}
		} catch (CoreException e) {
			LOG.log(Level.SEVERE, "Failed to get validation property", e);
		}

		return (ret);
	}
}
