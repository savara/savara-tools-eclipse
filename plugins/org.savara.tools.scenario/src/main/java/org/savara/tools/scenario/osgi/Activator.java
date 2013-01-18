package org.savara.tools.scenario.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.savara.scenario.simulation.RoleSimulator;
import org.savara.scenario.simulation.RoleSimulatorFactory;
import org.savara.scenario.simulator.protocol.ProtocolRoleSimulator;
import org.savara.tools.scenario.designer.simulate.RoleSimulatorBundleRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.savara.tools.scenario"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
    private static Logger LOG = Logger.getLogger(Activator.class.getName());

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

		RoleSimulatorFactory.register(new ProtocolRoleSimulator());
        
		try {
			// Initialize list of generators
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint("org.savara.scenario.simulation.RoleSimulator");
	
			if (point != null) {
				IExtension[] extensions = point.getExtensions();
				
				for (int i = 0; i < extensions.length; i++) {
					for (int j=0; j < extensions[i].getConfigurationElements().length; j++) {
						
						if (extensions[i].getConfigurationElements()[j].getName().equals("simulator")) {
							IConfigurationElement elem=extensions[i].getConfigurationElements()[j];

							if (LOG.isLoggable(Level.FINE)) {
								LOG.fine("Role simulator extension: "+elem.getAttribute("class"));
							}
							
							try {
								Object am=elem.createExecutableExtension("class");	
								
								if (am instanceof RoleSimulator) {
									RoleSimulatorFactory.register((RoleSimulator)am);
									
									String bundleName=elem.getAttribute("bundle");
									
									Bundle bundle=Platform.getBundle(bundleName);
									
									if (bundle != null) {
										RoleSimulatorBundleRegistry.register(bundle);
									}
									
								} else {
									LOG.severe("Failed to load role simulator: "+am);
								}
							} catch(Exception e) {
								LOG.log(Level.SEVERE, "Failed to load role simulator", e);
							}
						}
					}
				}
			}
		} catch(Throwable t) {
			// Ignore classes not found, so can be used outside Eclipse
		}
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
		
		LOG.severe("LOG ERROR: "+mesg+
				(t == null ? "" : ": "+t));
	}
}
