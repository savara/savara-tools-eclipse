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

import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.pi4soa.common.resource.eclipse.ResourceUtil;
import org.savara.tools.monitor.preferences.MonitorPreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.DebugPlugin;

/**
 * This class invokes the monitor action on the selected
 * choreography file.
 * 
 */
public class MonitorAction implements IObjectActionDelegate {

	private static final String SET_LIBRARY_PATHS = "Need to set library paths. See Window->Preferences->Savara->Monitor.";
	/**
	 * This method implements the action's run method.
	 * 
	 * @param action The action
	 */
	public void run(IAction action) {
		
		if (m_selection instanceof StructuredSelection) {
			StructuredSelection sel=(StructuredSelection)m_selection;
			
			IResource res=(IResource)sel.getFirstElement();
			
            // Make sure there are no markers associated
            // with the resource
			if (ResourceUtil.hasErrors(res) == false) {
			
				if (MonitorPreferences.isLibraryPathsDefined()) {
					launch(res.getProject().getName(),
							res.getProjectRelativePath().toString());
				} else {
					error(SET_LIBRARY_PATHS);
				}
							
			} else {
				error(ERRORS_NO_TEST);
			}
		}
	}
	
	/**
	 * This method invokes the launch action.
	 * 
	 * @param project The project
	 * @param relativePath The relative path within the project
	 */
	protected void launch(String project, String relativePath) {
		
		MonitorLauncher launcher=new MonitorLauncher();
				
		try {
			ILaunchManager manager =
				DebugPlugin.getDefault().getLaunchManager();
			
			ILaunchConfigurationType type =
				manager.getLaunchConfigurationType(
			      	MonitorLaunchConfigurationConstants.LAUNCH_CONFIG_TYPE);
			ILaunchConfiguration[] configurations =
			      manager.getLaunchConfigurations(type);
			
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(CHOREOGRAPHY_MONITOR)) {
					configuration.delete();
					break;
				}
			}
			
			ILaunchConfigurationWorkingCopy workingCopy =
			      type.newInstance(null, CHOREOGRAPHY_MONITOR);

			workingCopy.setAttribute(MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					project);
			workingCopy.setAttribute(MonitorLaunchConfigurationConstants.ATTR_CHOREOGRAPHY_DESCRIPTION,
					relativePath);
			
			ILaunchConfiguration configuration=workingCopy.doSave();
		

			Launch launch=new Launch(configuration, LAUNCH_MODE, null);
			
			launcher.launch(configuration, LAUNCH_MODE, launch, null);

		} catch(Exception e) {
			logger.severe("Failed to launch monitor: "+e);
			
			e.printStackTrace();
		}
	}
	
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
	
	private static Logger logger = Logger.getLogger("org.savara.tools.monitor.eclipse");

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

	private static final String ERRORS_NO_TEST = "Choreography Description has errors, so cannot run monitor";

	private static final String LAUNCH_MODE = "run";
	private static final String CHOREOGRAPHY_MONITOR = "Choreography Monitor";
}
