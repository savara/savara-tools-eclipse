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
 * Jul 14, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.policies;

import org.eclipse.draw2d.ConnectionAnchor;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.commands.ConnectionCommand;
import org.savara.tools.scenario.designer.parts.*;

/**
 * This class provides the edit policy for the connectable
 * types container.
 */
public class ConnectableMessageEventEditPolicy 
		extends org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy {

    /**
     * 
     */
    public ConnectableMessageEventEditPolicy() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
     */
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        ConnectionCommand command =null;
        ConnectionAnchor anchor =
        	getConnectableEditPart().getTargetConnectionAnchor();
        
        if (anchor != null &&
        		request.getStartCommand() instanceof ConnectionCommand) {
            command = (ConnectionCommand)request.getStartCommand();
            
            if (command.getSource() != getMessageEvent()) {
            	command.setTarget((MessageEvent)getMessageEvent());
            } else {
            	command = null;
            }
        }
        
        return command;
    }

    /**
     * Feedback should be added to the scaled feedback layer.
     * @see org.eclipse.gef.editpolicies.GraphicalEditPolicy#getFeedbackLayer()
     */
    protected org.eclipse.draw2d.IFigure getFeedbackLayer() {
    	return getLayer(org.eclipse.gef.LayerConstants.SCALED_FEEDBACK_LAYER);
    }

    protected MessageEventEditPart getConnectableEditPart() {
        return((MessageEventEditPart)getHost());
    }

    protected MessageEvent getMessageEvent() {
        return((MessageEvent)getHost().getModel());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
     */
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        ConnectionCommand command=null;
        
        if (getMessageEvent() instanceof SendEvent) {
        	command = new ConnectionCommand();
        	
	        command.setLink((Link)request.getNewObject());
	
	        command.setSource((MessageEvent)getMessageEvent());
	        request.setStartCommand(command);
        }

        return(command);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
     */
    protected Command getReconnectTargetCommand(ReconnectRequest request) {
        ConnectionCommand command = new ConnectionCommand();
        Link rel = (Link)((LinkEditPart) request.getConnectionEditPart()).getModel();
        command.setLink(rel);
        command.setOldTarget((MessageEvent)rel.getTarget());
        command.setTarget((MessageEvent)getMessageEvent());

        return command;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
     */
    protected Command getReconnectSourceCommand(ReconnectRequest request) {
        ConnectionCommand command = new ConnectionCommand();
        Link rel = (Link)((LinkEditPart) request.getConnectionEditPart()).getModel();
        command.setLink(rel);
        command.setOldSource((MessageEvent)rel.getSource());
        command.setSource((MessageEvent)getMessageEvent());

        return command;
    }
}
