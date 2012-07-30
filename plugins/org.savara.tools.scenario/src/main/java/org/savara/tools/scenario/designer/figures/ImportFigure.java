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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.view.*;

/**
 * This class provides a figure for the simple type.
 */
public class ImportFigure extends org.eclipse.draw2d.Figure {

	public ImportFigure(ScenarioDiagram diagram) {
		m_diagram = diagram;
	}
	
	protected boolean useLocalCoordinates() {
		return(true);
	}
	
	public void setURL(String url) {
		m_url = url;
		repaint();
	}
	
	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		
		Rectangle r=getBounds();
		
		//graphics.setBackgroundColor(getFillColor());

		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillRectangle(r);
		
		if (m_diagram != null) {
			java.util.List list=m_diagram.getScenario().getRole();
			
			graphics.setBackgroundColor(getFillColor());
			graphics.fillRectangle(r);
			
			for (int i=0; i < list.size(); i++) {
				Role p=(Role)list.get(i);
				
				int xpos=ViewSupport.getChildXPosition(m_diagram.getScenario(),
							p, m_diagram);
				
				if (xpos > 0) {
					
					graphics.setBackgroundColor(ColorConstants.lightBlue);
				
					graphics.fillRectangle(xpos, r.y, 10, r.height);
				}
			}
		}
		
		graphics.setForegroundColor(ColorConstants.black);
		
		org.eclipse.swt.graphics.Font font =
			new org.eclipse.swt.graphics.Font(org.eclipse.swt.widgets.Display.getCurrent(),
					"Arial",6,org.eclipse.swt.SWT.ITALIC);
		
		graphics.setFont(font);
				
		String text="Import: ";
		if (m_url != null) {
			text += m_url;
		}
		graphics.drawText(text, r.x+5, r.y+2);
		
		font.dispose();
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
	
	private String m_url=null;
	private ScenarioDiagram m_diagram=null;
	
	private int m_state=STATE_RESET;
	
	public static final int STATE_RESET=0;
	public static final int STATE_PROCESSING=1;
	public static final int STATE_SUCCESSFUL=2;
	public static final int STATE_UNSUCCESSFUL=3;
}
