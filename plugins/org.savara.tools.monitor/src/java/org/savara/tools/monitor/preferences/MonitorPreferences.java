/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.savara.tools.monitor.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

public class MonitorPreferences {

	public static final String MONITOR_LIBRARIES="savara.monitor.libraries";
	public static final String JNDI_FACTORY_INITIAL="savara.monitor.jndi.factory.initial";
	public static final String JNDI_PROVIDER_URL="savara.monitor.jndi.provider.url";
	public static final String JNDI_FACTORY_URL_PKGS="savara.monitor.jndi.factory.url.pkgs";
	public static final String JMS_FACTORY="savara.monitor.jms.factory";
	public static final String JMS_DESTINATION="savara.monitor.jms.destination";
	
	public static String getLibraryPaths() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(MONITOR_LIBRARIES));
	}
	
	public static boolean isLibraryPathsDefined() {
		boolean ret=false;
		
		IPreferenceStore prefs=
			org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		String paths=prefs.getString(MONITOR_LIBRARIES);
		
		if (paths != null && paths.length() > 0) {
			ret = true;
		}
		
		return(ret);
	}

	public static String getJNDIFactoryInitial() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(JNDI_FACTORY_INITIAL));
	}
	

	public static String getJNDIProviderURL() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(JNDI_PROVIDER_URL));
	}

	public static String getJNDIFactoryURLPackages() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(JNDI_FACTORY_URL_PKGS));
	}

	public static String getJMSFactory() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(JMS_FACTORY));
	}

	public static String getJMSDestination() {
		IPreferenceStore prefs=
				org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		
		return(prefs.getString(JMS_DESTINATION));
	}
}
