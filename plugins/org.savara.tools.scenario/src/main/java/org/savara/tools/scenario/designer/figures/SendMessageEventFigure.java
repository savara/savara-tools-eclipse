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
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class provides a figure for the simple type.
 */
public class SendMessageEventFigure extends MessageEventFigure {

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		/*
		if (selected) {
			graphics.pushState();
			graphics.setBackgroundColor(ColorConstants.menuBackgroundSelected);
			graphics.fillRectangle(getSelectionRectangle());
			graphics.popState();
			graphics.setForegroundColor(ColorConstants.white);
		}
		if (hasFocus) {
			graphics.pushState();
			graphics.setXORMode(true);
			graphics.setForegroundColor(ColorConstants.menuBackgroundSelected);
			graphics.setBackgroundColor(ColorConstants.white);
			graphics.drawFocus(getSelectionRectangle().resize(-1, -1));
			graphics.popState();
		}
		*/
		super.paintFigure(graphics);
		
		Rectangle r=getBounds();
		
		/*
		r.resize(-1, -1);
		r.expand(1, 1);
		r.width -= 1;
		r.x -= 2;
		*/
		
		graphics.setBackgroundColor(getFillColor());
		graphics.fillPolygon(new int[]{
				r.x+3, r.y+2,
				r.right()-10, r.y+2,
				r.right()-2, r.y + r.height / 2,
				r.right()-10, r.bottom()-2,
				r.x+3, r.bottom()-2
		});
		
		graphics.setForegroundColor(getBoundaryColor());
		graphics.setLineWidth(getBoundaryWidth());

		graphics.drawLine(r.x+3, r.y+2, r.right()-10, r.y+2); //Top line
		graphics.drawLine(r.x+3, r.bottom()-2, r.right()-10, r.bottom()-2); //Bottom line
		graphics.drawLine(r.x+3, r.bottom()-2, r.x+3, r.y+2); //left line

		graphics.drawLine(r.right()-2, r.y + r.height / 2, r.right()-10, r.y+2);
		graphics.drawLine(r.right()-2, r.y + r.height / 2, r.right()-10, r.bottom()-2);
	}
}
