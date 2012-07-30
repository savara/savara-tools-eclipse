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
package org.savara.tools.common;

import org.scribble.protocol.parser.ProtocolParserManager;
import org.scribble.protocol.validation.ProtocolValidationManager;

/**
 * This class represents tools available to the Eclipse environment.
 *
 */
public class Tools {

	private static ProtocolParserManager _protocolParserManager=null;
	private static ProtocolValidationManager _protocolValidationManager=null;
	
	/**
	 * This method returns the protocol parser manager.
	 * 
	 * @return The protocol parser manager
	 */
	public static ProtocolParserManager getProtocolParserManager() {
		return (_protocolParserManager);
	}
	
	/**
	 * This method sets the protocol parser manager.
	 * 
	 * @param ppm The protocol parser manager
	 */
	public static void setProtocolParserManager(ProtocolParserManager ppm) {
		_protocolParserManager = ppm;
	}
	
	/**
	 * This method returns the protocol validation manager.
	 * 
	 * @return The protocol validation manager
	 */
	public static ProtocolValidationManager getProtocolValidationManager() {
		return (_protocolValidationManager);
	}
	
	/**
	 * This method sets the protocol validation manager.
	 * 
	 * @param pvm The protocol validation manager
	 */
	public static void setProtocolValidationManager(ProtocolValidationManager pvm) {
		_protocolValidationManager = pvm;
	}

}
