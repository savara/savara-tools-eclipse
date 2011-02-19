/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.monitor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.service.Channel;
import org.pi4soa.service.DefaultMessage;
import org.pi4soa.service.Identity;
import org.pi4soa.service.Message;
import org.pi4soa.service.tracker.*;
import org.savara.activity.model.*;
import org.savara.activity.util.*;

public class ServiceTrackerClient extends org.pi4soa.service.tracker.jms.JMSServiceTrackerClient {

	private static Logger logger=Logger.getLogger(ServiceTrackerClient.class.getName());
	
	public void handleTrackerRecord(String record) {
		try {
			Activity act=ActivityModelUtil.deserialize(new java.io.ByteArrayInputStream(record.getBytes()));
			
			logger.fine("Activity="+act);
			
			if (act instanceof InteractionActivity) {
				handleTrackerRecord(new InteractionActivityProxy((InteractionActivity)act));
			}
		} catch(Throwable e) {
			// Maybe a legacy 'service description' record
			// TODO: Remove when legacy format no longer being supported
			super.handleTrackerRecord(record);
			
			//logger.log(Level.SEVERE, "Failed to process tracker record", e);
			//e.printStackTrace();
		}
	}
	
	public class InteractionActivityProxy implements TrackerRecord, TrackerEvent {
		
		private InteractionActivity m_activity=null;
		
		public InteractionActivityProxy(InteractionActivity ia) {
			m_activity = ia;
		}

		public String getServiceDescriptionName() {
			String ret=null;

			for (Analysis a : m_activity.getAnalysis()) {
				if (a instanceof ProtocolAnalysis) {
					ret = ((ProtocolAnalysis)a).getProtocol()+"@"+
								((ProtocolAnalysis)a).getRole();
					break;
				}
			}
			
			return(ret);
		}

		public String getServiceDescriptionVersion() {
			// TODO Auto-generated method stub
			return null;
		}

		public Identity getSessionIdentity() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<Identity> getPrimaryIdentities() {
			List<Identity> ret=new java.util.Vector<Identity>();
			
			for (Context context : m_activity.getContext()) {				
				String[] tokens=context.getName().split(":");
				String[] values=context.getValue().split(":");
				
				ret.add(new Identity(context.getName(), tokens, values));
			}
			
			return(ret);
		}

		public TrackerEvent[] getTrackerEvents() {
			return(new TrackerEvent[]{this});
		}

		public String toXML() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getEventType() {
			String ret=null;
			
			// TODO: Need to check if message was expected, otherwise
			// should return 'unexpected message' type
			if (m_activity.isOutbound()) {
				ret = TrackerEvent.SENT_MESSAGE;
			} else {
				ret = TrackerEvent.RECEIVED_MESSAGE;
			}
			
			return(ret);
		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServiceInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getLocalSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getLocalParentSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getParentSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getTimestamp() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Message getMessage() {
			DefaultMessage ret=new DefaultMessage();
			
			ret.setMessageIdentities(getPrimaryIdentities());
			ret.setOperationName(m_activity.getOperationName());
			ret.setFaultName(m_activity.getFaultName());
			//ret.setServiceType(m_activity.getDestinationType());
			
			if (m_activity.getParameter().size() > 0) {				
				ret.setType(m_activity.getParameter().get(0).getType());
				ret.setValue(m_activity.getParameter().get(0).getValue());
			}
			
			return(ret);
		}

		public Channel getChannel() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getDetails() {
			// Only return first value, if parameter defined
			if (m_activity.getParameter().size() > 0) {
				return(m_activity.getParameter().get(0).getValue());
			}
			return(null);
		}

		public String getException() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
