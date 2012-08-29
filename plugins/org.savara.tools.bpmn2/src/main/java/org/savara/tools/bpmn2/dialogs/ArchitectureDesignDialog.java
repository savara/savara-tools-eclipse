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
package org.savara.tools.bpmn2.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to obtain information for architecture/design generation.
 *
 */
public class ArchitectureDesignDialog extends TitleAreaDialog {

	private Text _modelNameField=null;
	private String _modelName=null;
	private Text _namespaceField=null;
	private String _namespace=null;
	
	private static final String DEFAULT_NAMESPACE_PREFIX="http://www.savara.org";

	public ArchitectureDesignDialog(Shell shell) {
		super(shell);
	}

	@Override
	public void create() {
		super.create();
		
		setTitle("Architecture and Design Models");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		Label modelNameLabel = new Label(parent, SWT.NONE);
		modelNameLabel.setText("Model Name");
		
		_modelNameField = new Text(parent, SWT.BORDER);
		_modelNameField.setLayoutData(gridData);
		
		_modelNameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (_namespaceField.getText().startsWith(DEFAULT_NAMESPACE_PREFIX)) {
					_namespaceField.setText(DEFAULT_NAMESPACE_PREFIX+"/"+_modelNameField.getText());
				}
			}
		});
		
		Label namespaceLabel = new Label(parent, SWT.NONE);
		namespaceLabel.setText("Namespace");
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		_namespaceField = new Text(parent, SWT.BORDER);
		_namespaceField.setLayoutData(gridData);
		_namespaceField.setText(DEFAULT_NAMESPACE_PREFIX);
		
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;
		
		parent.setLayoutData(gridData);
		
		createOkButton(parent, OK, "Ok", true);
		// Add a SelectionListener
		
		// Create Cancel button
		Button cancelButton = 
		    createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent e) {
		    setReturnCode(CANCEL);
		    close();
		  }
		});
	}

	protected Button createOkButton(Composite parent, int id, 
					String label, boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		boolean valid = true;
		if (_modelNameField.getText().length() == 0) {
			setErrorMessage("Model name must be specified");
			valid = false;
		} else if (_namespaceField.getText().length() == 0) {
			setErrorMessage("Namespace must be specified");
			valid = false;
		}
		return valid;
	}
  
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		// Cache the results
	    _modelName = _modelNameField.getText();
	    _namespace = _namespaceField.getText();
	    super.okPressed();
	}

	public String getModelName() {
		return _modelName;
	}

	public String getNamespace() {
		return _namespace;
	}
}