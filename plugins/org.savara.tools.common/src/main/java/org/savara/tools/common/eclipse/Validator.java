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
package org.savara.tools.common.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.savara.protocol.util.ProtocolServices;
import org.savara.tools.common.logging.EclipseLogger;
import org.savara.tools.common.osgi.Activator;
import org.scribble.common.resource.FileContent;
import org.scribble.protocol.DefaultProtocolContext;
import org.scribble.protocol.model.ProtocolModel;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * The class provides resource validation capabilities.
 */
public class Validator extends AbstractValidator {

    /**
	 * The constructor
	 */
	public Validator() {
	}

	/**
	 * {@inheritDoc}
	 */
    public ValidationResult validate(ValidationEvent event, ValidationState state, IProgressMonitor monitor) {
        ValidationResult result = new ValidationResult();

        if ((event.getKind() & IResourceDelta.REMOVED) != 0 || event.getResource().isDerived(IResource.CHECK_ANCESTORS)) {
            return result;
        }
        
        validateResource(event.getResource());

        return (result);
    }
    
	/**
	 * This method validates the supplied resource.
	 * 
	 * @param res The resource
	 */
	protected void validateResource(IResource res) {
		
        try {
             FileContent content=new FileContent(((IFile)res).getRawLocation().toFile());
             
             if (ProtocolServices.getParserManager().isParserAvailable(content)) {

                 DefaultProtocolContext context=new DefaultProtocolContext();
                 context.setProtocolParserManager(ProtocolServices.getParserManager());
                 
                 EclipseLogger journal=new EclipseLogger((IFile)res);

                 ProtocolModel pm=ProtocolServices.getParserManager().parse(context, content, journal);
            	 
                 if (pm != null && !journal.hasErrorOccurred()) {
                	 ProtocolServices.getValidationManager().validate(context, pm, journal);
                 }
                 
                 journal.finished();
             }
            
        } catch (Exception e) {
            Activator.logError("Failed to validate model for resource '"+res+"'", e);
        }
	}
	
}
