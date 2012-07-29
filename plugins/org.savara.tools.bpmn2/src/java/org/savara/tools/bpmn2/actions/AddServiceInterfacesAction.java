/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.bpmn2.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.savara.bpmn2.model.TDefinitions;
import org.savara.bpmn2.model.TInterface;
import org.savara.bpmn2.model.TParticipant;
import org.savara.bpmn2.model.util.BPMN2ModelUtil;
import org.savara.bpmn2.util.BPMN2ServiceUtil;
import org.savara.common.logging.MessageFormatter;
import org.savara.tools.bpmn2.osgi.Activator;


/**
 * This class implements the action to generate service interfaces
 * from a BPMN2 model.
 */
public class AddServiceInterfacesAction implements IObjectActionDelegate {

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

	public AddServiceInterfacesAction() {
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
				try {
					java.io.InputStream is=((IFile)res).getContents();
					
					TDefinitions defns=BPMN2ModelUtil.deserialize(is);
					
					java.util.Map<TParticipant,TInterface> intfs=
									BPMN2ServiceUtil.introspect(defns);
					
					if (intfs.size() == 0) {
						warn(MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
								"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00002"));
					} else {
						BPMN2ServiceUtil.merge(defns, intfs);
						
						java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
						
						BPMN2ModelUtil.serialize(defns, baos, null,
								AddServiceInterfacesAction.class.getClassLoader());
						
						java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(baos.toByteArray());
						
						((IFile) res).setContents(bais, true, false, null);
					}
					
				} catch(Exception e) {
					Activator.logError("Failed to add service interfaces", e);
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
