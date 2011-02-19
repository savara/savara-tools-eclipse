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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.savara.tools.scenario.designer.view.ViewSupport;

/**
 * This figure represents a sequential grouping construct.
 */
public class GroupFigure extends GroupingFigure {

	static final MarginBorder MARGIN_BORDER = new MarginBorder(0, 8, 0, 0);

	static final PointList ARROW = new PointList(3); {
		ARROW.addPoint(0,0);
		ARROW.addPoint(10,0);
		ARROW.addPoint(5,5);
	}

	/**
	 * @param header
	 * @param footer
	 */
	public GroupFigure(Image image) {
		super(new StartTagFigure("", image, false));
		setBorder(MARGIN_BORDER);
		setOpaque(false);
	}

	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		graphics.setBackgroundColor(ColorConstants.buttonDarker);
		Rectangle r = getBounds().getCopy();
		
		r.y += ViewSupport.TYPES_INITIAL_YPADDING;
		r.height -= (1 + ViewSupport.TYPES_INITIAL_YPADDING);
		r.width -= 1;

		graphics.setLineStyle(graphics.LINE_DASH);
		graphics.setForegroundColor(getBoundaryColor());
		graphics.drawRoundRectangle(r, 10, 10);
	}
	
	protected Color getBoundaryColor() {
		Color ret=ColorConstants.gray;
		
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
	
	private int m_state=STATE_RESET;

	public static final int STATE_RESET=0;
	public static final int STATE_PROCESSING=1;
	public static final int STATE_SUCCESSFUL=2;
	public static final int STATE_UNSUCCESSFUL=3;
}
