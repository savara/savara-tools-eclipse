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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.savara.tools.scenario.designer.DesignerDefinitions;
import org.savara.tools.scenario.designer.model.ModelSupport;

/**
 * This class provides the common features associated with all
 * pages in the Choreography Description Editor.
 */
public abstract class AbstractEditorPage extends EditorPart {

    /**
     * Creates a new AbstractEditorPage instance.
     * 
     * @param parent the parent multi page editor
     * @param domain the edit domain
     */
    public AbstractEditorPage(Editor parent,
    				EditDomain domain) {
        m_parent = parent;
        m_domain = domain;
    }
    
    /**
     * Refresh the editor page.
     * 
     * @param input The editor input
     */
    public void refresh(IEditorInput input) {
    	super.setInput(input);
    	
    	getViewer().setContents(getDescription());
    	
    	focus(getDescription());
    }
    
    /**
     * Refresh the editor page without a new input.
     * 
     */
    public void refresh() {
    }
    
    /**
     * This method returns the Description
     * 
     * @return The description
     */
    public Object getDescription() {
    	return(m_parent.getDescription());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public final void doSave(IProgressMonitor monitor) {
        // our policy: delegate saving to the parent
        getEditor().doSave(monitor);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public final void doSaveAs() {
        // our policy: delegate saving to the parent
        getEditor().doSaveAs();
    }

	/**
	 * This method focuses on the editor page on the supplied
	 * component.
	 * 
	 * @param component The component
	 */
    public void focus(Object component) {
    }
    
    /**
     * This method returns the current model component which
     * is the focus of the editor page.
     * 
     * @return The focus component
     */
    public Object getFocusComponent() {
    	return(null);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName() + ": " + getPageName());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public final boolean isDirty() {
        // our policy: delegate saving to the parent
        // note: this method shouldn't get called anyway
        return getEditor().isDirty();
    }

    /**
     * Returns the <code>CommandStack</code> of this editor page.
     * @return the <code>CommandStack</code> of this editor page
     */
    protected final CommandStack getCommandStack()
    {
        return getEditDomain().getCommandStack();
    }

    /**
     * Returns the <code>PaletteRoot</code> this editor page uses.
     * @return the <code>PaletteRoot</code>
     */
    protected PaletteRoot getPaletteRoot() {
        // by default we use the root provided by the multi-page editor
        return(getEditor().getPaletteRoot());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public final boolean isSaveAsAllowed() {
        // our policy: delegate saving to the parent
        return(getEditor().isSaveAsAllowed());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
    	try {
    		getViewer().getControl().setFocus();
    	} catch(Exception e) {
    		if (logger.isLoggable(Level.FINE)) {
    			logger.fine("Failed to set focus: "+e);
    		}
    	}
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public final void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 5;
        layout.numColumns = 1;
        composite.setLayout(layout);
        composite.setBackground(
                parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        composite.setForeground(
            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));

        // label on top
        /*
        Label label =
            new Label(composite, SWT.HORIZONTAL | SWT.SHADOW_OUT | SWT.LEFT);
        label.setText(getTitle());
        label.setFont(
            JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
        label.setBackground(
            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        label.setForeground(
            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
*/
        createBannerControl(composite);
        

        // now the main editor page
        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(new FillLayout());
        composite.setBackground(
            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        composite.setForeground(
            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createPageControl(composite);
    }

    /**
     * Returns the human readable name of this editor page.
     * @return the human readable name of this editor page
     */
    protected abstract String getPageName();

    /**
     * Creates the control of this editor banner.
     * @param parent
     */
    protected void createBannerControl(Composite parent) {
    	
    }

    /**
     * Creates the cotrol of this editor page.
     * @param parent
     */
    protected abstract void createPageControl(Composite parent);

    /**
     * Returns the multi page workflow editor this editor page is contained in.
     * @return the parent multi page editor
     */
    protected final Editor getEditor() {
        return(m_parent);
    }

    /**
     * Returns the edit domain this editor page uses.
     * @return the edit domain this editor page uses
     */
    public final EditDomain getEditDomain() {
        return(m_domain);
    }

    /**
     * Hooks a <code>EditPartViewer</code> to the rest of the Editor.
     * 
     * <p>By default, the viewer is added to the SelectionSynchronizer, 
     * which can be used to keep 2 or more EditPartViewers in sync.
     * The viewer is also registered as the ISelectionProvider
     * for the Editor's PartSite.
     * 
     * @param viewer the viewer to hook into the editor
     */
    protected void registerEditPartViewer(EditPartViewer viewer) {
    	
        // register viewer to edit domain
        getEditDomain().addViewer(viewer);

        // the multi page workflow editor keeps track of synchronizing
        getEditor().getSelectionSynchronizer().addViewer(viewer);

        // add viewer as selection provider
        getSite().setSelectionProvider(viewer);
    }

    /**
     * Configures the specified <code>EditPartViewer</code>.
     * 
     * @param viewer
     */
    protected void configureEditPartViewer(EditPartViewer viewer) {
    	
        // configure the shared key handler
        if (viewer.getKeyHandler() != null) {
            viewer.getKeyHandler().setParent(
                getEditor().getSharedKeyHandler());
        }

        // configure the context menu
        ContextMenuProvider provider = createContextMenuProvider(viewer);
                  
        // TODO: GPB - check if 'flow.editor.contextmenu' maps
        // to anything
        viewer.setContextMenu(provider);
        getSite().registerContextMenu(DesignerDefinitions.DESIGNER_PLUGIN_ID+
        		".flow.editor.contextmenu", provider,
				getSite().getSelectionProvider()); //$NON-NLS-1$

        // enable viewer as drop target for template transfers
        viewer.addDropTargetListener(createTransferDropTargetListener(viewer));
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
    protected abstract org.eclipse.jface.util.TransferDropTargetListener createTransferDropTargetListener(EditPartViewer viewer);

    /**
     * Creates the createPaletteViewer on the specified <code>Composite</code>.
     * @param parent the parent composite
     */
    protected void createPaletteViewer(Composite parent) {
        // create graphical viewer
        m_paletteViewer = new PaletteViewer();
        m_paletteViewer.createControl(parent);

        // configure the viewer
        m_paletteViewer.getControl().setBackground(parent.getBackground());

        // hook the viewer into the EditDomain (only one palette per EditDomain)
        getEditDomain().setPaletteViewer(m_paletteViewer);

        // important: the palette is initialized via EditDomain
        //fancy palette: paletteViewer.setEditPartFactory(new CustomizedPaletteEditPartFactory());
        getEditDomain().setPaletteRoot(getPaletteRoot());

        // enable the palette as source for drag operations
        m_paletteViewer.addDragSourceListener(
            new TemplateTransferDragSourceListener(m_paletteViewer));
    }

    /**
     * Returns the palette viewer.
     * @return the palette viewer
     */
    protected PaletteViewer getPaletteViewer() {
        return(m_paletteViewer);
    }

    /**
     * Returns the viewer of this page.
     * 
     * <p>This viewer is used for example for zoom support 
     * and for the thumbnail in the overview of the outline page.
     * 
     * @return the viewer
     */
    protected abstract EditPartViewer getViewer();
    
    /**
     * This method returns the appropriate adapter for the
     * supplied type.
     * 
     * @param type The type
     * @return The adapter
     */
    public Object getAdapter(Class type) {
    	if (type == GraphicalViewer.class &&
    			getViewer() instanceof GraphicalViewer) {
    		return(getViewer());
    	}
    	return(super.getAdapter(type));
    }
    	
    /**
     * This method recursively scans the editpart hierarchy to
     * locate the editpart associated with the supplied
     * component.
     * 
     * @param editpart The current editpart to traverse
     * @param component The component
     * @return The located editpart, or null if not found
     */
    protected org.eclipse.gef.EditPart findSelectedEditPart(org.eclipse.gef.EditPart editpart,
    						Object component) {
    	org.eclipse.gef.EditPart ret=null;
    	
    	if (component != null && editpart != null) {
    		
    		if (ModelSupport.isSame(editpart.getModel(), component)) {
    			ret = editpart;
    		} else {
    			
    			if (editpart instanceof org.eclipse.gef.GraphicalEditPart) {
    				java.util.List cons=((org.eclipse.gef.GraphicalEditPart)editpart).getSourceConnections();
    				
    				for (int i=0; ret == null && i < cons.size(); i++) {
    					org.eclipse.gef.EditPart subpart=
    							(org.eclipse.gef.EditPart)cons.get(i);
						
    					if (subpart.getModel().equals(component)) {
    						ret = subpart;
    					}
    				}
    			}
    			
    			java.util.List list=editpart.getChildren();
    			java.util.Iterator iter=list.iterator();
    			
    			while (ret == null && iter.hasNext()) {
    				org.eclipse.gef.EditPart subpart=(org.eclipse.gef.EditPart)
								iter.next();
    				
    				ret = findSelectedEditPart(subpart, component);
    			}
    		}
    	}
    	
    	return(ret);
    }

    /**
     * The <code>WorkbenchPart</code> implementation of this 
     * <code>IWorkbenchPart</code> method disposes the title image
     * loaded by <code>setInitializationData</code>. Subclasses may extend.
     */
    public void dispose() {
    	super.dispose();
    }

    private static Logger logger = Logger.getLogger("org.pi4soa.designer.editor");	

    private final Editor m_parent;
    private final EditDomain m_domain;
    private PaletteViewer m_paletteViewer=null;
}
