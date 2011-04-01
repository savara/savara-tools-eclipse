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
 * Jul 5, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import org.savara.tools.scenario.designer.dnd.ScenarioTemplateTransferDropTargetListener;
import org.savara.tools.scenario.designer.parts.*;
import org.savara.tools.scenario.designer.simulate.SimulationEntity;
import org.savara.tools.scenario.designer.view.GraphicalComponent;

/**
 * This class represents the flow based representation of a
 * choreography.
 */
public class ScenarioEditorPage extends AbstractEditorPage
			implements org.savara.tools.scenario.designer.simulate.ScenarioSimulation {

	/**
	 * The constructor for the choreography flow page.
	 * 
	 * @param parent The multipage editor
	 */
	public ScenarioEditorPage(ScenarioDesigner parent) {
		super(parent, new EditDomain());
	}
	
	/**
	 * This method returns the page name.
	 * 
	 * @return The page name
	 */
	public String getPageName() {
		return("Scenario Editor");
	}
	
	protected CommandStack getCommandStack2() {
		return(super.getCommandStack());
	}
	
    /**
     * This method returns the scenario.
     * 
     * @return The scenario
     */
    public org.savara.scenario.model.Scenario getScenario() {
    	return((org.savara.scenario.model.Scenario)getDescription());
    }

    /**
	 * This method creates the page control.
	 */
    protected void createPageControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setBackground(parent.getBackground());
        composite.setLayout(new GridLayout(2, false));
        
        createPaletteViewer(composite);
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        gd.widthHint = 125;
        getPaletteViewer().getControl().setLayoutData(gd);
        
        createGraphicalViewer(composite);
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 275;
        getViewer().getControl().setLayoutData(gd);
    }

    /**
     * Creates the GraphicalViewer on the specified <code>Composite</code>.
     * @param parent the parent composite
     */
    private void createGraphicalViewer(Composite parent) {
        m_viewer = new ScrollingGraphicalViewer();
        m_viewer.createControl(parent);

        // configure the viewer
        m_viewer.getControl().setBackground(parent.getBackground());
        m_viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        m_viewer.setKeyHandler(new GraphicalViewerKeyHandler(m_viewer));

        // hook the viewer into the editor
        registerEditPartViewer(m_viewer);

        // configure the viewer with drag and drop
        configureEditPartViewer(m_viewer);
        
        // initialize the viewer with input
        m_viewer.setEditPartFactory(
        		new org.savara.tools.scenario.designer.parts.ScenarioEditPartsFactory(this));

        m_viewer.setContents(getScenario());
        
        focus(getScenario());
    }
    
    /**
     * Refresh the editor page without a new input.
     * 
     */
    public void refresh() {
    	EditPart ep=getFocusEditPart();
    	
    	if (ep != null) {
    		ep.refresh();
    	} else {
    		resetViewer();
    	}
    }
    
    public Object getFocusComponent() {
    	Object ret=null;
    	
    	java.util.List parts=m_viewer.getSelectedEditParts();
    	if (parts.size() == 1) {
    		EditPart part=(EditPart)parts.get(0);
    		
    		if (part instanceof ScenarioBaseEditPart) {
    			ret = ((ScenarioBaseEditPart)part).getModel();
    		}
    	}
    	
    	return(ret);
    }

    public EditPart getFocusEditPart() {
    	EditPart ret=null;
    	
    	java.util.List parts=m_viewer.getSelectedEditParts();
    	if (parts.size() == 1) {
    		ret=(EditPart)parts.get(0);
    	}
    	
    	return(ret);
    }

    protected void resetViewer() {
    	if (m_viewer != null &&
    			getScenario() != null) {
    		m_viewer.setContents(getScenario());
    	}
    }
    
    /**
     * This method returns the GraphicalViewer.
     * 
     * @return The graphical viewer
     * @see com.ibm.itso.sal330r.gefdemo.editor.AbstractEditorPage#getGraphicalViewerForZoomSupport()
     */
    public org.eclipse.gef.EditPartViewer getViewer() {
        return(m_viewer);
    }
    
    /**
     * This method returns the context menu provider.
     * 
     * @param viewer The edit part viewer
     * @return The context menu provider
     */
    protected ContextMenuProvider createContextMenuProvider(EditPartViewer viewer) {
    	return(new EditorContextMenuProvider(viewer,
                getEditor().getActionRegistry()));
    }
    
    /**
     * This method returns a transfer drop target listener.
     * 
     * @param viewer The edit part viewer
     * @return The transfer drop target listener
     */
    protected org.eclipse.jface.util.TransferDropTargetListener createTransferDropTargetListener(EditPartViewer viewer) {
    	return(new ScenarioTemplateTransferDropTargetListener(viewer));
    }

    /**
     * This method returns the title.
     * 
     * @return The title
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    public String getTitle() {
        return("Edit the test scenario");
    }
        
	/**
	 * This method focuses the editor page on the supplied
	 * component.
	 * 
	 * @param component The component
	 */
    public void focus(Object scenarioObject) {
    	
    	/*
    	if ((component instanceof org.pi4soa.scenario.ScenarioObject) == false) {
    		return;
    	}
    	
    	org.pi4soa.scenario.ScenarioObject scenarioObject=
    		(org.pi4soa.scenario.ScenarioObject)component;
    	*/
    	
    	resetViewer();
    	
    	m_viewer.flush();
    	
    	focusOnEditPart(scenarioObject);
    }
    
    /**
     * This method finds an editpart associated with the
     * supplied scenario object.
     * 
     * @param scenarioObject The scenario object
     * @return The edit part
     */
    protected org.eclipse.gef.EditPart findEditPart(Object scenarioObject) {
    	org.eclipse.gef.EditPart ret=null;
    	
    	if (scenarioObject instanceof org.savara.scenario.model.Link) {
	    	if (m_viewer.getContents() instanceof ScenarioBaseEditPart) {
	    		ret = ((ScenarioBaseEditPart)m_viewer.getContents()).
	    					findEditPartForModel(scenarioObject);
	    	}
    		
    	} else {
    		ret = m_viewer.getContents();
	    	if (ret instanceof ScenarioBaseEditPart) {
	    		
	    		ret = findSelectedEditPart((ScenarioBaseEditPart)ret,
	    							scenarioObject);
	    	}
    	}
    	
    	return(ret);
    }
    	
    /**
     * This method focuses on the editpart associated with the
     * supplied scenario object, and moves the viewport (if
     * necessary) to ensure the editpart is visible.
     * 
     * @param scenarioObject The scenario object
     * @return The edit part
     */
    protected org.eclipse.gef.EditPart focusOnEditPart(Object scenarioObject) {
    	org.eclipse.gef.EditPart ret=findEditPart(scenarioObject);
    	
    	if (ret != null) {
    		m_viewer.select(ret);
    	}

    	if (ret instanceof ScenarioEditPart) {
    		
	    	FigureCanvas canvas=(FigureCanvas)
						m_viewer.getControl();

			canvas.scrollSmoothTo(0, 0);
			
    	} else if (ret instanceof GraphicalComponent) {
			GraphicalComponent ep=(GraphicalComponent)ret;
			int x=0;
			int y=0;
			int width=ep.getComponentBounds().width;
			int height=ep.getComponentBounds().height;
			
			while (ep != null) {
				Rectangle r=ep.getComponentBounds();
				
				x += r.x;
				y += r.y;
				
				if (ep.getComponentParent() instanceof GraphicalComponent) {
					ep = (GraphicalComponent)ep.getComponentParent();
				} else {
					ep = null;
				}
			}
	    	
	    	FigureCanvas canvas=(FigureCanvas)
						m_viewer.getControl();
	    	
	    	Viewport port = canvas.getViewport();
	    	Dimension viewportSize = port.getClientArea().getSize();
	    	
	    	x -= (viewportSize.width - width)/2;
	    	y -= (viewportSize.height - height)/2;
	    	
	    	canvas.scrollSmoothTo(x, y);
		}
		
		return(ret);
    }
    
	public void startSimulation() {
		m_simulationRunning = true;		
	}
	
	public void resetSimulation() {
    	if (m_viewer.getContents() instanceof SimulationEntity) {
    		((SimulationEntity)m_viewer.getContents()).reset();
    	}
    	
    	m_log = new StringBuffer();
    	
		m_simulationRunning = false;

    	if (m_viewer.getContents() instanceof SimulationEntity) {
    		focusOnEditPart(((SimulationEntity)m_viewer.getContents()));
    	}
	}
	
	public boolean isSimulationRunning() {
    	return(m_simulationRunning);
	}
	
	public SimulationEntity getSimulationEntity(Object model, boolean focus) {
		SimulationEntity ret=null;
		
		m_simulationRunning = true;
		
		org.eclipse.gef.EditPart ep=null;
		
		if (focus) {
			ep = focusOnEditPart(model);
		} else {
			ep = findEditPart(model);
		}
		
		if (ep instanceof SimulationEntity) {
			ret = (SimulationEntity)ep;
		}
		
		return(ret);
	}
        
	public void appendLogEntry(String results) {
		m_log.append(results);
	}
	
	public String getLogEntry(int start, int end) {
		String ret=null;
		
		if (start >= 0 && end >= 0 && end >= start &&
				end < m_log.length()) {
			ret = m_log.substring(start, end);
		}
		
		return(ret);
	}
	
    public String getLogEntry(Object scenarioObject) {
    	String ret=null;
    	
    	org.eclipse.gef.EditPart ep=findEditPart(scenarioObject);
    	
    	if (ep instanceof SimulationEntity) {
    		ret = getLogEntry(((SimulationEntity)ep).getLogStartPosition(),
    				((SimulationEntity)ep).getLogEndPosition());
    	}
    	
    	return(ret);
    }
    
    /**
     * This method focuses the environment on the supplied
     * URL and region name.
     * 
     * @param scenarioURL The scenario path
     * @param regionName The optional region name
     */
    public void focus(String scenarioURL, String regionName) {
    	
		if (getEditorInput() instanceof FileEditorInput) {
			FileEditorInput fei=(FileEditorInput)getEditorInput();
			
			org.eclipse.core.resources.IFile modelFile=
				fei.getFile().getParent().getFile(
						new org.eclipse.core.runtime.Path(scenarioURL));
			
			IWorkbenchWindow workbenchWindow=
					getSite().getWorkbenchWindow();
			try {
				org.eclipse.ui.IEditorPart editorPart=
					workbenchWindow.getActivePage().openEditor
					(new FileEditorInput(modelFile),
							workbenchWindow.getWorkbench().
							getEditorRegistry().
							getDefaultEditor(modelFile.getFullPath().toString()).getId());
				
				if (regionName != null &&
						editorPart instanceof ScenarioDesigner) {
					ScenarioDesigner designer=(ScenarioDesigner)editorPart;
					
					/* TODO: GPB: need to recursively check for group by name
					org.savara.scenario.model.Group region=
							designer.getScenario().getRegion(regionName);
					
					if (region != null) {
						designer.getScenarioEditorPage().focus(region);
					}
					*/
				}
			} catch (PartInitException exception) {
				MessageDialog.openError(workbenchWindow.getShell(),
						"Open Editor", exception.getMessage());
			}
		}				
    }


    private GraphicalViewer m_viewer=null;    
    private boolean m_simulationRunning=false;
    private StringBuffer m_log=new StringBuffer();
}
