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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.io.FileInputStream;
import java.io.ObjectInputStream;


import org.pi4soa.service.correlator.CorrelationSession;
import org.savara.tools.monitor.ExchangeEvent;

/**
 *
 */
public class ExchangeEventsData{

	private static Logger logger = Logger.getLogger("org.savara.tools.monitor.ui");
	
    Vector exchangeEvents = null;

    Vector warningAndErrorExchangeEvents = null;

    Vector errorExchangeEvents = null;

    Vector warningExchangeEvents = null;
    
    Vector unexpectedExchangeEvents = null;

    Hashtable exchangeEventsByChannelTable = null;
    
    Hashtable exchangeEventsBySessionTable = null;

    Hashtable sessionHasErrorsTable = null;
    Hashtable sessionHasWarningsTable = null;    
    Hashtable sessionHasUnexpectedMessagesTable = null;
    Hashtable channelHasErrorsTable = null;
    Hashtable channelHasWarningsTable = null;    
    Hashtable channelHasUnexpectedMessagesTable = null;

    Hashtable exchangeEventToWrapperMap = null;

    boolean hasErrors = false;

    boolean hasWarnings = false;
    
    boolean hasUnexpectedMessages = false;

    /**
     *
     */
    public ExchangeEventsData(){
        exchangeEvents = new Vector();
        warningAndErrorExchangeEvents = new Vector();
        errorExchangeEvents = new Vector();
        warningExchangeEvents = new Vector();
        unexpectedExchangeEvents = new Vector();
        exchangeEventsByChannelTable = new Hashtable();
        exchangeEventsBySessionTable = new Hashtable();
        exchangeEventToWrapperMap = new Hashtable();
        channelHasErrorsTable = new Hashtable();
        channelHasUnexpectedMessagesTable = new Hashtable();
        channelHasWarningsTable = new Hashtable();
        sessionHasErrorsTable = new Hashtable();
        sessionHasUnexpectedMessagesTable = new Hashtable();
        sessionHasWarningsTable = new Hashtable();
    }



    /**
     *
     */
    public void clear(){
        exchangeEvents.clear();
        exchangeEventsByChannelTable.clear();
        exchangeEventsBySessionTable.clear();
        exchangeEventToWrapperMap.clear();
        warningAndErrorExchangeEvents.clear();
        errorExchangeEvents.clear();
        warningExchangeEvents.clear();
        unexpectedExchangeEvents.clear();
        channelHasErrorsTable.clear();
        channelHasWarningsTable.clear();
        channelHasUnexpectedMessagesTable.clear();
        sessionHasErrorsTable.clear();
        sessionHasWarningsTable.clear();
        sessionHasUnexpectedMessagesTable.clear();
        hasErrors = false;
        hasWarnings = false;
    }


    /**
     *
     */
    public boolean exportEvents(String path){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(exchangeEvents);
            objectOutputStream.close();
        }
        catch(Exception e){
            logger.severe("Exception: " + e);
            return false;
        }

