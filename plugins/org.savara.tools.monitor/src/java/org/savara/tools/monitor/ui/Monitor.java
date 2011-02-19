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
package org.savara.tools.monitor.ui;

import javax.swing.*;

import java.awt.BorderLayout;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.service.correlator.CorrelationSession;
import org.savara.tools.monitor.CorrelationManagerListener;
import org.savara.tools.monitor.ExchangeEvent;
import org.savara.tools.monitor.TxnMonitor;

/**
 * Monitor user interface class.
 */
public class Monitor extends JApplet implements CorrelationManagerListener /* , TreeSelectionListener */{

	private static final long serialVersionUID = -4323114698930604980L;

	private static Logger logger = Logger.getLogger("org.savara.tools.monitor.ui");
    
    static final int NEW_FRAME_WIDTH = 800;
    static final int NEW_FRAME_HEIGHT = 600;

    MonitorMainPanel smPanel = null;

    String choreographyPath = null;

    org.pi4soa.cdl.Package choreography = null;

    TxnMonitor txnMonitor = null;

    ExchangeEventsData exchangeEventsData = null;

    boolean isMonitoring = false;
    boolean choreographyProvided = false;

    /**
     * Initialisation.
     */
    public Monitor(boolean choreoProvided) {

        exchangeEventsData = new ExchangeEventsData();

        smPanel = new MonitorMainPanel(exchangeEventsData, choreoProvided);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(smPanel, BorderLayout.CENTER);

        smPanel.setApplet(this);
    }


    /**
     * Initialisation.
     */
    public void init() {
        // hack to get around system event queue check - which doesn't work, of course
        this.getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
    }


    /**
     * Returns info about the acceptable parameters
     */
    public String[][] getParameterInfo()
    {
        String pinfo[][] = {
            // actual parameters might include
            // ip address, directories, colors
            // etc.

            // properties should be used for
            // most stuff

            {"parameter1", "String", "A String."},
            {"parameter2", "String",    "Another String."},
            {"parameter3", "integer",    "An integer."},
        };

        return pinfo;
    }

