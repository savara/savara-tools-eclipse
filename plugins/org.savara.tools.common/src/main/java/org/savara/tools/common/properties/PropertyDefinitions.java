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
package org.savara.tools.common.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.savara.tools.common.osgi.Activator;

/**
 * This interface defines the common property definitions.
 *
 */
public class PropertyDefinitions {

	private static final Logger LOG=Logger.getLogger(PropertyDefinitions.class.getName());
	
	/**
	 * This property is a boolean flag to indicate whether
	 * validation should be performed on the associated
	 * resource.
	 */
	public static final String VALIDATE_PROPERTY="savara.validate";
	
	/**
	 * This method determines whether validation has been enabled for the supplied
	 * resource.
	 * 
	 * @param res The resource
	 * @return Whether validation has been enabled
	 */
	public static boolean isValidationEnabled(IResource res) {
		boolean ret=false;
		
		try {
			if (res.exists()) {
				String val=res.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
								PropertyDefinitions.VALIDATE_PROPERTY));
				
				if (val != null) {
					ret = Boolean.parseBoolean(val);
				}
			}
		} catch (CoreException e) {
			LOG.log(Level.SEVERE, "Failed to get validation property", e);
		}

		return(ret);
	}
}
