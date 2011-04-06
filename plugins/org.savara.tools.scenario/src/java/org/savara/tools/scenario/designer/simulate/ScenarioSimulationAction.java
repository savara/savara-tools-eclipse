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
 * Feb 22, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.simulate;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This class invokes the scenario test action on the selected
 * folder or scenario file.
 * 
 */
public class ScenarioSimulationAction implements IObjectActionDelegate {

	/**
	 * This method implements the action's run method.
	 * 
	 * @param action The action
	 */
	public void run(IAction action) {
		
		if (m_selection instanceof StructuredSelection) {
			StructuredSelection sel=(StructuredSelection)m_selection;
			
			IResource res=(IResource)sel.getFirstElement();
			
			if (res instanceof IFile) {
				
				ScenarioSimulationDialog ssd=
					new ScenarioSimulationDialog(m_targetPart.getSite().getShell());
			
				//ssd.setScenarioFile()
				ssd.open();
			}
			
            // Make sure there are no markers associated
            // with the resource
			/* TODO: GPB: How to manage errors?
			//if (ResourceUtil.hasErrors(res) == false) {
			
				// Load the test scenario and return the CDL resource
				IResource cdlResource=getCDLResource(res);
				
				if (cdlResource != null) {
					
					// Make sure there are no markers associated
			        // with the resource
					
					if (ResourceUtil.hasErrors(cdlResource) == false) {
	
						// Load the CDL model
						org.pi4soa.cdl.Package cdlpack=null;
						String path=cdlResource.getLocation().toOSString();
									
						try {
							cdlpack = CDLManager.load(path);
							
						} catch(IOException ioe) {
							logger.severe("Failed to load CDL '"+path+"': "+ioe);
							
							error(ERROR_LOADING_CDL);
						}
						
						if (cdlpack != null) {
							ScenarioSimulationWindow dialog=
								new ScenarioSimulationWindow(m_targetPart.getSite().getShell(),
										res.getProject().getName(),
										res.getProjectRelativePath().toString(),
										cdlpack, res);
							
							dialog.open();
						}
						
						//launch(res.getLocation().toString(),
						//		res.getProject().getName(),
						//		res.getProjectRelativePath().toString());
					} else {
						error(ERRORS_NO_TEST);
					}
				}
			} else {
				error(SCENARIO_ERRORS_NO_TEST);
			}
			*/
		}
	}
	
	/**
	 * This method returns the CDL resource associated with
	 * the supplied test scenario resource.
	 * 
	 * @param res The test scenario resource
	 * @return The CDL resource, or null if not found
	 */
	/* TODO: GPB: CDL specific

	protected IResource getCDLResource(IResource res) {
		IResource ret=null;
		
		try {
			Scenario scenario=
				ScenarioManager.load(res.getLocation().toString());
			
			java.io.File f=new java.io.File(scenario.getChoreographyDescriptionURL());
			
			if (f.isAbsolute()) {
				org.eclipse.core.runtime.Path urlpath=
					new org.eclipse.core.runtime.Path(scenario.getChoreographyDescriptionURL());
				
				ret = res.getWorkspace().getRoot().getFileForLocation(urlpath);
			} else {
				ret = res.getParent().findMember(
						scenario.getChoreographyDescriptionURL());
			}
			
			if (ret == null) {
				error(ERROR_LOCATING_CDL);
			}
		} catch(Exception e) {
			logger.info("Failed to load scenario '"+
					res.getLocation().toOSString()+"': "+e);
			
			error(ERROR_LOADING_SCENARIO);
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method is used to report an error.
	 * 
	 * @param mesg The error message
	 */
	public void error(String mesg) {
		
		logger.severe("Error occurred: "+mesg);
		
		MessageBox mbox=new MessageBox(m_targetPart.getSite().getShell(),
				SWT.ICON_ERROR|SWT.OK);
		
		if (mesg == null) {
			mesg = "Null pointer exception has occurred";
		}

		mbox.setMessage(mesg);
		mbox.open();
	}
	
	/**
	 * This method indicates that the selection has changed.
	 * 
	 * @param action The action
	 * @param selection The selection
	 */
	public void selectionChanged(IAction action,
            ISelection selection) {
		m_selection = selection;
	}

	/**
	 * This method sets the currently active workbench part.
	 * 
	 * @param action The action
	 * @param targetPart The active workbench part
	 */
	public void setActivePart(IAction action,
            IWorkbenchPart targetPart) {
		m_targetPart = targetPart;
	}
	
	private static Logger logger = Logger.getLogger("org.pi4soa.service.test.eclipse");

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

	private static final String SCENARIO_ERRORS_NO_TEST = "Scenario has errors, so cannot run test scenario";
	private static final String ERRORS_NO_TEST = "Choreography Description has errors, so cannot run test scenario";
	private static final String ERROR_LOCATING_CDL = "Choreography Description could not be located";
	private static final String ERROR_LOADING_CDL = "Choreography Description could not be loaded";
	private static final String ERROR_LOADING_SCENARIO = "Test Scenario could not be loaded";
}
