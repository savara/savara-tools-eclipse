/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.common.generation.ui;

import org.apache.commons.logging.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.savara.tools.common.ArtifactType;


/**
 * This class implements the action to generate artifacts
 * from a protocol model.
 */
public class GenerateServiceContractAction implements IObjectActionDelegate {

	private static Log logger = LogFactory.getLog(GenerateServiceContractAction.class);
	
	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

	public GenerateServiceContractAction() {
	}

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
				
				GenerateDialog dialog=
					new GenerateDialog(m_targetPart.getSite().getShell(),
								(IFile)res, ArtifactType.ServiceContract);
				
				try {
					dialog.open();
				} catch(Throwable e) {
					logger.error("Failed to open generate dialog", e);
				}
			}
		}
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
	
	/**
	 * This method is used to report a warning.
	 * 
	 * @param mesg The warning message
	 */
	public void warn(String mesg) {
		
		MessageBox mbox=new MessageBox(m_targetPart.getSite().getShell(),
				SWT.ICON_WARNING|SWT.OK);
		mbox.setMessage(mesg);
		mbox.open();
	}
}
