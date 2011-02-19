/*
 * Copyright 2005-8 Pi4 Technologies Ltd
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
 * 16 Jan, 2008 : Initial version created by martin
 */
package org.savara.tools.monitor;

import org.pi4soa.service.correlator.CorrelationSession;

/**
 * This interface represents a listener interested in changes
 * that occur within the correlation manager.
 *
 */
public interface CorrelationManagerListener {

	/**
	 * This method indicates that a correlation session has
	 * started.
	 * 
	 * @param session The correlation session
	 */
	public void correlationSessionStarted(CorrelationSession session);
	
	/**
	 * This method indicates that a correlation session has
	 * finished.
	 * 
	 * @param session The correlation session
	 */
	public void correlationSessionFinished(CorrelationSession session);

    /**
     * A new exchange event has been added to a correlation session.
     *
     * @param exchangeEvent The exchange event.
     */
    public void exchangeEventAdded(ExchangeEvent exchangeEvent);


    /**
     * An exchange event has been updated.
     *
     * @param exchangeEvent The exchange event.
     */
    public void exchangeEventUpdated(ExchangeEvent exchangeEvent);
    
    /**
     * An unexpected event has occured
     * 
     * @param exchangeEvent. The exchange event.
     * @param serviceName The service reporting the error
     */
    public void unexpectedExchangeEventAdded(ExchangeEvent exchangeEvent,
    					String serviceName);

    /**
     * An error occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The error message
     * @param exception The optional exception trace
     * @param serviceName The service reporting the error
     */
    public void error(CorrelationSession session, String mesg,
    						String exception, String serviceName);
    
    /**
     * A warning occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The warning message
     * @param exception The optional exception trace
     * @param serviceName The service reporting the warning
     */
    public void warning(CorrelationSession session, String mesg,
							String exception, String serviceName);
    
    /**
     * An information event occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The information message
     * @param serviceName The service reporting the information
     */
    public void information(CorrelationSession session,
    					String mesg, String serviceName);
    
}