        return true;
    }



    /**
     *
     */
    public boolean importEvents(String path){
        clear();

        Vector v = null;

        try{
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            v = (Vector) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch(Exception e){
            logger.severe("Exception: " + e);
            return false;
        }

        Iterator iterator = v.iterator();
        int i = 0;
        while(iterator.hasNext()){
            ExchangeEventWrapper wrapper = (ExchangeEventWrapper) iterator.next();

            logger.fine("Getting wrapper message value");

            String wrapperMessageValue = wrapper.getMessageValue();

            logger.fine("Imported event wrapper message value = " + wrapperMessageValue);

            Integer key = new Integer(i);
            i++;
            exchangeEventToWrapperMap.put(key, wrapper);
            addExchangeEvent(wrapper);
        }

        return true;
    }




    /**
     *
     */
    public ExchangeEventWrapper createExchangeEventWrapper(ExchangeEvent exchangeEvent){
        ExchangeEventWrapper wrapper=null;
        
        logger.fine(">>>> creating new wrapper");
        wrapper = new ExchangeEventWrapper(exchangeEvent, new Integer(exchangeEvents.size()));
        logger.fine(">>>> created new wrapper");
    	
        java.util.List list=(java.util.List)
        		exchangeEventToWrapperMap.get(exchangeEvent);
        	
        if (list == null) {
        	list = new java.util.Vector();
        	exchangeEventToWrapperMap.put(exchangeEvent, list);
    		logger.fine("Added new list for map entry: "+exchangeEvent);
        }
        
        list.add(wrapper);
    	
        return wrapper;
    }

    public ExchangeEventWrapper matchExchangeEventWrapper(ExchangeEvent exchangeEvent){
    	logger.info("getExchangeEventWrapper for " + exchangeEvent);
        logger.fine(">>>> looking for pre-existing wrapper for " + exchangeEvent);
        
        java.util.List list=
        	(java.util.List)exchangeEventToWrapperMap.get(exchangeEvent);
 
        ExchangeEventWrapper wrapper=null;
        
        if (list != null && list.size() > 0) {
        	wrapper = (ExchangeEventWrapper)list.remove(0);
        	
        	if (list.size() == 0) {
        		exchangeEventToWrapperMap.remove(exchangeEvent);
        		
        		logger.fine("Cleaned up map entry for: "+exchangeEvent);
        	}
        }
        
        if(wrapper == null){
            logger.severe(">>>> Failed to find existing wrapper: "+exchangeEvent);
        }
        else{
            logger.fine(">>>> found old wrapper");
            
            // Remove from map
            exchangeEventToWrapperMap.remove(exchangeEvent);
        }
        
        return wrapper;
    }

    public ExchangeEventWrapper errorExchangeEventWrapper(ExchangeEvent exchangeEvent){
        ExchangeEventWrapper wrapper=null;
        
        logger.fine(">>>> creating new error wrapper");
        wrapper = new ExchangeEventWrapper(exchangeEvent, new Integer(exchangeEvents.size()));
        logger.fine(">>>> created new error wrapper");
    	
        return wrapper;
    }

    public ExchangeEventWrapper simpleWrapper(CorrelationSession session,
    			String text, String exception) {
        ExchangeEventWrapper wrapper=null;
        
        logger.fine(">>>> creating new empty wrapper");
        wrapper = new ExchangeEventWrapper(session,
        		new Integer(exchangeEvents.size()), text, exception);
        logger.fine(">>>> created new empty wrapper");
    	
        return wrapper;
    }

    /**
     *
     */
    public void addExchangeEvent(ExchangeEventWrapper wrapper){

        String channelType = wrapper.getChannelType();
        String sessionName = wrapper.getSessionName();   
        
        Vector exchangeEventsByChannel = (Vector) exchangeEventsByChannelTable.get(channelType);
        Vector exchangeEventsBySession = (Vector) exchangeEventsBySessionTable.get(sessionName);


        if(exchangeEventsByChannel == null){
            exchangeEventsByChannel = new Vector();
        }
        logger.info("addExchangeEvent for CHANNEL TYPE: " + channelType);
        exchangeEventsByChannel.add(wrapper);

        if (exchangeEventsBySession == null){
        	exchangeEventsBySession = new Vector();
        }
        logger.info("addExchangeEvent for SESSION: " + sessionName);
        exchangeEventsBySession.add(wrapper);
        
        // is this necessary if we already had an entry?
        exchangeEventsByChannelTable.put(channelType, exchangeEventsByChannel);
        exchangeEventsBySessionTable.put(sessionName, exchangeEventsBySession);
        
        exchangeEvents.add(wrapper);

        logger.fine(">>>> added to exchange events list. size = " + exchangeEvents.size());

        // OLD fault handling.
        //        if(wrapper.isFault() == true){
        //            channelHasErrorsTable.put(channelType, new Boolean(true));
        //            hasErrors = true;
        //        }

        // NEW fault handling.
logger.warning("Checking if error....");        
        if(wrapper.isWarning() == true || wrapper.isError() == true || wrapper.isUnexpected() == true){
            //            channelHasErrorsTable.put(channelType, new Boolean(true));
            //            hasErrors = true;

            warningAndErrorExchangeEvents.add(wrapper);

        	if (wrapper.isError() == true) {
        		errorExchangeEvents.add(wrapper);
logger.warning("RECORD ERROR channel="+channelType);                
				channelHasErrorsTable.put(channelType, new Boolean(true));
				sessionHasErrorsTable.put(sessionName, new Boolean(true));
				hasErrors = true;
        	} else if(wrapper.isWarning() == true){
                warningExchangeEvents.add(wrapper);
                channelHasWarningsTable.put(channelType, new Boolean(true));
				sessionHasWarningsTable.put(sessionName, new Boolean(true));
logger.warning("RECORD WARNING channel="+channelType);                
                hasWarnings = true;
            } else if (wrapper.isUnexpected() == true) {
            	unexpectedExchangeEvents.add(wrapper);
            	channelHasUnexpectedMessagesTable.put(channelType, new Boolean(true));
				sessionHasUnexpectedMessagesTable.put(sessionName, new Boolean(true));
            	hasUnexpectedMessages = true;
            	logger.info("ADDED UNEXPECTED WRAPPER TO ExchangeEventData for channelType " + channelType);
            }
logger.warning("Checked");            
        }
    }


    /**
     *
     */
    public ExchangeEventWrapper getWrapper(int i){ return (ExchangeEventWrapper) exchangeEvents.elementAt(i); }



    /**
     *
     */
    public Vector getExchangeEventsForChannel(String channel){
        return (Vector) exchangeEventsByChannelTable.get(channel);
    }
    
    public Vector getExchangeEventsForSession(String session) {
    	return (Vector)exchangeEventsBySessionTable.get(session);
    }


    /**
     *
     */
    public Vector getExchangeEvents(){ return exchangeEvents; }

    /**
     *
     */
    public Vector getWarningAndErrorExchangeEvents(){ return warningAndErrorExchangeEvents; }

    /**
     *
     */
    public Vector getErrorExchangeEvents(){ return errorExchangeEvents; }

    /**
     *
     */
    public Vector getWarningExchangeEvents(){ return warningExchangeEvents; }

    /**
     * 
     */
    public Vector getUnexpectedExchangeEvents() { return unexpectedExchangeEvents; }

    /**
     * 
     */
    public boolean getHasUnexpectedExchangeEventsForChannel(String channel){
        if(hasUnexpectedMessages == true){
            Boolean hasUnexpectedMessagesForChannel = (Boolean) channelHasUnexpectedMessagesTable.get(channel);
            if (hasUnexpectedMessagesForChannel != null) return true;
        }
        return false;
    }

    public boolean getHasUnexpectedExchangeEventsForSession(String session){
        if(hasUnexpectedMessages == true){
            Boolean hasUnexpectedMessagesForSession = (Boolean) sessionHasUnexpectedMessagesTable.get(session);
            if (hasUnexpectedMessagesForSession != null) return true;
        }
        return false;
    }

    /**
     *
     */
    public boolean getHasErrorsForChannel(String channel){
        if(hasErrors == true){
            Boolean hasErrorsForChannel = (Boolean) channelHasErrorsTable.get(channel);
            if (hasErrorsForChannel != null) return true;
        }
        return false;
    }

    public boolean getHasErrorsForSession(String session){
        if(hasErrors == true){
            Boolean hasErrorsForSession = (Boolean) sessionHasErrorsTable.get(session);
            if (hasErrorsForSession != null) return true;
        }
        return false;
    }

    /**
     *
     */
    public boolean getHasWarningsForChannel(String channel){
        if(hasWarnings == true){
            Boolean hasWarningsForChannel = (Boolean) channelHasWarningsTable.get(channel);
            if (hasWarningsForChannel != null) {
                return true;
            }
        }
        return false;
    }

    public boolean getHasWarningsForSession(String session){
        if(hasWarnings == true){
            Boolean hasWarningsForSession = (Boolean) sessionHasWarningsTable.get(session);
            if (hasWarningsForSession != null) {
                return true;
            }
        }
        return false;
    }


    /**
     * 
     */
    public boolean getHasUnexpectedMessages(){ return hasUnexpectedMessages; }
    /**
     *
     */
    public boolean getHasErrors(){ return hasErrors; }


    /**
     *
     */
    public boolean getHasWarnings(){ return hasWarnings; }

}
