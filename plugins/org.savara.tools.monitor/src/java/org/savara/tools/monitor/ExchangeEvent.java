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

import java.io.Serializable;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.Message;
import org.pi4soa.service.Channel;
import org.pi4soa.service.correlator.CorrelationSession;

/**
 * This class represents the 'exchange' correlation event.
 *
 */
public class ExchangeEvent implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1984285030410307153L;

	Message m_message = null;

	/**
	 * This class represents the 'exchange' correlation event.
	 * 
	 * @param exchange The exchange details
	 * @param channel The channel
	 * @param session The session
	 * @param serviceDescriptionName The service description name
	 */
	public ExchangeEvent(ExchangeDetails exchange,
				Channel channel, CorrelationSession session,
						String serviceDescriptionName, Message message) {
		m_exchange = exchange;
		m_channel = channel;
		m_session = session;
		m_serviceDescriptionName = serviceDescriptionName;
                this.m_message = message;
	}
	
	/**
	 * This method returns the exchange details.
	 * 
	 * @return The exchange details
	 */
	public ExchangeDetails getExchange() {
		return(m_exchange);
	}
	
	/**
	 * This method returns the channel.
	 * 
	 * @return The channel
	 */
	public Channel getChannel() {
		return(m_channel);
	}
	
	/**
	 * This method returns the correlation session.
	 * 
	 * @return The correlation session
	 */
	public CorrelationSession getCorrelationSession() {
		return(m_session);
	}
	
	/**
	 * This method returns the service description name.
	 * 
	 * @return The service description name
	 */
	public String getServiceDescriptionName() {
		return(m_serviceDescriptionName);
	}

	/**
	 * This method returns the message.
	 * 
	 * @return The service description name
	 */
	public Message getMessage() {
		return m_message;
	}
	
	/**
	 * This method returns whether the exchange has been
	 * initiated and completed.
	 * 
	 * @return The 'complete' status of the exchange event
	 */
	public boolean isExchangeComplete() {
		return(m_initiated && m_completed);
	}

	/**
	 * This method determines whether the event has been
	 * initiated.
	 * 
	 * @return Whether event has been initiated
	 */
    public boolean getInitiated(){
		return m_initiated;
    }

	/**
	 * This method determines whether the event has been
	 * completed.
	 * 
	 * @return Whether event has been completed
	 */
    public boolean getCompleted(){
		return m_completed;
    }
	
	/**
	 * This method indicates that the exchange associated with
	 * the event has initiated.
	 *
	 */
	public void initiated() {
		m_initiated = true;
                checkIfExchangeCorrelated();
	}
	
	/**
	 * This method indicates that the exchange associated with
	 * the event has completed.
	 *
	 */
	public void completed() {
		m_completed = true;
                checkIfExchangeCorrelated();
	}


    protected void checkIfExchangeCorrelated() {
        if (isExchangeComplete()) {
            //            logger.fine("EXCHANGE CORRELATED: "+this);
        }
    }

    public int hashCode() {
        return(m_message.getOperationName().hashCode());
    }

    public boolean equals(Object obj) {
        boolean ret=false;
        if (obj instanceof ExchangeEvent) {
            ExchangeEvent other=(ExchangeEvent)obj;
            if (m_exchange == null || other.getExchange() == null) {
                // Compare on message basis
                if (m_message.isRPCStyle() &&
                				other.getMessage().isRPCStyle()) {
                	if (m_message.getOperationName().equals(
                            other.getMessage().getOperationName()) &&
                            m_message.isRequest() == other.getMessage().isRequest() &&
                            m_message.getServiceType().equals(
                             other.getMessage().getServiceType())) {
                		ret = true;
                	}
                } else if (m_message.isRPCStyle() == false &&
        				other.getMessage().isRPCStyle() == false) {
                	if (m_message.getType().equals(
                			other.getMessage().getType())) {
                		ret = true;
                	}
                }
            } else if (other.getExchange() == m_exchange &&
                       other.getChannel().getName().equals(m_channel.getName()) &&
                       other.getCorrelationSession() == m_session) {
                ret = true;
            }
            
            if (ret) {
            	
            	// Check identities
            	if (other.getMessage().getMessageIdentities().size()
            			== getMessage().getMessageIdentities().size()) {
            		
            		for (int i=0; ret &&
            				i < getMessage().getMessageIdentities().size(); i++) {
            			ret = false;
            			
            			org.pi4soa.service.Identity id=
            					getMessage().getMessageIdentities().get(i);
            			
                		if (logger.isLoggable(Level.FINEST)) {
                			logger.finest("Checking message identity ("+i+
                					"): "+id);
                		}

                		for (int j=0; ret == false &&
            					j < other.getMessage().getMessageIdentities().size(); j++) {

                    		if (id.equals(other.getMessage().getMessageIdentities().get(j))) {
            					ret = true;
            				}
            				
                    		if (logger.isLoggable(Level.FINEST)) {
                    			logger.finest("Against message identity ("+j+
                    					"): "+other.getMessage().getMessageIdentities().get(j)+
                    					" = "+ret);
                    		}
            			}
            		}
            	} else {
            		if (logger.isLoggable(Level.FINEST)) {
            			logger.finest("Message identity list length mismatch");
            		}

            		ret = false;
            	}
            }
        }
        return(ret);
    }
	
    public String toString() {
        StringBuffer ret=new StringBuffer();
        ret.append("ExchangeEvent[");
        if (m_exchange != null) {

            //            if (NamesUtil.isSet(m_exchange.getDescription())) {


            if (m_exchange.getDescription() != null && "".equals(m_exchange.getDescription()) == false) {

                ret.append(m_exchange.getDescription());
            } else {
                ret.append(m_exchange.getName());
            }
            ret.append(", ");
        }
        ret.append(m_message.toString()+"]");
        return(ret.toString());
    }

    private static Logger logger = Logger.getLogger("org.savara.tools.monitor");

    private ExchangeDetails m_exchange=null;
	private Channel m_channel=null;
	private CorrelationSession m_session;
	private String m_serviceDescriptionName=null;
	private boolean m_initiated=false;
	private boolean m_completed=false;
}
