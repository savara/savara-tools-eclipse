/*
 * Copyright 2005-8 Pi4 Technologies Ltd
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
 * 17 Jan, 2008 : Initial version created by gary
 */
package org.savara.tools.monitor.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * This class represents the first main tab within the tab group
 * associated with the monitor launch configuration.
 */
public class MonitorMainTab extends AbstractLaunchConfigurationTab {
	
	/**
	 * @see ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns= 3;
		comp.setLayout(topLayout);		
		
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		createChoreographySection(comp);
		
		label = new Label(comp, SWT.NONE);
		gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		Dialog.applyDialogFont(comp);
		validatePage();
	}
	
	/**
	 * This method creates the GUI components for the
	 * monitor tab.
	 * 
	 * @param comp The composite
	 */
	protected void createChoreographySection(Composite comp) {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		
		m_projectLabel = new Label(comp, SWT.NONE);
		m_projectLabel.setText("Project");
		gd= new GridData();
		gd.horizontalIndent = 25;
		m_projectLabel.setLayoutData(gd);
		
		m_project= new Text(comp, SWT.SINGLE | SWT.BORDER);
		m_project.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_project.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				validatePage();
				updateLaunchConfigurationDialog();				
				m_choreographySearch.setEnabled(m_project.getText().length() > 0);
			}
		});
			
		m_projectButton = new Button(comp, SWT.PUSH);
		m_projectButton.setText("Browse");
		m_projectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleProjectButtonSelected();
			}
		});
		setButtonGridData(m_projectButton);
		
		m_choreographyLabel = new Label(comp, SWT.NONE);
		gd = new GridData();
		gd.horizontalIndent = 25;
		m_choreographyLabel.setLayoutData(gd);
		m_choreographyLabel.setText("Choreography");
		
		m_choreography = new Text(comp, SWT.SINGLE | SWT.BORDER);
		m_choreography.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_choreography.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				validatePage();
				updateLaunchConfigurationDialog();
			}
		});
		
		m_choreographySearch = new Button(comp, SWT.PUSH);
		m_choreographySearch.setEnabled(m_project.getText().length() > 0);		
		m_choreographySearch.setText("Search");
		m_choreographySearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleSearchButtonSelected();
			}
		});
		setButtonGridData(m_choreographySearch);
	}

	protected static Image createImage(String path) {
		return null;
	}


	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration config) {
		String projectName= "";
		String choreography= "";

		try {
			projectName = config.getAttribute(MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		} catch (CoreException ce) {
		}
		m_project.setText(projectName);
		
		try {
			choreography = config.getAttribute(MonitorLaunchConfigurationConstants.ATTR_CHOREOGRAPHY_DESCRIPTION, ""); //$NON-NLS-1$
		} catch (CoreException ce) {			
		}
		m_choreography.setText(choreography);
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy config) {
	}

	/**
	 * @see ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * @see AbstractLaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return(null);
	}

	/**
	 * This method sets the grid data for the button.
	 * 
	 * @param button The button
	 */
	protected void setButtonGridData(Button button) {
		GridData gridData= new GridData();
		button.setLayoutData(gridData);
		//SWTUtil.setButtonDimensionHint(button);
	}

	/**
	 * Show a dialog that lists all choreography files within the
	 * selected project
	 */
	protected void handleSearchButtonSelected() {
		
		IProject project = getProject();

		ILabelProvider labelProvider=new LabelProvider() {
			public String getText(Object obj) {
				String ret="<unknown>";
				if (obj instanceof IResource) {
					String filename=((IResource)obj).getName();
					if (filename.endsWith(org.pi4soa.cdl.CDLDefinitions.CDL_FILE_EXTENSION)) {
						filename = filename.substring(0, filename.length()-
								org.pi4soa.cdl.CDLDefinitions.CDL_FILE_EXTENSION.length()-1);
					}
					ret = filename+" ["+
						((IResource)obj).getParent().
						getProjectRelativePath()+"]";
				}
				return(ret);
			}
		};
		
		IResource[] choreos=null;
		
		if (project.exists() == false) {
			choreos = new IResource[0];
		} else {
			choreos = getChoreographies(project);
		}

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Choreographies");
		dialog.setMessage("Select the relevant choreography");
		dialog.setElements(choreos);
		
		if (dialog.open() == Window.OK) {			
			IResource file=(IResource)dialog.getFirstResult();
			m_choreography.setText(file.getProjectRelativePath().toString());
		}
	}
	
	/**
	 * This method returns the list of choreography resource files within
	 * the supplied project.
	 * 
	 * @param project The project
	 * @return The list of choreography resource files
	 */
	protected IResource[] getChoreographies(IProject project) {
		IResource[] ret=null;
		final java.util.Vector list=new java.util.Vector();
		
		try {
			project.accept(new org.eclipse.core.resources.IResourceVisitor() {
				public boolean visit(IResource res) {

					if (res.getFileExtension() != null &&
							res.getFileExtension().equals(
									org.pi4soa.cdl.CDLDefinitions.CDL_FILE_EXTENSION)) {
						list.add(res);
					}

					return(true);
				}
			});
			
			ret = new IResource[list.size()];
			list.copyInto(ret);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return(ret);
	}
		
	/**
	 * Show a dialog that lets the user select a project.  This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleProjectButtonSelected() {
		IProject project = chooseProject();
		if (project == null) {
			return;
		}
		
		String projectName = project.getName();
		m_project.setText(projectName);		
	}
	
	/**
	 * Realize a Java Project selection dialog and return the first selected project,
	 * or null if there was none.
	 */
	protected IProject chooseProject() {
		IProject[] projects;
		try {
			projects= getWorkspaceRoot().getProjects();
		} catch (Exception e) {
			projects= new IProject[0];
		}
		
		ILabelProvider labelProvider=new LabelProvider() {
			public String getText(Object obj) {
				String ret="<unknown>";
				if (obj instanceof IResource) {
					ret = ((IResource)obj).getName();
				}
				return(ret);
			}
		};

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Projects");
		dialog.setMessage("Select the relevant project");
		dialog.setElements(projects);
		
		IProject project = getProject();
		if (project != null) {
			dialog.setInitialSelections(new Object[] { project });
		}
		if (dialog.open() == Window.OK) {			
			return (IProject) dialog.getFirstResult();
		}			
		return null;		
	}
	
	/**
	 * Return the IProject corresponding to the project name in the project name
	 * text field, or null if the text does not match a project name.
	 */
	protected IProject getProject() {
		String projectName = m_project.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return(getWorkspaceRoot().getProject(projectName));
	}
	
	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {		
		return getErrorMessage() == null;
	}
	
	/**
	 * This method validates the page.
	 *
	 */
	private void validatePage() {
		setErrorMessage(null);
		setMessage(null);
		
		String projectName = m_project.getText().trim();
		if (projectName.length() == 0) {
			setErrorMessage("Project name not specified");
			return;
		}
			
		IProject project = getWorkspaceRoot().getProject(projectName);
		if (!project.exists()) {
			setErrorMessage("Project '"+projectName+"' does not exist");
			return;
		}
		
		try {
			String choreographyName = m_choreography.getText().trim();
			if (choreographyName.length() == 0) {
				setErrorMessage("Choreography has not been defined");
				return;
			}
			IResource resource = project.findMember(choreographyName);
			if (resource == null) {
				setErrorMessage("Could not find choreography '"+choreographyName+"'");
			} else {
				
				// TODO: Check is valid choreography model
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		
		IResource resource = getContext();
		if (resource != null) {
			initializeProject(resource, config);
		} else {
			config.setAttribute(MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			config.setAttribute(MonitorLaunchConfigurationConstants.ATTR_CHOREOGRAPHY_DESCRIPTION, "");
		}
		initializeTestAttributes(resource, config);
	}

	/**
	 * This method identifies the context associated with the
	 * monitor.
	 * 
	 * @return The context resource
	 */
	protected IResource getContext() {
		IResource ret=null;
		IWorkbenchPage page =
			org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				
		if (page != null) {
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection)selection;
				if (!ss.isEmpty()) {
					Object obj = ss.getFirstElement();
					if (obj instanceof IResource) {
						ret = (IResource)obj;
					}
				}
			}
			
			if (ret == null) {
				IEditorPart part = page.getActiveEditor();
				if (part != null) {
					IEditorInput input = part.getEditorInput();
					ret =(IResource)input.getAdapter(IResource.class);
				}
			}
		}
		
		return(ret);
	}

	/**
	 * This method initializes the project details.
	 * 
	 * @param resource The resource
	 * @param config The configuration
	 */
	protected void initializeProject(IResource resource, ILaunchConfigurationWorkingCopy config) {
		IProject project = resource.getProject();
		String name = null;
		if (project != null && project.exists()) {
			name = project.getName();
		}
		config.setAttribute(MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
	}
	
	/**
	 * This method initializes the choreography details.
	 * 
	 * @param resource The selected resource
	 * @param config The configuration
	 */
	private void initializeTestAttributes(IResource resource, ILaunchConfigurationWorkingCopy config) {
		if (resource != null && (resource.getType() == IResource.FOLDER ||
				(resource.getType() == IResource.FILE &&
				resource.getFileExtension().equals(
						org.pi4soa.cdl.CDLDefinitions.CDL_FILE_EXTENSION)))) {
			
			config.setAttribute(MonitorLaunchConfigurationConstants.ATTR_CHOREOGRAPHY_DESCRIPTION,
					resource.getProjectRelativePath().toString());
		
			initializeName(config, resource.getName());
		}
	}

	/**
	 * This method initializes the launch configuration name.
	 * 
	 * @param config The configuration
	 * @param name The name
	 */
	private void initializeName(ILaunchConfigurationWorkingCopy config, String name) {
		if (name == null) {
			name= "";
		}
		if (name.length() > 0) {
			
			int index = name.lastIndexOf('.');
			if (index > 0) {
				name = name.substring(0, index);
			}
			name= getLaunchConfigurationDialog().generateName(name);
			config.rename(name);
		}
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return("Monitor");
	}

	private Label m_projectLabel=null;
	private Text m_project=null;
	private Button m_projectButton=null;
	private Label m_choreographyLabel=null;
	private Text m_choreography=null;
	private Button m_choreographySearch=null;	
}
