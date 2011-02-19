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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.pi4soa.service.Identity;
import org.pi4soa.service.correlator.CorrelationSession;


/**
 * The left hand pane contains a tree whose root is the
 * choreography, and whose leaves are channels and transactions.
 *
 * Selecting a channel filters the exchange events list (or should
 * do).
 */
public class ChannelJPanel extends JPanel{

    public final static String UNEXPECTED_MESSAGES_NAME = "Unexpected Messages";
    public final static String ERROR_NAME="Errors";
    public final static String WARNING_NAME="Warnings";
    public final static String INFORMATION_NAME="Information";
    
    private static Logger 	logger = Logger.getLogger("org.savara.tools.monitor.ui");
  
    JTree 					tree = null;
    TreeSelectionListener 	listener = null;
    JScrollPane 			treeView = null;

    ImageIcon 				channelLeafIcon = null;
    ImageIcon 				channelOpenIcon = null;
    ImageIcon 				channelClosedIcon = null;
    ImageIcon 				channelEmptyIcon = null;
    ImageIcon 				errorsLeafIcon = null;
    ImageIcon 				unexpectedLeafIcon = null;
    ImageIcon 				warningsLeafIcon = null;
    ImageIcon 				issuesOpenIcon = null;
    ImageIcon 				issuesClosedIcon = null;
    ImageIcon 				issuesEmptyIcon = null;
    ImageIcon 				txnLeafIcon = null;
    ImageIcon 				txnOpenIcon = null;
    ImageIcon 				txnClosedIcon = null;
    ImageIcon 				txnEmptyIcon = null;
    
    //    Vector exchangeEvents = null;
    ExchangeEventsData 		exchangeEventsData = null;
    
    DefaultTreeModel 		treeModel = null;
    DefaultMutableTreeNode 	rootNode = null;
    
    //DefaultMutableTreeNode  channelRoot = null;
    DefaultMutableTreeNode  issuesRoot = null;
    DefaultMutableTreeNode  errorsRoot = null;
    DefaultMutableTreeNode  warningsRoot = null;
    DefaultMutableTreeNode  unexpectedMessagesRoot = null;
    DefaultMutableTreeNode  sessionRoot = null;
    
    String	sessionRootName = "Sessions";
    String	channelRootName = "Channels";
    String	unexpectedMessagesRootName = UNEXPECTED_MESSAGES_NAME;
    String 	errorsRootName = ERROR_NAME;
    String 	warningsRootName = WARNING_NAME;
    String 	issuesRootName = "Issues";
    


    /**
     * Creates a new ChannelJPanel.
     *
     * @param listener The listener for tree selection changes.
     */
    public ChannelJPanel(TreeSelectionListener listener){

        this.listener = listener;
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
    }
    
    public ChannelJPanel(ExchangeEventsData eed, TreeSelectionListener listener){
        this.listener = listener;
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        exchangeEventsData = eed;
    }

    ////////////////////////////////////////////

    static public String getUnexpectedMessagesName()
    {	
    	return UNEXPECTED_MESSAGES_NAME;
    }
    
    static public String getErrorName() {
    	return(ERROR_NAME);
    }
    
    static public String getWarningName() {
    	return(WARNING_NAME);
    }
    
    static public String getInformationName() {
    	return(INFORMATION_NAME);
    }
    
    public void addedSession(CorrelationSession s)
    {
    	logger.fine("ADD SESSION TO PANEL " + s);
    	logger.fine("rootNote child count is: " + rootNode.getChildCount());
    	String name = ChannelJPanel.getSessionIdentity(s);
    	if (name == null)
    	{
    		name = "Session :" + s.toString().substring(s.getClass().getName().length());
    	}
		logger.info("ADDING SESSION FOR TXN: " + name);
		
		addObject(sessionRoot,new ProxyTreeNode(name, s),true);
    }
     
