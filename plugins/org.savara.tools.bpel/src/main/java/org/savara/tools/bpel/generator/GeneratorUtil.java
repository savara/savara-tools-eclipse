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
package org.savara.tools.bpel.generator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

/**
 * This class provides general utility functions for
 * use when generating artefacts.
 */
public class GeneratorUtil {

	/**
	 * This method checks whether the parent folder exists,
	 * and if not attempts to create it.
	 * 
	 * @param res The current resource
	 */
	public static void createParentFolder(IResource res) {
		
		if (res.getParent() instanceof IFolder) {
			IFolder parent=(IFolder)res.getParent();
			
			if (parent.exists() == false) {
				createParentFolder(parent);

				try {
					parent.create(true, true,
							new org.eclipse.core.runtime.NullProgressMonitor());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
