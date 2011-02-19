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
 * Feb 1, 2007 : Initial version created by gary
 */
package org.savara.tools.bpmn.eclipse;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.cdl.CDLDefinitions;
import org.pi4soa.cdl.ParticipantType;
import org.pi4soa.common.resource.FileSystemArtifactManager;
import org.pi4soa.common.resource.eclipse.ResourceUtil;
import org.pi4soa.service.ServiceDefinitions;
import org.pi4soa.service.behavior.ServiceDescription;
import org.pi4soa.service.behavior.projection.BehaviorProjection;
import org.savara.tools.bpmn.generation.BPMNGenerator;
import org.savara.tools.bpmn.generation.DefaultBPMNConfiguration;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.resources.*;


/**
 * This class implements the Eclipse ExportWizrd for exporting
 * a CDL object model to a BPMN representation.
 */
public class ExportBPMNWizard extends Wizard implements IExportWizard {

	/**
     * This method is invoked when the 'finish' button is
     * pressed on the export wizard.
     */
	public boolean performFinish() {
		boolean ret=true;
		
		// Export CDL's service endpoint projections to folder
		if (m_cdlFile != null) {
			
			// Load the CDL object model
			try {
				org.pi4soa.cdl.Package cdlpack=
					org.pi4soa.cdl.eclipse.Activator.getPackage(m_cdlFile);
			
				BPMNGenerator generator=new BPMNGenerator();
				
				generator.generate(cdlpack, null,
						m_selectionPage.getFolderName(),
						new DefaultBPMNConfiguration());

			} catch(Exception e) {
			    
			    // Record error message on wizard page
				m_selectionPage.setErrorMessage("Failed to export Choreography to BPMN: "+e);
				
				logger.log(Level.SEVERE,
						"Failed to export Choreography to BPMN", e);
				ret = false;
			}
		}
		
		return(ret);
	}

	/**
	 * This method is used to initialize the export wizard.
	 * 
	 * @param workbench The workbench
	 * @param selection The selected item(s)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	    String errMesg=null;
	    
	    // Create the selection page
		m_selectionPage = new FolderSelectionPage(PAGE_NAME,
		        "Select a folder as the destination for "+
		        "the exported BPMN");
		
	    // Check that a single CDL file has been selected
		if (selection != null) {
		    
		    // Check element is a file
		    if (selection.getFirstElement() instanceof IFile) {
			
		        m_cdlFile = (IFile)selection.getFirstElement();
			
		        // Check the file extension is valid
		        if (m_cdlFile.getFileExtension().equals(
		        		CDLDefinitions.CDL_FILE_EXTENSION)) {
		            
		            // Make sure there are no markers associated
		            // with the resource
		        	if (ResourceUtil.hasErrors(m_cdlFile)) {
					    errMesg = RESOURCE_ERROR;		        		
		        	}
				} else {
				    errMesg = RESOURCE_NOT_CDL_ERROR;
				}
			} else {
			    errMesg = RESOURCE_NOT_FILE_ERROR;
		    }
		} else {
		    errMesg = RESOURCE_NOT_SELECTED_ERROR;
		}
		
		// Check if an error should be reported
		if (errMesg != null) {
		    m_selectionPage.setErrorMessage(errMesg);
		}
	}

	/**
	 * This method configures the pages associated with
	 * the CDL export wizard.
	 */
	public void addPages() {
		addPage(m_selectionPage);
	}
	
    private static Logger logger = Logger.getLogger("org.savara.tools.bpmn.eclipse");
	private IFile m_cdlFile=null;
	private FolderSelectionPage m_selectionPage=null;
	
    private static final String BEHAVIOR_EXTENSION = ServiceDefinitions.SERVICE_ENDPOINT_FILE_EXTENSION;
	
	private static String PAGE_NAME="Export BPMN";
	private static String RESOURCE_NOT_SELECTED_ERROR="A resource has not been selected";
	private static String RESOURCE_NOT_FILE_ERROR="The selected resource is not a file";
	private static String RESOURCE_NOT_CDL_ERROR="The selected file is not a valid CDL object model";
	private static String RESOURCE_ERROR="Invalid CDL cannot be exported as BPMN";
}