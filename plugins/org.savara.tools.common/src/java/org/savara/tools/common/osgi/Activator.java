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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.savara.tools.common.properties.PropertyDefinitions;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.savara.tools.core";

	// The shared instance
	private static Activator plugin;
	
	private static final Logger LOG=Logger.getLogger(Activator.class.getName());
	
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

		// Make sure any bundles, associated with scribble and savara, are started (excluding
		// the designer itself)
		// TODO: This should may be in a more general Eclipse plugin, but currently
		// there is no tools core
		Bundle[] bundles=context.getBundles();

		for (int i=0; i < bundles.length; i++) {
			Bundle bundle=bundles[i];
			
			if (bundle != null) {
				if ((bundle.getSymbolicName().startsWith("org.scribble.") &&
						bundle.getSymbolicName().endsWith("designer") == false) ||
						(bundle.getSymbolicName().startsWith("org.savara.") &&
						bundle.getSymbolicName().startsWith("org.savara.tools.") == false)) {
				
					//if (bundle.getState() == Bundle.RESOLVED) {
						LOG.fine("Pre-empt bundle start: "+bundle);
						bundle.start();
					//}
				}
			}
		}

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
        	// TODO: Check if protocol parser available, and if so, then
        	// validate
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
}
