/*
 * Copyright 2005-7 Pi4 Technologies Ltd
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
 * 26 Feb 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.simulate;

public interface ScenarioSimulation {

    /**
     * This method returns the scenario.
     * 
     * @return The scenario
     */
    public org.savara.scenario.model.Scenario getScenario();
    
	public void startSimulation();
	
	public void resetSimulation();
	
	public boolean isSimulationRunning();
	
	public SimulationEntity getSimulationEntity(Object model, boolean focus);
	
	public void appendLogEntry(String results);
	
	public String getLogEntry(int start, int end);
	
    public String getLogEntry(Object scenarioObject);
    
    /**
     * This method focuses the environment on the supplied
     * URL and region name.
     * 
     * @param scenarioURL The scenario path
     * @param regionName The optional region name
     */
    public void focus(String scenarioURL, String regionName);
}
