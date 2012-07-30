/*
 * Copyright 2005-7 Pi4 Technologies Ltd
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
 * Feb 23, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.savara.tools.scenario.designer.parts.ScenarioEditPart;

/**
 * This class provides the 'reset simulation' action implementation.
 *
 */
public class GenerateImageAction extends org.eclipse.gef.ui.actions.SelectionAction {

	public static final String ID = "org.pi4soa.service.test.designer.editor.GenerateImageID";

	/**
	 * Creates a <code>CreateMessageLinksAction</code> and 
	 * associates it with the given workbench part.
	 * @param part the workbench part
	 */
	public GenerateImageAction(IWorkbenchPart part) {
		super(part);
	}
	
	/**
	 * Initializes this action.
	 */
	protected void init() {
		setId(ID);
		setText("Generate Image...");
		
		setImageDescriptor(org.savara.tools.scenario.designer.DesignerImages.getImageDescriptor("GenerateImage.gif"));
	}
	
	/**
	 * Calculates and returns the enabled state of this action.  
	 * @return <code>true</code> if the action is enabled
	 */
	protected boolean calculateEnabled() {
		boolean ret=false;
		
		if (getWorkbenchPart() instanceof ScenarioDesigner &&
				((ScenarioDesigner)getWorkbenchPart()).getScenarioEditorPage().getViewer()
							instanceof GraphicalViewer) {
			ret = true;
		}
		
		return(ret);
	}

    /**
     * Perform this action.
     * 
     */
    public void run() {  
    	save(getWorkbenchPart().getSite().getPage().getActiveEditor(),
    			(GraphicalViewer)((ScenarioDesigner)
    			getWorkbenchPart()).getScenarioEditorPage().getViewer());   	
    }

	public boolean save(IEditorPart editorPart, GraphicalViewer viewer) {
		Assert.isNotNull(editorPart, "null editorPart passed to ImageSaveUtil::save");
		Assert.isNotNull(viewer, "null viewer passed to ImageSaveUtil::save");		
		
		String saveFilePath = getSaveFilePath(editorPart, viewer, -1);
		if( saveFilePath == null ) return false;
		
		int format = SWT.IMAGE_JPEG;
		if( saveFilePath.endsWith(".jpeg") )
			format = SWT.IMAGE_JPEG;
		else if( saveFilePath.endsWith(".gif") )
			format = SWT.IMAGE_GIF;
		else if( saveFilePath.endsWith(".png") )
			format = SWT.IMAGE_PNG;
		else if( saveFilePath.endsWith(".bmp") )
			format = SWT.IMAGE_BMP;
		else if( saveFilePath.endsWith(".ico") )
			format = SWT.IMAGE_ICO;
			
		// GIF currently throughs 'unsupported color depth'
		if( format != SWT.IMAGE_BMP && format != SWT.IMAGE_JPEG 
				/*&& format != SWT.IMAGE_GIF*/
				&& format != SWT.IMAGE_PNG
				&& format != SWT.IMAGE_ICO )
			throw new IllegalArgumentException("Save format not supported");
				
		try {
			saveEditorContentsAsImage(editorPart, viewer, saveFilePath, format);
		} catch (Exception ex) {
			MessageDialog.openError(editorPart.getEditorSite().getShell(), "Save Error", "Could not save editor contents");
			//org.pi4soa.designer.eclipse.Activator.logError("Failed to save image", ex);
			return false;
		}
			
		return true;
	}
	
