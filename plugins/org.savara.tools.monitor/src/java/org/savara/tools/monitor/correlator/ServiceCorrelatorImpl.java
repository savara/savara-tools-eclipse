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
 * Aug 25, 2005 : Initial version created by gary
 */
package org.savara.tools.monitor.correlator;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.pi4soa.cdl.ParticipantType;
import org.pi4soa.common.util.NamesUtil;
import org.pi4soa.common.xml.XMLUtils;
import org.pi4soa.service.Identity;
import org.pi4soa.service.ServiceException;
import org.pi4soa.service.behavior.ServiceDescription;
import org.pi4soa.service.behavior.projection.BehaviorProjection;
import org.pi4soa.service.correlator.CorrelationSession;
import org.pi4soa.service.correlator.ServiceCorrelator;
import org.pi4soa.service.correlator.ServiceCorrelatorListener;
import org.pi4soa.service.monitor.DefaultMonitorConfiguration;
import org.pi4soa.service.monitor.ServiceMonitor;
import org.pi4soa.service.monitor.ServiceMonitorFactory;
import org.pi4soa.service.tracker.TrackerEvent;
import org.pi4soa.service.tracker.TrackerRecord;

/**
 * This class implements the service correlator interface.
 *
 */
public class ServiceCorrelatorImpl implements ServiceCorrelator {

	/**
	 * The default constructor for the service correlator
	 * implementation.
	 *
	 */
	public ServiceCorrelatorImpl() {
		initialize();
	}
	
	/**
	 * This method initializes the service correlator.
	 *
	 */
	protected void initialize() {

		m_notifier = new CorrelationNotifier();

		m_sessionManager = new CorrelationSessionManager(m_notifier);
		
		m_correlatingServiceTracker =
			new CorrelatingServiceTracker(m_sessionManager, m_notifier);
	}
	
	/**
	 * This method registers a choreography description with the
	 * service correlator, to be informed when service tracker
	 * activities associated with the description are correlated.
	 * 
	 * @param cdl The choreography description
	 * @exception ServiceException Failed to register
	 */
	public void register(org.pi4soa.cdl.Package cdl)
						throws ServiceException {
		
		synchronized(m_choreographyDescriptions) {
			if (m_choreographyDescriptions.containsKey(cdl)) {
				throw new ServiceException("Choreography " +
						"description already registered");
			}
				
			// Generate the endpoint projections and register them
			// with the monitor (in its service repository)
			Vector sds=new Vector();
			java.util.List participants=
					cdl.getTypeDefinitions().getParticipantTypes();
			
			java.util.Iterator iter=participants.iterator();
			while (iter.hasNext()) {
				ParticipantType partType=(ParticipantType)
							iter.next();
				
				// CDL File path not provided, as not required
				// for a service description that is only being
				// used for correlation
				ServiceDescription sd=
					BehaviorProjection.projectServiceDescription(cdl,
							partType, null);
								
				DefaultMonitorConfiguration config=
					new DefaultMonitorConfiguration();
				config.setServiceTracker(m_correlatingServiceTracker);
				config.setSessionManager(m_sessionManager);
				
				ServiceMonitor serviceMonitor =
					ServiceMonitorFactory.getServiceMonitor(config);
				serviceMonitor.getConfiguration().getServiceRepository().
						addServiceDescription(sd);
				
				logger.info("Registering service monitor for '"+
						sd.getFullyQualifiedName()+"'");
				
				m_serviceMonitors.put(sd.getFullyQualifiedName(),
								serviceMonitor);
				
				m_serviceMonitors.put(getLocatedProtocolName(cdl, partType), serviceMonitor);
				
				sds.add(sd);
				
				m_sessionManager.registerServiceChoreography(
						sd.getFullyQualifiedName(), cdl);

				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				try {
					org.pi4soa.service.util.ServiceDescriptionManager.save(
							sd,	baos);
					logger.info(baos.toString());
				} catch(Exception e) {
					e.printStackTrace();
				}	
			}
		
			// Register choreography description
			m_choreographyDescriptions.put(cdl, sds);
		}
		
		// Notify any registered listeners
		m_notifier.choreographyDescriptionRegistered(cdl);
	}
	
	protected String getLocatedProtocolName(org.pi4soa.cdl.Package cdl, ParticipantType partType) {
		String ret=null;
		
		ret = new QName(cdl.getTargetNamespace(), cdl.getName()).toString();
		
		ret += "@"+XMLUtils.getLocalname(partType.getName());
		
		return(ret);
	}
	
