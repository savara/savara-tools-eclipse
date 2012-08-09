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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.savara.common.resources.DefaultResourceLocator;
import org.savara.common.resources.ResourceLocator;
import org.savara.scenario.model.Role;
import org.savara.scenario.model.Scenario;
import org.savara.scenario.simulation.RoleSimulator;
import org.savara.scenario.simulation.RoleSimulatorFactory;
import org.savara.scenario.simulation.SimulationModel;
import org.savara.scenario.simulation.model.RoleDetails;
import org.savara.scenario.simulation.model.Simulation;
import org.savara.scenario.simulation.model.SimulatorDetails;
import org.savara.scenario.util.ScenarioModelUtil;
import org.savara.scenario.util.SimulationModelUtil;
import org.savara.tools.scenario.osgi.Activator;

public class ScenarioSimulationDialog extends Dialog {

	private static final String SCENARIO_SIMULATOR_MAIN = "ScenarioSimulatorMain";
	private static final String LAUNCH_MODE = "run";

	private static final QualifiedName MODELS_QUALIFIED_NAME = new QualifiedName(Activator.PLUGIN_ID, "models");

	private static final String DON_T_SIMULATE = "Don't Simulate";
	
	//private Button m_sameModelButton=null;
	//private Button m_sameSimulatorButton=null;
	private java.util.List<Combo> m_models=new java.util.Vector<Combo>();
	private java.util.List<Button> m_browseButtons=new java.util.Vector<Button>();
	private java.util.List<Combo> m_modelRoles=new java.util.Vector<Combo>();
	private java.util.List<Combo> m_simulatorTypes=new java.util.Vector<Combo>();
	private java.io.File m_scenarioFile=null;
	private IFile m_scenarioIFile=null;
	private java.util.List<SimulationModel> m_simulationModels=new java.util.Vector<SimulationModel>();
	private java.util.List<ResourceLocator> m_resourceLocators=new java.util.Vector<ResourceLocator>();
	private Scenario m_scenario=null;
	private org.savara.tools.scenario.designer.editor.ScenarioDesigner m_designer=null;
	private boolean m_simulate=false;
	private java.util.Map<Role,RoleSimulator> m_roleSimulators=new java.util.HashMap<Role,RoleSimulator>();
	//private java.util.Map<Role,SimulationContext> m_contexts=new java.util.HashMap<Role,SimulationContext>();
	private Simulation m_simulation=null;
	private boolean f_initialConfig=true;
		
	private static final Logger logger=Logger.getLogger(ScenarioSimulationDialog.class.getName());
	
	/*
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);
		
		// Register dummy role simulators
		RoleSimulator rs1=new org.savara.scenario.simulation.TestRoleSimulator("Test Simulator 1", true);
		RoleSimulator rs2=new org.savara.scenario.simulation.TestRoleSimulator("Test Simulator 2", true);
		
		RoleSimulatorFactory.register(rs1);
		RoleSimulatorFactory.register(rs2);

		ScenarioSimulationDialog ssd=new ScenarioSimulationDialog(shell);
		
		org.savara.scenario.simulation.TestSimulationHandler handler=
				new org.savara.scenario.simulation.TestSimulationHandler();
		
		try {
			ssd.initializeScenario(new java.io.File(args[0]));
			
			ssd.setSimulationHandler(handler);
		
			ssd.open();
			
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
    
    protected java.io.File getScenarioFile() {
    	if (m_scenarioFile != null) {
    		return(m_scenarioFile);
    	} else if (m_scenarioIFile != null) {
    		return(m_scenarioIFile.getRawLocation().toFile());
    	}
    	return(null);
    }
    
    public void initializeScenario(IFile scenarioFile) throws IOException {
    	m_scenarioIFile = scenarioFile;
    	
    	try {
	    	java.io.InputStream is=m_scenarioIFile.getContents();
	    	
	    	m_scenario = ScenarioModelUtil.deserialize(is);
	    	
	    	is.close();
    	} catch(Exception e) {
    		throw new IOException("Failed to load scenario", e);
    	}
    }
    
    public void setScenarioDesigner(org.savara.tools.scenario.designer.editor.ScenarioDesigner designer) {
    	m_designer = designer;
    }
    
    public Object open() {
    	Shell parent = getParent();
    	
    	final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    	dialog.setText("Scenario Simulation");
    	
    	
    	dialog.setLayout(new FillLayout());
    	
    	// Construct main area
    	ScrolledComposite scrollingArea=new ScrolledComposite(dialog, SWT.H_SCROLL | SWT.V_SCROLL|SWT.BORDER);

    	Composite mainArea=new Composite(scrollingArea, SWT.NONE);
    	mainArea.setLayout(new GridLayout());

    	/*
    	m_sameModelButton=new Button(mainArea, SWT.CHECK);
    	m_sameModelButton.setText("Use same model for all roles");
    	m_sameModelButton.setSelection(true);
    	
    	m_sameSimulatorButton=new Button(mainArea, SWT.CHECK);
    	m_sameSimulatorButton.setText("Use same simulator for all roles");
    	m_sameSimulatorButton.setSelection(true);
    	
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
    	*/
    	
