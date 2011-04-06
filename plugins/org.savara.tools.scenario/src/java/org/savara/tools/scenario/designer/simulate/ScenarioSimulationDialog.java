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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.savara.scenario.model.Role;
import org.savara.scenario.model.Scenario;
import org.savara.scenario.simulation.DefaultSimulationContext;
import org.savara.scenario.simulation.RoleSimulator;
import org.savara.scenario.simulation.RoleSimulatorFactory;
import org.savara.scenario.simulation.ScenarioSimulator;
import org.savara.scenario.simulation.ScenarioSimulatorFactory;
import org.savara.scenario.simulation.SimulationContext;
import org.savara.scenario.simulation.SimulationHandler;
import org.savara.scenario.simulation.SimulationModel;
import org.savara.scenario.util.ScenarioModelUtil;
//import org.savara.scenario.simulation.TestRoleSimulator;
//import org.savara.scenario.simulator.cdm.CDMRoleSimulator;
//import org.savara.scenario.simulator.cdm.TestSimulationHandler;
//import java.io.File;

public class ScenarioSimulationDialog extends Dialog {

	private static final String DON_T_SIMULATE = "Don't Simulate";
	
	private Button m_sameModelButton=null;
	private Button m_sameSimulatorButton=null;
	private java.util.List<Text> m_models=new java.util.Vector<Text>();
	private java.util.List<Button> m_browseButtons=new java.util.Vector<Button>();
	private java.util.List<Combo> m_modelRoles=new java.util.Vector<Combo>();
	private java.util.List<Combo> m_simulatorTypes=new java.util.Vector<Combo>();
	private java.io.File m_scenarioFile=null;
	private java.util.List<SimulationModel> m_simulationModels=new java.util.Vector<SimulationModel>();
	private Scenario m_scenario=null;
	private SimulationHandler m_handler=null;
	private boolean m_simulate=false;
	private java.util.Map<Role,RoleSimulator> m_roleSimulators=new java.util.HashMap<Role,RoleSimulator>();
	private java.util.Map<Role,SimulationContext> m_contexts=new java.util.HashMap<Role,SimulationContext>();
		
	private static final Logger logger=Logger.getLogger(ScenarioSimulationDialog.class.getName());
	
	/*
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);
		
		// Register dummy role simulators
		RoleSimulator rs1=new CDMRoleSimulator();
		RoleSimulator rs2=new TestRoleSimulator("Test Simulator 2", true);
		
		RoleSimulatorFactory.register(rs1);
		RoleSimulatorFactory.register(rs2);

		ScenarioSimulationDialog ssd=new ScenarioSimulationDialog(shell);
		
		TestSimulationHandler handler=new TestSimulationHandler();
		
		try {
			ssd.initializeScenario(new java.io.File(args[0]));
			
			ssd.setSimulationHandler(handler);
		
			ssd.open();
			
			System.out.println(handler);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	*/

	public ScenarioSimulationDialog(Shell parent, int style) {
        super (parent, style);
    }
    
    public ScenarioSimulationDialog(Shell parent) {
    	this (parent, 0);
    }
    
    public void initializeScenario(java.io.File scenarioFile) throws IOException {
    	m_scenarioFile = scenarioFile;
    	
    	java.io.InputStream is=new java.io.FileInputStream(m_scenarioFile);
    	
    	m_scenario = ScenarioModelUtil.deserialize(is);
    	
    	is.close();
    }
    
