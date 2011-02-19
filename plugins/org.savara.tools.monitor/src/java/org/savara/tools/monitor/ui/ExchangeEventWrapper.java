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


import org.pi4soa.cdl.Interaction;
import org.pi4soa.cdl.RoleType;
import org.pi4soa.cdl.Variable;

import org.pi4soa.service.Message;
import org.pi4soa.service.Identity;

import org.pi4soa.service.correlator.CorrelationSession;
import org.savara.tools.monitor.ExchangeEvent;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.logging.Logger;



/**
 *
 */
public class ExchangeEventWrapper implements Serializable{
	
    Integer index = null;

    String realSessionName = null;
    String sessionName = null;
    String description = null;
    String toRoleTypeName = null;
    String fromRoleTypeName = null;
    String serviceName=null;
    String sendVariableString = null;
    String sendVariableTypeString = null;
    String messageTypeString = "";
    String messageTypeNoNamespaceString = "";
    String operationString = null;
    String messageValueString = null;
    String exceptionValueString = null;
    String messageIdentityString = null;
    String channelType = null;
    String correlationSessionString = null;

    // don't serialize this one ...
    transient private ExchangeEvent exchangeEvent = null;

    String status = null;
    boolean hasFrozenStatus = false;

    boolean hasSetErrorsAndWarnings = false;
    boolean isError = false;
    boolean isWarning = false;
    
    boolean isErrorMessage=false;
    boolean isWarningMessage=false;
    boolean isInformationMessage=false;

    boolean isFault = false;
    boolean hasSetIsFault = false;

    boolean isRequest;
    
    boolean isUnexpected = false;
    
    private static Logger logger = Logger.getLogger("org.savara.tools.monitor.ui");

    public ExchangeEventWrapper(CorrelationSession session,
    		Integer index, String mesgValue, String exception){
        this.index = index;

        correlationSessionString = (session == null?
    			"<unknown>":session.toString());
        
        setMessageValue(mesgValue);
        
        if (messageValueString == null) {
        	messageValueString = "";
        }
        
       	exceptionValueString = exception;
        
        messageIdentityString = "";
        operationString = "";
        description = "";
        sendVariableString="";

        setRealSessionName(session);
        setSessionName(session);
    }
    
    /**
     *
     */
    public ExchangeEventWrapper(ExchangeEvent event, Integer index){
        this.index = index;
        this.exchangeEvent = event;

        correlationSessionString = (event.getCorrelationSession() == null?
        			"<unknown>":event.getCorrelationSession().toString());

        Message message = exchangeEvent.getMessage();
        if(message != null){
        	
        	if (exchangeEvent.getExchange() != null) {
        		isRequest = (exchangeEvent.getExchange().getAction()
        					== org.pi4soa.cdl.ExchangeActionType.REQUEST);
        	} else {
        		isRequest = message.isRequest();
        	}
            
            messageTypeString = message.getType();
            
            messageTypeNoNamespaceString = 
            	org.pi4soa.common.xml.NameSpaceUtil.getLocalPart(message.getType());

        }
        else{
            logger.fine(">>>> Null message");
            isRequest = true;
        }

        setRealSessionName(event.getCorrelationSession());
        setSessionName(event.getCorrelationSession());
        setDescription(event);
        setFromRoleTypeName(event);
        setToRoleTypeName(event);
        setSendVariableName(event);
        setSendVariableTypeName(event);
        setOperationString(event);
        setMessageValue(event);
        setMessageIdentity(event);
        setChannelType(event);
    }

    /**
     *
     */
    public Integer getIndex(){ return index; }


    /**
     * 
     */
    public String getRealSessionName() { return realSessionName; }
    public String getSessionName() { return sessionName; }
    
    /**
     * 
     */
    public void setRealSessionName(CorrelationSession s){
    	
    	if (s == null) {
    		realSessionName = "Session:unknown";
    	} else {
    		realSessionName = "Session:" + s.toString().substring(s.getClass().getName().length());
    	}
    	
    	logger.fine("Set real session name in wrapper: " + realSessionName);
    }
    
