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
package org.savara.tools.scenario.designer.simulate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ScenarioSimulationDialog extends Dialog {

	private Button m_sameModelButton=null;
	private Button m_sameSimulatorButton=null;
	private java.util.List<Text> m_models=new java.util.Vector<Text>();
	private java.util.List<Button> m_browseButtons=new java.util.Vector<Button>();
	private java.util.List<Combo> m_modelRoles=new java.util.Vector<Combo>();
	private java.util.List<Combo> m_simulatorTypes=new java.util.Vector<Combo>();
	
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);

		ScenarioSimulationDialog ssd=new ScenarioSimulationDialog(shell);
		
		ssd.open();
	}

	public ScenarioSimulationDialog(Shell parent, int style) {
        super (parent, style);
    }
    
    public ScenarioSimulationDialog(Shell parent) {
    	this (parent, 0);
    }
    
    public Object open() {
    	Shell parent = getParent();
    	
    	final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	dialog.setText("Scenario Simulation");
    	
    	FormLayout form = new FormLayout();
    	form.marginWidth = form.marginHeight = 8;
    	dialog.setLayout(form);
    	
    	m_sameModelButton=new Button(dialog, SWT.CHECK);
    	m_sameModelButton.setText("Use same model for all roles");
    	m_sameModelButton.setSelection(true);
    	
    	FormData sameModelButtonData = new FormData();
    	sameModelButtonData.top = new FormAttachment(0);
    	m_sameModelButton.setLayoutData(sameModelButtonData);
    	
    	m_sameSimulatorButton=new Button(dialog, SWT.CHECK);
    	m_sameSimulatorButton.setText("Use same simulator for all roles");
    	m_sameSimulatorButton.setSelection(true);
    	
    	FormData sameSimulatorButtonData = new FormData();
    	sameSimulatorButtonData.top = new FormAttachment(0);
    	sameSimulatorButtonData.left = new FormAttachment(m_sameModelButton, 10);
    	m_sameSimulatorButton.setLayoutData(sameSimulatorButtonData);
    	
    	m_sameModelButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateModel();
				updateSimulatorTypes();
			}  		
    	});
    	
    	m_sameSimulatorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateSimulatorTypes();
			}  		
    	});
    	
    	// Construct main area
    	Composite mainArea=new Composite(dialog, SWT.NONE); //SWT.V_SCROLL);

        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        rowLayout.pack = false;
        rowLayout.justify = true;
        rowLayout.type = SWT.VERTICAL;
        rowLayout.marginLeft = 5;
        rowLayout.marginTop = 5;
        rowLayout.marginRight = 5;
        rowLayout.marginBottom = 5;
        rowLayout.spacing = 0;
        mainArea.setLayout(rowLayout);
    	
    	FormData mainAreaData = new FormData();
    	mainAreaData.top = new FormAttachment(m_sameModelButton, 10);
    	mainAreaData.left = new FormAttachment(0);
    	//mainAreaData.right = new FormAttachment(10);
    	mainAreaData.width = 450;
    	//mainAreaData.height = 300;
    	mainArea.setLayoutData(mainAreaData);
    	
    	java.util.List<String> roles=new java.util.Vector<String>();
    	roles.add("Buyer");
    	roles.add("Seller");
    	roles.add("CreditAgency");
    	roles.add("Other");
    	
    	for (int i=0; i < roles.size(); i++) {
    		Group rolePanel=new Group(mainArea, SWT.NONE);
    		rolePanel.setText(roles.get(i));
    		
        	FormLayout groupform = new FormLayout();
        	groupform.marginWidth = groupform.marginHeight = 8;
        	rolePanel.setLayout(groupform);
        	
    		rolePanel.setLayoutData(new RowData(440, 100));
    		
    		Label modelLabel=new Label(rolePanel, SWT.NONE);
    		modelLabel.setText("Model:");
    		
        	FormData modelLabelData = new FormData();
        	modelLabelData.top = new FormAttachment(5);
        	modelLabel.setLayoutData(modelLabelData);
        	
    		final Text model=new Text(rolePanel, SWT.NONE);
    		m_models.add(model);
    		
        	FormData modelData = new FormData();
        	modelData.left = new FormAttachment(modelLabel, 5);
        	//modelData.right = new FormAttachment(0);
        	modelData.width = 340;
        	model.setLayoutData(modelData);
        	
        	model.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					
					if (model == m_models.get(0) &&
							m_sameModelButton.getSelection()) {
						for (int i=1; i < m_models.size(); i++) {
							m_models.get(i).setText(model.getText());
						}
					}
				}        		
        	});
        	
    		final Button browseButton=new Button(rolePanel, SWT.NONE);
    		m_browseButtons.add(browseButton);
    		
    		browseButton.setText("...");
    		
        	FormData browseButtonData = new FormData();
        	browseButtonData.left = new FormAttachment(model, 5);
        	browseButtonData.top = new FormAttachment(0);
        	browseButtonData.height = 20;
        	browseButtonData.width = 25;
        	browseButton.setLayoutData(browseButtonData);
        	
        	browseButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}
				public void widgetSelected(SelectionEvent arg0) {
					FileDialog fd=new FileDialog(dialog);
					
					String path=fd.open();
					
					if (path != null) {
						model.setText(path);
					}
				}
        	});   	
        	
    		Label modelRoleLabel=new Label(rolePanel, SWT.NONE);
    		modelRoleLabel.setText("Model Role:");
    		
        	FormData modelRoleLabelData = new FormData();
        	modelRoleLabelData.top = new FormAttachment(modelLabel, 12);
        	modelRoleLabel.setLayoutData(modelRoleLabelData);
        	
        	final Combo modelRole=new Combo(rolePanel, SWT.NONE);
        	modelRole.add("Fred");
        	modelRole.add("Joe");
    		m_modelRoles.add(modelRole);
    		
        	FormData modelRoleData = new FormData();
        	modelRoleData.left = new FormAttachment(modelRoleLabel, 5);
        	modelRoleData.right = new FormAttachment(100);
        	modelRoleData.top = new FormAttachment(model, 5);
        	modelRole.setLayoutData(modelRoleData);
        	
        	modelRole.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}
				public void widgetSelected(SelectionEvent arg0) {
				}
        	});
        	
    		Label simulatorLabel=new Label(rolePanel, SWT.NONE);
    		simulatorLabel.setText("Simulator:");
    		
        	FormData simulatorLabelData = new FormData();
        	simulatorLabelData.top = new FormAttachment(modelRoleLabel, 12);
        	simulatorLabel.setLayoutData(simulatorLabelData);
        	
        	final Combo simulatorType=new Combo(rolePanel, SWT.NONE);
    		simulatorType.add("Don't Simulate");
    		simulatorType.add("WS-CDL Simulator");
    		m_simulatorTypes.add(simulatorType);
    		
        	FormData simulatorTypeData = new FormData();
        	simulatorTypeData.left = new FormAttachment(simulatorLabel, 5);
        	simulatorTypeData.right = new FormAttachment(100);
        	simulatorTypeData.top = new FormAttachment(modelRole, 5);
        	simulatorType.setLayoutData(simulatorTypeData);
        	
        	simulatorType.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}
				public void widgetSelected(SelectionEvent arg0) {
					if (simulatorType == m_simulatorTypes.get(0) &&
							m_sameSimulatorButton.getSelection()) {
						for (int i=1; i < m_simulatorTypes.size(); i++) {
							m_simulatorTypes.get(i).select(simulatorType.getSelectionIndex());
						}
					}
				}
        	});
        	
        	initializeSimulatorTypes(simulatorType, model);
    	}
    	
    	Button okButton = new Button (dialog, SWT.PUSH);
    	okButton.setText ("&OK");
    	Button cancelButton = new Button (dialog, SWT.PUSH);
    	cancelButton.setText ("&Cancel");
    	
    	FormData okData = new FormData();
    	okData.top = new FormAttachment(mainArea, 8);
    	okData.left = new FormAttachment(0, 150);
    	okData.width = 80;
    	okButton.setLayoutData(okData);
    	
    	FormData cancelData = new FormData();
    	cancelData.left = new FormAttachment(okButton, 8);
    	cancelData.top = new FormAttachment(mainArea, 8);
    	cancelData.width = 80;
    	cancelButton.setLayoutData(cancelData);
    	
    	dialog.setDefaultButton(okButton);
    	dialog.pack();
    	dialog.open();

    	okButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			public void widgetSelected(SelectionEvent arg0) {
				//m_parameter.setType(m_type.getText());
				//m_parameter.setValue(m_value.getText());
				//m_ok = true;
				dialog.dispose();
			}	    		
    	});
    	
    	cancelButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
    	});
    	
    	updateModel();
    	updateSimulatorTypes();
    	
    	dialog.open();
    	Display display = parent.getDisplay();
    	while (!dialog.isDisposed()) {
    		if (!display.readAndDispatch()) display.sleep();
    	}
    	
    	return(null);
    }

    protected void initializeSimulatorTypes(Combo simulatorTypes, Text modelPath) {
    	simulatorTypes.removeAll();
    	
    	simulatorTypes.add("No Simulator");
    	simulatorTypes.add("WS-CDL Simulator");
    	
    	simulatorTypes.select(0);
    }
    
    protected void updateSimulatorTypes() {
		for (int i=1; i < m_simulatorTypes.size(); i++) {
			if (m_sameSimulatorButton.getSelection()) {
				m_simulatorTypes.get(i).setEnabled(false);
				m_simulatorTypes.get(i).select(m_simulatorTypes.get(0).getSelectionIndex());
			} else {
				m_simulatorTypes.get(i).setEnabled(true);
				
				// TODO: Need to reset the list of simulator types based
				// on the model
				initializeSimulatorTypes(m_simulatorTypes.get(i), m_models.get(i));
			}
		}
    }
    
    protected void updateModel() {
		for (int i=1; i < m_models.size(); i++) {
			if (m_sameModelButton.getSelection()) {
				m_models.get(i).setText(m_models.get(0).getText());
				m_models.get(i).setEnabled(false);
				m_models.get(i).setEditable(false);
				
				m_sameSimulatorButton.setSelection(true);
				m_sameSimulatorButton.setEnabled(true);
			} else {
				m_models.get(i).setEnabled(true);
				m_models.get(i).setEditable(true);
				m_sameSimulatorButton.setSelection(false);
				m_sameSimulatorButton.setEnabled(false);
			}
		}
    }
}
