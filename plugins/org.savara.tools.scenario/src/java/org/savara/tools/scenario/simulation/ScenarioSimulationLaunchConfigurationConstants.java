/*
 * Copyright 2004-5 Enigmatec Corporation Ltd
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
 * Feb 17, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.simulation;

/**
 * This interface defines the constants for the scenario test
 * launch configuration.
 */
public interface ScenarioSimulationLaunchConfigurationConstants {

	public static final String ATTR_PROJECT_NAME="project";
	
	public static final String ATTR_SCENARIO="scenario";
	
	public static final String ATTR_EXECUTE_SERVICES="execute_services";
	
	public static final String LAUNCH_CONFIG_TYPE=
			"org.pi4soa.scenario.eclipse.ScenarioTestLauncher";
}