    public void setSessionName(CorrelationSession s){
    	
    	if (s == null) {
    		sessionName = "unknown";
    	} else {
    		sessionName = ChannelJPanel.getSessionIdentity(s);
    	}
    	
    	logger.fine("Set session name alias in wrapper: " + sessionName);
    }
    /**
     *
     */
    public String getDescription(){ return description; }

    /**
     *
     */
    protected void setDescription(ExchangeEvent exchangeEvent){
        if(exchangeEvent == null || exchangeEvent.getExchange() == null){
            description = "null exchange";
        }
        else{
            description = exchangeEvent.getExchange().getDescription();
            if(description != null){
                String newDescription = description.replaceAll("\\s+", " ");
                description = newDescription;
            }
            else{
                description = "";
            }
        }
    }


    /**
     *
     */
    public String getStatus(){
        String returnValue = null;
        if(hasFrozenStatus == true){
            returnValue = status;
        } else {
        	if (exchangeEvent == null) {
        		returnValue = "";
        	} else if (exchangeEvent.getChannel() == null) {
        		returnValue = "Unexpected";
        	} else if(exchangeEvent.isExchangeComplete()) {
                returnValue = "Completed";
            } else{
                returnValue = "Initiated";
            }
        }

        return returnValue;
    }


    /**
     *
     */
    public void freezeStatus(){
        status = getStatus();
        hasFrozenStatus = true;
    }


    /**
     *
     */
    public boolean equals(Object object){
        boolean result = false;

        if (object != null && object instanceof ExchangeEventWrapper) {
            ExchangeEventWrapper other = (ExchangeEventWrapper) object;
            if(other.exchangeEvent != null && this.exchangeEvent != null){
                if(other.exchangeEvent.equals(this.exchangeEvent)){
                    result = true;
                }
            }
            else{
                result = other.getDescription().equals(this.getDescription()) &&
                    other.getFromRoleTypeName().equals(this.getFromRoleTypeName()) &&
                    other.getToRoleTypeName().equals(this.getToRoleTypeName()) &&
                    other.getSendVariableName().equals(this.getSendVariableName()) &&
                    other.getOperationString().equals(this.getOperationString()) &&
                    other.getMessageValue().equals(this.getMessageValue()) &&
                    other.getMessageIdentity().equals(this.getMessageIdentity()) &&
                    other.getChannelType().equals(this.getChannelType());
            }

        }

        return result;
    }



    /**
     *
     */
    public String getFromRoleTypeName(){
    	if (exchangeEvent == null || exchangeEvent.getChannel() == null)
    	{
    		return(""); // "Unknown From Role";
    	}
        if(isRequest){
            return fromRoleTypeName;
        }
        else{
            return toRoleTypeName;
        }
    }


    /**
     *
     */
    protected void setFromRoleTypeName(ExchangeEvent exchangeEvent){
        if(exchangeEvent.getExchange() == null){
            fromRoleTypeName = ""; //"Unknown From Role";
        }
        else{
            Interaction interaction = exchangeEvent.getExchange().getInteraction();
            if(interaction != null){
                RoleType fromRoleType = interaction.getFromRoleType();
                if(fromRoleType != null){
                    fromRoleTypeName = fromRoleType.getName() != null ? fromRoleType.getName() : "";
                }
                else{
                    fromRoleTypeName = ""; //"null fromRoleType";
                }
            }
            else{
                fromRoleTypeName = ""; //"null interaction";
            }
        }
    }
    
    protected void setServiceName(String name) {
    	serviceName = name;
    }
    
    public String getServiceName() { return serviceName; }

    /**
     *
     */
    public String getToRoleTypeName(){
    	if (exchangeEvent == null || exchangeEvent.getChannel() == null)
    	{
    		return(""); // "Unknown To Role";
    	}
        if(isRequest){
            return toRoleTypeName;
        }
        else{
            return fromRoleTypeName;
        }
    }


