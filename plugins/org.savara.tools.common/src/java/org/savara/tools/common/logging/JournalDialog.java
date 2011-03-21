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

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
//import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.scribble.common.logging.Journal;

public class JournalDialog extends TitleAreaDialog implements Journal {

	private List m_list=null;
	
	public JournalDialog(Shell shell) {
		super(shell);
		setTitleAreaColor(new RGB(250, 250, 250));		
	}

	protected Control createDialogArea(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		setTitle("SAVARA Journal");
		setMessage("Log of error and warning messages");
		
		//new Label(contents, SWT.LEFT).setText("Hello World in Content Area");
		
		m_list=new org.eclipse.swt.widgets.List(contents, SWT.FILL);
		m_list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Dialog.applyDialogFont(parent);
		Point defaultMargins = LayoutConstants.getMargins();
		GridLayoutFactory.fillDefaults().numColumns(1).margins(defaultMargins.x, defaultMargins.y).generateLayout(contents);
		return contents;
	}
	
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Title Area Shell");
		shell.pack();
		JournalDialog taDialog = new JournalDialog(shell);
		//taDialog.setTitleAreaColor(new RGB(0, 100, 200));
		taDialog.error("Test message", null);
		
		taDialog.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void error(String issue, Map<String, Object> props) {
		
		open();

		if (m_list != null) {
			m_list.add("ERROR: "+issue);
			m_list.update();
		}
	}

	public void warning(String issue, Map<String, Object> props) {
		m_list.add("WARNING: "+issue);
	}

	public void info(String issue, Map<String, Object> props) {
	}
}
