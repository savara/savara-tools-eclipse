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


import java.util.Date;

import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.Channel;
import org.pi4soa.cdl.ExchangeActionType;

/**
 * Convenience class for representing an exchange, and providing
 * convenient access to data for display.
 */
public class MonitorExchangeEvent {

    Date initiatedDate = null;
    Date completedDate = null;

    Channel channel = null;
    ExchangeDetails exchangeDetails = null;

    /**
     *
     */
    public MonitorExchangeEvent(Channel channel, ExchangeDetails exchangeDetails){
        this.channel = channel;
        this.exchangeDetails = exchangeDetails;
    }

    /**
     *
     */
    public String getChannelName(){
        return channel.getName();
    }

    /**
     *
     */
    public String getAction(){
        if(exchangeDetails.getAction() == ExchangeActionType.REQUEST) return "Request";
        else return "Response";
    }


    /**
     *
     */
    public String getName(){
        return exchangeDetails.getName();
    }


    /**
     *
     */
    public String getDescription(){
        return exchangeDetails.getDescription();
    }


    /**
     *
     */
    public Date getInitiatedDate(){
        return initiatedDate;
    }


    /**
     *
     */
    public Date getCompletedDate(){
        return completedDate;
    }


    /**
     *
     */
    public String getStatus(){
        if(completedDate != null) return "Completed";
        else return "Initiated";
    }


    /**
     *
     */
    public int hashCode(){
        return exchangeDetails.hashCode();
    }
    

    /**
     *
     */
    public boolean equals(Object object){
        boolean result = false;
        
        if(object instanceof MonitorExchangeEvent){
            MonitorExchangeEvent other = (MonitorExchangeEvent) object;
        
            if(other.exchangeDetails == this.exchangeDetails &&
               other.getChannelName().equals(this.getChannelName())){
                   /*                    other.getCorrelationSession() == m_session) { */
                result = true;
            }
        }
        
        return result;
    }


} // End of class
