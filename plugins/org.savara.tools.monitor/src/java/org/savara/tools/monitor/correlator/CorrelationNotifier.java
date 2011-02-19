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
 * Sep 9, 2005 : Initial version created by gary
 */
package org.savara.tools.monitor.correlator;

import java.util.Vector;

import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.Channel;
import org.pi4soa.service.Message;
import org.pi4soa.service.correlator.CorrelationSession;
import org.pi4soa.service.correlator.ServiceCorrelatorListener;

public class CorrelationNotifier implements ServiceCorrelatorListener {

	/**
	 * This method indicates that the supplied choreography
	 * description has been registered with the service
	 * correlator.
	 * 
	 * @param cdlpack The choreography description
	 */
	public void choreographyDescriptionRegistered(org.pi4soa.cdl.Package cdlpack) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.choreographyDescriptionRegistered(cdlpack);
			}
		}
	}
	
	/**
	 * This method indicates that the supplied choreography
	 * description has been unregistered from the service
	 * correlator.
	 * 
	 * @param cdlpack The choreography description
	 */
	public void choreographyDescriptionUnregistered(org.pi4soa.cdl.Package cdlpack) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.choreographyDescriptionRegistered(cdlpack);
			}
		}
	}
	
	/**
	 * This method indicates that a new correlated session has
	 * been started.
	 * 
	 * @param session The session
	 */
	public void sessionStarted(CorrelationSession session) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.sessionStarted(session);
			}
		}
	}
	
	/**
	 * This method indicates that a correlated session has
	 * been finished.
	 * 
	 * @param session The session
	 */
	public void sessionFinished(CorrelationSession session) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.sessionFinished(session);
			}
		}
	}
	
	/**
	 * This method is invoked to indicate that a choreography
	 * exchange has been initiated by one participant.
	 * 
	 * @param exchange The exchange details
	 * @param channel The channel associated with the exchange
	 * @param message The message
	 * @param session The session
	 * @param serviceDescriptionName The name of the service
	 * 				description that caused this exchange to
	 * 				be initiated
	 */
	public void exchangeInitiated(ExchangeDetails exchange,
			Channel channel, Message message,
			CorrelationSession session, String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.exchangeInitiated(exchange,
						channel, message, session,
						serviceDescriptionName);
			}
		}
	}

	/**
	 * This method is invoked to indicate that a choreography
	 * exchange has been completed by the target participant.
	 * 
	 * @param exchange The exchange details
	 * @param channel The channel associated with the exchange
	 * @param message The message
	 * @param session The session
	 * @param serviceDescriptionName The name of the service
	 * 				description that caused this exchange to
	 * 				be completed
	 */
	public void exchangeCompleted(ExchangeDetails exchange,
			Channel channel, Message message,
			CorrelationSession session,	String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.exchangeCompleted(exchange,
						channel, message, session,
						serviceDescriptionName);
			}
		}
	}
	
	/**
	 * This method is invoked to indicate that a message
	 * was unexpected at a participant.
	 * 
	 * @param message The message
	 * @param session The session
	 * @param serviceDescriptionName The name of the service
	 * 				description that caused this unexpected
	 * 				message error
	 */
	public void unexpectedMessage(Message message,
			CorrelationSession session, String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.unexpectedMessage(message, session,
						serviceDescriptionName);
			}
		}
	}
	
    /**
     * An error occurred related to the specified correlation
     * session.
     * 
     * @param mesg The error message
     * @param exception The optional exception details
     * @param session The correlation session
     * @param serviceDescriptionName The service name
     */
    public void error(String mesg, String exception,
    		CorrelationSession session, String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.error(mesg, exception, session,
						serviceDescriptionName);
			}
		}
	}
    
    /**
     * A warning occurred related to the specified correlation
     * session.
     * 
     * @param mesg The warning message
     * @param exception The optional exception details
     * @param session The correlation session
     * @param serviceDescriptionName The service name
     */
    public void warning(String mesg, String exception,
    		CorrelationSession session, String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.warning(mesg, exception, session,
						serviceDescriptionName);
			}
		}
	}
    
    /**
     * An information event occurred related to the specified correlation
     * session.
     * 
     * @param mesg The information message
     * @param session The correlation session
     * @param serviceDescriptionName The service name
     */
    public void information(String mesg, CorrelationSession session,
			String serviceDescriptionName) {
		
		synchronized(m_listeners) {
			for (int i=0; i < m_listeners.size(); i++) {
				ServiceCorrelatorListener l=
					(ServiceCorrelatorListener)m_listeners.get(i);
				
				l.information(mesg, session,
						serviceDescriptionName);
			}
		}
	}
    
	/**
	 * This method adds a listener for notifications regarding
	 * correlated sessions.
	 * 
	 * @param l The listener
	 */
	public void addServiceCorrelatorListener(ServiceCorrelatorListener l) {
		synchronized(m_listeners) {
			m_listeners.add(l);
		}
	}
	
	/**
	 * This method removes a listener for notifications regarding
	 * correlated sessions.
	 * 
	 * @param l The listener
	 */
	public void removeServiceCorrelatorListener(ServiceCorrelatorListener l) {
		synchronized(m_listeners) {
			m_listeners.remove(l);
		}
	}
	
	private Vector m_listeners=new Vector();	
}
