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

import java.io.ByteArrayOutputStream;

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
import org.savara.bpmn2.generation.process.ProtocolToBPMN2ProcessModelGenerator;
import org.savara.bpmn2.model.TDefinitions;
import org.savara.bpmn2.model.util.BPMN2ModelUtil;
import org.savara.common.logging.FeedbackHandler;
import org.savara.common.logging.MessageFormatter;
import org.savara.common.model.annotation.Annotation;
import org.savara.common.model.annotation.AnnotationDefinitions;
import org.savara.protocol.aggregator.ProtocolAggregatorFactory;
import org.savara.scenario.model.Scenario;
import org.savara.scenario.protocol.ProtocolModelGeneratorFactory;
import org.savara.tools.bpmn2.generator.BPMN2GeneratorImpl;
import org.savara.tools.bpmn2.osgi.Activator;
import org.savara.tools.common.logging.FeedbackHandlerDialog;
import org.scribble.protocol.model.ProtocolModel;


/**
 * This class implements the action to generate service architecture and design
 * BPMN2 models from interaction models (e.g. scenarios).
 */
public class CreateArchitectureDesignAction implements IObjectActionDelegate {

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;
    
	private static org.savara.scenario.protocol.ProtocolModelGenerator PMG=
				ProtocolModelGeneratorFactory.createProtocolModelGenerator();
	private static org.savara.protocol.aggregator.ProtocolAggregator PA=
				ProtocolAggregatorFactory.createProtocolAggregator();
	private static ProtocolToBPMN2ProcessModelGenerator P2PMG=
				new ProtocolToBPMN2ProcessModelGenerator();
	private static org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator P2CMG=
			new org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator();	

	public CreateArchitectureDesignAction() {
	}

