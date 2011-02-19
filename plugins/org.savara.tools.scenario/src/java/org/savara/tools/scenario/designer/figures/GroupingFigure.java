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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class represents a grouping figure, that can be expanded,
 * or collapsed.
 */
public class GroupingFigure extends Figure {

	public GroupingFigure(IFigure header) {
		contents = new Figure();
		//contents.setLayoutManager(new DummyLayout());
		contents.setLayoutManager(new XYLayout());
		add(contents);
		add(this.header = header);
	}

	public IFigure getContents() {
		return contents;
	}

	public IFigure getHeader() {
		return header;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension dim = new Dimension();
		//dim.width = getFooter().getPreferredSize().width;
		dim.width += getInsets().getWidth();
		dim.height = 50;
		return dim;
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		rect = Rectangle.SINGLETON;
		getClientArea(rect);
		contents.setBounds(rect);
		//Dimension size = footer.getPreferredSize();
		//footer.setLocation(rect.getBottomLeft().translate(0, -size.height));
		//footer.setSize(size);
		
		Dimension size = header.getPreferredSize();
		header.setSize(size);
		header.setLocation(rect.getLocation());
	}

	public void setSelected(boolean value) {
	}

	IFigure contents;
	IFigure header;
}
