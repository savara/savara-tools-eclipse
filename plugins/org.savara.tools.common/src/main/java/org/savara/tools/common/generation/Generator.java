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
import org.savara.tools.common.ArtifactType;
import org.savara.common.logging.FeedbackHandler;
import org.scribble.protocol.model.ProtocolModel;
import org.scribble.protocol.model.Role;

/**
 * This interface represents a generator that will create 
 * technology specific artifacts based on the supplied protocol
 * model and role information.
 *
 */
public interface Generator {

	/**
	 * This method returns the name of the generator, used in dialog windows to
	 * select the generator.
	 * 
	 * @return The generator
	 */
	public String getName();
	
	/**
	 * This method generates some artifacts based on the supplied model and
	 * role.
	 * 
	 * If specified, the optional project name will be used to create a
	 * new Eclipse project for the generated artifacts. If no project name
	 * is specified, then the artifacts will be created in the model
	 * resource's project.
	 * 
	 * @param model The protocol model
	 * @param role The role
	 * @param projectName The optional project name
	 * @param modelResource The resource associated with the model
	 * @param journal The journal for reporting issues
	 */
	public void generate(ProtocolModel model, Role role, String projectName,
						IResource modelResource, FeedbackHandler journal);
	
	/**
	 * This method returns the artifact type that will be generated.
	 * 
	 * @return The artifact type
	 */
	public ArtifactType getArtifactType();
	
}