    /**
     *
     */
    protected void setToRoleTypeName(ExchangeEvent exchangeEvent){
        if(exchangeEvent == null || exchangeEvent.getExchange() == null){
            toRoleTypeName = ""; //"Unknown To Role";
        }
        else{
            Interaction interaction = exchangeEvent.getExchange().getInteraction();
            if(interaction != null){
                RoleType toRoleType = interaction.getToRoleType();
                if(toRoleType != null){
                    toRoleTypeName = toRoleType.getName() != null ? toRoleType.getName() : "";
                }
                else{
                    toRoleTypeName = ""; //"null toRoleType";
                }
            }
            else{
                toRoleTypeName = ""; //"null interaction";
            }
        }
    }


    /**
     *
     */
    public String getSendVariableName(){ return sendVariableString; }
    public String getSendVariableTypeName() { return sendVariableTypeString; }

    /**
     * 
     */
    protected void setSendVariableTypeName(ExchangeEvent exchangeEvent)
    {
        if(exchangeEvent.getExchange() == null){
        	if (exchangeEvent.getMessage().getType() != null) {
        		String tmp = exchangeEvent.getMessage().getType();
        		sendVariableTypeString = tmp.substring(tmp.indexOf("}")+1);
        		// Need to abberivate after the last "{"
        	} else {
        		sendVariableTypeString = "Unknown type";
        	}
        }
        else{
            Variable sendVariable = exchangeEvent.getExchange().getSendVariable();
            if(sendVariable != null){
            	//sendVariableString = sendVariable.getName() != null ? sendVariable.getName()  : "Type: " + sendVariable.getType().getName();
                sendVariableTypeString = sendVariable.getType().getName();
            }
        }
    }
    /**
     *
     */
    protected void setSendVariableName(ExchangeEvent exchangeEvent){
        if(exchangeEvent.getExchange() == null){
        	if (exchangeEvent.getMessage().getType() != null) {
        		String tmp = exchangeEvent.getMessage().getType();
        		sendVariableString = "None : " + tmp.substring(tmp.indexOf("}")+1);
        		// Need to abberivate after the last "{"
        	} else
        		sendVariableString = "null exchange";
        }
        else{
            Variable sendVariable = exchangeEvent.getExchange().getSendVariable();
            if(sendVariable != null){
            	//sendVariableString = sendVariable.getName() != null ? sendVariable.getName()  : "Type: " + sendVariable.getType().getName();
                sendVariableString = sendVariable.getName() != null ? sendVariable.getName()  : " : " + sendVariable.getType().getName();
            }
            else{
            	sendVariableString = "None : " + exchangeEvent.getExchange().getType().getName();
                //sendVariableString = "null sendVariable";
            }
        }
    }


    /**
     *
     */
    public String getOperationString(){ return operationString; }



    /**
     *
     */
    protected void setOperationString(ExchangeEvent exchangeEvent){
        if(exchangeEvent.getExchange() == null){
        	operationString = ""; //"Unknown Operation";
        }
        Message message = exchangeEvent.getMessage();
        if(message == null){
            operationString = ""; //"null message";
        }
        else{
            operationString = message.getOperationName();
            if(operationString == null){
                operationString = ""; //"null operation";
            }
        }
    }

    public String getMessageSummary() {
    	String ret="";
    	
    	if (exchangeEvent != null) {
	    	String op=getOperationString();
	    	if (op != null && op.trim().length() > 0) {
	    		ret += op+"(";
	        }
	
	    	ret += getMessageTypeNoNamespace();
	    	if (op != null && op.trim().length() > 0) {
	    		ret += ")";
	    	}
    	} else {
    		ret = getMessageValue();
    	}
    	
    	if (ret != null && ret.length() > 0 &&
    			ret.charAt(0) == '<') {
    		ret = getChannelType()+" ...";
    	}
    	
    	return(ret);
    }

    /**
     *
     */
    public String getMessageValue(){ return messageValueString; }

    public String getExceptionValue() { return exceptionValueString; }