    /**
     *
     */
    public static void main(String args[]){
        Monitor monitor = new Monitor(args.length > 0);
        JFrame jFrame = new JFrame("SAVARA Monitor");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setLayout(new BorderLayout());
        jFrame.getContentPane().add(monitor, BorderLayout.CENTER);
        monitor.init();

        jFrame.setIconImage(MonitorMainPanel.createImageIcon("icons/monitor.png").getImage());
        
        jFrame.setSize(NEW_FRAME_WIDTH, NEW_FRAME_HEIGHT);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension frameSize = jFrame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        jFrame.setLocation( (screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
        jFrame.setVisible(true);
        
        if(args.length > 0){
            if(monitor.loadChoreography(args[0])){
                monitor.startMonitoring();
            } else {
            	System.exit(1);
            }
        }
    }

    //////////////////////////


    /**
     *
     */
    public boolean isMonitoring(){ return isMonitoring; }

    public boolean isChoreographyLoaded() { return choreography!=null; }

    /**
     *
     */
    public void setIsMonitoring(boolean isMonitoring){ this.isMonitoring = isMonitoring; }

    public void close() {
    	this.destroy();
    	System.exit(0);
    }

    /**
     *
     */
    public boolean loadChoreography(String choreographyPath){

        logger.info("Loading " + choreographyPath);
        smPanel.setStatus("Loading " + choreographyPath);

        // try and read the new one ...
        org.pi4soa.cdl.Package newChoreography = null;

        try{
            newChoreography = org.pi4soa.cdl.CDLManager.load(choreographyPath);
        }
        catch(IOException e){
            logger.severe("Exception: " + e);
            smPanel.setStatus("Failed to load " + choreographyPath);

            JOptionPane.showMessageDialog(null,
            		"Failed to load "+choreographyPath,
            		 "Error", JOptionPane.ERROR_MESSAGE);
            
            return false;
        }

        // ok, we were successful ...
        if(this.choreographyPath != null){
            if(this.isMonitoring() == true){
                // stop monitoring
            }

            exchangeEventsData.clear();
        }

        this.choreographyPath = choreographyPath;
        this.choreography = newChoreography;

        // tell the panel we loaded a new choreography ...
        smPanel.loadedChoreography(choreography);

        return true;
    }


    /**
     *
     */
    public boolean startMonitoring(){

        logger.info("Starting monitoring");

        if(this.isMonitoring() == true){
            logger.info("Already monitoring");
            return false;
        }

        if(this.choreography == null){
            logger.info("No choreography to monitor");
            return false;
        }

        smPanel.setStatus("Trying to monitor " + choreography.getName());
        logger.info("Trying to monitor " + choreography.getName());

        try{
            this.txnMonitor = TxnMonitor.getInstance(choreographyPath);
            this.txnMonitor.addCorrelationManagerListener(this);
        }
        catch(Exception e){
            // hack
            logger.log(Level.SEVERE, "Exception while trying to monitor choreography " + e, e);
            
            JOptionPane.showMessageDialog(null,
            		"Failed to initialize monitor: "+e.getLocalizedMessage(),
            		 "Error", JOptionPane.ERROR_MESSAGE);
        }

        logger.fine("Past txnMonitor calls");

        this.setIsMonitoring(true);

        // tell the panel
        smPanel.startedMonitoring();

        return true;
    }


    /**
     *
     */
    public boolean stopMonitoring(){
        if(this.isMonitoring() == false){
            logger.info("Not monitoring");
            return false;
        }

        this.txnMonitor.removeCorrelationManagerListener(this);

        try{
            this.txnMonitor.unmonitor(this.choreography);
        }
        catch(Exception e){
            logger.severe("Exception: " + e);
            return false;
        }

        this.setIsMonitoring(false);

        // tell the panel
        smPanel.stoppedMonitoring();

        return true;
    }


    /**
     *
     */
    public boolean importEvents(String path){
        boolean result = exchangeEventsData.importEvents(path);
        smPanel.importedEvents();
        return result;
    }


    /**
     *
     */
    public boolean exportEvents(String path){
        return exchangeEventsData.exportEvents(path);
        //
        //        try{
        //            FileOutputStream fileOutputStream = new FileOutputStream(path);
        //            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        //            objectOutputStream.writeObject(exchangeEvents);
        //            objectOutputStream.close();
        //        }
        //        catch(Exception e){
        //            System.err.println("Exception: " + e);
        //            return false;
        //        }
        //
        //        return true;
    }


    // CorrelationManagerListener interface

   /**
    * This method indicates that a correlation session has
    * started.
    *
    * @param session The correlation session
    */
    public void correlationSessionStarted(CorrelationSession session){
        logger.info(">>>> CORRELATION SESSION STARTED: " + session );
        
        // TO DO: Need to add something to the sessionPanel at this point.
        ChannelJPanel cp = smPanel.getChannelPanel();
        cp.addedSession(session);
    }


    /**
     * This method indicates that a correlation session has
     * finished.
     *
     * @param session The correlation session
     */
    public void correlationSessionFinished(CorrelationSession session){
        logger.info(">>>> CORRELATION SESSION FINSIHED: " + session);
        smPanel.getChannelPanel().addedSession(session);
        // TO DO: Need to change colour for the same session item in the session Panel
    }


    /**
     * A new exchange event has been added to a correlation session.
     *
     * @param exchangeEvent The exchange event.
     */
    public void exchangeEventAdded(ExchangeEvent exchangeEvent){
    	logger.fine(">>>> ADDING EXCHANGE EVENT" + exchangeEvent);
        // System.err.println(">>>> Adding exchange event");
        //        System.err.println(">>>> Getting correlation session");
        CorrelationSession session = exchangeEvent.getCorrelationSession();
        logger.fine("Identity for new exchange is: " + ChannelJPanel.getSessionIdentity(exchangeEvent.getCorrelationSession()));
        //        System.err.println(">>>> Getting wrapper");
        ExchangeEventWrapper wrapper = exchangeEventsData.createExchangeEventWrapper(exchangeEvent);
        //        System.err.println(">>>> Adding to data");
        exchangeEventsData.addExchangeEvent(wrapper);
        //        System.err.println(">>>> updating smPanel");
        smPanel.addedExchangeEvent(wrapper);
        logger.fine(">>>> ADDED EXCHANGE EVENT FOR SESSION: " + session);
    }


    /**
     * An exchange event has been updated.
     *
     * @param exchangeEvent The exchange event.
     */
    public void exchangeEventUpdated(ExchangeEvent exchangeEvent){
        ExchangeEventWrapper wrapper = exchangeEventsData.matchExchangeEventWrapper(exchangeEvent);
        logger.fine("Identity for updated exchange is: " + ChannelJPanel.getSessionIdentity(exchangeEvent.getCorrelationSession()));
        smPanel.updatedExchangeEvent(wrapper);
    }
    
    /**
     * An unexpcted exchange event has occured
     * 
     * @param exchangeEvent. The exchange event.
     * @param serviceName The service reporting the error
     */
    public void unexpectedExchangeEventAdded(ExchangeEvent exchangeEvent,
    						String serviceName)
    {
    	logger.info(">>>> unexpectedExchangeEventAdded");
    	
    	//CorrelationSession session = exchangeEvent.getCorrelationSession();
    	String ident = null;
    	
    	if (exchangeEvent.getCorrelationSession() != null) {
    		ident = ChannelJPanel.getSessionIdentity(exchangeEvent.getCorrelationSession());
    	}
    	
    	logger.info("Identity for unexpected is: " + ident);
        ExchangeEventWrapper wrapper = exchangeEventsData.errorExchangeEventWrapper(exchangeEvent);
        wrapper.setUnexpected(true);
        wrapper.setMessageIdentity(ident);
        wrapper.setServiceName(serviceName);
        exchangeEventsData.addExchangeEvent(wrapper);
        smPanel.addedUnexpectedExchangeEvent(wrapper);
        
    	logger.fine("<<<< unexpectedExchangeEventAdded");
    }
    
    /**
     * An error occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The error message
     * @param exception The optional exception
     * @param serviceName The service reporting the error
     */
    public void error(CorrelationSession session, String mesg,
    					String exception, String serviceName) {
    	logger.info(">>>> error");
    	
    	//CorrelationSession session = exchangeEvent.getCorrelationSession();
    	String ident = null;
    	
    	if (session != null) {
    		ident = ChannelJPanel.getSessionIdentity(session);
    	}
    	
    	logger.info("Identity for unexpected is: " + ident);
        ExchangeEventWrapper wrapper =
        		exchangeEventsData.simpleWrapper(session, mesg,
        					exception);
        wrapper.setErrorMessage(true);
        wrapper.setMessageIdentity(ident);
        wrapper.setChannelType(ChannelJPanel.getErrorName());
        wrapper.setServiceName(serviceName);
        exchangeEventsData.addExchangeEvent(wrapper);
        smPanel.addedErrorEvent(wrapper);
        
    	logger.fine("<<<< error");
    }
    
    /**
     * A warning occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The warning message
     * @param exception The optional exception
     * @param serviceName The service reporting the warning
     */
    public void warning(CorrelationSession session, String mesg,
    				String exception, String serviceName) {
    	logger.info(">>>> warning");
    	
    	//CorrelationSession session = exchangeEvent.getCorrelationSession();
    	String ident = null;
    	
    	if (session != null) {
    		ident = ChannelJPanel.getSessionIdentity(session);
    	}
    	
    	logger.info("Identity for unexpected is: " + ident);
        ExchangeEventWrapper wrapper =
        		exchangeEventsData.simpleWrapper(session, mesg,
        					exception);
        wrapper.setWarningMessage(true);
        wrapper.setMessageIdentity(ident);
        wrapper.setChannelType(ChannelJPanel.getWarningName());
        wrapper.setServiceName(serviceName);
        exchangeEventsData.addExchangeEvent(wrapper);
        smPanel.addedErrorEvent(wrapper);
        
    	logger.fine("<<<< warning");
    }
    
    /**
     * An information event occurred related to the specified correlation
     * session.
     * 
     * @param session The correlation session
     * @param mesg The information message
     * @param serviceName The service reporting the information
     */
    public void information(CorrelationSession session, String mesg,
    					String serviceName) {
    	logger.info(">>>> information");
    	
    	//CorrelationSession session = exchangeEvent.getCorrelationSession();
    	String ident = null;
    	
    	if (session != null) {
    		ident = ChannelJPanel.getSessionIdentity(session);
    	}
    	
    	logger.info("Identity for unexpected is: " + ident);
        ExchangeEventWrapper wrapper =
        		exchangeEventsData.simpleWrapper(session, mesg, null);
        wrapper.setInformationMessage(true);
        wrapper.setMessageIdentity(ident);
        wrapper.setChannelType(ChannelJPanel.getInformationName());
        wrapper.setServiceName(serviceName);
        exchangeEventsData.addExchangeEvent(wrapper);
        smPanel.addedErrorEvent(wrapper);
        
    	logger.fine("<<<< information");
    }
    

} // End of Applet

// EOF
