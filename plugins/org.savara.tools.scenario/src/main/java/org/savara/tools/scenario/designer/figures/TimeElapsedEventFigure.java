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
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.model.*;
import org.savara.tools.scenario.designer.view.*;

/**
 * This class provides a figure for the simple type.
 */
public class TimeElapsedEventFigure extends org.eclipse.draw2d.Figure {

	public TimeElapsedEventFigure(ScenarioDiagram diagram) {
		m_diagram = diagram;
	}
	
	protected boolean useLocalCoordinates() {
		return(true);
	}
	
	public void setElapsedTime(String elapsedTime) {
		m_elapsedTime = elapsedTime;
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
			
			for (int i=0; i < list.size(); i++) {
				Role p=(Role)list.get(i);
				
				int xpos=ViewSupport.getChildXPosition(m_diagram.getScenario(),
							p, m_diagram);
				
				if (xpos > 0) {
					
					graphics.setBackgroundColor(ColorConstants.lightGray);
					graphics.fillPolygon(new int[]{
							xpos, r.y,
							xpos-10, r.y+(r.height/3),
							xpos, r.y+(r.height/3),
							xpos+10, r.y
					});
					graphics.fillPolygon(new int[]{
							xpos-10, r.y+(r.height/3),
							xpos+10, r.y +(2*r.height / 3),
							xpos+20, r.y +(2*r.height / 3),
							xpos, r.y+(r.height/3)
					});
					graphics.fillPolygon(new int[]{
							xpos+10, r.y +(2*r.height / 3),
							xpos, r.y +r.height,
							xpos+10, r.y +r.height,
							xpos+20, r.y +(2*r.height / 3)
					});
				}
			}
		}
		
		graphics.setForegroundColor(ColorConstants.black);
		
		org.eclipse.swt.graphics.Font font =
			new org.eclipse.swt.graphics.Font(org.eclipse.swt.widgets.Display.getCurrent(),
					"Arial",6,org.eclipse.swt.SWT.ITALIC);
		
		graphics.setFont(font);
		
		String text="Elapsed: ";
		
		if (m_elapsedTime != null) {
			text += m_elapsedTime;
		}
		
		graphics.drawText(text, r.x+5, r.y+2);
		
		font.dispose();
	}

	private String m_elapsedTime=null;
	private ScenarioDiagram m_diagram=null;
}