    /**
     *
     */
    protected void setMessageValue(ExchangeEvent exchangeEvent){
        logger.fine("**** Setting message value");
        messageValueString = "";
        Message message = exchangeEvent.getMessage();
        if(message == null){
            logger.fine("**** null message");
            messageValueString = "null message";
        }
        else{
            Serializable messageValue = message.getValue();
            if(messageValue == null){
                messageValueString = "null message value";
                logger.fine("**** null message value");
            }
            else{
                setMessageValue(messageValue.toString());
            }
        }
    }
    
    protected void setMessageValue(String originalMessageValueString) {
        logger.fine("**** original message = " + originalMessageValueString);

        logger.fine("**** prettifying");

        messageValueString = XmlPrettyPrinter.prettify(originalMessageValueString);

        logger.fine("**** prettified");

        logger.fine(messageValueString);

        if(messageValueString == null) {

            logger.fine("**** prettified string was null");

            // messageValueString = originalMessageValueString;
            //                        messageValueString = "prettyprinting failed";
            messageValueString = originalMessageValueString.replaceAll("[ \\t]+", " ");

            logger.fine("**** reprettified");
            logger.fine(messageValueString);
        }
    }


    /**
     *
     */
    public String getMessageIdentity(){ return messageIdentityString; }

    public String getMessageType() { return messageTypeString; }
    
    public String getMessageTypeNoNamespace() { return messageTypeNoNamespaceString; }

    /**
     *
     */
    protected void setMessageIdentity(ExchangeEvent exchangeEvent){
        Message message = exchangeEvent.getMessage();
        if(message == null){
            messageIdentityString = "null message";
        }
        else{
        	java.util.List<Identity> messageIdentityArray=message.getMessageIdentities();
            if(messageIdentityArray == null || messageIdentityArray.size() == 0){
                messageIdentityString = "no identities";
            }
            else{
                Identity firstMessageIdentity = messageIdentityArray.get(0);
                //                    messageIdentityString = firstMessageIdentity.toText();
                String tokens[] = firstMessageIdentity.getTokens();
                // valu
                Object values_obj[] = firstMessageIdentity.getValues();
                
                String[] values = new String[values_obj.length];
                
                for (int values_index=0; values_index < values_obj.length; values_index++)
                {
                	values[values_index] = values_obj[values_index].toString();
                }

                if(tokens == null || values == null){
                    if(tokens == null){
                        logger.fine("identity.getTokens() returned null");
                        //                        messageIdentityString = "null tokens";
                    }

                    if(values == null){
                        logger.fine("identity.getValues() returned null");
                        //                        messageIdentityString = "null values";
                    }

                    messageIdentityString = firstMessageIdentity.toString();
                }
                else{
                    if(tokens != null && values != null && tokens.length == values.length){
                        messageIdentityString = "";
                        for(int i = 0; i < tokens.length - 1; i++){
                            messageIdentityString += values[i] + " (" + tokens[i] + "), ";
                        }
                        messageIdentityString += values[tokens.length - 1] + " (" + tokens[tokens.length - 1]+")";
                    }
                    else{
                        messageIdentityString = "tokens/values mismatch";
                    }
                }
            }
        }
    }
    
    public void setMessageIdentity(String ident)
    {
    	messageIdentityString = ident;
    }


    /**
     *
     */
    public boolean isFault(){
        if(hasSetIsFault == false){
            if(exchangeEvent != null && exchangeEvent.getExchange() != null){
                isFault = exchangeEvent.getExchange().isFault();
            }
            //else{
            //    isFault = true;
            //}
            hasSetIsFault = true;
        }
        return isFault;
    }


    /**
     *
     */
    private void setErrorsAndWarnings(){
        if(hasSetErrorsAndWarnings == false){
            if(getToRoleTypeName().toLowerCase().matches("exception")){
                if(getOperationString().toLowerCase().matches("fatal")){
                    isError = true;
                }
                else{
                    isWarning = true;
                }
            }
            else if(getFromRoleTypeName().toLowerCase().matches("exception")){
                if(isFault()){
                    isError = true;
                }
            }
            else{
                if(exchangeEvent != null && exchangeEvent.getExchange() == null){
                	isUnexpected = true;
                }
                else if(isFault()){
                    isWarning = true;
                }

                // if the session was terminated early. color me red.
            }
            hasSetErrorsAndWarnings = true;
        }
    }

