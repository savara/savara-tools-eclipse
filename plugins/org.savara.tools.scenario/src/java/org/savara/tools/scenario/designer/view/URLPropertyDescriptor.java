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
 * Feb 22, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * This class provides a URL property descriptor. 
 *
 */
public class URLPropertyDescriptor extends PropertyDescriptor {

	/**
	 * The constructor for the text region property descriptor.
	 * 
	 * @param id The id
	 * @param displayName The display name
	 */
    public URLPropertyDescriptor(Object id, String displayName,
    					Object selectedObject) {
        super(id, displayName);
        
        m_selectedObject = selectedObject;
    }
    
    /**
     * This method returns the cell editor.
     * 
     * @param parent The parent
     * @return The cell editor
     */
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor ret=new FileURLCellEditor(parent,
        				null, m_selectedObject);
        
        if (getValidator() != null) {
            ret.setValidator(getValidator());
        }
        
        return(ret);
    }
    
    private Object m_selectedObject=null;
}