	private String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer, int format) {		
		FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);
		
		String[] filterExtensions = new String[] {"*.jpeg", "*.bmp", "*.ico", "*.png"/*"*.gif", "*.png"*/};
		if( format == SWT.IMAGE_BMP )
			filterExtensions = new String[] {"*.bmp"};
		else if( format == SWT.IMAGE_JPEG )
			filterExtensions = new String[] {"*.jpeg"};
		else if( format == SWT.IMAGE_GIF )
			filterExtensions = new String[] {"*.gif"};
		else if( format == SWT.IMAGE_PNG )
			filterExtensions = new String[] {"*.png"};
		else if( format == SWT.IMAGE_ICO )
			filterExtensions = new String[] {"*.ico"};
		fileDialog.setFilterExtensions(filterExtensions);		
		
		return fileDialog.open();
	}
	
	private void saveEditorContentsAsImage(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format) {
		/* 1. First get the figure whose visuals we want to save as image.
		 * So we would like to save the rooteditpart which actually hosts all the printable layers.
		 * 
		 * NOTE: ScalableRootEditPart manages layers and is registered graphicalviewer's editpartregistry with
		 * the key LayerManager.ID ... well that is because ScalableRootEditPart manages all layers that
		 * are hosted on a FigureCanvas. Many layers exist for doing different things */
		//ScalableRootEditPart rootEditPart = (ScalableRootEditPart)viewer.getEditPartRegistry().get(LayerManager.ID);
		org.eclipse.gef.editparts.LayerManager lm=
				(org.eclipse.gef.editparts.LayerManager)
				viewer.getEditPartRegistry().get(LayerManager.ID);
		
		IFigure rootFigure = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
		Rectangle rootFigureBounds = null;
		Rectangle additionalBounds = null;
		java.util.List children=null;
		
		if (lm instanceof org.eclipse.gef.RootEditPart) {
			org.eclipse.gef.EditPart ep=((org.eclipse.gef.RootEditPart)lm).getContents();
			
			if (ep instanceof ScenarioEditPart) {
				children = ep.getChildren();
				
				rootFigureBounds = ((ScenarioEditPart)ep).getComponentBoundsWithoutIdentityDetails();
				additionalBounds = ((ScenarioEditPart)ep).getIdentityDetailsBounds();
			}
		}
		
		if (rootFigureBounds == null) {
			rootFigureBounds = rootFigure.getBounds();
		}
		
		/* 2. Now we want to get the GC associated with the control on which all figures are
		 * painted by SWTGraphics. For that first get the SWT Control associated with the viewer on which the
		 * rooteditpart is set as contents */
		Control figureCanvas = viewer.getControl();				
		GC figureCanvasGC = new GC(figureCanvas);		
		
		/* 3. Create a new Graphics for an Image onto which we want to paint rootFigure */
		Image img = new Image(null, rootFigureBounds.width, rootFigureBounds.height);
		GC imageGC = new GC(img);
		imageGC.setBackground(figureCanvasGC.getBackground());
		imageGC.setForeground(figureCanvasGC.getForeground());
		imageGC.setFont(figureCanvasGC.getFont());
		imageGC.setLineStyle(figureCanvasGC.getLineStyle());
		imageGC.setLineWidth(figureCanvasGC.getLineWidth());
		imageGC.setXORMode(figureCanvasGC.getXORMode());
		Graphics imgGraphics = new SWTGraphics(imageGC);
		
		/* 4. Draw rootFigure onto image. After that image will be ready for save */
		rootFigure.paint(imgGraphics);
		
		/* 5. Save image */		
		ImageData[] imgData = new ImageData[1];
		imgData[0] = img.getImageData();
		
		ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = imgData;
		imgLoader.save(saveFilePath, format);
		
		/* release OS resources */
		imageGC.dispose();
		img.dispose();

		if (additionalBounds != null &&
				additionalBounds.width > 0 &&
				additionalBounds.height > 0) {
			/* 3. Create a new Graphics for an Image onto which we want to paint rootFigure */
			int width=additionalBounds.width;
			if (rootFigureBounds.width > width) {
				width = rootFigureBounds.width;
			}
			
			img = new Image(null, width,
					rootFigureBounds.height+additionalBounds.height);
			imageGC = new GC(img);
			imageGC.setBackground(figureCanvasGC.getBackground());
			imageGC.setForeground(figureCanvasGC.getForeground());
			imageGC.setFont(figureCanvasGC.getFont());
			imageGC.setLineStyle(figureCanvasGC.getLineStyle());
			imageGC.setLineWidth(figureCanvasGC.getLineWidth());
			imageGC.setXORMode(figureCanvasGC.getXORMode());
			imgGraphics = new SWTGraphics(imageGC);
			
			/* 4. Draw rootFigure onto image. After that image will be ready for save */
			rootFigure.paint(imgGraphics);
			
			Image copy=new Image(null, additionalBounds.width,
							additionalBounds.height);
			imageGC.copyArea(copy, 0, rootFigureBounds.height);
			
			StringBuffer filepath=new StringBuffer();
			filepath.append(saveFilePath);
			
			int ind=saveFilePath.lastIndexOf('.');
			if (ind != -1) {
				filepath.insert(ind, "-ids");
			} else {
				filepath.append("-ids");
			}
			
			/* 5. Save image */		
			imgData = new ImageData[1];
			imgData[0] = copy.getImageData();
			
			imgLoader = new ImageLoader();
			imgLoader.data = imgData;
			imgLoader.save(filepath.toString(), format);
			
			/* release OS resources */
			figureCanvasGC.dispose();
			imageGC.dispose();
			img.dispose();
			copy.dispose();
			
			// Write message link information in tabular form
			//saveMessageLinkTable(editorPart, filepath.toString(), children);
		}

		figureCanvasGC.dispose();
	}
	
	/*
	protected void saveMessageLinkTable(IEditorPart editorPart,
			String imageFilePath, java.util.List children) {
		String filepath=imageFilePath;
		int ind=filepath.lastIndexOf('.');
		
		if (ind != -1) {
			filepath = imageFilePath.substring(0, ind);
			filepath += ".csv";
		} else {
			filepath += ".csv";
		}
		
		try {
			java.io.FileWriter writer=new java.io.FileWriter(filepath);
			
			writer.write("Ref,Identity Tokens,Identity Values," +
					"Query Expressions,Message File\r\n");
			
			for (int i=0; i < children.size(); i++) {
				
				if (children.get(i) instanceof MessageLinkInfoEditPart) {
					MessageLinkInfo mli=(MessageLinkInfo)
							((MessageLinkInfoEditPart)children.get(i)).getModel();
					
					writer.write(mli.getReference()+","+
							mli.getIdentityTokens()+","+
							mli.getIdentityValues()+","+
							mli.getQueryExpressions()+","+
							mli.getFile()+"\r\n");
				}
			}
			
			writer.flush();
			writer.close();
			
		} catch(Exception e) {
			MessageDialog.openError(editorPart.getEditorSite().getShell(), "Save Error", "Could not save csv file");
			//org.pi4soa.designer.eclipse.Activator.logError("Failed to save csv file", e);
		}
	}
	*/
}
