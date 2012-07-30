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
 * Jul 14, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.policies;

import org.eclipse.draw2d.BendpointLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;
import org.eclipse.gef.handles.BendpointCreationHandle;
import org.eclipse.gef.handles.BendpointHandle;

/**
 * 
 */
public class LinkSelectionHandlesEditPolicy
				extends SelectionHandlesEditPolicy {

	/**
	 * 
	 */
	public LinkSelectionHandlesEditPolicy() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles()
	 */
	protected java.util.List createSelectionHandles() {
		java.util.List list = new java.util.ArrayList();
		ConnectionEditPart connPart = (ConnectionEditPart)getHost();
		PointList points = getConnection().getPoints();
		for (int i = 0; i < points.size() - 2; i++) {
			BendpointHandle handle = new BendpointCreationHandle(connPart, 0, 
				new BendpointLocator(getConnection(), i + 1));
			list.add(handle);
		}
		return list;
	}

	protected Connection getConnection() {
		return (Connection)getHostFigure();
	}
}
