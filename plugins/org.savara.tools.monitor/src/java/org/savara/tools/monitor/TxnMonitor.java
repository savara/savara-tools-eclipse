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

import java.util.logging.Logger;

import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.Channel;
import org.pi4soa.service.Message;
import org.pi4soa.service.ServiceException;
import org.pi4soa.service.correlator.CorrelationSession;
import org.pi4soa.service.correlator.ServiceCorrelator;
import org.pi4soa.service.correlator.ServiceCorrelatorFactory;
import org.pi4soa.service.correlator.ServiceCorrelatorListener;
import org.pi4soa.service.tracker.jms.JMSServiceTrackerClient;
import org.savara.tools.monitor.correlator.ServiceCorrelatorImpl;

/**
 * The TxnMonitor class is a generic transaction monitor class that
 * takes in a CDL description and monitors the progress of transactions,
 * which are long lived interactions, against the CDL description.
 * A TxnMonitor MAY be used as a generic monitor for any CDL description.
 *
 */
public class TxnMonitor {

    public TxnMonitor(String fname)
    {
        m_cdlFile = fname;
    }
    
    public void initialize() throws ServiceException
    {
        
        m_correlator=new ServiceCorrelatorImpl();	//ServiceCorrelatorFactory.getServiceCorrelator();
        m_correlator.addServiceCorrelatorListener(new CorrelatorListener());
        
        // Obtain service tracker client
        m_trackerClient = new ServiceTrackerClient();
        
        // Check if system properties have been provided for JNDI
        if (System.getProperty("java.naming.factory.initial") != null) {
        	m_trackerClient.setJNDIInitialContextFactory(System.getProperty("java.naming.factory.initial"));
        }
        
        if (System.getProperty("java.naming.provider.url") != null) {
        	m_trackerClient.setJNDIProviderURL(System.getProperty("java.naming.provider.url"));
        }
        
        if (System.getProperty("java.naming.factory.url.pkgs") != null) {
        	m_trackerClient.setJNDIFactoryURLPackages(System.getProperty("java.naming.factory.url.pkgs"));
        }
        
        if (System.getProperty("java.messaging.factory") != null) {
        	m_trackerClient.setJMSConnectionFactory(System.getProperty("java.messaging.factory"));
        }
        
        if (System.getProperty("java.messaging.destination") != null) {
        	m_trackerClient.setJMSDestination(System.getProperty("java.messaging.destination"));
        }
        
        // Add correlator as listener to the tracker
        m_trackerClient.addServiceTrackerListener(m_correlator);
        
        //System.err.println("");            
        //System.err.println("Staring to monitor events against " + m_cdlFile + " .... ");
        //System.err.println("");
        try {
        	monitor(m_cdlFile);
        } catch(java.io.IOException ioe) {
        	throw new ServiceException("Failed to initialize monitor for '"+
        			m_cdlFile+"'", ioe);
        }
    }
    
    /**
     * This method returns the singleton for the TxnMonitor.
     * 
     * @return The singleton
     * @exception ServiceException Failed to obtain monitor
     */
    public static TxnMonitor getInstance(String fname) throws ServiceException {
        // TODO: See if there is a better way to make an instance
        // available to a view
        if (m_instance == null) {
            m_instance = new TxnMonitor(fname);
            m_instance.initialize();
        }
        
        return(m_instance);
    }
    
    /**
     * This method loads the choreography description associated
     * with the supplied filename, and then monitors it, before
     * returning it to the caller.
     * 
     * @param filename The choreography description filename
     * @return The choreography description being monitored
     * @throws java.io.IOException Failed to load
     * @throws ServiceException Failed to monitor
     */
    public org.pi4soa.cdl.Package monitor(String filename)
                        throws java.io.IOException, ServiceException {
        org.pi4soa.cdl.Package ret=
            org.pi4soa.cdl.CDLManager.load(filename);
        
        monitor(ret);
        
        return(ret);
    }
    
    /**
     * This method initializes the correlator with the choreography
     * description to be monitored.
     * 
     * @param cdl The choreography description
     * @exception ServiceException Failed to monitor choreography
     *                         description
     */
    public void monitor(org.pi4soa.cdl.Package cdl)
                        throws ServiceException {        
        m_correlator.register(cdl);
    }
    
