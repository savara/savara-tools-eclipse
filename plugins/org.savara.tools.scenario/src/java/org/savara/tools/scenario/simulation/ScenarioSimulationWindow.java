/*
 * Copyright 2005 Pi4 Technologies Ltd
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
 * Oct 24, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.simulation;

import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class provides the dialog window to obtain relevant service
 * scenario test information from the user and then present the
 * results of running the scenario.
 *
 */
public class ScenarioSimulationWindow extends org.eclipse.jface.window.Window {

	/**
	 * Constructor for the scenario test dialog.
	 * 
	 * @param shell The shell
	 * @param project The project
	 * @param relativePath The project relative path of the
	 * 							choreography description
	 * @param cdlpack The CDL package
	 * @param cdlres The CDL resource
	 */
	public ScenarioSimulationWindow(Shell shell, String project,
			String relativePath, org.scribble.protocol.model.ProtocolModel protocol,
						IResource cdlres) {
		super(shell);
		
		m_relativePath = relativePath;
		m_protocol = protocol;
		m_project = project;
		m_cdlResource = cdlres;
	}
	
	/**
	 * This method creates the dialog details.
	 * 
	 * @param parent The parent control
	 * @return The control containing the dialog components
	 */
	protected Control createContents(Composite parent) {
		Composite composite=(Composite)super.createContents(parent);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);		
	
		GridData gd=null;

		gd=new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.verticalSpan = 1;
		gd.widthHint = 700;
		gd.heightHint = 500;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		composite.setLayoutData(gd);
		
		Group servgroup=new Group(composite, SWT.H_SCROLL|SWT.V_SCROLL);
		
		gd=new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.widthHint = 700;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		servgroup.setLayoutData(gd);
		
		layout = new GridLayout();
		layout.numColumns = 4;
		servgroup.setLayout(layout);		
			
		Tree tree=new Tree(servgroup, SWT.CHECK|
				SWT.H_SCROLL|SWT.V_SCROLL);
		
		gd=new GridData();
		//gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.widthHint = 180;
		gd.heightHint = 200;
		gd.grabExcessVerticalSpace = true;
		tree.setLayoutData(gd);
		
		m_services = new TreeItem(tree, SWT.CHECK);
		
		m_services.setText("Execute Services");
		
		tree.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (e.item == m_services &&
						m_allServiceState != m_services.getChecked()) {
					TreeItem[] treeItems=m_services.getItems();
					
					for (int i=0; i < treeItems.length; i++) {
						treeItems[i].setChecked(m_services.getChecked());
					}
					
					m_allServiceState = m_services.getChecked();
				}
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				widgetSelected(e);
			}
		});
	
		java.util.Iterator<org.scribble.protocol.model.Role> iter=m_protocol.getRoles().iterator();
		
		while (iter.hasNext()) {
			org.scribble.protocol.model.Role role=
					(org.scribble.protocol.model.Role)iter.next();
			
			TreeItem ti=new TreeItem(m_services, SWT.CHECK);
			ti.setText(role.getName());
			ti.setChecked(false);
		}
		
		m_services.setExpanded(true);

		TabFolder tabfolder=new TabFolder(servgroup, SWT.NONE);
		
		gd=new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 3;
		gd.widthHint = 400;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tabfolder.setLayoutData(gd);
		
		TabItem treePanel=new TabItem(tabfolder, SWT.NONE);
		treePanel.setText("Scenario Results");
		
		m_results = new Tree(tabfolder, SWT.H_SCROLL|SWT.V_SCROLL);
		treePanel.setControl(m_results);
		
		TabItem tracePanel=new TabItem(tabfolder, SWT.NONE);
		tracePanel.setText("Trace Output");

		m_output=new Text(tabfolder, SWT.MULTI|SWT.READ_ONLY|SWT.H_SCROLL|SWT.V_SCROLL);
		tracePanel.setControl(m_output);
		
		gd=new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 3;
		gd.widthHint = 400;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		m_output.setLayoutData(gd);		
		
		Group buttons=new Group(composite, SWT.NONE);
		
		layout = new GridLayout();
		layout.numColumns = 2;
		buttons.setLayout(layout);		

		gd=new GridData();
		gd.horizontalAlignment = SWT.CENTER;

		buttons.setLayoutData(gd);

		Button run=new Button(buttons, SWT.NONE);
		run.setText("Run Scenario");
		
		run.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				clearResults();
				
				launch(m_cdlResource.getLocation().toString(),
						m_project, m_relativePath);
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		Button close=new Button(buttons, SWT.NONE);
		close.setText("Close");
		
		close.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				close();
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		return(composite);
	}
	
	protected void clearResults() {
		m_output.setText("");
		m_results.removeAll();
	}
	
	/**
	 * Configure the dialog shell.
	 * 
	 * @param shell The shell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		String text="Scenario Tester ["+m_cdlResource.getName()+"]";
		
		newShell.setText(text);
	}
	
	/**
	 * This method invokes the launch action.
	 * 
	 * @param path The full path
	 * @param project The project
	 * @param relativePath The relative path within the project
	 */
	protected void launch(String path, String project, String relativePath) {
		
		ScenarioSimulationUILauncher launcher=
				new ScenarioSimulationUILauncher(
						getShell().getDisplay(), m_results, m_output);
				
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
				if (configuration.getName().equals(PI4SOA_TEST_SCENARIO)) {
					configuration.delete();
					break;
				}
			}
			
			ILaunchConfigurationWorkingCopy workingCopy =
			      type.newInstance(null, PI4SOA_TEST_SCENARIO);

			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					project);
			workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_SCENARIO,
					relativePath);
			
			String services=getServiceList();
			if (services != null) {
				workingCopy.setAttribute(ScenarioSimulationLaunchConfigurationConstants.ATTR_EXECUTE_SERVICES,
						services);
			}
				
			ILaunchConfiguration configuration=workingCopy.doSave();
		

			Launch launch=new Launch(configuration, LAUNCH_MODE, null);
			
			launcher.launch(configuration, LAUNCH_MODE, launch, null);

		} catch(Exception e) {
			logger.severe("Failed to launch scenario tester: "+e);
			
			e.printStackTrace();
		}
	}
	
	protected String getServiceList() {
		String ret=null;
		
		TreeItem[] treeItems=m_services.getItems();
		for (int i=0; i < treeItems.length; i++) {
			
			if (treeItems[i].getChecked()) {
				if (ret == null) {
					ret = treeItems[i].getText();
				} else {
					ret += ","+treeItems[i].getText();
				}
			}
		}
		
		return(ret);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.test.eclipse");	
	
	private String m_relativePath=null;
	private String m_project=null;
	private org.scribble.protocol.model.ProtocolModel m_protocol=null;
	private boolean m_allServiceState=false;
	private IResource m_cdlResource=null;
	private Text m_output=null;
	private Tree m_results=null;
	private TreeItem m_services=null;
	
	private static final String LAUNCH_MODE = "run";
	private static final String PI4SOA_TEST_SCENARIO = "Pi4SOA Test Scenario";
}