    public void setErrorMessage(boolean b) {
    	isErrorMessage = b;
    	
    	if (b) {
    		channelType = ChannelJPanel.getErrorName();
    	}
    }
    
    public boolean isErrorMessage() {
    	return(isErrorMessage);
    }

    public void setWarningMessage(boolean b) {
    	isWarningMessage = b;
    	
    	if (b) {
    		channelType = ChannelJPanel.getWarningName();
    	}
    }
    
    public boolean isWarningMessage() {
    	return(isWarningMessage);
    }

    public void setInformationMessage(boolean b) {
    	isInformationMessage = b;
    	
    	if (b) {
    		channelType = ChannelJPanel.getInformationName();
    	}
    }
    
    public boolean isInformationMessage() {
    	return(isInformationMessage);
    }

    /**
     *
     */
    public boolean isError(){
        if(hasSetErrorsAndWarnings == false){
            setErrorsAndWarnings();
        }
        return(isError || isErrorMessage());
    }

    public boolean isUnexpected(){
    	return isUnexpected;
    }
    
    public void setUnexpected(boolean b){
    	isUnexpected = b;
    }

    /**
     *
     */
    public boolean isWarning(){
        if(hasSetErrorsAndWarnings == false){
            setErrorsAndWarnings();
        }
        return(isWarning || isWarningMessage());
    }


    /**
     *
     */
    public String getChannelType(){ return channelType; }

    protected void setChannelType(String ctype) {
    	channelType = ctype;
    }
    
    /**
     *
     */
    public String getCorrelationSession(){ return correlationSessionString; }

    /**
     *
     */
    public String toString()
    {
        String s = "EXCHANGE EVENT WRAPPER: " +
				"<sessionName = " + sessionName + ">" +
				"<realSessionName = " + realSessionName + ">" +
				"<serviceName = " + serviceName + ">" +
				"<description = " + description + ">" +
				"<toRoleTypeName = " + toRoleTypeName + ">" +
				"<fromRoleTypeName = " + fromRoleTypeName + ">" +
				"<sendVariableString = " + sendVariableString + ">" +
				"<operationString = " + operationString + ">" +
				"<messageValueString = " + messageValueString + ">" +
				"<messageIdentityString = " + messageIdentityString + ">" +
				"<channelType = " + channelType + ">" +
				"<correlationSessionString = " + correlationSessionString + ">" +
				"<status = " + status + ">" +
				"<hasFrozenStatus = " + hasFrozenStatus + ">" +
				"<hasSetErrorsAndWarnings = " + hasSetErrorsAndWarnings + ">" +
				"<isError = " + isError + ">" +
				"<isWarning = " + isWarning + ">" +
				"<isFault = " + isFault + ">" +
				"<hasSetIsFault = " + hasSetIsFault + ">" +
				"<isRequest = " + isRequest + ">" +
				"<isUnexpected = " + isUnexpected + ">";
        
        return s;
    }
    /**
     * 
     */
    
    protected void setChannelType(ExchangeEvent exchangeEvent){
        if(exchangeEvent.getChannel() == null){
            channelType = ChannelJPanel.getUnexpectedMessagesName();
        }
        else{
            channelType = exchangeEvent.getExchange().getInteraction().getChannelVariable().getType().getName(); // Not getType
        	logger.fine("Channel type name: " + channelType);
        	logger.fine("Channel service type name: " + exchangeEvent.getChannel().getServiceType());
        	logger.fine("Channel instance name: " + exchangeEvent.getChannel().getName());
        	logger.fine("OR Channel instance name: " + exchangeEvent.getExchange().getInteraction().getChannelVariable().getType().getName());
        }
    }

    // Serialization support


    /**
     *
     */
    private void writeObject(ObjectOutputStream out) throws IOException{
        // to make sure that this has been set.
        boolean myIsError = this.isError();
        boolean myIsFault = this.isFault();
        freezeStatus();
        out.defaultWriteObject();
    }


    /**
     *
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
    }

}
