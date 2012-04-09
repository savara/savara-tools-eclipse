/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.scenario.designer.simulate;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScenarioDesignerSimulationLauncherTest {

	@Test
	public void testTransformToHTMLNewLine() {
		String text="First line\r\nSecond Line";
		
		String html="First line\r<br>Second Line";
		
		String result=ScenarioDesignerSimulationLauncher.transformToHTML(text);
		
		if (!result.equals(html)) {
			fail("HTML transformation failed: expecting="+html+" got="+result);
		}
	}

	@Test
	public void testTransformToHTMLSevereRed() {
		String first="First line";
		String errorLine="SEVERE: This is the error";
		String third="Third line";
		
		String text=first+"\n"+errorLine+"\n"+third;
		
		String html=first+"<br>"+"<font color=\"red\">"+errorLine+"</font>"+"<br>"+third;
		
		String result=ScenarioDesignerSimulationLauncher.transformToHTML(text);
		
		if (!result.equals(html)) {
			fail("HTML transformation failed: expecting="+html+" got="+result);
		}
	}

}
