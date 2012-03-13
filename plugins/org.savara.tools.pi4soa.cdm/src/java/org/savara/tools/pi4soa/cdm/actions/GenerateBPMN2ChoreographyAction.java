/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.tools.pi4soa.cdm.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.savara.bpmn2.model.TDefinitions;
import org.savara.bpmn2.util.BPMN2ModelUtil;
import org.savara.common.model.annotation.Annotation;
import org.savara.common.model.annotation.AnnotationDefinitions;
import org.savara.pi4soa.cdm.parser.CDMProtocolParser;
import org.savara.protocol.util.JournalProxy;
import org.savara.tools.common.logging.FeedbackHandlerDialog;
import org.savara.tools.pi4soa.cdm.osgi.Activator;
import org.scribble.common.logging.Journal;
import org.scribble.common.resource.FileContent;
import org.scribble.protocol.model.ProtocolModel;

/**
 * This class implements the action to generate a BPMN2 choreography model
 * from a CDM choreography.
 */
public class GenerateBPMN2ChoreographyAction implements IObjectActionDelegate {

	private ISelection m_selection=null;
    private IWorkbenchPart m_targetPart=null;

	public GenerateBPMN2ChoreographyAction() {
	}

	/**
	 * This method implements the action's run method.
	 * 
	 * @param action The action
	 */
	public void run(IAction action) {
		if (m_selection instanceof StructuredSelection) {
			StructuredSelection sel=(StructuredSelection)m_selection;
			
			IResource res=(IResource)sel.getFirstElement();
			
			if (res instanceof IFile) {
				try {
					org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator gen=
							new org.savara.bpmn2.generation.choreo.ProtocolToBPMN2ChoreoModelGenerator();
					
					FeedbackHandlerDialog handler=new FeedbackHandlerDialog(Display.getCurrent().getActiveShell());			
	    			Journal journal=new JournalProxy(handler);
	    			
	    			CDMProtocolParser parser=new CDMProtocolParser();
	    			
	    			FileContent content=new FileContent(((IFile)res).getLocation().toFile());
	    			
	    			ProtocolModel pm=parser.parse(null, content, journal);

	    			java.util.Map<String,Object> models=gen.generate(pm, handler, null);
	    			
	    			if (handler.hasErrors()) {
	    				handler.show();
	    			} else {
					
		    			for (String modelName : models.keySet()) {
		    				Object model=models.get(modelName);
		    				
		    				if (model instanceof TDefinitions) {
		    					TDefinitions defns=(TDefinitions)model;
		    					
								// Obtain any namespace prefix map
								java.util.Map<String, String> prefixes=
										new java.util.HashMap<String, String>();
								
								java.util.List<Annotation> list=
										AnnotationDefinitions.getAnnotations(pm.getProtocol().getAnnotations(),
												AnnotationDefinitions.TYPE);
									
								for (Annotation annotation : list) {
									if (annotation.getProperties().containsKey(AnnotationDefinitions.NAMESPACE_PROPERTY) &&
											annotation.getProperties().containsKey(AnnotationDefinitions.PREFIX_PROPERTY)) {
										prefixes.put((String)annotation.getProperties().get(AnnotationDefinitions.NAMESPACE_PROPERTY),
												(String)annotation.getProperties().get(AnnotationDefinitions.PREFIX_PROPERTY));
									}
								}
								
								java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
								
								BPMN2ModelUtil.serialize(defns, baos, prefixes,
										GenerateBPMN2ChoreographyAction.class.getClassLoader());
								
								java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(baos.toByteArray());
								
								IFile modelFile=((IFile) res).getParent().getFile(new Path(modelName));
								
								if (!modelFile.exists()) {
									modelFile.create(bais, false, null);
								} else {
									modelFile.setContents(bais, true, false, null);
								}
								
								bais.close();
		    				}
		    			}
	    			}
					
				} catch(Exception e) {
					Activator.logError("Failed to generate BPMN2 choreography", e);
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