    /**
     * This method disassociates the correlator from the choreography
     * description being monitored.
     * 
     * @param cdl The choreography description
     * @exception ServiceException Failed to stop monitor choreography
     *                         description
     */
    public void unmonitor(org.pi4soa.cdl.Package cdl)
                        throws ServiceException {        
        m_correlator.unregister(cdl);
    }
    
    /**
     * @param args - the first argument in args is the location of the
     * CDM file that will be used to drive the monitoring.
     */
    public static void main(String[] args) 
    {
        // TODO Auto-generated method stub
        String s = "Constructing a monitor for " + args[0];
        logger.fine(s);
        
        try {
        	TxnMonitor mon = TxnMonitor.getInstance(args[0]);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * This method will return an existing list, associated with the
     * supplied session, or if not found, will create one - unless
     * the session's event list has previously been removed and
     * therefore a null will be returned (i.e. events to be
     * ignored).
     * 
     * @param session The session
     * @return The list, or null if session to be ignored
     */
    protected java.util.List createEventList(CorrelationSession session) {
        java.util.List ret=null;
        
        logger.fine("createEventList for session - " + session);
        
        synchronized(m_eventLists) {
            ret = (java.util.List)m_eventLists.get(session);
        
            if (ret == null) {
                ret = new java.util.Vector();

                logger.fine("Creating event list for session: "+session);
                
                m_eventLists.put(session, ret);
            }
        }
        
        logger.fine("createEventList returning - " + ret);

        return(ret);
    }
    
    /**
     * This method will remove the event list associated with
     * the supplied session, and add the session to the list
     * of sessions to be ignored from now on.
     * 
     * @param session The session
     */
    protected void removeEventList(CorrelationSession session) {
        
        synchronized(m_eventLists) {

            logger.fine("Remove correlation session: "+session);
            
            m_eventLists.remove(session);
        }
    }

    protected void fireSessionStarted(CorrelationSession session) {
            System.err.println(">>>> fireSessionStarted");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.correlationSessionStarted(session);
        }
            System.err.println(">>>> finished fireSessionStarted");
    }
    
    protected void fireSessionFinished(CorrelationSession session) {
            System.err.println(">>>> fireSessionFinished");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.correlationSessionFinished(session);
        }
            logger.fine(">>>> finished fireSessionFinished");
    }
    
