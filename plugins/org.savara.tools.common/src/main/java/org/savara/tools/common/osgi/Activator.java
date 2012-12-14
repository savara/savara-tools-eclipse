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
package org.savara.tools.common.osgi;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.savara.protocol.util.ProtocolServices;
import org.savara.tools.common.logging.EclipseLogger;
import org.savara.tools.common.properties.PropertyDefinitions;
import org.scribble.common.logging.ConsoleJournal;
import org.scribble.common.logging.Journal;
import org.scribble.common.resource.FileContent;
import org.scribble.protocol.DefaultProtocolContext;
import org.scribble.protocol.export.DefaultProtocolExportManager;
import org.scribble.protocol.export.ProtocolExportManager;
import org.scribble.protocol.export.ProtocolExporter;
import org.scribble.protocol.export.text.TextProtocolExporter;
import org.scribble.protocol.export.text.TextProtocolExporterRule;
import org.scribble.protocol.model.ProtocolModel;
import org.scribble.protocol.parser.DefaultProtocolParserManager;
import org.scribble.protocol.parser.ProtocolParser;
import org.scribble.protocol.parser.ProtocolParserManager;
import org.scribble.protocol.parser.antlr.ANTLRProtocolParser;
import org.scribble.protocol.projection.ProtocolProjector;
import org.scribble.protocol.projection.impl.ProjectorRule;
import org.scribble.protocol.projection.impl.ProtocolProjectorImpl;
import org.scribble.protocol.validation.DefaultProtocolValidationManager;
import org.scribble.protocol.validation.ProtocolValidationManager;
import org.scribble.protocol.validation.ProtocolValidator;
import org.scribble.protocol.validation.rules.DefaultProtocolComponentValidator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.savara.tools.core";

	// The shared instance
	private static Activator plugin;
	
	private static final Logger LOG=Logger.getLogger(Activator.class.getName());
	
    private org.osgi.util.tracker.ServiceTracker _protocolParserTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolValidatorTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolProjectorTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolExporterTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolTextExporterRuleTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolValidationManagerTracker=null;
    private org.osgi.util.tracker.ServiceTracker _protocolProjectorRuleTracker=null;

    /**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

        java.util.Hashtable<String,Object> props = new java.util.Hashtable<String,Object>();

        // Register parser manager
        final ProtocolParserManager pm=new DefaultProtocolParserManager();
        
        context.registerService(ProtocolParserManager.class.getName(), 
                            pm, props);
        
        LOG.fine("Registered Parser Manager");
        
        _protocolParserTracker = new ServiceTracker(context,
                org.scribble.protocol.parser.ProtocolParser.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Parser has been added: "+ret);
                
                pm.getParsers().add((ProtocolParser)ret);
                
                return (ret);
            }
        };
        
        _protocolParserTracker.open();

        // Register validation manager
        final ProtocolValidationManager vm=new DefaultProtocolValidationManager();
        
        context.registerService(ProtocolValidationManager.class.getName(), 
                            vm, props);
        
        LOG.fine("Registered Validation Manager");
        
        _protocolValidatorTracker = new ServiceTracker(context,
                org.scribble.protocol.validation.ProtocolValidator.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Validator has been added: "+ret);
                
                vm.getValidators().add((ProtocolValidator)ret);
                
                return (ret);
            }
        };
        
        _protocolValidatorTracker.open();

        _protocolProjectorTracker = new ServiceTracker(context,
                org.scribble.protocol.projection.ProtocolProjector.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Projector has been added to validator manager: "+ret);
                
                vm.setProtocolProjector((ProtocolProjector)ret);
                
                return (ret);
            }
        };
        
        _protocolProjectorTracker.open();

        // Register export manager
        final ProtocolExportManager em=new DefaultProtocolExportManager();
        
        context.registerService(ProtocolExportManager.class.getName(), 
                            em, props);
        
        LOG.fine("Registered Export Manager");
        
        _protocolExporterTracker = new ServiceTracker(context,
                org.scribble.protocol.export.ProtocolExporter.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Exporter has been added: "+ret);
                
                em.getExporters().add((ProtocolExporter)ret);
                
                return (ret);
            }
        };
        
        _protocolExporterTracker.open();

        // Register console journal
        context.registerService(Journal.class.getName(), 
                new ConsoleJournal(), props);

        // Register protocol validator
        ProtocolValidator pv=new DefaultProtocolComponentValidator();
        
        context.registerService(ProtocolValidator.class.getName(), 
        			pv, props);        

        // Register text based exporter
        final TextProtocolExporter tpe=new TextProtocolExporter();
        
        context.registerService(ProtocolExporter.class.getName(), 
                new TextProtocolExporter(), props);        

        LOG.fine("Registered Text Protocol Exporter");
        
        _protocolTextExporterRuleTracker = new ServiceTracker(context,
                org.scribble.protocol.export.text.TextProtocolExporterRule.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Text Exporter Rule has been added: "+ret);
                
                tpe.register((TextProtocolExporterRule)ret);
                
                return (ret);
            }
        };
        
        _protocolTextExporterRuleTracker.open();

		
		
		
		
		
		
		
		
		
		
		
		
		
		// Make sure any bundles, associated with scribble and savara, are started (excluding
		// the designer itself)
		// TODO: This should may be in a more general Eclipse plugin, but currently
		// there is no tools core
		Bundle[] bundles=context.getBundles();

		for (int i=0; i < bundles.length; i++) {
			Bundle bundle=bundles[i];
			
			if (bundle != null) {
				if (bundle.getSymbolicName().startsWith("org.savara.") &&
						!bundle.getSymbolicName().startsWith("org.savara.tools.common")) {
				
					//if (bundle.getState() == Bundle.RESOLVED) {
						LOG.fine("Pre-empt bundle start: "+bundle);
						bundle.start();
					//}
				}
			}
		}


        ANTLRProtocolParser pp=new ANTLRProtocolParser();
        
        context.registerService(ProtocolParser.class.getName(), 
                pp, props);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Protocol parser registered");
        }

        final ProtocolProjectorImpl ppj=new ProtocolProjectorImpl();
        
        context.registerService(ProtocolProjector.class.getName(), 
        				ppj, props);
        
        // Detect protocol validation manager
        _protocolValidationManagerTracker = new ServiceTracker(context,
                org.scribble.protocol.validation.ProtocolValidationManager.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Validation manager has been added to projector: "+ret);
                
                ppj.setProtocolValidationManager((org.scribble.protocol.validation.ProtocolValidationManager)ret);
                
                return (ret);
            }
        };
        
        _protocolValidationManagerTracker.open();

        // Detect additional protocol projection rules
        _protocolProjectorRuleTracker = new ServiceTracker(context,
                org.scribble.protocol.projection.impl.ProjectorRule.class.getName(),
                        null) {
            
            public Object addingService(ServiceReference ref) {
                Object ret=super.addingService(ref);
                
                LOG.fine("Projection rule has been added: "+ret);
                
                ppj.getCustomRules().add((ProjectorRule)ret);
                
                return (ret);
            }
        };
        
        _protocolProjectorRuleTracker.open();

        // Set the parser and validation managers
        ProtocolServices.setParserManager(pm);
        ProtocolServices.setValidationManager(vm);

		// Register resource change listener
		IResourceChangeListener rcl=
				new IResourceChangeListener() {
		
			public void resourceChanged(IResourceChangeEvent evt) {
	
				try {
					evt.getDelta().accept(new IResourceDeltaVisitor() {
						
				        public boolean visit(IResourceDelta delta) {
				        	boolean ret=true;
				        	IResource res = delta.getResource();
				        	
							// Determine if the change is relevant
							if (isChangeRelevant(res,
										delta)) {
								
								// Validate the resource
								validateResource(res);
							}
							
				        	return(ret);
				        }
				 	});
				} catch(Exception e) {
					LOG.log(Level.SEVERE, "Failed to process resource change event", e);
				}
			}
		};
	
		// Register the resource change listener
		ResourcesPlugin.getWorkspace().addResourceChangeListener(rcl,
				IResourceChangeEvent.POST_CHANGE);		

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
            	 
                 if (!journal.hasErrorOccurred()) {
                	 ProtocolServices.getValidationManager().validate(context, pm, journal);
                 }
                 
                 journal.finished();
             }
            
        } catch (Exception e) {
            Activator.logError("Failed to record validation issue on resource '"+res+"'", e);
        }
	}
	
	/**
	 * This method determines whether the supplied resource
	 * change event is relevant.
	 * 
	 * @param res The resource
	 * @param deltaFlags The flags
	 * @return Whether the change is relevant
	 */
	protected boolean isChangeRelevant(IResource res, IResourceDelta delta) {
		boolean ret=false;

		if (res != null && PropertyDefinitions.isValidationEnabled(res) &&
				(((delta.getFlags() & IResourceDelta.CONTENT) != 0) ||
				delta.getKind() == IResourceDelta.ADDED)) {
			ret = true;
		}

		return(ret);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * This method logs an error against the plugin.
	 * 
	 * @param mesg The error message
	 * @param t The optional exception
	 */
	public static void logError(String mesg, Throwable t) {
		
		if (getDefault() != null) {
			Status status=new Status(IStatus.ERROR,
					PLUGIN_ID, 0, mesg, t);
			
			getDefault().getLog().log(status);
		}
		
		LOG.log(Level.SEVERE, "LOG ERROR: "+mesg+
				(t == null ? "" : ": "+t), t);
	}

	@Override
	public void earlyStartup() {
	}
}
