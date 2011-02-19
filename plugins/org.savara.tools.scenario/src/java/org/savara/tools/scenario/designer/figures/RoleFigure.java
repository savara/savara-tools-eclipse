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
import org.savara.tools.scenario.designer.view.ViewSupport;
import org.eclipse.draw2d.MarginBorder;

/**
 * This class provides a figure for the simple type.
 */
public class RoleFigure extends GroupingFigure {//org.eclipse.draw2d.Figure {

	public RoleFigure() {
		super(new StartTagFigure("", null, true));
		setOpaque(true);
		
		((org.eclipse.draw2d.Label)getHeader()).setBackgroundColor(ColorConstants.menuBackground);
		((org.eclipse.draw2d.Label)getHeader()).setForegroundColor(ColorConstants.black);
			
		((org.eclipse.draw2d.Label)getHeader()).setTextAlignment(
				org.eclipse.draw2d.PositionConstants.LEFT);

		m_connectionAnchor = new org.eclipse.draw2d.ChopboxAnchor(this);
	}
	
	/*
	private Rectangle getSelectionRectangle() {
		Rectangle bounds = getBounds();
		bounds.expand(new Insets(2,2,0,0));
		translateToParent(bounds);
		bounds.intersect(getBounds());
		return bounds;
	}
	*/

	protected boolean useLocalCoordinates() {
		return(true);
	}
	
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
				
		graphics.setBackgroundColor(ColorConstants.lightGray);
		graphics.fillRectangle(r.x+(r.width/2)-5, r.y+40, 10, r.height-40);
		
		
		/*
		r.resize(-1, -1);
		r.expand(1, 1);
		r.width -= 1;
		r.x -= 2;
		*/
		
		/*
		graphics.drawLine(r.x+2, r.bottom()-2, r.x+2, r.y+2); //right line
		graphics.drawLine(r.x+2, r.y+2, r.right()-2, r.y+2); //Top line
		graphics.drawLine(r.x+2, r.bottom()-2, r.right()-2, r.bottom()-2); //Bottom line
		graphics.drawLine(r.right()-2, r.bottom()-2, r.right()-2, r.y+2); //right line
		*/
	}

	public void setBounds(Rectangle rect) {
		
		if (getHeader().getPreferredSize().width < rect.width) {
			getHeader().getPreferredSize().width = rect.width;
		}
		
		if (getHeader().getPreferredSize().height < 46) {
			getHeader().getPreferredSize().height = 46;
		}
		
		super.setBounds(rect);
	}
	
	public void setText(String text) {
		m_text = text;
		((Label)getHeader()).setText(text);
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
	
	private ConnectionAnchor m_connectionAnchor=null;
	
	private boolean selected;
	private boolean hasFocus;
	private String m_text=null;
}
