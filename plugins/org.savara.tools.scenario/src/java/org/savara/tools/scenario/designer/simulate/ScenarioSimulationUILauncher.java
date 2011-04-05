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
public class ScenarioSimulationUILauncher {

	public ScenarioSimulationUILauncher(Display display,
					Tree results, Text output) {
		m_display = display;
		m_results = results;
		m_output = output;
		
		/*
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
		*/
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
					processResultLine(infoPos,
							newlinePos);
					
					m_currentPosition = newlinePos;
					
					f_entryFound = true;
				}
			}
		} while(f_entryFound);
	}
	
	protected void processResultLine(int start, int end) {
		/* TODO: GPB: How is the stdout processed?
		 * 
		 *
		String tag=null;
		String line=m_buffer.substring(start, end);
		
		if (line.startsWith("INFO:")) {
			int tagEndPos=line.indexOf(' ', 7);
			tag = line.substring(7, tagEndPos);
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
		
		// Get text
		int textPos=line.indexOf(ScenarioSimulator.TEXT_ATTR+"=\"");
		int textEndPos=line.indexOf('"', textPos+2+
				ScenarioSimulator.TEXT_ATTR.length());
		String text=line.substring(textPos+2+
				ScenarioSimulator.TEXT_ATTR.length(), textEndPos);

		boolean create=tag.equals(ScenarioSimulator.PROCESSING_TAG);
		TreeItem ti=getTreeItem(id, create);
		
		if (ti != null) {
			if (create) {
				ti.setText(text);
				ti.setForeground(m_results.getShell().getDisplay().
						getSystemColor(SWT.COLOR_DARK_YELLOW));
				
				// Add data to tree item to indicate position
				// information
				ScenarioDetails details=new ScenarioDetails();
				details.setStartPosition(start);
				
				ti.setData(details);
			} else {
				
				if (tag.equals(ScenarioSimulator.COMPLETED_TAG)) {
					ti.setForeground(m_results.getShell().getDisplay().
							getSystemColor(SWT.COLOR_DARK_GREEN));
				} else {
					
					ti.setForeground(m_results.getShell().getDisplay().
							getSystemColor(SWT.COLOR_RED));

					// Need to propagate error to parent tree items
					TreeItem parent=ti;
					Image image=ScenarioSimulationImages.getImage("error_obj.gif");

					while (parent != null) {
						parent.setImage(image);
						
						parent = parent.getParentItem();						
					}
				}
				
				ScenarioDetails details=(ScenarioDetails)ti.getData();
				if (details != null) {
					details.setEndPosition(end);
					
					// Check for signs of SEVERE messages
					String substr=null;
					
					if (ti.getItemCount() == 0) {
						
						// Only check leaf nodes if they have not completed
						// successfully - otherwise it means that the
						// exceptions (errors) are being ignored (i.e. they
						// are expected)
						if (tag.equals(ScenarioSimulator.COMPLETED_TAG) == false) {
							substr = m_buffer.substring(details.getStartPosition(),
										details.getEndPosition());
						}
						
					} else {
						TreeItem sub1=ti.getItem(0);
						ScenarioDetails sub1data=(ScenarioDetails)sub1.getData();
						TreeItem sub2=ti.getItem(ti.getItemCount()-1);
						ScenarioDetails sub2data=(ScenarioDetails)sub2.getData();
						
						substr = m_buffer.substring(details.getStartPosition(),
								sub1data.getStartPosition());
						
						substr += m_buffer.substring(sub2data.getEndPosition(),
								details.getEndPosition());
					}
					
					if (substr != null && substr.indexOf("SEVERE:") != -1) {
						
						Image image=ScenarioSimulationImages.getImage("error_obj.gif");
						ti.setImage(image);
						
					}
				}

				m_results.update();
			}
		}
		*/
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
