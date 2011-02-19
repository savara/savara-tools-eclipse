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

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.service.Identity;
import org.pi4soa.service.session.DefaultSessionManager;
import org.pi4soa.service.session.Session;
import org.pi4soa.service.session.SessionManagerException;

/**
 * This class provides the management of service and correlation
 * sessions.
 *
 */
public class CorrelationSessionManager extends DefaultSessionManager {

	/**
	 * This is the constructor for the correlation session manager.
	 * 
	 * @param notifier The notifier
	 */
	public CorrelationSessionManager(CorrelationNotifier notifier) {
		m_notifier = notifier;
	}
	
	/**
	 * This method adds a new session instance to the session manager,
	 * and returns a reference that can be used to retrieve the
	 * session at a later time.
	 * 
	 * @param session The session
	 * @return The session reference for the added session
	 * @exception SessionManagerException Failed to add session
	 */
	@Override
	public void addSession(Session session)
					throws SessionManagerException {
		super.addSession(session);
		
		// Synchronize on correlation session list, as
		// other methods use these lists (pendingId and
		// correlSession) at the same time
		synchronized(m_correlationSessions) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Adding session '"+session+
						"' to pending identification list");
			}
			
			// Add session to 'pending identification' list
			m_pendingIdentification.add(session);
		}
	}
	
	/**
	 * This method returns the correlation session implementation
	 * associated with the supplied session. If more than one
	 * correlation session is found to have the same identity
	 * information, then a merge of the correlation sessions will be
	 * performed.
	 * 
	 * @param session The service session
	 * @param mesg The optional message
	 * @return The correlation session
	 */
	protected CorrelationSessionImpl getCorrelationSessionImpl(Session session,
							org.pi4soa.service.Message mesg) {
		CorrelationSessionImpl corrSess=null;
				
		synchronized(m_correlationSessions) {
			boolean pendingStarted=false;
			
			// TODO: Could check which choreography session this
			// session can be added to - then generate 'participant'
			// joined session messages?
			
			// Find an appropriate correlation session to associate
			// with the session
			java.util.Set<Identity> primIds=session.getPrimaryIdentities();
			
			corrSess = getCorrelationSessionImpl(primIds);
			
			if (corrSess == null && mesg != null) {
				// Check if message identities can find the
				// correlation session
				corrSess = getCorrelationSessionImpl(mesg.getMessageIdentities());
			}
			
			if (corrSess == null) {
				
				if (primIds == null || primIds.size() == 0) {
					logger.severe("Correlation session is going to be " +
							"created with no primary ids available");
				} else {
					String str="Primary ids: ";
					
					java.util.Iterator<Identity> iter=primIds.iterator();
					while (iter.hasNext()) {
						str += iter.next().getId();
					}
					
					logger.info(str);
				}
				
				// Find choreography for this session
				org.pi4soa.cdl.Package cdlpack=
						getChoreographyForService(session.getId().
								getServiceDescriptionName());
				
				if (cdlpack == null) {
					logger.severe("Unable to find choreography for service '"+
							session.getId().getServiceDescriptionName());
				} else {
					corrSess = new CorrelationSessionImpl(cdlpack);
					m_correlationSessions.add(corrSess);
					
					pendingStarted = true;
					
					logger.info("New correlation session="+corrSess);
				}
			} else {
				
				if (corrSess.getNumberOfServiceSessions() == 0) {
					
					if (m_pendingCloseSessions.remove(corrSess)) {
						logger.fine("Removed correlation session " +
								"from pending close list: "+corrSess);						
					}
				}
			}
			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Check if session '"+session+
						"' is pending identification: "+
						m_pendingIdentification.contains(session));
			}
			
			if (corrSess != null && m_pendingIdentification.contains(session)) {
				corrSess.associateServiceSession(session);
				
				logger.info("Associating service session '"+
						session+"' with correlation session '"+
						corrSess+"'");
				
				m_pendingIdentification.remove(session);
			
				// TODO: IDEAS: choreography session is used to store the
				// list of initiated and completed activities (exchanges)
				// recorded by the send and receive events for the
				// associated CDL exchange activity. Possibly the list
				// of completed activities is retained to act as an
				// audit trail? Not sure if service sessions are necessary
				// to be recorded against correlation session, but may
				// provide some relevant details about participants joining
				// choreography and then leaving.
			}
			
			if (pendingStarted) {
				m_notifier.sessionStarted(corrSess);
			}
		}

		return(corrSess);
	}

	/**
	 * This method removes a session instance from the session manager.
	 * 
	 * @param session The session
	 * @exception SessionManagerException Failed to remove session
	 */
	@Override
	public void removeSession(Session session) throws SessionManagerException {
		
		synchronized(m_correlationSessions) {
			// TODO: May want to make this configurable, because we
			// could just rely on the tracker events from each participant
			// to inform us when they have completed
			
			CorrelationSessionImpl corrSess=
				getCorrelationSessionImpl(session.getPrimaryIdentities());

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Remove session '"+session+
						"': correlation session="+corrSess);
			}

			if (corrSess != null) {
				corrSess.disassociateServiceSession(session);
				
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Removing session '"+session+
							"' from pending identification list");
				}

				// Remove from pending identification list just in case
				m_pendingIdentification.remove(session);
				
				if (corrSess.getNumberOfServiceSessions() == 0) {	
					pendingClose(corrSess);
				}
			}
		}
		
		super.removeSession(session);
	}
	
	/**
	 * This method removes the supplied correlation session.
	 * 
	 * @param cs The correlation session
	 */
	public void pendingClose(CorrelationSessionImpl cs) {

		logger.info("Moving correlation session to pending close list: "+
				cs);
		
		m_pendingCloseSessions.add(cs);
		
		//m_correlationSessions.remove(corrSess);
		
		//m_notifier.sessionFinished(corrSess);
		//synchronized(m_correlationSessions) {
			//m_correlationSessions.remove(cs);
		//}
	}
	
	/**
	 * This method returns the correlation session implementation
	 * associated with the supplied session. If more than one
	 * correlation session is found to have the same identity
	 * information, then a merge of the correlation sessions will be
	 * performed.
	 * 
	 * @param session The service session
	 * @return The correlation session
	 */
	protected CorrelationSessionImpl getCorrelationSessionImpl(java.util.Collection<Identity> ids) {
		CorrelationSessionImpl ret=null;
		
		synchronized(m_correlationSessions) {
			// TODO: Get correlation session for primary identities
			// associated with the supplied session. If none, then
			// create one - not sure how we get the choreography
			// description??? - if more than one, then we need to
			// merge the correlation sessions
			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Number of sessions="+m_correlationSessions.size());
			}
			
			Vector sessions=new Vector();
			for (int i=0; i < m_correlationSessions.size(); i++) {
				CorrelationSessionImpl cs=(CorrelationSessionImpl)
						m_correlationSessions.get(i);
				
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Checking correlation session="+cs);
				}
				
				if (cs.isIdentifiedBy(ids)) {
					sessions.add(cs);
				}
			}
			
			if (sessions.size() > 1) {
				
				logger.severe("MULTIPLE CORRELATION SESSIONS DETECTED - currently not handled");
	
				// Merge sessions
				
				// TODO: MERGING
				
			} else if (sessions.size() == 1) {
				ret = (CorrelationSessionImpl)sessions.get(0);
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Returning correlation session="+ret);
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the list of correlation sessions.
	 * 
	 * @return The correlation sessions
	 */
	public java.util.List getCorrelationSessions() {
		return(m_correlationSessions);
	}
	
	/**
	 * This method associates the supplied choreography with the
	 * service name.
	 * 
	 * @param serviceName The service name
	 * @param cdlpack The choreography
	 */
	public void registerServiceChoreography(String serviceName,
			org.pi4soa.cdl.Package cdlpack) {
		m_serviceChoreographies.put(serviceName, cdlpack);
	}

	/**
	 * This method disassociates the supplied choreography from the
	 * service name.
	 * 
	 * @param serviceName The service name
	 * @param cdlpack The choreography
	 */
	public void unregisterServiceChoreography(String serviceName,
			org.pi4soa.cdl.Package cdlpack) {
		m_serviceChoreographies.remove(serviceName);
	}

	/**
	 * This method returns the choreography description associated
	 * with the supplied service name. If no choreography description
	 * can be located, then a null is returned.
	 * 
	 * @param serviceName The service name
	 * @return The choreography, or null if not found
	 */
	public org.pi4soa.cdl.Package getChoreographyForService(String serviceName) {
		return((org.pi4soa.cdl.Package)m_serviceChoreographies.get(serviceName));
	}
	
	private Vector m_correlationSessions=new Vector();
	private Hashtable m_serviceChoreographies=new Hashtable();
	private Vector m_pendingIdentification=new Vector();
	private Vector m_pendingCloseSessions=new Vector();
	private CorrelationNotifier m_notifier=null;
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.correlator.impl");
}