    static public String getSessionIdentity(CorrelationSession s)
    {
    	String identityString = null;
    	java.util.List<Identity> messageIdentityArray = s.getIdentities();
    	if (messageIdentityArray != null)
    		logger.info("sessionIdentArray length is: " + messageIdentityArray.size());
    	else
    		logger.info("messageIdentityArray was null");
        if(messageIdentityArray == null || messageIdentityArray.size() == 0)
        {
                return identityString;
        } else {
                Identity firstMessageIdentity = messageIdentityArray.get(0);
                //                    messageIdentityString = firstMessageIdentity.toText();
                String tokens[] = firstMessageIdentity.getTokens();
                // value
                Object values_obj[] = firstMessageIdentity.getValues();
                
                String[] values = new String[values_obj.length];
                
                for (int values_index=0; values_index < values_obj.length; values_index++)
                {
                	values[values_index] = values_obj[values_index].toString();
                	if (values_obj[values_index] instanceof String) 
                	{
                	    values[values_index] = (String)values_obj[values_index];
                	} 
                	else if (values_obj[values_index] instanceof java.util.List) 
                	{
                		java.util.List list = (java.util.List)values_obj[values_index];
                	    values[values_index] = "{";
                	    for (int i=0; (i < list.size()); i++ ) 
                	    {
                	    	values[values_index] += (String)list.get(i);
                	    }
                	    values[values_index] += "}";
                	} else {
                		logger.severe("Problem creating an identity string, values is of unknown type.");
                	}

                }

                if(tokens == null || values == null)
                {
                    if(tokens == null)
                    {
                        logger.fine("identity.getTokens() returned null");
                        //                        messageIdentityString = "null tokens";
                    }

                    if(values == null)
                    {
                        logger.fine("identity.getValues() returned null");
                        //                        messageIdentityString = "null values";
                    }

                    identityString = firstMessageIdentity.toString();
                } else {
                    if(tokens != null && values != null && tokens.length == values.length)
                    {
                        identityString = "";
                        for(int i = 0; i < tokens.length - 1; i++)
                        {
                            identityString += values[i] + " (" + tokens[i] + "), ";
                        }
                        identityString += values[tokens.length - 1] + " (" + tokens[tokens.length - 1]+")";
                    } else {
                        identityString = "tokens/values mismatch";
                    }
                }
        }
        
        return identityString;
    }
    
    /**
     * Adds the channel nodes to the tree, sorted in name order.
     */
    public void createAndAddTree(org.pi4soa.cdl.Package choreography){        
        if(tree != null){
            // remove the current tree ...
            this.remove(treeView);
            treeView = null;
            tree = null;
        }
        
        rootNode = new DefaultMutableTreeNode(choreography.getName());
    	if (treeModel == null){
    		treeModel = new DefaultTreeModel(rootNode);
    		treeModel.addTreeModelListener(new MonitorTreeModelListener());
    	}
    	
        createChannelNodes(treeModel, choreography);

        tree = new JTree(treeModel); // instead of rootNode instead of treeModel
        ChannelTreeCellRenderer renderer = new ChannelTreeCellRenderer();

        if(txnLeafIcon == null) txnLeafIcon = MonitorMainPanel.createImageIcon("icons/txnleaf.png");
        if(txnOpenIcon == null) txnOpenIcon = MonitorMainPanel.createImageIcon("icons/txnopen.png");
        if(txnClosedIcon == null) txnClosedIcon = MonitorMainPanel.createImageIcon("icons/txnclosed.png");
        if(txnEmptyIcon == null) txnEmptyIcon = MonitorMainPanel.createImageIcon("icons/txnempty.png");
        if(channelLeafIcon == null) channelLeafIcon = MonitorMainPanel.createImageIcon("icons/channelleaf.png");
        if(channelOpenIcon == null) channelOpenIcon = MonitorMainPanel.createImageIcon("icons/channelopen.png");
        if(channelClosedIcon == null) channelClosedIcon = MonitorMainPanel.createImageIcon("icons/channelclosed.png");
        if(channelEmptyIcon == null) channelEmptyIcon = MonitorMainPanel.createImageIcon("icons/channelempty.png");
        if(errorsLeafIcon == null) errorsLeafIcon = MonitorMainPanel.createImageIcon("icons/errorsleaf.png");
        if(warningsLeafIcon == null) warningsLeafIcon = MonitorMainPanel.createImageIcon("icons/warningsleaf.png");
        if(unexpectedLeafIcon == null) unexpectedLeafIcon = MonitorMainPanel.createImageIcon("icons/unexpectedleaf.png");
        if(issuesOpenIcon == null) issuesOpenIcon = MonitorMainPanel.createImageIcon("icons/issuesopen.png");
        if(issuesClosedIcon == null) issuesClosedIcon = MonitorMainPanel.createImageIcon("icons/issuesclosed.png");
        if(issuesEmptyIcon == null) issuesEmptyIcon = MonitorMainPanel.createImageIcon("icons/issuesempty.png");

        tree.setCellRenderer(renderer);
        
        tree.setRootVisible(false);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Listen for when the selection changes.
        tree.addTreeSelectionListener(listener);

        treeView = new JScrollPane(this.tree);
        this.add(treeView, BorderLayout.CENTER);

        // hack.
        this.revalidate();
    }
    
