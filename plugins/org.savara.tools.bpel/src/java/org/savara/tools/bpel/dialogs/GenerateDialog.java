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
package org.savara.tools.bpel.dialogs;

import org.apache.commons.logging.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.savara.contract.model.Contract;
import org.savara.protocol.contract.generator.ContractGenerator;
import org.savara.protocol.contract.generator.ContractGeneratorFactory;
import org.savara.protocol.util.ProtocolServices;
import org.savara.tools.bpel.generator.*;
import org.scribble.common.logging.CachedJournal;
import org.scribble.common.logging.Journal;
import org.scribble.protocol.model.*;

/**
 * This class provides the dialog for generating BPEL
 * artefacts.
 */
public class GenerateDialog extends org.eclipse.jface.dialogs.Dialog {

	private static Log logger = LogFactory.getLog(GenerateDialog.class);

	private IFile m_file=null;
	private ProtocolModel m_protocolModel=null;
	private java.util.List<Button> m_roleButtons=new java.util.Vector<Button>();
	private java.util.List<Text> m_projectNames=new java.util.Vector<Text>();

	/**
	 * This is the constructor for the generate dialog.
	 * 
	 * @param shell The shell
	 */
	public GenerateDialog(Shell shell, IFile file) {
		super(shell);
		
		m_file = file;
		
		initialize(m_file);
	}
	
	/**
	 * This method initializes the conversation model associated
	 * with the supplied file resource.
	 * 
	 * @param res The file
	 */
	protected void initialize(IFile res) {
		Journal journal=new CachedJournal();
		
		try {
			m_protocolModel = ProtocolServices.getParserManager().parse(res.getFileExtension(),
								res.getContents(), journal, null);
			
			if (m_protocolModel == null) {
				logger.error("Unable to load model used to generate the BPEL process");
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Failed to generate BPEL process", e);
		}
	}
	
	/**
	 * This method creates the dialog details.
	 * 
	 * @param parent The parent control
	 * @return The control containing the dialog components
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite composite=(Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);		
	
		GridData gd=null;

		Group group=new Group(composite, SWT.H_SCROLL|SWT.V_SCROLL);
		
		gd=new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.widthHint = 530;
		gd.grabExcessHorizontalSpace = true;
		group.setLayoutData(gd);
		
		layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);

		// Labels
		Label label=new Label(group, SWT.NONE);
		label.setText("Service Role");
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint = 150;
		label.setLayoutData(gd);

		label = new Label(group, SWT.NONE);
		label.setText("Project Name");
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.widthHint = 300;
		label.setLayoutData(gd);

		if (m_protocolModel != null) {
			java.util.List<Role> roles=m_protocolModel.getProtocol().getRoles();

			ContractGenerator cg=ContractGeneratorFactory.getContractGenerator();
			
			for (int i=0; i < roles.size(); i++) {
				Role role=roles.get(i);
				boolean f_server=true;
				
				if (cg != null) {
					Contract c=cg.generate(m_protocolModel.getProtocol(),
							null, role, new CachedJournal());
					
					if (c != null && c.getInterfaces().size() == 0) {
						f_server = false;
						if (logger.isDebugEnabled()) {
							logger.debug("Role "+role+" is not a service");
						}
					}
				}

				if (f_server) {
					Button button=new Button(group, SWT.CHECK);
					button.setText(roles.get(i).getName());
					button.setSelection(true);
					
					gd = new GridData();
					gd.horizontalSpan = 2;
					gd.widthHint = 195;
					button.setLayoutData(gd);
					
					m_roleButtons.add(button);
					
					button.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							widgetSelected(e);
						}
	
						public void widgetSelected(SelectionEvent e) {
							checkStatus();
						}
					});
					
					Text projectName=new Text(group, SWT.NONE);
					
					String prjName=roles.get(i).getName();
					
					if (m_protocolModel.getProtocol() != null) {
						prjName = m_protocolModel.getProtocol().getName()+"-"+prjName;
					}
					
					projectName.setText(prjName);
					
					gd = new GridData();
					gd.horizontalSpan = 2;
					gd.widthHint = 300;
					projectName.setLayoutData(gd);
					
					m_projectNames.add(projectName);
	
					projectName.addModifyListener(new ModifyListener() {					
						public void modifyText(ModifyEvent e) {
							checkStatus();
						}
					});
				}
			}
		}

		Button button=new Button(group, SWT.NONE);
		button.setText("Check All");
		
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		button.setLayoutData(gd);
		
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);	
			}
			
			public void widgetSelected(SelectionEvent e) {
				for (int i=0; i < m_roleButtons.size(); i++) {
					m_roleButtons.get(i).setSelection(true);
				}
				checkStatus();
			}			
		});

		button=new Button(group, SWT.NONE);
		button.setText("Clear All");
		
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		button.setLayoutData(gd);
		
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);	
			}
			
			public void widgetSelected(SelectionEvent e) {
				for (int i=0; i < m_roleButtons.size(); i++) {
					m_roleButtons.get(i).setSelection(false);
				}
				checkStatus();
			}			
		});
		
		return(composite);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control ret=super.createButtonBar(parent);
		
		checkStatus();
		
		return(ret);
	}
	
	protected void checkStatus() {
		int selected=0;
		boolean f_error=false;
		
		for (int i=0; i < m_roleButtons.size(); i++) {
			if (m_roleButtons.get(i).getSelection()) {
				selected++;
				
				m_projectNames.get(i).setEnabled(true);
				
				// Check project name
				String projectName=m_projectNames.get(i).getText();
				
				if (isProjectNameValid(projectName) == false) {
					f_error = true;
					
					m_projectNames.get(i).setBackground(
							Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				} else {
					m_projectNames.get(i).setBackground(
							Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				}
			} else {
				m_projectNames.get(i).setEnabled(false);
				m_projectNames.get(i).setBackground(
						Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			}
		}
		
		if (f_error || selected == 0) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}
	
	protected boolean isProjectNameValid(String name) {
		boolean ret=true;
		
		if (name == null || name.trim().length() == 0) {
			ret = false;
		} else if (m_file.getWorkspace().getRoot().getProject(name).exists()) {
			ret = false;
		} else {
			for (int i=0; ret && i < name.length(); i++) {
				if (i == 0) {
					ret = Character.isJavaIdentifierStart(name.charAt(i));
				} else if ("-.".indexOf(name.charAt(i)) != -1) {
					ret = true;
				} else {
					ret = Character.isJavaIdentifierPart(name.charAt(i));
				}
			}
		}
		
		return(ret);
	}
	
	/**
	 * The ok button has been pressed.
	 */
	public void okPressed() {
		
		try {
			Generator generator=new Generator(m_file);
			
			java.util.List<Role> roles=m_protocolModel.getProtocol().getRoles();
			
			for (int i=0; i < roles.size(); i++) {
				
				if (m_roleButtons.get(i).getSelection()) {
					generator.generateRole(roles.get(i),
							m_projectNames.get(i).getText(), m_file);
				}
			}
			
			super.okPressed();
		} catch(Exception e) {
			error("Failed to generate BPEL artefacts", e);
		}
	}
	
	/**
	 * This method is used to report an error.
	 * 
	 * @param mesg The error message
	 * @param ex The exception 
	 */
	public void error(String mesg, Exception ex) {
		
		org.savara.tools.bpel.osgi.Activator.logError(mesg, ex);
		
		MessageBox mbox=new MessageBox(getShell(),
				SWT.ICON_ERROR|SWT.OK);
		mbox.setMessage(mesg);
		mbox.open();
		
		logger.error(mesg, ex);
	}
}
