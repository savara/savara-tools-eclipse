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

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class StartTagFigure extends Label {


	static final Border BORDER = new MarginBorder(2,0,2,9);

	/**
	 * Creates a new StartTag
	 * @param name the text to display in this StartTag
	 */
	public StartTagFigure(String name, Image image, boolean border) {
		setText(name);
		
		if (image != null) {
			setIconTextGap(4);
			setIcon(image);
		}
		
		setForegroundColor(ColorConstants.lightGray);
		setOpaque(true);
		
		if (border) {
			setBorder(new MarginBorder(2,0,2,9));
		}
		setTextAlignment(org.eclipse.draw2d.PositionConstants.LEFT);
		//setBackgroundColor(ColorConstants.tooltipBackground);
	}

	/*
	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		Rectangle r = getTextBounds();

		/*
		r.resize(-1, -1);
		r.expand(1, 1);
		r.width -= 1;
		r.x -= 2;
		g.drawLine(r.x, r.y, r.right(), r.y); //Top line
		g.drawLine(r.x, r.bottom(), r.right(), r.bottom()); //Bottom line
		g.drawLine(r.x, r.bottom(), r.x, r.y); //left line

		g.drawLine(r.right() + 7, r.y + r.height / 2, r.right(), r.y);
		g.drawLine(r.right()+7, r.y + r.height / 2, r.right(), r.bottom());
		*/
	//}
}
