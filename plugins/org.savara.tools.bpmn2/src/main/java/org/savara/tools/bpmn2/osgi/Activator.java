package org.savara.tools.bpmn2.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.savara.bpmn2.parser.choreo.BPMN2ChoreographyProtocolParser;
import org.scribble.protocol.parser.ProtocolParser;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.savara.tools.bpel";

	// The shared instance
	private static Activator plugin;
	
	private static Logger logger = Logger.getLogger(Activator.class.getName());
	
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
		
		java.util.Dictionary<String,Object> props = new java.util.Hashtable<String,Object>();
        
		BPMN2ChoreographyProtocolParser pp=new BPMN2ChoreographyProtocolParser();
        
		context.registerService(ProtocolParser.class.getName(),
						pp, props);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("BPMN2 Protocol Parser registered");
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
		
		logger.log(Level.SEVERE, "LOG ERROR: "+mesg+
				(t == null ? "" : ": "+t), t);
	}
}
