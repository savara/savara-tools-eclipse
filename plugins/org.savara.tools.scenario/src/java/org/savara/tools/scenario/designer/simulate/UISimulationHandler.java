/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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

import org.savara.scenario.model.Event;
import org.savara.scenario.simulation.SimulationHandler;

public class UISimulationHandler implements SimulationHandler {

	private ScenarioSimulation m_simulation=null;
	
	public UISimulationHandler(ScenarioSimulation ssim) {
		m_simulation = ssim;
	}
	
	public void noSimulator(Event event) {
		SimulationEntity se=m_simulation.getSimulationEntity(event, false);
		
		if (se != null) {
			se.reset();
		}
	}

	public void processed(Event event) {
		SimulationEntity se=m_simulation.getSimulationEntity(event, false);
		
		if (se != null) {
			se.successful();
		}
	}

	public void unexpected(Event event) {
		SimulationEntity se=m_simulation.getSimulationEntity(event, false);
		
		if (se != null) {
			se.unsuccessful();
		}
	}

	public void error(String mesg, Event event, Throwable e) {		
		SimulationEntity se=m_simulation.getSimulationEntity(event, false);
		
		if (se != null) {
			se.unsuccessful();
		}
	}

}