    public void setSimulationHandler(SimulationHandler handler) {
    	m_handler = handler;
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
				updateSameModel();
				updateSameSimulatorTypes();
			}  		
    	});
    	
    	m_sameSimulatorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateSameSimulatorTypes();
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
    	
    	for (int i=0; i < m_scenario.getRole().size(); i++) {
    		Group rolePanel=new Group(mainArea, SWT.NONE);
    		rolePanel.setText(m_scenario.getRole().get(i).getName());
    		
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
					
					updateModel(m_models.indexOf(model));
					
					if (model == m_models.get(0) &&
							m_sameModelButton.getSelection()) {
						for (int i=1; i < m_models.size(); i++) {
							m_models.get(i).setText(model.getText());
							updateModel(i);
						}
					}
				}        		
        	});
        	
        	try {
        		m_simulationModels.add(new SimulationModel(null, null));
        	} catch(Exception e) {
        		logger.log(Level.SEVERE, "Failed to initialize simulation model", e);
        	}
        	
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
					
					fd.setFileName(m_scenarioFile.getAbsolutePath());
					
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
        	
        	final Combo modelRole=new Combo(rolePanel, SWT.READ_ONLY);
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
        	
        	final Combo simulatorType=new Combo(rolePanel, SWT.READ_ONLY);
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
        	
        	updateModel(i);
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
				initSimulation();
				
				m_simulate = true;
				
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
    	
    	updateSameModel();
    	updateSameSimulatorTypes();
    	
    	dialog.open();
    	Display display = parent.getDisplay();
    	while (!dialog.isDisposed()) {
    		if (!display.readAndDispatch()) display.sleep();
    	}
    	
    	if (m_simulate) {
			// Run the simulation
			runSimulation();
    	}
    	
    	return(null);
    }

    protected void updateSameSimulatorTypes() {
		for (int i=1; i < m_simulatorTypes.size(); i++) {
			if (m_sameSimulatorButton.getSelection()) {
				m_simulatorTypes.get(i).setEnabled(false);
				int index=m_simulatorTypes.get(i).indexOf(m_simulatorTypes.get(0).getText());
				if (index == -1) {
					index = 0;
				}
				m_simulatorTypes.get(i).select(index);
			} else {
				m_simulatorTypes.get(i).setEnabled(true);
			}
		}
    }
    
    protected void updateSameModel() {
		for (int i=1; i < m_models.size(); i++) {
			if (m_sameModelButton.getSelection()) {
				m_models.get(i).setText(m_models.get(0).getText());
				
				m_simulationModels.set(i, m_simulationModels.get(0));
				
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
			
			updateModel(i);
		}
    }
    
    protected void updateModel(int index) {
    	
    	// Initialize the simulation model
    	java.io.InputStream is=null;
    	
    	try {
    		is = new java.io.FileInputStream(m_models.get(index).getText());
    	} catch(Exception e) {
    		logger.log(Level.FINE, "Unable to get input stream for '"+
    						m_models.get(index).getText()+"'", e);
    	}
    	
    	try {
    		SimulationModel sm=new SimulationModel(m_models.get(index).getText().trim(), is);
    		m_simulationModels.set(index, sm);
    	} catch(Exception e) {
    		logger.log(Level.SEVERE, "Failed to initialize simulation model for '"+
					m_models.get(index).getText()+"'", e);    		
    	}
    	
    	m_simulatorTypes.get(index).removeAll();
    	
    	m_simulatorTypes.get(index).add(DON_T_SIMULATE);
    	
    	java.util.List<RoleSimulator> rsims=RoleSimulatorFactory.getRoleSimulators();
    	for (RoleSimulator rsim : rsims) {
    		Object model=rsim.getSupportedModel(m_simulationModels.get(index));
    		if (model != null) {
    			m_simulatorTypes.get(index).add(rsim.getName());
    			
    			m_modelRoles.get(index).removeAll();
    			
    			java.util.List<Role> roles=rsim.getModelRoles(model);
    			int mainDefaultRole=-1;
    			int secondaryDefaultRole=-1;
    			
    			for (Role role : roles) {    				
    				if (role.getName().endsWith(m_scenario.getRole().get(index).getName())) {
    					mainDefaultRole = m_modelRoles.get(index).getItemCount();
    				}
    				
    				if (role.getName().indexOf(m_scenario.getRole().get(index).getName()) != -1) {
    					secondaryDefaultRole = m_modelRoles.get(index).getItemCount();
    				}

    				m_modelRoles.get(index).add(role.getName());
    			}
    			
    			if (mainDefaultRole != -1) {
    				m_modelRoles.get(index).select(mainDefaultRole);
    			} else if (secondaryDefaultRole != -1) {
    				m_modelRoles.get(index).select(secondaryDefaultRole);
    			}
    		}
    	}
    	
    	m_simulatorTypes.get(index).select(m_simulatorTypes.get(index).getItemCount() > 0 ? 1 : 0);
    }
    
    protected void initSimulation() {
		try {
			for (int i=0; i < m_scenario.getRole().size(); i++) {
				RoleSimulator rsim=RoleSimulatorFactory.getRoleSimulator(m_simulatorTypes.get(i).getText());
				
				if (rsim != null) {
					Object model=rsim.getSupportedModel(m_simulationModels.get(i));
					
					if (model != null) {
						java.util.List<Role> roles=rsim.getModelRoles(model);
						Role selected=null;
						
						for (int j=0; selected == null && j < roles.size(); j++) {
							if (roles.get(j).getName().equals(m_modelRoles.get(i).getText())) {
								selected = roles.get(j);
							}
						}
						
						if (selected != null || roles.size() == 0) {
							m_roleSimulators.put(m_scenario.getRole().get(i), rsim);
							
							DefaultSimulationContext context=new DefaultSimulationContext(m_scenarioFile);
							
							if (roles.size() == 0) {
								context.setModel(model);
							} else {
								context.setModel(rsim.getModelForRole(model, selected));
							}
							
							rsim.initialize(context);
							
							m_contexts.put(m_scenario.getRole().get(i), context);
						} else {
							logger.severe("Missing role '"+m_modelRoles.get(i).getText()+"'");
						}
					}
				}
			}
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to initialise simulation", e);
		}
    }
    
    protected void runSimulation() {
		try {
			// Create the simulator
			ScenarioSimulator ssim=ScenarioSimulatorFactory.getScenarioSimulator();
			
			ssim.simulate(m_scenario, m_roleSimulators, m_contexts, m_handler);
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to simulate", e);
		}
    }
}
