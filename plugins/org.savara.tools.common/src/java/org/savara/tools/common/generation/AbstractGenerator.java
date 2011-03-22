/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.common.generation;

import org.eclipse.core.resources.IResource;
import org.savara.common.model.annotation.AnnotationDefinitions;
import org.savara.protocol.util.ProtocolServices;
import org.scribble.common.logging.Journal;
import org.scribble.common.resource.DefaultResourceLocator;
import org.scribble.protocol.DefaultProtocolContext;
import org.scribble.protocol.model.ProtocolModel;
import org.scribble.protocol.model.Role;

/**
 * This abstract implementation represents the base class for
 * the generator, to provide useful capabilities that may be
 * used by any artifact specific generator implementation.
 *
 */
public abstract class AbstractGenerator implements Generator {

	private String m_name=null;
	
	/**
	 * The constructor.
	 * 
	 * @param name The name of the generator
	 */
	public AbstractGenerator(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the name of the generator, used in dialog windows to
	 * select the generator.
	 * 
	 * @return The generator
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method projects the supplied global model to a local model associated with
	 * the specified role.
	 * 
	 * @param global The global model
	 * @param role The role
	 * @param resource The resource associated with the model
	 * @param journal The journal
	 * @return The local model associated with the role
	 */
	protected ProtocolModel getProtocolModelForRole(ProtocolModel global, Role role, IResource resource,
									Journal journal) {
		ProtocolModel ret=null;
		
		DefaultProtocolContext context=new DefaultProtocolContext(ProtocolServices.getParserManager(),
				new DefaultResourceLocator(resource.getParent().getFullPath().toFile()));

		ret = ProtocolServices.getProtocolProjector().project(global,
						role, journal, context);

		if (ret != null) {
			// TODO: SAVARA-167 - issue when projection is based on a sub-protocol
			if (AnnotationDefinitions.getAnnotation(ret.getProtocol().getAnnotations(),
							AnnotationDefinitions.TYPE) == null &&
					AnnotationDefinitions.getAnnotation(global.getProtocol().getAnnotations(),
									AnnotationDefinitions.TYPE) != null) {				
				AnnotationDefinitions.copyAnnotations(global.getProtocol().getAnnotations(),
						ret.getProtocol().getAnnotations(), AnnotationDefinitions.TYPE);
			}
		}
		
		return(ret);
	}
}
