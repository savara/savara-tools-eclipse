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
package org.savara.tools.scenario.designer.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.draw2d.ConnectionAnchor;

/**
 * This class provides a figure for the simple type.
 */
public class MessageEventFigure extends org.eclipse.draw2d.Figure {

	public MessageEventFigure() {
		m_connectionAnchor = new org.eclipse.draw2d.ChopboxAnchor(this);
	}
	
	protected boolean useLocalCoordinates() {
		return(true);
	}
	
	/**
	 * Sets the selection state of this SimpleActivityLabel
	 * @param b true will cause the label to appear selected
	 */
	public void setSelected(boolean b) {
		selected = b;
		repaint();
	}

	/**
	 * Sets the focus state of this SimpleActivityLabel
	 * @param b true will cause a focus rectangle to be drawn around the text of the Label
	 */
	public void setFocus(boolean b) {
		hasFocus = b;
		repaint();
	}

	public ConnectionAnchor getConnectionAnchor() {
		return(m_connectionAnchor);
	}
	
	public void setErrorExpected(Boolean exc) {
		m_errorExpected = exc;
	}
	
	protected boolean isErrorExpected() {
		boolean ret=false;
		
		if (m_errorExpected != null) {
			ret = m_errorExpected.booleanValue();
		}
		
		return(ret);
	}
	
	protected Color getBoundaryColor() {
		Color ret=ColorConstants.gray;
		
		if (isErrorExpected()) {
			ret = ColorConstants.red;
		}		
		
		return(ret);
	}
	
	protected int getBoundaryWidth() {
		int ret=1;
		
		if (isErrorExpected()) {
			ret = 2;
		}
		
		return(ret);
	}
	
	protected Color getFillColor() {
		Color ret=ColorConstants.menuBackground;
		
		if (getState() == STATE_SUCCESSFUL) {
			ret = ColorConstants.green;
		} else if (getState() == STATE_UNSUCCESSFUL) {
			ret = ColorConstants.red;
		} else if (getState() == STATE_PROCESSING) {
			ret = ColorConstants.yellow;
		}		
		
		return(ret);
	}
	
	public void setState(int state) {
		m_state = state;
		repaint();
	}
	
	public int getState() {
		return(m_state);
	}
	
	private ConnectionAnchor m_connectionAnchor=null;
	
	private boolean selected;
	private boolean hasFocus;
	private Boolean m_errorExpected=null;
	private int m_state=STATE_RESET;
	
	public static final int STATE_RESET=0;
	public static final int STATE_PROCESSING=1;
	public static final int STATE_SUCCESSFUL=2;
	public static final int STATE_UNSUCCESSFUL=3;
}