	/**
	 * This method unregisters the supplied choreography
	 * description, to ignore further correlation situations
	 * that occur related to this description.
	 * 
	 * @param cdl The choreography description
	 * @exception ServiceException Failed to register
	 */
	public void unregister(org.pi4soa.cdl.Package cdl)
						throws ServiceException {
		boolean unregistered=false;
		
		synchronized(m_choreographyDescriptions) {
			if (m_choreographyDescriptions.containsKey(cdl)) {
			
				// Remove the endpoint projections, associated with
				// the supplied choreography description and unregister them
				// from the monitor's service repository
				Vector sds=(Vector)m_choreographyDescriptions.get(cdl);
				
				for (int i=0; i < sds.size(); i++) {
					ServiceDescription sd=(ServiceDescription)sds.get(i);
					
					ServiceMonitor serviceMonitor=(ServiceMonitor)
							m_serviceMonitors.get(sd.getFullyQualifiedName());
					
					serviceMonitor.getConfiguration().getServiceRepository().
								removeServiceDescription(sd);
					
					// TODO: Need to close down monitor
					
					m_serviceMonitors.remove(sd.getFullyQualifiedName());
					
					m_sessionManager.unregisterServiceChoreography(
							sd.getFullyQualifiedName(), cdl);
				}
				
				m_choreographyDescriptions.remove(cdl);
				
				unregistered = true;
			}
		}

		if (unregistered) {
 			// Notify any registered listeners
			m_notifier.choreographyDescriptionRegistered(cdl);
		}
	}
	
	/**
	 * This method returns the list of choreography descriptions
	 * registered with the service correlator.
	 * 
	 * @return The list of choreography descriptions
	 */
	public org.pi4soa.cdl.Package[] getChoreographyDescriptions() {
		org.pi4soa.cdl.Package[] ret=
			new org.pi4soa.cdl.Package[m_choreographyDescriptions.size()];
		
		Enumeration iter=m_choreographyDescriptions.elements();
		int index=0;
		while (iter.hasMoreElements()) {
			ret[index++] = (org.pi4soa.cdl.Package)iter.nextElement();
		}
		
		return(ret);                                           
	}
	
	/**
	 * This method returns the current list of correlated
	 * sessions associated with the specified choreography
	 * description.
	 * 
	 * @param cdl The choreography description
	 * @return The list of correlated sessions
	 */
	public CorrelationSession[] getCorrelationSessions(org.pi4soa.cdl.Package cdl) {
		CorrelationSession[] ret=null;
		java.util.Vector tmp=new java.util.Vector();
		
		java.util.List list=m_sessionManager.getCorrelationSessions();
		for (int i=0; i < list.size(); i++) {
			CorrelationSession sess=(CorrelationSession)list.get(i);
			
			if (sess.getChoreographyDescription() == cdl) {
				tmp.add(sess);
			}
		}
		
		ret = new CorrelationSession[tmp.size()];
		tmp.copyInto(ret);
		
		return(ret);
	}
	
