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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.cdl.CDLType;
import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.cdl.util.CDLTypeUtil;
import org.pi4soa.service.Channel;
import org.pi4soa.service.Message;
import org.pi4soa.service.behavior.MessageDefinition;
import org.pi4soa.service.behavior.Receive;
import org.pi4soa.service.behavior.Send;
import org.pi4soa.service.behavior.ServiceDescription;
import org.pi4soa.service.session.Session;
import org.pi4soa.service.session.internal.InternalSession;
import org.pi4soa.service.tracker.ServiceTracker;

/**
 * This class provides an implementation of the service tracker
 * interface, used to detect tracker events from distributed
 * service endpoints and correlate them back to the choreography
 * description being monitored.
 *
 */
public class CorrelatingServiceTracker implements ServiceTracker {

	private static final String UNEXPECTED_MESSAGE_EXCEPTION = "UnexpectedMessageException";

	private static final String UNHANDLED_EXCEPTION = "Unhandled exception: ";

	private static final String CORRELATER_REPORTED = "[Correlater reported] ";

	/**
	 * This is the constructor for the correlating service
	 * tracker.
	 *
	 * @param mgr The correlation session manager
	 * @param notifier The correlation notifier
	 */
	public CorrelatingServiceTracker(CorrelationSessionManager mgr,
			CorrelationNotifier notifier) {
		m_sessionManager = mgr;
		m_notifier = notifier;
	}
	
	/**
	 * This method initializes the service tracker.
	 *
	 */
	public void initialize() {
		
	}
	
	/**
	 * This method indicates that a new service instance
	 * has started.
	 * 
	 * @param service The service
	 * @param session The session
	 */
	public void serviceStarted(ServiceDescription service,
				Session session) {
		
		// The CorrelationSessionManager overrides the 'addSession'
		// method on the superclass DefaultSessionManager, to
		// intercept the addition of new service sessions
		// which are used to create or associated with existing
		// correlation sessions. This overridden method is used
		// instead of this tracker method, because by the time
		// the overridden method is called, the session has
		// identity information, whereas this method is invoked
		// just after the newly created service session is
		// instantiated.
	}

	/**
	 * This method indicates that a service instance
	 * has finished.
	 * 
	 * @param service The service
	 * @param session The session
	 */
	public void serviceFinished(ServiceDescription service,
				Session session) {
		
		// The overridden 'removeSession' method in the
		// CorrelationSessionManager is used, instead of this
		// tracker callback, to remove a session from the
		// associated correlation session - to remain
		// symmetrical with the way that the session is associated
		// (see note above).
	}

	/**
	 * This method indicates that a new sub session
	 * has started within an existing service instance.
	 * 
	 * @param parent The parent session
	 * @param session The session
	 */
	public void subSessionStarted(Session parent, Session session) {
	}
	
	/**
	 * This method indicates that an existing
	 * sub session has finished.
	 * 
	 * @param parent The parent session
	 * @param session The session
	 */
	public void subSessionFinished(Session parent, Session session) {
	}
	
	/**
	 * This method registers the fact that a message has been
	 * sent.
	 * 
	 * @param activity The behavioral activity, or null if a stateless service
	 * @param session The session, or null if a stateless service
	 * @param channel The channel, or null if a stateless service
	 * @param mesg The message that has been handled
	 */
	public void sentMessage(Send activity, Session session,
					Channel channel, Message mesg) {
		
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, mesg);
		
