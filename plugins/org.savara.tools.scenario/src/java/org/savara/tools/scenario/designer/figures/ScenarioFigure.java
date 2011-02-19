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
 * Jul 6, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * This figure represents a sequential grouping construct.
 */
public class ScenarioFigure extends FreeformLayer {

	/**
	 * @param header
	 * @param footer
	 */
	public ScenarioFigure() {
		//setOpaque(false);
	}

	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		
		int height=25;
		int width=0;
		
		if (m_name != null) {
			width = (int)((m_name.length()+6)*6.5);
		}
		
		if (m_author != null) {
			height += 25;
			if (width == 0 || m_author.length() > m_name.length()) { 
				width = (int)((m_author.length()+7)*6.5);
			}
		}
		
		//Rectangle r2=new Rectangle(5, 5, 200, 50);
		graphics.setForegroundColor(ColorConstants.buttonDarker);
				
		//graphics.setBackgroundColor(ColorConstants.button);
		//graphics.drawRoundRectangle(r2, 5, 5);
		
		//Rectangle r = getBounds().getCopy();

		String text=m_name;
		if (text == null || text.trim().length() == 0) {
			text = "<unknown>";
		}
		text = "Name: "+text;
		
		graphics.drawText(text, 10, 5);
			
		if (m_author != null) {
			text = "Author: "+m_author;
				
			graphics.drawText(text, 10, 25);
		}
		
		if (width < 200) {
			width = 200;
		} else {
			width += 20;
		}
		
		graphics.drawLine(0, height, width, height);
		graphics.drawLine(width, height, width+height, 0);
		
		/*
		r.y += ViewSupport.TYPES_INITIAL_YPADDING;
		r.height -= (1 + ViewSupport.TYPES_INITIAL_YPADDING);
		r.width -= 1;

		graphics.setLineStyle(graphics.LINE_DASH);
		graphics.setForegroundColor(getBoundaryColor());
		graphics.drawRoundRectangle(r, 10, 10);
		*/
	}
	
	public void setName(String name) {
		m_name = name;
		repaint();
	}
	
	public void setAuthor(String author) {
		m_author = author;
		repaint();
	}
	
	private String m_name=null;
	private String m_author=null;
}
