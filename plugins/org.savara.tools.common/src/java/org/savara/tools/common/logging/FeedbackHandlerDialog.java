/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.common.logging;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.savara.common.logging.DefaultFeedbackHandler;

public class FeedbackHandlerDialog extends DefaultFeedbackHandler {
	
	private Shell m_shell=null;

	public FeedbackHandlerDialog(Shell shell) {
		m_shell = shell;
	}
	
	/**
	 * This method will display an appropriate dialog to the user,
	 * if a warning or error has occurred.
	 */
	public void show() {
		
		if (hasErrors()) {
			MessageDialog.openError(m_shell, "Errors", getMessage());
		} else if (hasWarnings()) {
			MessageDialog.openWarning(m_shell, "Warnings", getMessage());
		}
	}	
	
	/**
	 * This method returns the formatted message to be displayed.
	 * 
	 * @return The message
	 */
	protected String getMessage() {
		StringBuffer buf=new StringBuffer();
		for (IssueDetails issue : getIssues()) {
			buf.append(issue.getIssueType()+": "+issue.getMessage()+"\r\n");
		}
		return(buf.toString());
	}
}