	/**
	 * This method returns the correlated
	 * session associated with the specified choreography
	 * description and set of identities.
	 * 
	 * @param cdl The choreography description
	 * @return The list of correlated sessions
	 * @exception ServiceException Failed to get single session for
	 * 					supplied choreography description and ids
	 */
	public CorrelationSession getCorrelationSession(org.pi4soa.cdl.Package cdl,
			java.util.List<Identity> ids) throws ServiceException {
		CorrelationSession ret=null;
		
		CorrelationSession[] sessions=getCorrelationSessions(cdl);
		for (int i=0; sessions != null &&
					i < sessions.length; i++) {
			if (sessions[i].isIdentifiedBy(ids)) {
				
				if (ret == null) {
					ret = sessions[i];
				} else {
					throw new ServiceException("Multiple correlation "+
							"sessions with same identity");
				}
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method adds a listener for notifications regarding
	 * correlated sessions.
	 * 
	 * @param l The listener
	 */
	public void addServiceCorrelatorListener(ServiceCorrelatorListener l) {
		m_notifier.addServiceCorrelatorListener(l);
	}
	
	/**
	 * This method removes a listener for notifications regarding
	 * correlated sessions.
	 * 
	 * @param l The listener
	 */
	public void removeServiceCorrelatorListener(ServiceCorrelatorListener l) {
		m_notifier.removeServiceCorrelatorListener(l);
	}
	
	/**
	 * This method is invoked to handle the supplied service
	 * tracker record.
	 * 
	 * @param record The record
	 */
	public void handleTrackerRecord(TrackerRecord record) {
		
		// Obtain message related tracker events
		if (record != null && record.getTrackerEvents() != null) {
			ServiceMonitor serviceMonitor=null;
			
			logger.info("Handle correlation record: "+record.toXML());

			if (NamesUtil.isSet(record.getServiceDescriptionName())) {
				serviceMonitor = (ServiceMonitor)
					m_serviceMonitors.get(record.getServiceDescriptionName());
			
				if (serviceMonitor == null) {
					logger.info("Could not find Service Monitor for '"+
							record.getServiceDescriptionName()+"'");
				}
			} else {
				logger.info("Tracker record is not associated with a service");
			}
			
			for (int i=0; serviceMonitor != null &&
						i < record.getTrackerEvents().length; i++) {
				TrackerEvent event=record.getTrackerEvents()[i];
				
				if (event.getEventType() == null) {
					
					logger.warning("Tracker event '"+event+
								"' has no event type");
					
				} else if (event.getEventType().equals(TrackerEvent.RECEIVED_MESSAGE)) {
					
					try {
						serviceMonitor.messageReceived(event.getMessage());
					} catch(Exception se) {
						logger.log(Level.SEVERE,
								"Failed to handle 'messageReceived' tracker event '"+
								event.toXML()+"': "+se, se);
						
						// Report error to notifier
						java.util.List<Identity> ids=record.getPrimaryIdentities();
						
						if ((ids == null || ids.size() == 0) &&
								record.getSessionIdentity() != null) {
							ids = new java.util.Vector<Identity>();
							ids.add(record.getSessionIdentity());
						}
						
						CorrelationSessionImpl cs=
							m_sessionManager.getCorrelationSessionImpl(ids);
						
						m_notifier.error("Correlater detected problem: "+se.getMessage(),
								event.getException(),
								cs, record.getServiceDescriptionName());
					}
				} else if (event.getEventType().equals(TrackerEvent.SENT_MESSAGE)) {
					
					try {
						serviceMonitor.messageSent(event.getMessage());
					} catch(Exception se) {
						logger.log(Level.SEVERE,
								"Failed to handle 'messageSent' tracker event '"+
								event.toXML()+"': "+se, se);
						
						// Report error to notifier
						java.util.List<Identity> ids=record.getPrimaryIdentities();
						
						if ((ids == null || ids.size() == 0) &&
								record.getSessionIdentity() != null) {
							ids = new java.util.Vector<Identity>();
							ids.add(record.getSessionIdentity());
						}
						
						CorrelationSessionImpl cs=
							m_sessionManager.getCorrelationSessionImpl(ids);
						
						m_notifier.error("Correlater detected problem: "+se.getMessage(),
								event.getException(),
								cs, record.getServiceDescriptionName());
					}
				} else if (event.getEventType().equals(TrackerEvent.UNEXPECTED_MESSAGE)) {
					
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);
					
					m_notifier.unexpectedMessage(event.getMessage(),
							cs, record.getServiceDescriptionName());
					
				} else if (event.getEventType().equals(TrackerEvent.ERROR)) {
					
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);
					
					m_notifier.error(event.getDetails(),
							event.getException(),
							cs, record.getServiceDescriptionName());
					
				} else if (event.getEventType().equals(TrackerEvent.WARNING)) {
					
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);
					
					m_notifier.warning(event.getDetails(), 
							event.getException(),
							cs, record.getServiceDescriptionName());
					
				} else if (event.getEventType().equals(TrackerEvent.INFORMATION)) {
					
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);
					
					m_notifier.information(event.getDetails(), 
							cs, record.getServiceDescriptionName());
					
				} else if (event.getEventType().equals(TrackerEvent.UNHANDLED_EXCEPTION)) {
					
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);
					
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Report as error: "+event.getDetails()+
									" with exception "+event.getException());
					}
					
					m_notifier.error(event.getDetails(),
							event.getException(),
							cs, record.getServiceDescriptionName());
					
				} else if (event.getEventType().equals(TrackerEvent.SERVICE_FINISHED)) {
					
					// TODO: May be want to have a configuration
					// parameter to indicate whether these tracker
					// events should be used to close down service
					// sessions - may be better to only use the
					// observable exchanges to achieve this?
					java.util.List<Identity> ids=record.getPrimaryIdentities();
					
					if ((ids == null || ids.size() == 0) &&
							record.getSessionIdentity() != null) {
						ids = new java.util.Vector<Identity>();
						ids.add(record.getSessionIdentity());
					}
					
					CorrelationSessionImpl cs=
						m_sessionManager.getCorrelationSessionImpl(ids);

					// If session not returned, then assume that
					// it has already been tied up by the internal
					// state of the monitored sessions
					if (cs != null) {
						cs.disassociateServiceSession(
								record.getServiceDescriptionName());
						
						// Check if correlation session has completed
						if (cs.getNumberOfServiceSessions() == 0) {							
							m_sessionManager.pendingClose(cs);
						}
					}
				} else if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Tracker event type not handled by correlator: "+
							event.getEventType());
				}
			}
		}
		
		// TODO: Could possibly use the 'serviceFinished'
		// events to indicate that the participant (service)
		// will no longer be sending further messages -
		// how can we use this information? Possibly ensuring
		// that the correlated participant also indicates
		// the end? Possibly send to the 'correlated session'
		// which can then enable the information to be
		// presented
	}
	
	/**
	 * This method returns the correlation session manager.
	 * 
	 * @return The correlation session manager
	 */
	protected CorrelationSessionManager getCorrelationSessionManager() {
		return(m_sessionManager);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.correlator.impl");		
	
	private Hashtable m_choreographyDescriptions=new Hashtable();
	private Hashtable m_serviceMonitors=new Hashtable();
	private CorrelatingServiceTracker m_correlatingServiceTracker=null;
	private CorrelationSessionManager m_sessionManager=null;
	private CorrelationNotifier m_notifier=null;
}
