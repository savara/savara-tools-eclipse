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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class represents the wizard page for selecting
 * a folder.
 */
class FolderSelectionPage extends org.eclipse.jface.wizard.WizardPage {

	/**
	 * Constructor for the folder selection page.
	 * 
	 * @param pageName The page name
	 * @param description The description
	 */
	public FolderSelectionPage(String pageName,
	        String description) {
		super(pageName);
		setTitle(pageName);
		setDescription(description);
		setPageComplete(false);
	}

	/**
	 * Create the control for the selection page.
	 * 
	 * @param parent The parent component
	 */
	public void createControl(Composite parent) {
	    
	    // Identify the shell
		m_shell = parent.getShell();
		
		// Create the composite component
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL));
		
		// Create the text field for entering the
		// path to the folder
		m_text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		m_text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
				    
		// Ensure there is no error before setting up the
		// text listener
		if (getErrorMessage() == null) {
			m_text.addModifyListener(new ModifyListener() {
			    
				public void modifyText(ModifyEvent e) {
					String errMesg=null;
					
					try {
					    // Setup file associated with entered
					    // name
					    java.io.File file=
					        new java.io.File(m_text.getText());
						
					  	// Check that the folder exists
						if (file.exists() == false) {
							errMesg = "The folder does not exist";
						} else if (file.isDirectory() == false) {
							errMesg = "A folder should be selected";
						}
					} catch(Exception e2) {
						// Ignore
					}
					
					// Check if error has been found
					if (errMesg != null) {
						setErrorMessage(errMesg);
						setPageComplete(false);
					} else {
						setErrorMessage(null);
						setPageComplete(true);
					}
				}
			});
		}		

		// Create a 'browse' button
		m_button=new Button(composite, 0);
		
		m_button.setText("Browse");
		m_button.setToolTipText("Browse file system to locate destination folder");
		
		m_button.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(SelectionEvent evt) {
				DirectoryDialog dialog=new DirectoryDialog(m_shell);

				String filename=dialog.open();
				
				if (filename != null) {
					
					filename = preProcessFilename(filename);
					m_text.setText(filename);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent evt) {					
			}
		});
		
		if (getErrorMessage() != null) {
			m_button.setEnabled(false);
			m_text.setEnabled(false);
			m_text.setEditable(false);
		}
		
		setControl(composite);
	}
		
	/**
	 * This method preprocesses the filename before it is
	 * used.
	 * 
	 * @param filename The original filename
	 * @return The pre-processed filename
	 */
	protected String preProcessFilename(String filename) {
		return(filename);
	}
	
	/**
	 * This method returns the folder path that has been
	 * entered by the user.
	 * 
	 * @return The folder name
	 */
	public String getFolderName() {
	    if (m_text == null) {
	        return(null);
	    }
	    return(m_text.getText());
	}
	
	private Text m_text=null;
	private Shell m_shell=null;
	private Button m_button=null;
}
