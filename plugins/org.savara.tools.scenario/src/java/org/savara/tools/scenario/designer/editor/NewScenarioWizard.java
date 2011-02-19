/*
 * Copyright 2004-5 Enigmatec Corporation Ltd
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
 * 04-Feb-2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;

import org.eclipse.core.runtime.Path;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.savara.scenario.util.ScenarioDefinitions;
import org.savara.scenario.util.ScenarioModelUtil;

/**
 * This class provides the wizard responsible for creating
 * new Scenario object models.
 */
public class NewScenarioWizard extends Wizard implements INewWizard {

    /**
     * This method initializes the wizard.
     * 
     * @param workbench The workbench
     * @param selection The selected resource
     */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		m_workbench = workbench;
		m_selection = selection;
        setWindowTitle("New");
	}
	
	/**
	 * This method is invoked when the new Service Test Scenario object model
	 * should be created.
	 */
	public boolean performFinish() {
		try {
			// Remember the file.
			//
			final IFile modelFile = getModelFile();

			// Do the work within an operation.
			//
			WorkspaceModifyOperation operation =
				new WorkspaceModifyOperation() {
					protected void execute(IProgressMonitor progressMonitor) {
						try {

							// Add the initial model object to the contents.
							//
							org.savara.scenario.model.Scenario scenario=
							    		new org.savara.scenario.model.Scenario();
							
							/* Create an example configuration
							 * 
							 *
							org.savara.scenario.model.Role role1=new org.savara.scenario.model.Role();
							role1.setID("role1");
							role1.setName("role1");
							scenario.getRoles().add(role1);

							org.savara.scenario.model.Role role2=new org.savara.scenario.model.Role();
							role2.setID("role2");
							role2.setName("role2");
							scenario.getRoles().add(role2);
							
							org.savara.scenario.model.SendEvent se1=new org.savara.scenario.model.SendEvent();
							se1.setID("se1");
							se1.setRole(role1);
							se1.setOperationName("hello");
							scenario.getEvents().add(se1);

							org.savara.scenario.model.ReceiveEvent re1=new org.savara.scenario.model.ReceiveEvent();
							re1.setID("re1");
							re1.setRole(role2);
							re1.setOperationName("hello");
							scenario.getEvents().add(re1);

							org.savara.scenario.model.Link l1=new org.savara.scenario.model.Link();
							l1.setSource(se1);
							l1.setTarget(re1);
							scenario.getLinks().add(l1);
							*/

							ByteArrayOutputStream os=new ByteArrayOutputStream();
							
							ScenarioModelUtil.serialize(scenario, os);
							
							os.close();
							
							ByteArrayInputStream is=new ByteArrayInputStream(os.toByteArray());
							
							modelFile.create(is, true, null);
							
							is.close();
						}
						catch (Exception exception) {
							//scenarioEditorPlugin.INSTANCE.log(exception);
							org.savara.tools.scenario.osgi.Activator.logError(
									exception.getMessage(), exception);
						}
						finally {
							progressMonitor.done();
						}
					}
				};

			getContainer().run(false, false, operation);

			// Select the new file resource in the current view.
			//
			IWorkbenchWindow workbenchWindow =
			    m_workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			final IWorkbenchPart activePart = page.getActivePart();
			if (activePart instanceof ISetSelectionTarget) {
				final ISelection targetSelection = new StructuredSelection(modelFile);
				getShell().getDisplay().asyncExec
					(new Runnable() {
						 public void run() {
							 ((ISetSelectionTarget)activePart).selectReveal(targetSelection);
						 }
					 });
			}

			// Open an editor on the new file.
			//
			try {
				page.openEditor
					(new FileEditorInput(modelFile),
					 m_workbench.getEditorRegistry().getDefaultEditor(modelFile.getFullPath().toString()).getId());
			}
			catch (PartInitException exception) {
				MessageDialog.openError(workbenchWindow.getShell(),
						"Open Editor", exception.getMessage());
						//scenarioEditorPlugin.INSTANCE.getString("_UI_OpenEditorError_label"), exception.getMessage());
				return false;
			}

			return true;
		}
		catch (Exception exception) {
			//scenarioEditorPlugin.INSTANCE.log(exception);
			org.savara.tools.scenario.osgi.Activator.logError(
					exception.getMessage(), exception);
			
			return false;
		}
	}

    /**
     * Get the file from the page.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IFile getModelFile() {
        return m_newFileCreationPage.getModelFile();
    }

    /**
     * The framework calls this to create the contents of the wizard.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void addPages() {
        // Create a page, set the title, and the initial model file name.
        //
		m_newFileCreationPage = new ScenarioModelWizardNewFileCreationPage("Whatever", m_selection);
		//m_newFileCreationPage.setTitle(scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioModelWizard_label"));
		//m_newFileCreationPage.setDescription(scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioModelWizard_description"));
		//m_newFileCreationPage.setFileName(scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioEditorFilenameDefaultBase") + "." + scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioEditorFilenameExtension"));
		m_newFileCreationPage.setTitle("Scenario");
		m_newFileCreationPage.setDescription("Create a new Scenario");
		m_newFileCreationPage.setFileName("My"+"."+
						ScenarioDefinitions.SCENARIO_FILE_EXTENSION);
        addPage(m_newFileCreationPage);

        // Try and get the resource selection to determine a current directory for the file dialog.
        //
        if (m_selection != null && !m_selection.isEmpty()) {
            // Get the resource...
            //
            Object selectedElement = m_selection.iterator().next();
            if (selectedElement instanceof IResource) {
                // Get the resource parent, if its a file.
                //
                IResource selectedResource = (IResource)selectedElement;
                if (selectedResource.getType() == IResource.FILE) {
                    selectedResource = selectedResource.getParent();
                }

                // This gives us a directory...
                //
                if (selectedResource instanceof IFolder || selectedResource instanceof IProject) {
                    // Set this for the container.
                    //
                    m_newFileCreationPage.setContainerFullPath(selectedResource.getFullPath());

                    // Make up a unique new name here.
                    //
                    //String defaultModelBaseFilename = scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioEditorFilenameDefaultBase");
                    //String defaultModelFilenameExtension = scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioEditorFilenameExtension");
                    String defaultModelBaseFilename = "My";
                    String defaultModelFilenameExtension =
                    				ScenarioDefinitions.SCENARIO_FILE_EXTENSION;
                    String modelFilename = defaultModelBaseFilename + "." + defaultModelFilenameExtension;
                    for (int i = 1; ((IContainer)selectedResource).findMember(modelFilename) != null; ++i) {
                        modelFilename = defaultModelBaseFilename + i + "." + defaultModelFilenameExtension;
                    }
                    m_newFileCreationPage.setFileName(modelFilename);
                }
            }
        }
    }

    private IWorkbench m_workbench=null;
	private IStructuredSelection m_selection=null;
	private ScenarioModelWizardNewFileCreationPage m_newFileCreationPage=null;
	
    /**
     * This is the one page of the wizard.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public class ScenarioModelWizardNewFileCreationPage extends WizardNewFileCreationPage {
        /**
         * Remember the model file.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected IFile modelFile;
    
        /**
         * Pass in the selection.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public ScenarioModelWizardNewFileCreationPage(String pageId, IStructuredSelection selection) {
            super(pageId, selection);
        }
    
        /**
         * The framework calls this to see if the file is correct.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected boolean validatePage() {
            if (super.validatePage()) {
                // Make sure the file ends in ".scenario".
                //
                //String requiredExt = scenarioEditorPlugin.INSTANCE.getString("_UI_ScenarioEditorFilenameExtension");
            	
            	if (new Path(getFileName()).getFileExtension().equals(ScenarioDefinitions.SCENARIO_FILE_EXTENSION)) {
            		return true;
            	} else {
                    setErrorMessage("The filename must end in \"."+ScenarioDefinitions.SCENARIO_FILE_EXTENSION+"\"");
                    return false;
            	}
            	/*
                String requiredExt = "scenario";
                String enteredExt = new Path(getFileName()).getFileExtension();
                if (enteredExt == null || !enteredExt.equals(requiredExt)) {
                    //setErrorMessage(scenarioEditorPlugin.INSTANCE.getString("_WARN_FilenameExtension", new Object [] { requiredExt }));
                    setErrorMessage("The filename must end in \".scenario\"");
                    return false;
                }
                else {
                    return true;
                }
                */
            }
            else {
                return false;
            }
        }
    
        /**
         * Store the dialog field settings upon completion.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public boolean performFinish() {
            modelFile = getModelFile();
            return true;
        }
    
        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public IFile getModelFile() {
            return
                modelFile == null ?
                    ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName())) :
                    modelFile;
        }
    }

}
