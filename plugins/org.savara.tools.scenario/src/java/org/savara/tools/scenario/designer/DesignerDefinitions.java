/*
 * Copyright 2005 Pi4 Technologies Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Change History:
 * Jul 5, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer;

//import org.pi4soa.common.resource.ResourceLocator;

/**
 * This interface contains general definitions used within the
 * designer.
 */
public class DesignerDefinitions {

    /**
     * This method returns a configuration message for the
     * designer plugin.
     * 
     * @param key The key
     * @param params The list of parameters
     * @return The message
     */
	/*
	public static String getMessage(String key, Object[] params) {
		return(ResourceLocator.getMessage(DesignerDefinitions.DESIGNER_RESOURCE,
				key, params));
	}
	*/
    
	/**
	 * This method returns the boolean preference value associated
	 * with the supplied name.
	 * 
	 * @param pref The preference name
	 * @return Whether the boolean preference is true
	 */
	public static boolean isPreference(String pref) {
		return(org.savara.tools.scenario.osgi.Activator.getDefault().getPreferenceStore().
					getBoolean(pref));
	}
    
    public static final String DESIGNER_PLUGIN_ID=
    			org.savara.tools.scenario.osgi.Activator.PLUGIN_ID;

    public static final String DESIGNER_RESOURCE="tsdesigner";
    
    public static final String BUSINESS_VIEW="businessView";
    
}
