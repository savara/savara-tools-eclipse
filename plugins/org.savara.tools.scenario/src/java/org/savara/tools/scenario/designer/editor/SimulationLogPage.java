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

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * This class represents the flow based representation of a
 * choreography.
 */
public class SimulationLogPage extends AbstractEditorPage {
	   
    private org.eclipse.swt.browser.Browser m_text=null;

	/**
	 * The constructor for the choreography flow page.
	 * 
	 * @param parent The multipage editor
	 */
	public SimulationLogPage(ScenarioDesigner parent) {
		super(parent, new EditDomain());
	}
	
	/**
	 * This method returns the page name.
	 * 
	 * @return The page name
	 */
	public String getPageName() {
		return("Simulation Log");
	}
	
	/**
	 * This method creates the page control.
	 */
    protected void createPageControl(Composite parent) {
    	m_text = new org.eclipse.swt.browser.Browser(parent, SWT.READ_ONLY);
    }

    /**
     * This method returns the TreeViewer.
     * 
     * @return The tree viewer
     * @see com.ibm.itso.sal330r.gefdemo.editor.AbstractEditorPage#getGraphicalViewerForZoomSupport()
     */
    protected org.eclipse.gef.EditPartViewer getViewer() {
        return(null);
    }
    
    /**
     * This method returns the title.
     * 
     * @return The title
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    public String getTitle() {
        return("Simulation logs");
    }
    
    /**
     * Refresh the editor page without a new input.
     * 
     */
    public void refresh() {    	
    }
    
	/**
	 * This method focuses on the editor page on the supplied
	 * component.
	 */
    public void focus(Object component) {
    }
    
    public void setText(String text) {
    	m_text.setText(text);
    }

	@Override
	protected TransferDropTargetListener createTransferDropTargetListener(
			EditPartViewer viewer) {
		// TODO Auto-generated method stub
		return null;
	}
}