    	Color blue = parent.getDisplay().getSystemColor(SWT.COLOR_BLUE);
   	
    	for (int i=0; i < m_scenario.getRole().size(); i++) {
    		Group rolePanel=new Group(mainArea, SWT.NONE);
    		rolePanel.setText(m_scenario.getRole().get(i).getName());
    		rolePanel.setForeground(blue);
    		
        	FormLayout groupform = new FormLayout();
        	groupform.marginWidth = groupform.marginHeight = 8;
        	rolePanel.setLayout(groupform);
        	
    		Label modelLabel=new Label(rolePanel, SWT.NONE);
    		modelLabel.setText("Model:");
    		
        	FormData modelLabelData = new FormData();
        	modelLabelData.top = new FormAttachment(6);
        	modelLabel.setLayoutData(modelLabelData);
        	
    		final Combo model=new Combo(rolePanel, SWT.NONE);
    		m_models.add(model);
    		
    		// Initialize model
    		initializeModels(model);
    		
    		if (model.getItemCount() > 0) {
    			model.select(0);
    		}
    		
        	FormData modelData = new FormData();
        	modelData.left = new FormAttachment(modelLabel, 36);
        	//modelData.right = new FormAttachment(0);
        	modelData.width = 340;
        	model.setLayoutData(modelData);
        	
        	model.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					
					updateModel(m_models.indexOf(model));
					
					/*
					if (model == m_models.get(0) &&
							m_sameModelButton.getSelection()) {
						for (int i=1; i < m_models.size(); i++) {
							m_models.get(i).setText(model.getText());
							updateModel(i);
						}
					}
					*/
				}        		
        	});
        	
        	try {
        		m_simulationModels.add(new SimulationModel(null, null));
        		m_resourceLocators.add(new DefaultResourceLocator(null));
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
					
					fd.setFileName(getScenarioFile().getAbsolutePath());
					
					String path=fd.open();
					
					if (path != null) {
						model.setText(path);
						
						if (f_initialConfig) {
							
							// Show dialog asking if model should be
							// applied to all roles
							MessageBox mb=new MessageBox(getParent(), SWT.ICON_QUESTION|SWT.YES|SWT.NO);
							
							mb.setMessage("Should model be set for all roles");
							
							if (mb.open() == SWT.YES) {
								for (int i=0; i < m_models.size(); i++) {
									m_models.get(i).setText(model.getText());
									updateModel(i);
								}
							}
							
							f_initialConfig = false;
						}
					}
				}
        	});   	
        	
    		Label modelRoleLabel=new Label(rolePanel, SWT.NONE);
    		modelRoleLabel.setText("Model Role:");
    		
        	FormData modelRoleLabelData = new FormData();
        	modelRoleLabelData.top = new FormAttachment(modelLabel, 15);
        	modelRoleLabel.setLayoutData(modelRoleLabelData);
        	
        	final Combo modelRole=new Combo(rolePanel, SWT.READ_ONLY);
    		m_modelRoles.add(modelRole);
    		
        	FormData modelRoleData = new FormData();
        	modelRoleData.left = new FormAttachment(modelRoleLabel, 5);
        	modelRoleData.right = new FormAttachment(100);
        	modelRoleData.top = new FormAttachment(model, 4);
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
        	simulatorLabelData.top = new FormAttachment(modelRoleLabel, 15);
        	simulatorLabel.setLayoutData(simulatorLabelData);
        	
        	final Combo simulatorType=new Combo(rolePanel, SWT.READ_ONLY);
    		m_simulatorTypes.add(simulatorType);
    		
        	FormData simulatorTypeData = new FormData();
        	simulatorTypeData.left = new FormAttachment(simulatorLabel, 13);
        	simulatorTypeData.right = new FormAttachment(100);
        	simulatorTypeData.top = new FormAttachment(modelRole, 5);
        	simulatorType.setLayoutData(simulatorTypeData);
        	
        	final int pos=i;
        	
        	simulatorType.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {
					widgetSelected(arg0);
				}
				public void widgetSelected(SelectionEvent arg0) {
					updateModelRoleList(pos);
				}
        	});
        	
        	updateModel(i);
    	}
    	
    	Group buttonGroup=new Group(mainArea, SWT.NONE);
    	
    	GridLayout gl=new GridLayout();
    	gl.numColumns = 2;
    	buttonGroup.setLayout(gl);
    	
    	Button okButton = new Button (buttonGroup, SWT.PUSH);
    	okButton.setText ("&Simulate");
    	Button cancelButton = new Button (buttonGroup, SWT.PUSH);
    	cancelButton.setText ("&Cancel");
    	
    	buttonGroup.setSize(buttonGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
    	buttonGroup.layout();
    	
    	loadSimulation();
    	
    	mainArea.setSize(mainArea.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
    	
    	scrollingArea.setContent(mainArea);
    	
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
				
				saveModels();
				
				saveSimulation();
				
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
    	
    	//updateSameModel();
    	//updateSameSimulatorTypes();
    	
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
    
    protected void initializeModels(Combo models) {
    	if (m_scenarioIFile != null) {
    		try {
	    		String modelList=m_scenarioIFile.getPersistentProperty(
	    					MODELS_QUALIFIED_NAME);
	    		
	    		if (modelList != null) {
		    		String[] names=modelList.split(",");
		    		
		    		if (names != null) {
		    			for (int i=0; i < names.length; i++) {
		    				java.io.File f=new java.io.File(names[i]);
		    				
		    				if (f.exists()) {
		    					models.add(names[i]);
		    				}
		    			}
		    		}
	    		}
    		} catch(Exception e) {
    			logger.severe("Failed to get list of models associated with scenario '"+
    					m_scenarioIFile.getName()+"'");
    		}
    	}
    }
    
    protected void saveModels() {
    	if (m_scenarioIFile != null) {
    		try {
    			java.util.List<String> modelFiles=new java.util.Vector<String>();
    	
    			// First add the selected models
    			for (Combo model : m_models) {
    				if (modelFiles.contains(model.getText()) == false) {
    					modelFiles.add(model.getText());
    				}
    			}
    			
    			// Add remaining entries
    			for (Combo model : m_models) {
    				for (int i=0; i < model.getItemCount(); i++) {
	    				if (modelFiles.contains(model.getItem(i)) == false) {
	    					modelFiles.add(model.getItem(i));
	    				}
    				}
    			}
    			
    			String str=null;
    			
    			for (String name : modelFiles) {
    				if (str == null) {
    					str = name;
    				} else {
    					str += ","+name;
    				}
    			}
    			
    			m_scenarioIFile.setPersistentProperty(MODELS_QUALIFIED_NAME, str);
   			
    		} catch(Exception e) {
    			logger.severe("Failed to save list of models associated with scenario '"+
    					m_scenarioIFile.getName()+"'");
    		}
    	}
    }
    
    protected void saveSimulation() {
    	if (m_scenarioIFile != null) {
    		IFile simFile=null;
    		
    		String filename=m_scenarioIFile.getName();
    		
    		filename = filename.substring(0, filename.length()-m_scenarioIFile.getFileExtension().length()-1);
    		
    		filename += ".simulation";
    		
    		if (m_scenarioIFile.getParent() instanceof IFolder) {
    			simFile = ((IFolder)m_scenarioIFile.getParent()).getFile(filename);
    		} else if (m_scenarioIFile.getParent() instanceof IProject) {
    			simFile = ((IProject)m_scenarioIFile.getParent()).getFile(filename);
    		}
    		
    		if (simFile != null) {
    			try {
	    			java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
	    		
	    			SimulationModelUtil.serialize(m_simulation, os);
	    			
	    			String str=os.toString();
	    			
	    			os.close();
	    			
	    			java.io.ByteArrayInputStream is=new java.io.ByteArrayInputStream(str.getBytes());
	    			
    				if (simFile.exists() == false) {
    					simFile.create(is, true, null);
    				} else {
    					simFile.setContents(is, true, false, null);
    				}
	    			
	    			is.close();
    			} catch(Exception e) {
    				logger.log(Level.SEVERE, "Failed to save simulation", e);
    			}
    		}
    	}
    }

    protected void loadSimulation() {
    	if (m_scenarioIFile != null) {
    		IFile simFile=null;
    		
    		String filename=m_scenarioIFile.getName();
    		
    		filename = filename.substring(0, filename.length()-m_scenarioIFile.getFileExtension().length()-1);
    		
    		filename += ".simulation";
    		
    		if (m_scenarioIFile.getParent() instanceof IFolder) {
    			simFile = ((IFolder)m_scenarioIFile.getParent()).getFile(filename);
    		} else if (m_scenarioIFile.getParent() instanceof IProject) {
    			simFile = ((IProject)m_scenarioIFile.getParent()).getFile(filename);
    		}
    		
    		if (simFile != null && simFile.exists()) {
    			f_initialConfig=false;
    			
    			try {
	    			java.io.InputStream is=simFile.getContents();

	    			m_simulation = SimulationModelUtil.deserialize(is);
	    			
	    			is.close();
    			} catch(Exception e) {
    				logger.log(Level.SEVERE, "Failed to save simulation", e);
    			}
    			
    			if (m_simulation != null) {
    				
    		    	for (int i=0; i < m_scenario.getRole().size(); i++) {
    		    		boolean f_initialized=false;
    		    		
    		    		// Check if role details are available for role
    		    		for (int j=0; f_initialized == false && j < m_simulation.getRoles().size(); j++) {
    		    			RoleDetails rd=m_simulation.getRoles().get(j);
    		    			if (rd.getScenarioRole().equals(m_scenario.getRole().get(i).getName())) {
    		    				m_models.get(i).setText(rd.getModel());
    		    				int index=m_simulatorTypes.get(i).indexOf(rd.getSimulator());
    		    				
    		    				if (index != -1) {
    		    					m_simulatorTypes.get(i).select(index);
    		    					updateModelRoleList(i);
    		    				}
    		    				if (rd.getModelRole() != null) {
    		    					m_modelRoles.get(i).setText(rd.getModelRole());
    		    				}
    		    				f_initialized = true;
    		    			}
    		    		}
    		    		
    		    		if (f_initialized == false) {
    		    			m_simulatorTypes.get(i).select(0);
    		    			m_models.get(i).setText("");
    		    		}
    		    	}
    			}
    		}
    	}
    }

    /*
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
				//m_models.get(i).setEditable(false);
				
				m_sameSimulatorButton.setSelection(true);
				m_sameSimulatorButton.setEnabled(true);
			} else {
				m_models.get(i).setEnabled(true);
				//m_models.get(i).setEditable(true);
				m_sameSimulatorButton.setSelection(false);
				m_sameSimulatorButton.setEnabled(false);
			}
			
			updateModel(i);
		}
    }
    */
    
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
    		java.io.File modelFile=new java.io.File(m_models.get(index).getText().trim());
    		
    		SimulationModel sm=new SimulationModel(modelFile.getAbsolutePath(), is);
    		m_simulationModels.set(index, sm);
    		m_resourceLocators.set(index, new DefaultResourceLocator(modelFile.getParentFile()));
    		
    	} catch(Exception e) {
    		logger.log(Level.SEVERE, "Failed to initialize simulation model for '"+
					m_models.get(index).getText()+"'", e);    		
    	}
    	
    	m_simulatorTypes.get(index).removeAll();
    	
    	m_simulatorTypes.get(index).add(DON_T_SIMULATE);
    	
    	java.util.List<RoleSimulator> rsims=RoleSimulatorFactory.getRoleSimulators();
    	for (RoleSimulator rsim : rsims) {
    		if (rsim.isSupported(m_simulationModels.get(index))) {
        		m_simulatorTypes.get(index).add(rsim.getName());
    			
        		Object model=rsim.getModel(m_simulationModels.get(index),
						m_resourceLocators.get(index));

    			m_modelRoles.get(index).removeAll();
    			
    			int mainDefaultRole=-1;
    			int secondaryDefaultRole=-1;
    			
    			// Model type may be supported, but might not be retrievable
    			// in the Eclipse environment, but that is ok as simulation
    			// is being done externally with classpath including user's
    			// project
    			if (model != null) {
	    			java.util.List<Role> roles=rsim.getModelRoles(model);
	    			
	    			for (Role role : roles) {    				
	    				if (role.getName().endsWith(m_scenario.getRole().get(index).getName())) {
	    					mainDefaultRole = m_modelRoles.get(index).getItemCount();
	    				}
	    				
	    				if (role.getName().indexOf(m_scenario.getRole().get(index).getName()) != -1) {
	    					secondaryDefaultRole = m_modelRoles.get(index).getItemCount();
	    				}
	
	    				m_modelRoles.get(index).add(role.getName());
	    			}
    			}
    			
    			if (mainDefaultRole != -1) {
    				m_modelRoles.get(index).select(mainDefaultRole);
    			} else if (secondaryDefaultRole != -1) {
    				m_modelRoles.get(index).select(secondaryDefaultRole);
    			}
    		}
    	}
    	
    	m_simulatorTypes.get(index).select(m_simulatorTypes.get(index).getItemCount() > 0 ? 1 : 0);
    	
    	updateModelRoleList(index);
    }
    
    protected void updateModelRoleList(int index) {
    	RoleSimulator rsim=RoleSimulatorFactory.getRoleSimulator(m_simulatorTypes.get(index).getText());
    	
    	if (rsim != null) {
			Object model=rsim.getModel(m_simulationModels.get(index),
					m_resourceLocators.get(index));
	
			m_modelRoles.get(index).removeAll();
			
			int mainDefaultRole=-1;
			int secondaryDefaultRole=-1;
			
			// Model type may be supported, but might not be retrievable
			// in the Eclipse environment, but that is ok as simulation
			// is being done externally with classpath including user's
			// project
			if (model != null) {
				java.util.List<Role> roles=rsim.getModelRoles(model);
				
				for (Role role : roles) {    				
					if (role.getName().endsWith(m_scenario.getRole().get(index).getName())) {
						mainDefaultRole = m_modelRoles.get(index).getItemCount();
					}
					
					if (role.getName().indexOf(m_scenario.getRole().get(index).getName()) != -1) {
						secondaryDefaultRole = m_modelRoles.get(index).getItemCount();
					}
	
					m_modelRoles.get(index).add(role.getName());
				}
			}
			
			if (mainDefaultRole != -1) {
				m_modelRoles.get(index).select(mainDefaultRole);
			} else if (secondaryDefaultRole != -1) {
				m_modelRoles.get(index).select(secondaryDefaultRole);
			}
    	}
    }
    
    protected void initSimulation() {
		try {
			m_simulation = new Simulation();
			
			m_simulation.setScenario(getScenarioFile().getAbsolutePath());
			
			java.util.List<RoleSimulator> simulators=new java.util.Vector<RoleSimulator>();
			
			for (int i=0; i < m_scenario.getRole().size(); i++) {
				RoleSimulator rsim=RoleSimulatorFactory.getRoleSimulator(m_simulatorTypes.get(i).getText());
				
				if (rsim != null) {
					if (simulators.contains(rsim) == false) {
						simulators.add(rsim);
					}
					
					RoleDetails roleDetails=new RoleDetails();
					roleDetails.setSimulator(rsim.getName());
					roleDetails.setScenarioRole(m_scenario.getRole().get(i).getName());	
					roleDetails.setModel(m_simulationModels.get(i).getName());
					
					Object model=rsim.getModel(m_simulationModels.get(i),
    						m_resourceLocators.get(i));
					
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
							
							//DefaultSimulationContext context=new DefaultSimulationContext(getScenarioFile());
							
							if (roles.size() != 0) {
								//context.setModel(model);
							//} else {
								roleDetails.setModelRole(selected.getName());
								
								//context.setModel(rsim.getModelForRole(model, selected,
		        				//		m_resourceLocators.get(i)));
							}
							
							//m_contexts.put(m_scenario.getRole().get(i), context);
						} else {
							logger.severe("Missing role '"+m_modelRoles.get(i).getText()+"'");
						}
					}
					
					m_simulation.getRoles().add(roleDetails);
				}
			}
			
			for (RoleSimulator rsim : simulators) {
				SimulatorDetails sd=new SimulatorDetails();
				sd.setClassName(rsim.getClass().getName());
				sd.setName(rsim.getName());
				
				m_simulation.getSimulators().add(sd);
			}
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to initialise simulation", e);
		}
    }
    
    protected void runSimulation() {
    	ScenarioDesignerSimulationLauncher launcher=
        		new ScenarioDesignerSimulationLauncher(getParent().getDisplay(),
        				m_scenario, m_designer.getScenarioSimulation());
        	
		try {
			ILaunchManager manager =
				DebugPlugin.getDefault().getLaunchManager();
			
			ILaunchConfigurationType type =
				manager.getLaunchConfigurationType(
			      	ScenarioSimulationLaunchConfigurationConstants.LAUNCH_CONFIG_TYPE);
			ILaunchConfiguration[] configurations =
			      manager.getLaunchConfigurations(type);
			
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(SCENARIO_SIMULATOR_MAIN)) {
					configuration.delete();
					break;
				}
			}
						
			ILaunchConfigurationWorkingCopy workingCopy =
			      type.newInstance(null, SCENARIO_SIMULATOR_MAIN);

			// Find projects associated with specified models			
			String projectNames=getProjectNames();
			
			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_PROJECT_NAME,
									projectNames);
			
			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_SCENARIO,
					m_designer.getFile().getProjectRelativePath().toString());
			
			java.io.File simulationFile=getSimulationFile();
			
			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_SIMULATION_FILE,
								simulationFile.getAbsolutePath());
			
			ILaunchConfiguration configuration=workingCopy.doSave();		

			Launch launch=new Launch(configuration, LAUNCH_MODE, null);
			
			launcher.launch(configuration, LAUNCH_MODE, launch, null);

			ScenarioSimulation view=m_designer.getScenarioSimulation();	
			view.startSimulation();
			
			m_designer.updateEditPartActions();
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to simulate", e);
		}
    }
    
    protected java.io.File getSimulationFile() throws Exception {
    	java.io.File ret=java.io.File.createTempFile("savara", ".simulation");
    	ret.deleteOnExit();
    	
    	java.io.FileOutputStream os=new java.io.FileOutputStream(ret);
    	
    	SimulationModelUtil.serialize(m_simulation, os);
    	
    	os.close();
    	
    	return(ret);
    }
    
    protected String getProjectNames() {
    	java.util.List<String> projects=new java.util.Vector<String>();
    	
    	projects.add(m_designer.getFile().getProject().getName());
    	
    	for (int i=0; i < m_models.size(); i++) {
    		IFile modelFile=ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(m_simulationModels.get(i).getName()));
    		
    		if (modelFile != null && modelFile.getProject() != null) {
    			String project=modelFile.getProject().getName();
    			
    			if (projects.contains(project)==false) {
    				projects.add(project);
    			}
    		}
    	}
    	    	
    	String ret="";
    	
    	for (int i=0; i < projects.size(); i++) {
    		if (i > 0) {
    			ret += ",";
    		}
    		ret += projects.get(i);
    	}
    	
    	if (logger.isLoggable(Level.FINE)) {
    		logger.fine("Simulating with projects: "+ret);
    	}
    	
    	return(ret);
    }
}
