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


import java.util.StringTokenizer;
//import java.util.logging.Logger;

import org.savara.scenario.simulation.ScenarioSimulator;
import org.eclipse.swt.widgets.Display;
import org.savara.tools.scenario.simulation.ScenarioSimulationLauncher;

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
			infoPos=m_log.indexOf(": <",
					m_currentPosition);
					
			int tmpPos=m_log.indexOf("SEVERE: <",
					m_currentPosition);
			
			if (tmpPos != -1 && tmpPos < infoPos) {
				infoPos = tmpPos;
			}
			
			if (infoPos != -1) {
				int newlinePos=0;
				
				// Check if newline found
				if ((newlinePos=m_log.indexOf("\r",
						infoPos)) != -1 ||
					(newlinePos=m_log.indexOf("\n",
								infoPos)) != -1) {
				
					// Complete line found
					processResultLine(infoPos,
							newlinePos);
					
					m_currentPosition = newlinePos;
					
					f_entryFound = true;
				}
			}
		} while(f_entryFound);
	}
	
	protected void processResultLine(int start, int end) {
		/* TODO: GPB: Need to decide how simulator would return its info on stdout
		 *
		 *
		String tag=null;
		String line=m_log.substring(start, end);
		
		if (line.startsWith("INFO:")) {
			int tagEndPos=line.indexOf(' ', 7);
			tag = line.substring(7, tagEndPos);
		} else if (line.startsWith(": <")) {
			int tagEndPos=line.indexOf(' ', 3);
			tag = line.substring(3, tagEndPos);
		} else if (line.startsWith("SEVERE:")) {
			int tagEndPos=line.indexOf(' ', 9);
			tag = line.substring(9, tagEndPos);
		}
		
		if (tag.equals(ScenarioSimulator.PROCESSING_TAG) == false &&
				tag.equals(ScenarioSimulator.COMPLETED_TAG) == false &&
				tag.equals(ScenarioSimulator.FAILED_TAG) == false) {
			return;
		}
		
		// Get id
		int idPos=line.indexOf(ScenarioSimulator.ID_ATTR+"=\"");
		int idEndPos=line.indexOf('"', idPos+2+
				ScenarioSimulator.ID_ATTR.length());
		String id=line.substring(idPos+2+
				ScenarioSimulator.ID_ATTR.length(), idEndPos);
		
		// Get scenario entity
		SimulationEntity se=getScenarioEntity(id);
		
		if (se != null) {
			if (tag.equals(ScenarioSimulator.PROCESSING_TAG)) {
				se.processing();
				se.setLogStartPosition(start);
			} else {
				if (tag.equals(ScenarioSimulator.COMPLETED_TAG)) {
					se.successful();
				} else {
					se.unsuccessful();
				}
				
				se.setLogEndPosition(end);
			}
		}
		*/
	}
	
	protected SimulationEntity getScenarioEntity(String id) {
		SimulationEntity ret=null;
		StringTokenizer st=new StringTokenizer(id, "/");
		
		// Ignore scenario identity - may be useful in constructing
		// tree items when multiple scenarios are being run
		st.nextToken();
		
		Object cur=m_scenario;
		
		while (st.hasMoreTokens()) {
			String token=st.nextToken();
			
			try {
				int pos=Integer.parseInt(token);
				
				java.util.List children=org.savara.tools.scenario.designer.model.ModelSupport.getChildren(cur);
				
				cur = children.get(pos);
				
			} catch(Exception e) {
				// Ignore
			}
		}
		
		// Focus if element is a message event
		ret = m_scenarioSimulation.getSimulationEntity(cur,
				(cur instanceof org.savara.scenario.model.MessageEvent));
		
		return(ret);
	}

	//private static Logger logger = Logger.getLogger("org.pi4soa.scenario.provider");

	private Display m_display=null;
	private ScenarioSimulation m_scenarioSimulation=null;
	private org.savara.scenario.model.Scenario m_scenario=null;
	private int m_currentPosition=0;
	private StringBuffer m_log=new StringBuffer();
}
