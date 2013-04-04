/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.bpmn2.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.savara.bpmn2.model.TDefinitions;
import org.savara.bpmn2.model.util.BPMN2ModelUtil;
import org.savara.common.logging.FeedbackHandler;
import org.savara.common.logging.MessageFormatter;
import org.savara.protocol.aggregator.ProtocolAggregatorFactory;
import org.savara.protocol.util.JournalProxy;
import org.savara.protocol.util.ProtocolServices;
import org.savara.tools.common.logging.FeedbackHandlerDialog;
import org.scribble.common.resource.Content;
import org.scribble.common.resource.DefaultResourceLocator;
import org.scribble.common.resource.ResourceContent;
import org.scribble.protocol.DefaultProtocolContext;
import org.scribble.protocol.model.ProtocolModel;


/**
 * This class implements the action to generate a BPMN2 choreography
 * from BPMN2 process models.
 */
public class CreateChoreographyAction implements IObjectActionDelegate {

	private static final Logger LOG=Logger.getLogger(CreateChoreographyAction.class.getName());
	
	private ISelection _selection=null;
    private IWorkbenchPart _targetPart=null;
    
	private static org.savara.protocol.aggregator.ProtocolAggregator PA=
				ProtocolAggregatorFactory.createProtocolAggregator();
	private static org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator P2CMG=
			new org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator();	

	/**
	 * The default constructor.
	 */
	public CreateChoreographyAction() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(IAction action) {
		if (_selection instanceof StructuredSelection) {
			StructuredSelection sel=(StructuredSelection)_selection;
			
			FeedbackHandlerDialog handler=new FeedbackHandlerDialog(_targetPart.getSite().getShell());
			
			IContainer container=null;
			java.util.List<ProtocolModel> localModels=new java.util.Vector<ProtocolModel>();
			
			for (Object res : sel.toList()) {			
				if (res instanceof IFile) {
					if (container == null) {
						container = ((IFile)res).getParent();
					}
					
					try {
						Content content=new ResourceContent(((IFile)res).getRawLocationURI());
						
						DefaultProtocolContext context=
								new DefaultProtocolContext(ProtocolServices.getParserManager(),
									new DefaultResourceLocator(((IFile)res).getRawLocation().toFile().getParentFile()));
						
						ProtocolModel model=
								ProtocolServices.getParserManager().parse(context, content,
										new JournalProxy(handler));
						
						if (model == null || !model.isLocated()) {
							handler.error(MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
									"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00008",
									((IFile)res).getRawLocationURI()), null);
						} else {
							localModels.add(model);
						}
					} catch (Exception e) {
						e.printStackTrace();
						
						String mesg=MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
								"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00007",
								((IFile)res).getRawLocationURI());
						
						handler.error(mesg, null);
						
						LOG.log(Level.SEVERE, mesg, e);
					}
				}
			}
			
			if (handler.hasErrors()) {
				handler.show();
			} else {
				InputDialog dialog=new InputDialog(_targetPart.getSite().getShell(),
						"Choreography Model", "Enter the model name",
						null, null);
				
				if (dialog.open() == InputDialog.OK) {
					String modelName=dialog.getValue();
					
					// If multiple local models
					ProtocolModel globalModel=PA.aggregateGlobalModel(modelName, null,
										localModels, handler);
					
					if (globalModel != null) {
						generateBPMN2ChoreographyModel(container, globalModel, handler);
					} else {
						handler.error(MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
								"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00006"), null);
					}
					
					if (handler.hasErrors()) {
						handler.show();
					}
				}
			}
		}
	}
	
	protected void generateBPMN2ChoreographyModel(IContainer container, ProtocolModel choreo,
						FeedbackHandler handler) {
		java.util.Map<String,Object> models=P2CMG.generate(choreo, handler, null);
		
		if (models != null && models.size() > 0) {
			for (String modelName : models.keySet()) {
				Object model=models.get(modelName);
				
				if (model instanceof TDefinitions) {
					TDefinitions defns=(TDefinitions)model;
					
					try {
						java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
						
						BPMN2ModelUtil.serialize(defns, baos,
								CreateChoreographyAction.class.getClassLoader());
						
						java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(baos.toByteArray());
						
						IFile modelFile=container.getFile(new Path(modelName));
						
						if (!modelFile.exists()) {
							modelFile.create(bais, false, null);
						} else {
							modelFile.setContents(bais, true, false, null);
						}
						
						bais.close();
					} catch(Exception e) {
						handler.error("Failed to generate BPMN2 choreography model", null);
					}
				}
			}
		}
	}

	/**
	 * This method indicates that the selection has changed.
	 * 
	 * @param action The action
	 * @param selection The selection
	 */
	public void selectionChanged(IAction action,
            ISelection selection) {
		_selection = selection;
	}

	/**
	 * This method sets the currently active workbench part.
	 * 
	 * @param action The action
	 * @param targetPart The active workbench part
	 */
	public void setActivePart(IAction action,
            IWorkbenchPart targetPart) {
		_targetPart = targetPart;
	}
	
	/**
	 * This method is used to report a warning.
	 * 
	 * @param mesg The warning message
	 */
	public void warn(String mesg) {
		
		MessageBox mbox=new MessageBox(_targetPart.getSite().getShell(),
				SWT.ICON_WARNING|SWT.OK);
		mbox.setMessage(mesg);
		mbox.open();
	}
}