    protected void fireUpdatedSession(CorrelationSession session) {
            logger.fine(">>>> fireSessionUpdated");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
                        //            l.correlationSessionUpdated(session);
        }
            logger.fine(">>>> finished fireSessionUpdated");
    }


    protected void fireAddedExchangeEvent(ExchangeEvent exchangeEvent) {
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.exchangeEventAdded(exchangeEvent);
        }
    }

    protected void fireUpdatedExchangeEvent(ExchangeEvent exchangeEvent) {
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.exchangeEventUpdated(exchangeEvent);
        }
    }
    
    protected void fireAddedUnexpectedExchangeEvent(ExchangeEvent exchangeEvent,
    				String serviceName) {
    	logger.fine(">>>> fireAddedUnexpectedExchangeEvent");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.unexpectedExchangeEventAdded(exchangeEvent, serviceName);
        }
    	logger.fine("<<<< fireAddedUnexpectedExchangeEvent");
    }
    
    protected void fireErrorEvent(CorrelationSession session,
    				String mesg, String exception, String serviceName) {
    	logger.fine(">>>> fireErrorEvent");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.error(session, mesg, exception, serviceName);
        }
    	logger.fine("<<<< fireErrorEvent");
    }
    
    protected void fireWarningEvent(CorrelationSession session,
    					String mesg, String exception, String serviceName) {
    	logger.fine(">>>> fireWarningEvent");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.warning(session, mesg, exception, serviceName);
        }
    	logger.fine("<<<< fireWarningEvent");
    }
    
    protected void fireInformationEvent(CorrelationSession session,
    						String mesg, String serviceName) {
    	logger.fine(">>>> fireInformationEvent");
        for (int i=0; i < m_listeners.size(); i++) {
            CorrelationManagerListener l=
                    (CorrelationManagerListener)m_listeners.get(i);
            
            l.information(session, mesg, serviceName);
        }
    	logger.fine("<<<< fireInformationEvent");
    }
    
    public void addCorrelationManagerListener(CorrelationManagerListener l) {
            logger.fine(">>>> addCorrelationManagerListener");
        m_listeners.add(l);
    }
    
    public void removeCorrelationManagerListener(CorrelationManagerListener l) {
            logger.fine(">>>> removeCorrelationManagerListener");
        m_listeners.remove(l);
    }
    
    private static Logger logger = Logger.getLogger("org.savara.tools.monitor");
    
    private static TxnMonitor m_instance=null;
    private ServiceCorrelator m_correlator=null;
    private JMSServiceTrackerClient m_trackerClient=null;
    private java.util.Hashtable m_eventLists=new java.util.Hashtable();
    private java.util.Vector m_listeners=new java.util.Vector();
    private String m_cdlFile = null;
    
    
    public class CorrelatorListener implements ServiceCorrelatorListener {
        
        /**
         * This method indicates that the supplied choreography
         * description has been registered with the service
         * correlator.
         * 
         * @param cdlpack The choreography description
         */
        public void choreographyDescriptionRegistered(org.pi4soa.cdl.Package cdlpack) {
                    logger.fine(">>>> choreographyDescriptionRegistered");            
        }
        
        /**
         * This method indicates that the supplied choreography
         * description has been unregistered from the service
         * correlator.
         * 
         * @param cdlpack The choreography description
         */
        public void choreographyDescriptionUnregistered(org.pi4soa.cdl.Package cdlpack) {
                    logger.fine(">>>> choreographyDescriptionUnregistered");                        
        }
        
        /**
         * This method indicates that a new correlated session has
         * been started.
         * 
         * @param session The session
         */
        public void sessionStarted(CorrelationSession session) {
                    logger.fine(">>>> sessionStarted");
            fireSessionStarted(session);
        }
        
        /**
         * This method indicates that a correlated session has
         * been finished.
         * 
         * @param session The session
         */
        public void sessionFinished(CorrelationSession session) {
                    logger.fine(">>>> sessionFinished");
            fireSessionFinished(session);
        }
        
        /**
         * This method is invoked to indicate that a choreography
         * exchange has been initiated by one participant.
         * 
         * @param exchange The exchange details
         * @param channel The channel associated with the exchange
         * @param session The session
         * @param serviceDescriptionName The name of the service
         *                 description that caused this exchange to
         *                 be initiated
         */
        public synchronized void exchangeInitiated(ExchangeDetails exchange,
                Channel channel, Message message, CorrelationSession session,
                        String serviceDescriptionName) {

            logger.fine(">>>> exchangeInitiated");

            java.util.List list = createEventList(session);
            
            if(list != null){
                ExchangeEvent event = new ExchangeEvent(exchange, channel, session, serviceDescriptionName, message);
                int index = 0;
                boolean f_updated=false;
                
                if ((index=list.indexOf(event)) == -1) {
                    logger.fine("Add new event '"+event+ "' to correlation session "+session);
                    list.add(event);
                } 
                else{
                    logger.fine("Retrieve existing event for index "+index);
                    ExchangeEvent existingEvent=
                    		(ExchangeEvent)list.get(index);
                    
                    if (existingEvent.getInitiated() == false) {
                    	event = existingEvent;
                    	
                    	// Remove event from list
                    	list.remove(index);
                    	
                    	if (list.size() == 0) {
                    		// Remove list
                    		removeEventList(session);
                    	}
                    	
                    	f_updated = true;
                    } else {
                        logger.fine("Add new event '"+event+ "' to correlation session "+session);
                        list.add(event);
                    }
                }
                
                event.initiated();
                logger.fine("EXCHANGE INITIATED: ");
                logger.fine("    CHANNEL DETAILS: " + channel + " (" + channel.getName() + ") session " + session);
                logger.fine("    EXCHANGE DETAILS: " + exchange);

                if(f_updated == false){
                    fireAddedExchangeEvent(event);
                }
                else{
                    fireUpdatedExchangeEvent(event);
                }
                //                fireUpdatedSession(session);
            }
        }

        /**
         * This method is invoked to indicate that a choreography
         * exchange has been completed by the target participant.
         * 
         * @param exchange The exchange details
         * @param channel The channel associated with the exchange
         * @param session The session
         * @param serviceDescriptionName The name of the service
         *                 description that caused this exchange to
         *                 be completed
         */
        public synchronized void exchangeCompleted(ExchangeDetails exchange,
                Channel channel, Message message, CorrelationSession session,
                        String serviceDescriptionName) {

            logger.fine(">>>> exchangeCompleted");
            
            java.util.List list=createEventList(session);
            
            if (list != null) {
                ExchangeEvent event=new ExchangeEvent(exchange, channel, session, serviceDescriptionName, message);
                
                int index = 0;
                
                if ((index=list.indexOf(event)) == -1) {
                    logger.fine("Add new event '"+event+ "' to correlation session "+session);
                    list.add(event);
                } 
                else{
                    logger.fine("Retrieve existing event for index "+index);
                    ExchangeEvent existingEvent=
                    		(ExchangeEvent)list.get(index);
                    
                    if (existingEvent.getCompleted() == false) {
                    	event = existingEvent;
                    	
                    	// Remove event from list
                    	list.remove(index);
                    	
                    	if (list.size() == 0) {
                    		// Remove list
                    		removeEventList(session);
                    	}
                    } else {
                        logger.fine("Add new event '"+event+ "' to correlation session "+session);
                        list.add(event);
                    }
                }
                
                event.completed();
                logger.fine("EXCHANGE COMPLETED: ");
                                //                logger.fine("    CHANNEL DETAILS: " + channel);
                logger.fine("    CHANNEL DETAILS: " + channel + " (" + channel.getName() + ") session " + session);
                logger.fine("    EXCHANGE DETAILS: " + exchange);

                if(index == -1){
                    fireAddedExchangeEvent(event);
                }
                else{
                    fireUpdatedExchangeEvent(event);
                }
                //                fireUpdatedSession(session);
                logger.fine("<<<< exchangeCompleted");
            }
        }
        
        /**
         * This method is invoked to indicate that a message
         * was unexpected at a participant.
         * 
         * @param message The message
         * @param session The session
         * @param serviceDescriptionName The name of the service
         *                 description that caused this unexpected
         *                 message error
         */
        public synchronized void unexpectedMessage(Message message,
                CorrelationSession session, String serviceDescriptionName) {

            logger.fine(">>>> unexpectedMessage");
            logger.fine("MESSAGE: " + message + " SESSION: " + session + " SERVICE: " + serviceDescriptionName);
            logger.fine("MESSAGE: " + message);
            logger.fine("Identities: " + message.getChannelIdentity());
            //java.util.List list = createEventList(session);

            //if(list != null){
                ExchangeDetails exchange = null;
                Channel channel = null;

                logger.fine(">>>> creating unexpected event");

                ExchangeEvent event = new ExchangeEvent(exchange, channel, session, serviceDescriptionName, message);

                logger.fine(">>>> created unexpected event");
                
                logger.fine("Add unexpected new event '"+event+ "' to correlation session "+session);
                //list.add(event);
 
                logger.fine(">>>> UNEXPECTED EVENT for session: " + session);
                
                fireAddedUnexpectedExchangeEvent(event, serviceDescriptionName);


                logger.fine(">>>> HANDLED UNEXPECTED EVENT for session: " + session);
            //}
            logger.fine("<<<< unexpectedMessage");
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
            logger.fine(">>>> error");

            fireErrorEvent(session, mesg, exception, serviceDescriptionName);

            logger.fine("<<<< error");
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
            logger.fine(">>>> warning");

            fireWarningEvent(session, mesg, exception, serviceDescriptionName);

            logger.fine("<<<< warning");
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
            logger.fine(">>>> information");

            fireInformationEvent(session, mesg, serviceDescriptionName);

            logger.fine("<<<< information");
        }
    }
}