    /**
     * Adds the channel nodes to the tree, sorted in name order.
     */
    private void createChannelNodes(DefaultTreeModel t, org.pi4soa.cdl.Package choreography){

        issuesRoot = addObject(null, issuesRootName);
        unexpectedMessagesRoot = addObject(issuesRoot,unexpectedMessagesRootName); // was nullChannel
        errorsRoot = addObject(issuesRoot,errorsRootName); // was nullChannel
        warningsRoot = addObject(issuesRoot,warningsRootName); // was nullChannel

        sessionRoot = addObject(null,sessionRootName); // was sessionRoot   
    	    	
    	logger.fine("createChannelNodes(new)");
    	
    	//channelRoot = addObject(null,channelRootName); // was channelRoot

        org.pi4soa.cdl.TypeDefinitions typeDefinitions = choreography.getTypeDefinitions();
        org.eclipse.emf.common.util.EList channelTypes = typeDefinitions.getChannelTypes();

        // we would like to sort the list of channels first

        org.eclipse.emf.common.util.ECollections.sort(channelTypes, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((org.pi4soa.cdl.ChannelType) o1).getName().compareTo(
                                             ((org.pi4soa.cdl.ChannelType) o2).getName()
                                             );
                }
            });

        /*
        Iterator listIterator = channelTypes.iterator();
        while(listIterator.hasNext()){
            org.pi4soa.cdl.ChannelType channelType = (org.pi4soa.cdl.ChannelType) listIterator.next();
            ProxyTreeNode node = new ProxyTreeNode(channelType.getName(),
            					channelType);
            logger.fine("Adding channel type to tree: " + channelType.getName());
            addObject(channelRoot,node);
        }
        */
    }
    
    
    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
    	logger.fine("addObject " + child.toString() + " to child");
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
    	logger.fine("addObject " + child.toString() + " to parent");
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child, 
                                            boolean shouldBeVisible) {
    	logger.fine("addObject " + child.toString() + " to parent with visibility " + shouldBeVisible);
        DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, 
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }


    /**
     * Adds the channel nodes to the tree, sorted in name order.
     */
    public void setChannelSelectionOn(boolean channelSelection){
        if(channelSelection == true){
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        else{

        }
    }


    /**
     *
     */
    public JTree getTree(){
        return tree;
    }


    /**
     *
     */
    public void redraw(){
        tree.setSelectionPaths(tree.getSelectionPaths());
        tree.revalidate();
        tree.repaint();
    }
    
    public class ProxyTreeNode {
    	
    	public ProxyTreeNode(String label, Object obj) {
    		m_label = label;
    		m_object = obj;
    	}
    	
    	public String getLabel() {
    		return(m_label);
    	}
    	
    	public Object getObject() {
    		return(m_object);
    	}
    	
    	public String toString() {
    		return(m_label);
    	}
    	
    	private String m_label=null;
    	private Object m_object=null;
    }
    
    public class ChannelTreeCellRenderer extends DefaultTreeCellRenderer{

        private Color originalTextNonSelectionColor = null;
        private Color originalTextSelectionColor = null;

        /**
         *
         */
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus){
        	
        	logger.fine("getTreeCellRendererComponent(" + value.toString() + ")");

            if(originalTextNonSelectionColor == null) originalTextNonSelectionColor = this.getTextNonSelectionColor();
            if(originalTextSelectionColor == null) originalTextSelectionColor = this.getTextSelectionColor();

            boolean nodeHasErrors = false;
            boolean nodeHasWarnings = false;
            boolean nodeHasUnexpectedMessage = false;

            if(leaf == true){
                if(exchangeEventsData.getHasErrorsForChannel(value.toString()) == true ||
                		exchangeEventsData.getHasErrorsForSession(value.toString())) {
                    nodeHasErrors = true;
                }
                else if(exchangeEventsData.getHasWarningsForChannel(value.toString()) == true ||
                		exchangeEventsData.getHasWarningsForSession(value.toString())) {
                    nodeHasWarnings = true;
                }
                else if (exchangeEventsData.getHasUnexpectedExchangeEventsForChannel(value.toString()) == true ||
                		exchangeEventsData.getHasUnexpectedExchangeEventsForSession(value.toString())) {
                	nodeHasUnexpectedMessage = true;
                }
            }
            else{
                //nodeHasErrors = exchangeEventsData.getHasErrors();
                //nodeHasWarnings = exchangeEventsData.getHasWarnings();
                //nodeHasUnexpectedMessage = exchangeEventsData.getHasUnexpectedMessages();
            }

            //
            // Sets the left panel colors
            //
            if(nodeHasErrors == true){
            	logger.fine("hasErrors - dark red");
                this.setTextNonSelectionColor(Color.red.darker());
                this.setTextSelectionColor(Color.red.darker());
            }
            else if(nodeHasWarnings == true){
            	logger.fine("hasWarnings - orange");
                this.setTextNonSelectionColor(Color.orange);
                this.setTextSelectionColor(Color.orange);
            }
            else if (nodeHasUnexpectedMessage == true) {
            	logger.fine("hasUnexpectedMessages - red");
                this.setTextNonSelectionColor(Color.red);
                this.setTextSelectionColor(Color.red);            	
            }
            else{
            	logger.fine("hasOkay");
                this.setTextNonSelectionColor(originalTextNonSelectionColor);
                this.setTextSelectionColor(originalTextSelectionColor);
            }

            if (value instanceof DefaultMutableTreeNode) {
                m_lastValue = ((DefaultMutableTreeNode)value).getUserObject();
            } else {
                m_lastValue = value;
            }
            
            return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }

        public Icon getOpenIcon() {
        	Icon ret=super.getOpenIcon();
        	
        	if (m_lastValue.toString().equals(sessionRootName)) {
        		ret = txnOpenIcon;
        	} else if (m_lastValue.toString().equals(channelRootName)) {
        		ret = channelOpenIcon;
        	} else if (m_lastValue.toString().equals(issuesRootName)) {
        		ret = issuesOpenIcon;
        	}
        	
        	logger.info("Returning open icon for "+m_lastValue);
        	return(ret);
        }

        public Icon getClosedIcon() {
        	Icon ret=super.getClosedIcon();

        	if (m_lastValue.toString().equals(sessionRootName)) {
        		ret = txnClosedIcon;
        	} else if (m_lastValue.toString().equals(channelRootName)) {
        		ret = channelClosedIcon;
        	} else if (m_lastValue.toString().equals(issuesRootName)) {
        		ret = issuesClosedIcon;
        	}
        	
        	logger.info("Returning closed icon for "+m_lastValue);
        	return(ret);
        }

        public Icon getLeafIcon() {
        	Icon ret=super.getLeafIcon();

        	if (m_lastValue instanceof ProxyTreeNode) {
        		if (((ProxyTreeNode)m_lastValue).getObject() instanceof CorrelationSession) {
        			ret = txnLeafIcon;
        		} else if (((ProxyTreeNode)m_lastValue).getObject()
        					instanceof org.pi4soa.cdl.ChannelType) {
        			ret = channelLeafIcon;
        		}
        	} else if (m_lastValue.toString().equals(sessionRootName)) {
        		ret = txnEmptyIcon;
        	} else if (m_lastValue.toString().equals(channelRootName)) {
        		ret = channelEmptyIcon;
        	} else if (m_lastValue.toString().equals(unexpectedMessagesRootName)) {
        		ret = unexpectedLeafIcon;
        	} else if (m_lastValue.toString().equals(errorsRootName)) {
        		ret = errorsLeafIcon;
        	} else if (m_lastValue.toString().equals(warningsRootName)) {
        		ret = warningsLeafIcon;
        	}
        	logger.info("Returning leaf icon for "+m_lastValue+" of type "+m_lastValue.getClass());
        	return(ret);
        }
        
        private Object m_lastValue=null;
    }
    
}