	/**
	 * This method implements the action's run method.
	 * 
	 * @param action The action
	 */
	public void run(IAction action) {
		if (m_selection instanceof StructuredSelection) {
			StructuredSelection sel=(StructuredSelection)m_selection;
			
			FeedbackHandlerDialog handler=new FeedbackHandlerDialog(m_targetPart.getSite().getShell());
			
			java.util.Map<String,java.util.List<ProtocolModel>> messageTraces=
						new java.util.HashMap<String,java.util.List<ProtocolModel>>();
			
			IContainer container=null;
			
			for (Object res : sel.toList()) {			
				if (res instanceof IFile) {
					if (container == null) {
						container = ((IFile)res).getParent();
					}
					
					deriveMessageTrace((IFile)res, messageTraces, handler);
				}
			}
			
			if (handler.hasErrors()) {
				handler.show();
			} else {
				InputDialog dialog=new InputDialog(m_targetPart.getSite().getShell(),
						"Architecture & Design Models", "Enter the model name",
						null, null);
				
				if (dialog.open() == InputDialog.OK) {
					String modelName=dialog.getValue();
					
					java.util.List<ProtocolModel> localModels=new java.util.Vector<ProtocolModel>();
					
					for (String roleName : messageTraces.keySet()) {
						ProtocolModel lm=PA.aggregateLocalModel(modelName, messageTraces.get(roleName),
												handler);
						
						if (lm != null) {
							generateBPMN2ProcessModel(container, lm, handler);
						
							localModels.add(lm);
						} else {
							handler.error(MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
									"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00005",
									roleName), null);
						}
					}
					
					// If multiple local models
					ProtocolModel globalModel=PA.aggregateGlobalModel(modelName,
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
	
	protected void generateBPMN2ProcessModel(IContainer container, ProtocolModel lm,
						FeedbackHandler handler) {
		java.util.Map<String,Object> target=P2PMG.generate(lm, handler, null);
		
		if (target != null && target.size() > 0) {
			
			for (String modelName : target.keySet()) {
				Object val=target.get(modelName);
				
				if (val instanceof TDefinitions) {
					TDefinitions process=(TDefinitions)val;
					
					try {
						// Store BPMN2 process
						IFile bpmn2File=container.getFile(new Path(modelName));
						
						bpmn2File.create(null, true,
								new org.eclipse.core.runtime.NullProgressMonitor());
						
						// Obtain any namespace prefix map
						java.util.Map<String, String> prefixes=
								new java.util.HashMap<String, String>();
						
						java.util.List<Annotation> list=
							AnnotationDefinitions.getAnnotations(lm.getProtocol().getAnnotations(),
									AnnotationDefinitions.TYPE);
						
						for (Annotation annotation : list) {
							if (annotation.getProperties().containsKey(AnnotationDefinitions.NAMESPACE_PROPERTY) &&
									annotation.getProperties().containsKey(AnnotationDefinitions.PREFIX_PROPERTY)) {
								prefixes.put((String)annotation.getProperties().get(AnnotationDefinitions.NAMESPACE_PROPERTY),
										(String)annotation.getProperties().get(AnnotationDefinitions.PREFIX_PROPERTY));
							}
						}
						
						//String bpelText=XMLUtils.toText(bpelProcess.getDOMElement());
						ByteArrayOutputStream os=new ByteArrayOutputStream();
						BPMN2ModelUtil.serialize(process, os, prefixes, BPMN2GeneratorImpl.class.getClassLoader());
						
						os.close();
						
						bpmn2File.setContents(new java.io.ByteArrayInputStream(
									os.toByteArray()), true, false,
									new org.eclipse.core.runtime.NullProgressMonitor());
					} catch(Exception e) {
						handler.error("Failed to store BPMN2 process model", null);
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
					
					// Obtain any namespace prefix map
					java.util.Map<String, String> prefixes=
							new java.util.HashMap<String, String>();
					
					java.util.List<Annotation> list=
							AnnotationDefinitions.getAnnotations(choreo.getProtocol().getAnnotations(),
									AnnotationDefinitions.TYPE);
						
					for (Annotation annotation : list) {
						if (annotation.getProperties().containsKey(AnnotationDefinitions.NAMESPACE_PROPERTY) &&
								annotation.getProperties().containsKey(AnnotationDefinitions.PREFIX_PROPERTY)) {
							prefixes.put((String)annotation.getProperties().get(AnnotationDefinitions.NAMESPACE_PROPERTY),
									(String)annotation.getProperties().get(AnnotationDefinitions.PREFIX_PROPERTY));
						}
					}
					
					try {
						java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
						
						BPMN2ModelUtil.serialize(defns, baos, prefixes,
								CreateArchitectureDesignAction.class.getClassLoader());
						
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

	protected void deriveMessageTrace(IFile res,
				java.util.Map<String,java.util.List<ProtocolModel>> messageTraces,
						FeedbackHandler handler) {
		try {
			java.io.InputStream is=res.getContents();
			
			Scenario scn=org.savara.scenario.util.ScenarioModelUtil.deserialize(is);
			
			java.util.Set<ProtocolModel> mtmodels=PMG.generate(scn, handler);
			
			for (ProtocolModel pm : mtmodels) {
				String roleName=pm.getProtocol().getLocatedRole().getName();
				
				java.util.List<ProtocolModel> roleModels=
							messageTraces.get(roleName);
				
				if (roleModels == null) {
					roleModels = new java.util.Vector<ProtocolModel>();
					messageTraces.put(roleName, roleModels);
				}
				
				roleModels.add(pm);
			}
			
		} catch(Exception e) {
			handler.error(MessageFormatter.format(java.util.PropertyResourceBundle.getBundle(
						"org.savara.tools.bpmn2.Messages"), "SAVARA-BPMN2TOOLS-00004",
						((IFile)res).getLocation().toString()), null);
			Activator.logError("Failed to process scenario '"+((IFile)res).getLocation()+"'", e);
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
		m_selection = selection;
	}

	/**
	 * This method sets the currently active workbench part.
	 * 
	 * @param action The action
	 * @param targetPart The active workbench part
	 */
	public void setActivePart(IAction action,
            IWorkbenchPart targetPart) {
		m_targetPart = targetPart;
	}
	
	/**
	 * This method is used to report a warning.
	 * 
	 * @param mesg The warning message
	 */
	public void warn(String mesg) {
		
		MessageBox mbox=new MessageBox(m_targetPart.getSite().getShell(),
				SWT.ICON_WARNING|SWT.OK);
		mbox.setMessage(mesg);
		mbox.open();
	}
}
