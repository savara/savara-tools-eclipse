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
 * Feb 23, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.simulate;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.savara.scenario.simulation.ScenarioSimulator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * This class is derived from the scenario test launcher with the
 * ability to present the results in a graphical form.
 */
public class ScenarioSimulationUILauncher extends ScenarioSimulationLauncher {

	public ScenarioSimulationUILauncher(Display display,
					Tree results, Text output) {
		m_display = display;
		m_results = results;
		m_output = output;
		
		m_results.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (e.item instanceof TreeItem &&
						e.item.getData() instanceof ScenarioDetails) {
					ScenarioDetails details=(ScenarioDetails)
									e.item.getData();
					
					m_output.clearSelection();
					
					if (details.getStartPosition() <
							details.getEndPosition()) {
						m_output.setSelection(details.getStartPosition(),
								details.getEndPosition());
					}
				}
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				widgetSelected(e);
			}
		});
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
		
		System.out.println(">>"+results);
		
		if (errorStream) {
			
			try {
				m_display.asyncExec(new Runnable() {
					public void run() {				
						processResults(text);
						
						m_output.append(text);
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
		
		m_buffer.append(results);
		
		do {
			int infoPos=0;
			f_entryFound=false;
			
			// Check if "INFO: <" is found
			infoPos=m_buffer.indexOf("INFO: <",
					m_currentPosition);
					
			int tmpPos=m_buffer.indexOf("SEVERE: <",
					m_currentPosition);
			
			if (tmpPos != -1 && tmpPos < infoPos) {
				infoPos = tmpPos;
			}
			
			if (infoPos != -1) {
				int newlinePos=0;
				
				// Check if newline found
				if ((newlinePos=m_buffer.indexOf("\r\n",
						infoPos)) != -1) {
				
					// Complete line found
					System.out.println("PROCESS RESULT LINE: "+infoPos+" "+newlinePos);
					// TODO:
					//processResultLine(infoPos,
					//		newlinePos);
					
					m_currentPosition = newlinePos;
					
					f_entryFound = true;
				}
			}
		} while(f_entryFound);
	}

	protected TreeItem getTreeItem(String id, boolean create) {
		TreeItem ret=null;
		StringTokenizer st=new StringTokenizer(id, "/");
		
		// Ignore scenario identity - may be useful in constructing
		// tree items when multiple scenarios are being run
		st.nextToken();
		
		while (st.hasMoreTokens()) {
			String token=st.nextToken();
			
			try {
				int pos=Integer.parseInt(token);
				
				if (ret == null) {
					if (create && st.hasMoreTokens()==false) {
						ret = new TreeItem(m_results, SWT.NONE);
					} else {
						ret = m_results.getItem(pos);
					}
				} else {
					if (create && st.hasMoreTokens()==false) {
						ret = new TreeItem(ret, SWT.NONE);
					} else {
						ret = ret.getItem(pos);
					}
				}
			} catch(Exception e) {
				// Ignore
			}
		}
		
		return(ret);
	}

	private Display m_display=null;
	private Tree m_results=null;
	private Text m_output=null;
	private int m_currentPosition=0;
	private StringBuffer m_buffer=new StringBuffer();
}
