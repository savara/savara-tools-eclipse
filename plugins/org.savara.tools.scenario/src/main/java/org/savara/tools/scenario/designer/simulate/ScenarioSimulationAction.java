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

	private static Logger logger = Logger.getLogger(ScenarioSimulationAction.class.getName());

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

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
			
				ssd.open();
			}
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
	
}
