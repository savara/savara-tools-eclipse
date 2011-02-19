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
 * Sep 8, 2005 : Initial version created by gary
 */
package org.savara.tools.monitor.correlator;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.Channel;
import org.pi4soa.service.Identity;
import org.pi4soa.service.correlator.CorrelationSession;
import org.pi4soa.service.session.Session;

/**
 * This class provides an implementation of the correlation
 * session.
 *
 */
public class CorrelationSessionImpl implements CorrelationSession {

	/**
	 * This is the constructor for the correlation session.
	 * 
	 * @param cdl The choreography description being correlated
	 */
	public CorrelationSessionImpl(org.pi4soa.cdl.Package cdl) {
		m_choreographyDescription = cdl;
	}
	
	/**
	 * This method returns the choreography description associated
	 * with the correlated session.
	 * 
	 * @return The 
	 */
	public org.pi4soa.cdl.Package getChoreographyDescription() {
		return(m_choreographyDescription);
	}
	
	/**
	 * This method associates a service session with the correlation
	 * session.
	 * 
	 * @param session The service session
	 */
	public void associateServiceSession(Session session) {
		logger.fine("Associate service session '"+session+"'");
		
		m_serviceSessions.add(session);
	}
	
	/**
	 * This method disassociates a service session from
	 * the correlation session.
	 * 
	 * @param session The service session
	 */
	public void disassociateServiceSession(Session session) {
		logger.fine("Disassociate service session name '"+session+"'");
		
		m_serviceSessions.remove(session);
		
		// Apply identities to past values - this is used when
		// all of the sessions are removed, but the session
		// is not necessarily completed
		java.util.Set<Identity> ids=session.getPrimaryIdentities();
		
		java.util.Iterator<Identity> iter=ids.iterator();
		while (iter.hasNext()) {
			Identity cur=iter.next();
			
			if (m_pastIdentities.contains(cur) == false) {
				
				logger.info("Correlation session '"+this+
						"' - adding identity to past list: "+cur);
				
				m_pastIdentities.add(cur);
			}
		}
	}
		
	/**
	 * This method disassociates a service session from
	 * the correlation session.
	 * 
	 * @param serviceName The service name
	 */
	public void disassociateServiceSession(String serviceName) {
		logger.fine("Disassociate service session name '"+serviceName+"'");
		
		for (int i=m_serviceSessions.size()-1; i >= 0; i--) {
			Session session=(Session)m_serviceSessions.get(i);
			
			if (session.getId().getServiceDescriptionName().
						equals(serviceName)) {
				
				disassociateServiceSession(session);
			}
		}
	}
	
	/**
	 * This method returns the number of service sessions
	 * associated with the correlation session.
	 * 
	 * @return The number of service sessions
	 */
	public int getNumberOfServiceSessions() {
		return(m_serviceSessions.size());
	}
	
	/**
	 * This method is invoked to indicate that a choreography
	 * exchange has been initiated by one participant.
	 * 
	 * @param exchange The exchange details
	 * @param channel The channel associated with the exchange
	 */
	public void exchangeInitiated(ExchangeDetails exchange, Channel channel) {
		
		synchronized(m_initiated) {			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Exchange initiated="+exchange+" on channel="+channel);
			}

			m_initiated.add(exchange);
		}
	}

	/**
	 * This method is invoked to indicate that a choreography
	 * exchange has been completed by a target participant.
	 * 
	 * @param exchange The exchange details
	 * @param channel The channel associated with the exchange
	 */
	public void exchangeCompleted(ExchangeDetails exchange, Channel channel) {
		
		synchronized(m_initiated) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Exchange completed="+exchange+" on channel="+channel);
			}

			if (m_initiated.contains(exchange)) {
				m_completed.add(exchange);
				
				m_initiated.remove(exchange);
			} else {
				
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("NOTE: Completed exchange="+
							exchange+" has not been initiated");
				}
			}
		}
	}
	
	/**
	 * This method returns the identities associated with this
	 * correlation session.
	 * 
	 * @return The identities
	 */
	public java.util.List<Identity> getIdentities() {
		java.util.List<Identity> ret=new java.util.Vector<Identity>();
		
		for (int i=0; i < m_pastIdentities.size(); i++) {
			Identity pastId=(Identity)m_pastIdentities.get(i);
		
			if (ret.contains(pastId) == false) {
				ret.add(pastId);
			}
		}
		
		for (int i=0; i < m_serviceSessions.size(); i++) {
			Session session=(Session)m_serviceSessions.get(i);
			
			java.util.Set<Identity> ids=session.getPrimaryIdentities();
			
			java.util.Iterator<Identity> iter=ids.iterator();
			
			while (iter.hasNext()) {
				Identity cur=iter.next();
				
				if (ret.contains(cur) == false) {
					ret.add(cur);
				}
			}
			
			if (session.getSessionIdentity() != null &&
					ret.contains(session.getSessionIdentity()) == false) {
				ret.add(session.getSessionIdentity());
			}
		}

		return(ret);
	}
	
	/**
	 * This method determines whether the session is identified by
	 * the supplied ids.
	 * 
	 * @param ids The identities
	 * @return Whether the session is associated with the supplied id
	 */
	public boolean isIdentifiedBy(java.util.Collection<Identity> ids) {
		boolean ret=false;
		
		if (ids != null) {
			java.util.Iterator<Identity> iter=ids.iterator();
			
			while (ret == false && iter.hasNext()) {
				Identity cur=iter.next();
				
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Is identified by="+cur);
				}
				
				for (int i=0; ret == false &&
						i < m_serviceSessions.size(); i++) {
					Session session=(Session)m_serviceSessions.get(i);
					
					ret = session.isIdentifiedBy(cur);
					
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Checked id="+cur+
								" against session="+session+" = "+ret);
					}
				}
				
				for (int i=0; ret == false &&
						i < m_pastIdentities.size(); i++) {
					Identity pastId=(Identity)m_pastIdentities.get(i);
			
					ret = pastId.equals(cur);
					
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Checked against past id="+pastId+" = "+ret);
					}
				}
			}
		}
		
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Is correlation session ("+this+
					") identified by "+ids+" = "+ret);
		}
		
		return(ret);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.correlator.impl");		
	
	private org.pi4soa.cdl.Package m_choreographyDescription=null;
	private Vector m_serviceSessions=new Vector();
	private Vector m_initiated=new Vector();
	private Vector m_completed=new Vector();
	private java.util.List<Identity> m_pastIdentities=new Vector<Identity>();
}