		if (corrSession != null) {

			// Derive the choreography description node associated
			// with the behavioral send activity
			CDLType cdlType=CDLTypeUtil.getCDLType(
					corrSession.getChoreographyDescription(),
						activity.getGlobalDescriptionURI());
			
			if (cdlType == null) {
				logger.severe("Failed to locate CDL type associated " +
						"with behavior activity '"+activity+
						"' in choreography '"+
						corrSession.getChoreographyDescription()+"'");
				
			} else if (cdlType instanceof ExchangeDetails) {
				
				corrSession.exchangeInitiated((ExchangeDetails)cdlType,
								channel);
				
				// Notify any registered listeners
				m_notifier.exchangeInitiated((ExchangeDetails)cdlType,
							channel, mesg, corrSession,
							session.getId().getServiceDescriptionName());
			} else {
				logger.severe("CDL type '"+cdlType+
						"', associated with behavior activity '"+
						activity+"' is not an ExchangeDetails type");
			}
		}
	}
	
	/**
	 * This method registers the fact that a message has been
	 * sent from a stateless service.
	 * 
	 * @param defn The message definition
	 * @param mesg The message that has been handled
	 */
	public void sentMessage(MessageDefinition defn, Message mesg) {
		
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(null, mesg);
		
		if (corrSession != null) {

			// TODO: Not sure if we need to report a stateless
			// message event to the correlation session??
			//corrSession.exchangeInitiated((ExchangeDetails)cdlType,
			//				channel);
			
			// Notify any registered listeners
			m_notifier.exchangeInitiated(null, null, mesg, corrSession,
						defn.getServiceDescription().getName());
		}
	}
	
	/**
	 * This method registers the fact that a message has been
	 * received.
	 * 
	 * @param activity The behavioral activity, or null if a stateless service
	 * @param session The session, or null if a stateless service
	 * @param channel The channel, or null if a stateless service
	 * @param mesg The message that has been handled
	 */
	public void receivedMessage(Receive activity, Session session,
					Channel channel, Message mesg) {
		
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
				getCorrelationSession(session, mesg);

		if (corrSession != null) {

			// Derive the choreography description node associated
			// with the behavioral receive activity
			CDLType cdlType=CDLTypeUtil.getCDLType(
					corrSession.getChoreographyDescription(),
						activity.getGlobalDescriptionURI());
			
			if (cdlType == null) {
				logger.severe("Failed to locate CDL type associated " +
						"with behavior activity '"+activity+
						"' in choreography '"+
						corrSession.getChoreographyDescription()+"'");
				
			} else if (cdlType instanceof ExchangeDetails) {
				
				corrSession.exchangeCompleted((ExchangeDetails)cdlType,
								channel);
				
				// Notify any registered listeners
				m_notifier.exchangeCompleted((ExchangeDetails)cdlType,
						channel, mesg, corrSession,
						session.getId().getServiceDescriptionName());
				
			} else {
				logger.severe("CDL type '"+cdlType+
						"', associated with behavior activity '"+
						activity+"' is not an ExchangeDetails type");
			}
		}
	}
	
	/**
	 * This method registers the fact that a message has been
	 * received from a stateless service.
	 * 
	 * @param defn The message definition
	 * @param mesg The message that has been handled
	 */
	public void receivedMessage(MessageDefinition defn, Message mesg) {
		
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(null, mesg);
		
		if (corrSession != null) {

			// TODO: Not sure if we need to report a stateless
			// message event to the correlation session??
			//corrSession.exchangeCompleted((ExchangeDetails)cdlType,
			//				channel);
			
			// Notify any registered listeners
			m_notifier.exchangeCompleted(null, null, mesg, corrSession,
						defn.getServiceDescription().getName());
		}
	}

	/**
	 * This method registers that a message was not expected.
	 * 
	 * @param session The session, or null if a stateless service
	 * @param mesg The message that was not expected
	 * @param reason The optional reason why the message was
	 * 					considered to be unexpected
	 * @deprecated Use unexpectedMessage(ServiceDescription sdesc,
	 *		Session session, Message mesg, String reason)
	 */
	public void unexpectedMessage(Session session, Message mesg,
							String reason) {
	}
	
	/**
	 * This method registers that a message was not expected.
	 * 
	 * @param sdesc The service description, if known
	 * @param session The session, or null if a stateless service,
	 * 				or cannot be associated with a session
	 * @param mesg The message that was not expected
	 * @param reason The optional reason why the message was
	 * 					considered to be unexpected
	 */
	public void unexpectedMessage(ServiceDescription sdesc,
			Session session, Message mesg, String reason) {
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, null);
		
		if (corrSession != null) {
			m_notifier.error(CORRELATER_REPORTED+mesg,
					UNEXPECTED_MESSAGE_EXCEPTION, corrSession,
					session.getId().getServiceDescriptionName());
		}		
	}
	
	/**
	 * This method registers that an exception was not handled.
	 * 
	 * @param session The session, or null if a stateless service
	 * @param excType The exception type
	 */
	public void unhandledException(Session session, String excType) {
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, null);
		
		if (corrSession != null) {
			m_notifier.error(CORRELATER_REPORTED+
					UNHANDLED_EXCEPTION+excType,
					excType, corrSession,
					session.getId().getServiceDescriptionName());
		}
	}
	
	/**
	 * This method reports information regarding the processing
	 * of a service session. The details can either be specified
	 * as a textual string (unstructured data), 
	 * or as a structured XML fragment.<p>
	 * 
	 * @param session The session, or null if a stateless service
	 * @param details The details
	 * @param type The optional type
	 */
	public void information(Session session, String details) {
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, null);
		
		if (corrSession != null) {
			m_notifier.information(CORRELATER_REPORTED+details,
					corrSession,
					session.getId().getServiceDescriptionName());
		}
	}
	
	/**
	 * This method reports information regarding the processing
	 * of a service session. The details can either be specified
	 * as a textual string (unstructured data), 
	 * or as a structured XML fragment.<p>
	 * 
	 * @param session The session, or null if a stateless service
	 * @param details The details
	 * @param exc The optional exception
	 */
	public void warning(Session session, String details, Throwable exc) {

		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, null);
		
		if (corrSession != null) {
			m_notifier.warning(CORRELATER_REPORTED+details,
					exc.toString(), corrSession,
					session.getId().getServiceDescriptionName());
		}
	}
	
	/**
	 * This method reports information regarding the processing
	 * of a service session. The details can either be specified
	 * as a textual string (unstructured data), 
	 * or as a structured XML fragment.<p>
	 * 
	 * @param session The session, or null if a stateless service
	 * @param details The details
	 * @param exc The optional exception
	 */
	public void error(Session session, String details, Throwable exc) {
		// Get the correlation session for the service session
		CorrelationSessionImpl corrSession=
					getCorrelationSession(session, null);
		
		if (corrSession != null) {
			m_notifier.error(CORRELATER_REPORTED+details,
					exc.toString(), corrSession,
					session.getId().getServiceDescriptionName());
		}
	}
	
	/**
	 * This method closes the service tracker.
	 *
	 */
	public void close() {
	}

	/**
	 * This method returns the top level session associated with
	 * the supplied session.
	 * 
	 * @param subsession The subsession
	 * @return The top level session
	 */
	protected Session getTopLevelSession(Session subsession) {
		Session ret=subsession;
		
		while (ret instanceof InternalSession &&
				((InternalSession)ret).getParent() != null) {
			ret = ((InternalSession)ret).getParent();
		}
		
		return(ret);
	}
	
	/**
	 * This method determines which correlation session should be
	 * used based on the supplied session and/or message.
	 * 
	 * @param session The session, or null if stateless session
	 * @param mesg The message
	 * @return The correlation session, or null if not found
	 */
	protected CorrelationSessionImpl getCorrelationSession(Session session,
							Message mesg) {
		CorrelationSessionImpl ret=null;
		
		if (session != null) {
			
			Session topLevelSession=getTopLevelSession(session);
			
			if (topLevelSession != null) {
				ret = m_sessionManager.getCorrelationSessionImpl(topLevelSession, mesg);
			} else {
				logger.warning("Failed to find top level session for '"+
						session+"'");
			}
			
			if (ret == null) {
				logger.severe("Failed to locate correlation session " +
						"for service session '"+topLevelSession+"'");
			}
		} else if (mesg != null) {
			
			// Stateless service
			ret = m_sessionManager.getCorrelationSessionImpl(
						mesg.getMessageIdentities());
			
			if (ret == null) {
				StringBuffer buf=new StringBuffer();
				
				for (int i=0; mesg.getMessageIdentities() != null &&
						i < mesg.getMessageIdentities().size(); i++) {
					
					if (i > 0) {
						buf.append(',');
					}
					buf.append(mesg.getMessageIdentities().get(i).getId());
				}
				
				logger.severe("Failed to locate correlation session " +
						"for stateless service with identities '"+
						buf.toString()+"'");
				
			} else if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found correlation session '"+ret+
							"' for stateless service");
			}
		}
		
		return(ret);
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.service.correlator.impl");
	
	private CorrelationSessionManager m_sessionManager=null;
	private CorrelationNotifier m_notifier=null;
}
