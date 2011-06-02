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
 * Feb 26, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.simulate;

import org.eclipse.swt.widgets.Display;
import org.savara.scenario.model.Event;

/**
 * This class is derived from the scenario simulation launcher with the
 * ability to present the results in a graphical form.
 */
public class ScenarioDesignerSimulationLauncher extends ScenarioSimulationLauncher {

	public ScenarioDesignerSimulationLauncher(Display display,
			org.savara.scenario.model.Scenario scenario,
					ScenarioSimulation simulation) {
		m_display = display;
		m_scenario = scenario;
		m_scenarioSimulation = simulation;
	}
	
	/**
	 * This method handles the results produced by the launched
	 * test.
	 * 
	 * @param results The results
	 * @param errorStream Whether the results are from the error
	 * 						stream
	 */
	protected void handleResults(String results, boolean errorStream) {		
		final String text=results;
		
System.out.println(">>: "+results);		
		if (errorStream) {
			
			try {
				m_display.asyncExec(new Runnable() {
					public void run() {				
						processResults(text);
					}			
				});
			} catch(Throwable e) {
				org.savara.tools.scenario.osgi.Activator.logError(
						"Failed to display scenario test results", e);
			}
		}
	}
	
	protected void processResults(String results) {
		boolean f_entryFound=false;
		
		m_scenarioSimulation.appendLogEntry(results);
		
		m_log.append(results);
		
		do {
			int infoPos=0;
			f_entryFound=false;
			
			// Check if "INFO: <" is found
			// GPB(4/7/08) removed 'INFO' to ensure internationalization
			// of java logging tags is catered for - issue is how to
			// detect SEVERE messages?
			infoPos=m_log.indexOf(">>> ",
					m_currentPosition);
					
			if (infoPos != -1) {
				int newlinePos=0;
				
				// Check if newline found
				if ((newlinePos=m_log.indexOf("\r",
						infoPos)) != -1 ||
					(newlinePos=m_log.indexOf("\n",
								infoPos)) != -1) {
				
					// Complete line found
					processResultLine(infoPos+4,
							newlinePos);
					
					m_currentPosition = newlinePos;
					
					f_entryFound = true;
				}
			}
		} while(f_entryFound);
		
	}
	
	protected void processResultLine(int start, int end) {
		String tag=null;
		String line=m_log.substring(start, end);
		
		int tagEndPos=line.indexOf(' ');
		tag = line.substring(0, tagEndPos);
		
		int idstart=line.indexOf("[ID=");
		int idend=line.indexOf(']');
		
		String id=line.substring(idstart+4, idend);
		
		// Get scenario entity
		SimulationEntity se=getScenarioEntity(id);
		
		if (se != null) {
			if (tag.equals("START")) {
				se.processing();
				se.setLogStartPosition(start);
			} else if (tag.equals("END")) {
				se.setLogEndPosition(end);
			} else if (tag.equals("SUCCESS")) {
				se.successful();
			} else if (tag.equals("FAIL")) {
				se.unsuccessful();
			}
		}
	}
	
	protected SimulationEntity getScenarioEntity(String id) {
		SimulationEntity ret=null;
		
		for (Event event : m_scenario.getEvent()) {
			if (event.getId().equals(id)) {
				ret = m_scenarioSimulation.getSimulationEntity(event, false);
				break;
			}
		}
		
		return(ret);
	}

	//private static Logger logger = Logger.getLogger("org.pi4soa.scenario.provider");

	private Display m_display=null;
	private ScenarioSimulation m_scenarioSimulation=null;
	private org.savara.scenario.model.Scenario m_scenario=null;
	private int m_currentPosition=0;
	private StringBuffer m_log=new StringBuffer();
}
