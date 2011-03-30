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
 * 6 Feb 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import org.eclipse.core.resources.*;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.*;
import org.eclipse.gef.commands.*;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This is an abstract base class for all Designer
 * property sections. It provides management of errors
 * associated with the properties.
 */
public abstract class AbstractDesignerPropertySection 
				extends AbstractPropertySection
				implements java.beans.PropertyChangeListener {

	public AbstractDesignerPropertySection(String propName,
						String displayName, String label,
						int start, int end, int top, int textGap) {
		m_propertyName = propName;
		m_displayName = displayName;
		m_label = label;
		m_startPercentage = start;
		m_endPercentage = end;
		m_topPercentage = top;
		m_textGap = textGap;
	}

    protected String getPropertyName() {
    	return(m_propertyName);
    }
    
    protected String getDisplayName() {
    	return(m_displayName);
    }
    
    protected String getLabel() {
    	return(m_label);
    }
    
    protected IPropertyDescriptor getPropertyDescriptor() {
    	return(m_propertyDescriptor);
    }
    
    protected IPropertySource getPropertySource() {
    	return(m_propertySource);
    }
    
    protected void initPropertyDescriptor() {
    	IPropertyDescriptor[] pds=m_propertySource.getPropertyDescriptors();
    	
    	for (int i=0; m_propertyDescriptor == null &&
    					i < pds.length; i++) {
    		if (pds[i].getDisplayName().equals(getDisplayName())) {
    			m_propertyDescriptor = pds[i];
    		}
    	}
    	
    	if (m_propertyDescriptor == null) {
    		logger.severe("Property descriptor could not be found for '"+
    				getDisplayName()+"'");
    	}
    }
    
    public void setTopPercentage(int percentage) {
    	m_topPercentage = percentage;
    }
    
    public int getTopPercentage() {
    	return(m_topPercentage);
    }
 
    public void setTextGap(int textGap) {
    	m_textGap = textGap;
    }
    
    public int getTextGap() {
    	return(m_textGap);
    }
    
    public void setStartPercentage(int start) {
    	m_startPercentage = start;
    }
    
    public int getStartPercentage() {
    	return(m_startPercentage);
    }

    public void setEndPercentage(int end) {
    	m_endPercentage = end;
    }
    
    public int getEndPercentage() {
    	return(m_endPercentage);
    }

    public void setToolTip(String tooltip) {
    	m_tooltip = tooltip;
    }
    
    protected String getToolTip() {
    	return(m_tooltip);
    }
    
    public boolean isCreateForm() {
		return(true);
	}
	
    public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
        
        if (aTabbedPropertySheetPage instanceof DesignerTabbedPropertySheetPage) {
        	m_tabbedPropertySheetPage =
        		(DesignerTabbedPropertySheetPage)aTabbedPropertySheetPage;
        }
    }
    
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
               
        if (selection instanceof IStructuredSelection) {
	        Object input = ((IStructuredSelection) selection).getFirstElement();

		    if (input instanceof EditPart) {
		    	input = ((EditPart)input).getModel();
		    }
	        
	        m_propertySource = ViewSupport.getPropertySource(input);
	        
	        if (m_propertySource != null) {
	        	
	        	initPropertyDescriptor();
	        	
	        	if (getWidget() != null) {
	        		refresh();
	        	}
	        } else {
	        	logger.severe("PROPERY NOT FOUND FOR: "+input);
	        }
        }
    }
    
    /**
     * This method returns the principle widget used for obtaining
     * the value of the property.
     * 
     * @return The widget
     */
    protected abstract org.eclipse.swt.widgets.Control getWidget();
    
    /*
    public void refresh() {
    	boolean f_processed=false;
    	String propertySourceURI=null;
    	
        if (m_propertySource != null) {
        	propertySourceURI = org.pi4soa.common.util.EMFUtil.getURI(
					m_propertySource.getElement());
        }
        
    	if (m_tabbedPropertySheetPage != null &&
    			propertySourceURI != null) {
    		IResource res=m_tabbedPropertySheetPage.getResource();
    		String tooltip=getToolTip();
    		
    		if (res != null) {
    			try {
    				IMarker[] markers=res.findMarkers(ValidationDefinitions.MARKER_TYPE, true,
    							IResource.DEPTH_INFINITE);
    				
    				for (int i=0; f_processed == false &&
    								i < markers.length; i++) {
    					Object uri=markers[i].getAttribute(ValidationDefinitions.URI_ATTRIBUTE);
    					Object prop=markers[i].getAttribute(ValidationDefinitions.PROPERTY_NAME_ATTRIBUTE);
    					int severity=markers[i].getAttribute(IMarker.SEVERITY, 0);
    					
    					if (uri != null && prop != null &&
    							uri.equals(propertySourceURI) &&
    							prop.equals(getPropertyName())) {
    						
    						// Check property name
    						org.eclipse.swt.graphics.Color color=null;
    						String mesgType="";
    						
    						if (severity == IMarker.SEVERITY_ERROR) {
    							color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    							mesgType = "ERROR: ";
    						} else if (severity == IMarker.SEVERITY_WARNING) {
    							color = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
      							mesgType = "WARNING: ";
       						}
    						
    						if (color != null) {
    							getWidget().setBackground(color);
    						}
    						
    						tooltip += "\r\n"+mesgType+
    								markers[i].getAttribute(IMarker.MESSAGE);

    						f_processed = true;
     					}
    				}
    			} catch(Exception e) {
    				
    			}
    		}
    		
			if (f_processed == false) {
				org.eclipse.swt.graphics.Color color=
					Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
				getWidget().setBackground(color);
			}
			
			getWidget().setToolTipText(tooltip);
    	}
	}
	*/
    
    /**
     * If property associated with this text property section
     * has been changed, then we need to update the display.
     * 
     * @param evt The property change event
     */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(getPropertyName())) {
			refresh();
		}
	}

	protected Object getDefaultValue() {
		return(null);
	}

	protected CommandStack getCommandStack() {
		return(m_tabbedPropertySheetPage.getCommandStack());
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.designer.editor.properties");	

    private String m_propertyName=null;
    private String m_displayName=null;
    private String m_label=null;

    private DesignerTabbedPropertySheetPage m_tabbedPropertySheetPage=null;
    private IPropertySource m_propertySource;
    private IPropertyDescriptor m_propertyDescriptor=null;
    private int m_topPercentage=0;
	private int m_textGap=100;
	private int m_startPercentage=0;
	private int m_endPercentage=50;
	private String m_tooltip=null;
}
