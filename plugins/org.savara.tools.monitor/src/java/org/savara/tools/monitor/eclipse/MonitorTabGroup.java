/*
 * Copyright 2005-8 Pi4 Technologies Ltd
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
 * 17 Jan, 2008 : Initial version created by gary
 */
package org.savara.tools.monitor.eclipse;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

/**
 * This class represents the UI tab group for the Scenario Test
 * launcher.
 */
public class MonitorTabGroup extends
			AbstractLaunchConfigurationTabGroup {

	/**
	 * The default constructor for the scenario type tab group.
	 */
	public MonitorTabGroup() {
	}

	/**
	 * This method creates the tabs for the scenario test launch
	 * configuration.
	 * 
	 * @param dialog The launch configuration dialog
	 * @param mode The mode
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			new MonitorMainTab(),
			new JavaJRETab(),
			new CommonTab()
		};
		setTabs(tabs);
	}
}
