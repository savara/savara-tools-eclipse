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
 * 16 Jan 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

import org.eclipse.ui.views.properties.tabbed.*;
import org.eclipse.gef.*;

/**
 * This class maps a graphical component to the underlying
 * model class represented by the component.
 */
public class TypeMapper extends AbstractTypeMapper {

	/**
	 * This method returns the class associated with the
	 * supplied object.
	 */
	public Class mapType(Object object) {
	    if (object instanceof EditPart) {
	        return ((EditPart) object).getModel().getClass();
	    }
	    
	    return super.mapType(object);
	}
}

