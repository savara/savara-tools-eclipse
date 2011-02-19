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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = org.savara.tools.monitor.eclipse.Activator.getDefault().getPreferenceStore();
		store.setDefault(MonitorPreferences.JNDI_FACTORY_INITIAL, "org.jnp.interfaces.NamingContextFactory");
		store.setDefault(MonitorPreferences.JNDI_PROVIDER_URL, "jnp://localhost:1099");
		store.setDefault(MonitorPreferences.JNDI_FACTORY_URL_PKGS, "org.jboss.naming:org.jnp.interfaces");
		store.setDefault(MonitorPreferences.JMS_FACTORY, "ConnectionFactory");
		store.setDefault(MonitorPreferences.JMS_DESTINATION, "topic/tracker");
	}

}
