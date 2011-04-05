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
package org.savara.tools.scenario.designer.editor.properties;

import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.savara.tools.scenario.designer.commands.AddToListPropertyCommand;
import org.savara.tools.scenario.designer.commands.MoveDownListPropertyCommand;
import org.savara.tools.scenario.designer.commands.MoveUpListPropertyCommand;
import org.savara.tools.scenario.designer.commands.RemoveFromListPropertyCommand;

/**
 * This is a default property section implementation used for
 * list based properties.
 */
public abstract class AbstractListPropertySection extends AbstractDesignerPropertySection {

	private static Logger logger = Logger.getLogger(AbstractListPropertySection.class.getName());
	
    private java.util.List<Object> m_values=null;
    private List m_widget=null;
    private Button m_addButton=null;
    private Button m_removeButton=null;
    private Button m_upButton=null;
    private Button m_downButton=null;

	public AbstractListPropertySection(String property, String displayName,
				String label, int start, int end) {
		super(property, displayName, label, start, end, 0, 100);
	}

    /**
     * This method returns the principle widget used for obtaining
     * the value of the property.
     * 
     * @return The widget
     */
    protected org.eclipse.swt.widgets.Control getWidget() {
    	return(m_widget);
    }
    
	@SuppressWarnings("unchecked")
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);

        m_values = (java.util.List<Object>)getPropertySource().getPropertyValue(getPropertyDescriptor().getId());
    }
    
	public void createControls(Composite parent,
            TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);
        Composite composite = parent;
        
        if (isCreateForm()) {
        	composite = getWidgetFactory().createFlatFormComposite(parent);
        }
        
        FormData data;

       	m_widget = getWidgetFactory().createList(composite, SWT.BORDER|SWT.V_SCROLL); //$NON-NLS-1$

        if (getToolTip() != null) {
        	m_widget.setToolTipText(getToolTip());
        }
        
        data = new FormData();
        data.left = new FormAttachment(getStartPercentage(), getTextGap());
        
       	data.right = new FormAttachment(getEndPercentage(), -80);        	
        data.top = new FormAttachment(getTopPercentage(), ITabbedPropertyConstants.VSPACE);
        
        data.width = 300;
        data.height = 150;
        
        m_widget.setLayoutData(data);
        
        CLabel labelLabel = getWidgetFactory()
            .createCLabel(composite, getLabel()+":"); //$NON-NLS-1$
        
        if (getToolTip() != null) {
        	labelLabel.setToolTipText(getToolTip());
        }
        
        data = new FormData();
        data.left = new FormAttachment(getStartPercentage(), 0);
        data.right = new FormAttachment(m_widget,
            -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(m_widget, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        
        m_addButton = getWidgetFactory().createButton(composite, "Add", SWT.PUSH);
        
        data = new FormData();
        
        data.width = 100;
        
        data.left = new FormAttachment(m_widget, 5);
       	data.right = new FormAttachment(getEndPercentage(), 0);        	
        //data.right = new FormAttachment(m_widget,
         //       -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        //data.top = new FormAttachment(m_widget, 40, SWT.CENTER);
        m_addButton.setLayoutData(data);
        
        m_addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                
            	Object newValue=createNewObject();
            	
            	if (newValue != null) {
            		
            		// Perform add to list command
                	AddToListPropertyCommand command=
            			new AddToListPropertyCommand();
	            	command.setPropertySource(getPropertySource());
	            	command.setPropertyDescriptor(getPropertyDescriptor());
	            	command.setValue(newValue);
	            	
	            	getCommandStack().execute(command);
            	}
            	
            	refresh();
            	
            	checkStatus();
            }
        });
        
        m_removeButton = getWidgetFactory().createButton(composite, "Remove", SWT.PUSH);
        
        data = new FormData();
        data.width = 100;
        data.left = new FormAttachment(m_widget, 5);
       	data.right = new FormAttachment(getEndPercentage(), 0);        	
        //data.right = new FormAttachment(m_widget,
         //       -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(m_addButton, 1);
        //data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        //data.top = new FormAttachment(m_widget, 40, SWT.CENTER);
        m_removeButton.setLayoutData(data);
        
        m_removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
             	
            	if (m_widget.getSelectionIndex() != -1) {
            		
            		// Perform remove from list command
                	RemoveFromListPropertyCommand command=
            			new RemoveFromListPropertyCommand();
	            	command.setPropertySource(getPropertySource());
	            	command.setPropertyDescriptor(getPropertyDescriptor());
	            	command.setIndex(m_widget.getSelectionIndex());
	            	
	            	getCommandStack().execute(command);
            	}
            	
            	refresh();
            	
            	checkStatus();
            }
        });
        
        m_upButton = getWidgetFactory().createButton(composite, "Up", SWT.PUSH);
        
        data = new FormData();
        data.width = 100;
        data.left = new FormAttachment(m_widget, 5);
       	data.right = new FormAttachment(getEndPercentage(), 0);        	
        //data.right = new FormAttachment(m_widget,
         //       -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(m_removeButton, 1);
        //data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        //data.top = new FormAttachment(m_widget, 40, SWT.CENTER);
        m_upButton.setLayoutData(data);
        
        m_upButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                
            	int index=m_widget.getSelectionIndex();
            	
            	if (index != -1) {
            		
            		// Perform up list command
                	MoveUpListPropertyCommand command=
            			new MoveUpListPropertyCommand();
	            	command.setPropertySource(getPropertySource());
	            	command.setPropertyDescriptor(getPropertyDescriptor());
	            	command.setIndex(m_widget.getSelectionIndex());
	            	
	            	getCommandStack().execute(command);
            	}
            	
            	refresh();
            	
            	m_widget.select(index-1);
            	
            	checkStatus();
            }
        });
        
        m_downButton = getWidgetFactory().createButton(composite, "Down", SWT.PUSH);
        
        data = new FormData();
        data.width = 100;
        //data.left = new FormAttachment(getStartPercentage(), -30);
        data.left = new FormAttachment(m_widget, 5);
       	data.right = new FormAttachment(getEndPercentage(), 0);        	
        //data.right = new FormAttachment(m_widget,
        //        -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(m_upButton, 1);
        //data.top = new FormAttachment(m_widget, 40, SWT.CENTER);
        m_downButton.setLayoutData(data);
        
        m_downButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                
            	int index=m_widget.getSelectionIndex();
            	
            	if (index != -1) {
            		
            		// Perform down list command
                	MoveDownListPropertyCommand command=
            			new MoveDownListPropertyCommand();
	            	command.setPropertySource(getPropertySource());
	            	command.setPropertyDescriptor(getPropertyDescriptor());
	            	command.setIndex(m_widget.getSelectionIndex());
	            	
	            	getCommandStack().execute(command);
            	}
            	
            	refresh();
            	
            	m_widget.select(index+1);
            	
				checkStatus();
            }
        });

        checkStatus();
        
        // Create listener for list selection
        m_widget.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			public void widgetSelected(SelectionEvent arg0) {
				checkStatus();
			}
        });
	}
	
	protected abstract Object createNewObject();
	
	protected void checkStatus() {
		
		if (m_widget.getSelectionCount() > 0) {
			m_removeButton.setEnabled(true);
			
			int index=m_widget.getSelectionIndex();
			
			m_upButton.setEnabled(index > 0);
			m_downButton.setEnabled(index < m_widget.getItemCount()-1);
		} else {
			m_removeButton.setEnabled(false);
	        m_upButton.setEnabled(false);
	        m_downButton.setEnabled(false);
		}
	}
    
    public void dispose() {
    	super.dispose();
    	
    	m_widget.dispose();
    	
    	/*
    	if (m_widget != null && m_editable &&
    			 m_text.isDisposed() == false) {
    		m_text.removeModifyListener(m_listener);
    	}
    	*/
    }
    
    protected abstract String getDisplayValue(Object sourceValue);

    public void refresh() {
    	super.refresh();
    	
    	if (m_widget != null) {
	    	int index=m_widget.getSelectionIndex();
	    	
	    	m_widget.removeAll();
	    	
	    	if (m_values != null) {
		    	for (Object sourceValue : m_values) {
		    		String displayValue=getDisplayValue(sourceValue);
		    		
		    		m_widget.add(displayValue);
		    	}
	    	}
	    	
	    	if (index != -1) {
	    		m_widget.select(index);
	    	}
    	}
    	
    	/*
    	Object val=getPropertySource().getPropertyValue(
    					getPropertyDescriptor().getId());
		
		if (val instanceof String &&
				m_text.getText().equals(val) == false) {
			
			if (m_text.isDisposed() == false) {
				
				if (m_editable) {
					m_text.removeModifyListener(m_listener);
				}
				
				m_text.setText((String)val);
				
				if (m_editable) {
					m_text.addModifyListener(m_listener);
				}
			} else {
				logger.severe("TEXT WIDGET HAS BEEN DISPOSED: "+this+" "+m_text);
			}
		}   
		*/ 	
    }
	
	/**
	 * Opens a dialog box under the given parent control and returns the
	 * dialog's value when it closes, or <code>null</code> if the dialog
	 * was cancelled or no selection was made in the dialog.
	 * <p>
	 * This framework method must be implemented by concrete subclasses.
	 * It is called when the user has pressed the button and the dialog
	 * box must pop up.
	 * </p>
	 *
	 * @param cellEditorWindow the parent control cell editor's window
	 *   so that a subclass can adjust the dialog box accordingly
	 * @return the selected value, or <code>null</code> if the dialog was 
	 *   cancelled or no selection was made in the dialog
	 */
    /*
	protected Object openDialogBox() {
		Object ret=null;
		
        m_values = getPropertySource().getValues(getPropertyDescriptor());

        org.eclipse.ui.dialogs.ListSelectionDialog dialog=
			new org.eclipse.ui.dialogs.ListSelectionDialog(
					m_text.getShell(), getPropertySource().getEditableValue(),
					new IStructuredContentProvider() {
						public Object[] getElements(Object inputElement) {
							return(m_values.toArray());
						}
						public void dispose() {
							
						}
						public void inputChanged(Viewer viewer,
		                         Object oldInput,
		                         Object newInput) {
							//m_dirty = true;
						}
					},
					new org.eclipse.jface.viewers.LabelProvider() {
						public String getText(Object elem) {
							return(ViewSupport.getName(elem));
						}
					}, getLabel());
		
		
		
		Object values=getPropertySource().getRawPropertyValue(getPropertyDescriptor());
		
		if (values instanceof java.util.List) {
			dialog.setInitialElementSelections((java.util.List)values);
		} else if (values instanceof String) {
			dialog.setInitialSelections(new Object[]{values});
		}
		
		if (dialog.open() == Window.OK) {
			
			if (m_singleSelection) {
				if (dialog.getResult().length > 0) {
					ret = dialog.getResult()[0];
				} else {
					ret = "";
				}
			} else {
				ret = new java.util.Vector();
				
				for (int i=0; i < dialog.getResult().length; i++) {
					((java.util.Vector)ret).add(dialog.getResult()[i]);
				}
			}
		}

		return(ret);
	}
	*/
	
	protected Object getDefaultValue() {
		return(new java.util.Vector());
	}
}
