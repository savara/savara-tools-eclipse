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

import java.awt.Component;

import java.util.Vector;
import java.util.Date;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;

import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JCheckBox;
//import javax.swing.JPopupMenu;
import javax.swing.JComboBox;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.ListSelectionEvent;

import javax.swing.event.ListSelectionListener;

import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;  

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.StringWriter;
import java.io.StringReader;

//import org.pi4soa.cdl.ExchangeDetails;
import org.pi4soa.service.correlator.CorrelationSession;
import org.pi4soa.cdl.ExchangeActionType;
import org.savara.tools.monitor.ExchangeEvent;
import org.savara.tools.monitor.ui.table.TableSorter;


/**
 * This is the main user interface for the swing monitor.
 *
 * It comprises a three-panel display, listing Channels, Exchange
 * Events, and Exchange Event Details.
 *
 * @author Martin Redington
 */
public class MonitorMainPanel extends JPanel{

    private static Logger logger = Logger.getLogger("org.savara.tools.monitor.ui");

    //    Vector exchangeEvents = null;
    ExchangeEventsData exchangeEventsData = null;
    
    MonitorMenuBar menuBar =null;

    ControlPanel controlPanel = null;

    ChannelJPanel channelPanel = null;
    StatusPanel statusPanel = null;

    ExchangeEventsTableModel exchangeEventsTableModel = null;
    TableSorter sorter = null;

    String choreographyName = null;

    ExchangeJPanel exchangePanel = null;

    public static final Comparator INTEGER_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Integer) o1).intValue() - ((Integer) o2).intValue();
            }
        };

    /**
     * Constructor.
     */
    public MonitorMainPanel(ExchangeEventsData exchangeEventsData,
    					boolean choreoProvided){

    	this.menuBar = new MonitorMenuBar(choreoProvided);
    	
        this.exchangeEventsData = exchangeEventsData;

        this.setLayout(new BorderLayout());
        this.add(menuBar, BorderLayout.PAGE_START);

        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BorderLayout());

        controlPanel = new ControlPanel();
        layoutPanel.add(controlPanel, BorderLayout.PAGE_START);

        MessageJPanel exchangeDetailPanel = null;

        if(true){
            exchangeDetailPanel = new MessageJPanel(true);
        }

        exchangeDetailPanel.setPreferredSize(new Dimension(600, 100));
        exchangeDetailPanel.setMinimumSize(new Dimension(400, 100));

        exchangePanel = new ExchangeJPanel(exchangeDetailPanel);
        exchangePanel.setPreferredSize(new Dimension(600, 350));
        exchangePanel.setMinimumSize(new Dimension(400, 200));

        channelPanel = new ChannelJPanel(this.exchangeEventsData,exchangeEventsTableModel);
        channelPanel.setPreferredSize(new Dimension(200, 580));
        channelPanel.setMinimumSize(new Dimension(180, 400));

        statusPanel = new StatusPanel();


        JSplitPane otherPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, exchangePanel, exchangeDetailPanel);
        JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, channelPanel, otherPanel);
        layoutPanel.add(mainPanel, BorderLayout.CENTER);
        layoutPanel.add(statusPanel, BorderLayout.SOUTH);

        this.add(layoutPanel, BorderLayout.CENTER);
    }

    ////////////////////////////////////////////////////////////
    
    /** Returns the Channel Panel **/
    public ChannelJPanel getChannelPanel()
    {
    	return channelPanel;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MonitorMainPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    ////////////////////////////////////////////////////////////

    /**
     *
     */
    public void loadedChoreography(org.pi4soa.cdl.Package choreography){
        channelPanel.createAndAddTree(choreography);
        exchangeEventsTableModel.fireTableDataChanged();
        menuBar.loadedChoreography();
        choreographyName = choreography.getName();
        setStatus("Loaded " + choreographyName);
    }

    ////////////////////////////////////////////////////////////

    /**
     *
     */
    public void startedMonitoring(){
        menuBar.startedMonitoring();
        setStatus("Monitoring " + choreographyName);
    }

    ////////////////////////////////////////////////////////////

    /**
     *
     */
    public void stoppedMonitoring(){
        menuBar.stoppedMonitoring();
        setStatus("Stopped monitoring " + choreographyName);
    }

    ////////////////////////////////////////////////////////////

    /**
     *
     */
    public void importedEvents(){
        exchangeEventsTableModel.cancelFiltering();
        exchangeEventsTableModel.fireTableDataChanged();
    }

    ////////////////////////////////////////////////////////////

    /**
     * Can't remember what this is for.
     */
    public void setApplet(Monitor applet) {
        //        ui.setApplet(applet);
        menuBar.setMonitor(applet);
    }
    


    ////////////////////////////////////////////

    /**
     * Called by the SwingMonitor when a new exchange event occurs.
     *
     * The event will already have been added to the vector or
     * exchange events.
     */
    public void addedExchangeEvent(ExchangeEventWrapper wrapper){
    	logger.fine(">>>> addedExchangeEvent");

        String selectedChannelName = (String) exchangeEventsTableModel.getSelectedChannelName();
        logger.fine("channel is: " + selectedChannelName);

        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
        	logger.fine("fix flickering ...");
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
            	logger.fine("inner ...");
                // This might not work when we hack the table ...
                //                exchangeEventsTableModel.fireTableRowsInserted(index, index);
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsInserted(newIndex, newIndex);
            }
        }

        updateTreeIfEventIsFault(wrapper);
        logger.fine("<<<< addedExchangeEvent");
    }
    
    ////////////////////////////////////////////

    /**
     * Called by the SwingMonitor when a new exchange event occurs.
     *
     * The event will already have been added to the vector or
     * exchange events.
     */
    public void addedUnexpectedExchangeEvent(ExchangeEventWrapper wrapper){
    	logger.fine(">>>> addedUnexpectedExchangeEvent");
        String selectedChannelName = ChannelJPanel.getUnexpectedMessagesName();
        logger.fine("channel is: " + selectedChannelName);
        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
        	logger.fine("fix flickering ...");
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
                // This might not work when we hack the table ...
                //                exchangeEventsTableModel.fireTableRowsInserted(index, index);
            	logger.fine("inner ...");
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsInserted(newIndex, newIndex);
            } else {
            	exchangeEventsTableModel.fireTableDataChanged();
            }
        }
        exchangeEventsTableModel.fireTableDataChanged();
        updateTreeIfEventIsFault(wrapper);
        
    	logger.fine("<<<< addedUnexpectedExchangeEvent");
    }

    public void addedErrorEvent(ExchangeEventWrapper wrapper){
    	logger.fine(">>>> addedErrorEvent");
        String selectedChannelName = ChannelJPanel.getErrorName();
        logger.fine("channel is: " + selectedChannelName);
        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
        	logger.fine("fix flickering ...");
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
                // This might not work when we hack the table ...
                //                exchangeEventsTableModel.fireTableRowsInserted(index, index);
            	logger.fine("inner ...");
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsInserted(newIndex, newIndex);
            } else {
            	exchangeEventsTableModel.fireTableDataChanged();
            }
        }
        exchangeEventsTableModel.fireTableDataChanged();
        updateTreeIfEventIsFault(wrapper);
        
    	logger.fine("<<<< addedErrorEvent");
    }

    public void addedWarningEvent(ExchangeEventWrapper wrapper){
    	logger.fine(">>>> addedErrorEvent");
        String selectedChannelName = ChannelJPanel.getWarningName();
        logger.fine("channel is: " + selectedChannelName);
        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
        	logger.fine("fix flickering ...");
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
                // This might not work when we hack the table ...
                //                exchangeEventsTableModel.fireTableRowsInserted(index, index);
            	logger.fine("inner ...");
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsInserted(newIndex, newIndex);
            } else {
            	exchangeEventsTableModel.fireTableDataChanged();
            }
        }
        exchangeEventsTableModel.fireTableDataChanged();
        updateTreeIfEventIsFault(wrapper);
        
    	logger.fine("<<<< addedErrorEvent");
    }

    public void addedInformationEvent(ExchangeEventWrapper wrapper){
    	logger.fine(">>>> addedErrorEvent");
        String selectedChannelName = ChannelJPanel.getInformationName();
        logger.fine("channel is: " + selectedChannelName);
        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
        	logger.fine("fix flickering ...");
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
                // This might not work when we hack the table ...
                //                exchangeEventsTableModel.fireTableRowsInserted(index, index);
            	logger.fine("inner ...");
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsInserted(newIndex, newIndex);
            } else {
            	exchangeEventsTableModel.fireTableDataChanged();
            }
        }
        exchangeEventsTableModel.fireTableDataChanged();
        updateTreeIfEventIsFault(wrapper);
        
    	logger.fine("<<<< addedErrorEvent");
    }
    
    ////////////////////////////////////////////

    /**
     * Called by the SwingMonitor when an existing exchange event is updated.
     *
     */
    public void updatedExchangeEvent(ExchangeEventWrapper wrapper){

        String selectedChannelName = (String) exchangeEventsTableModel.getSelectedChannelName();

        // we are trying to fix the flickering updates here ...
        if(selectedChannelName == null || wrapper.getChannelType().equals(selectedChannelName)){
            int index = -1;
            if((index = exchangeEventsTableModel.indexOfExchangeEventWrapperInFilteredList(wrapper)) != -1){
                int newIndex = exchangeEventsTableModel.getRowCount() - 1 - index;
                exchangeEventsTableModel.fireTableRowsUpdated(newIndex, newIndex);
            }
        }

        updateTreeIfEventIsFault(wrapper);
    }

    ////////////////////////////////////////////

    /**
     *
     */
    void updateTreeIfEventIsFault(ExchangeEventWrapper wrapper){
        if(wrapper.isFault()){
            channelPanel.revalidate();
            channelPanel.redraw();
        }
    }

    ////////////////////////////////////////////

    /**
     *
     */
    public void setStatus(String status){
        statusPanel.setStatus(status);
    }

    ////////////////// Inner classes



    ////////////////////////////////////////////

    /**
     *
     */
  
    ////////////////////////////////////////////

    /**
     * The upper pane contains a table listing the exchange events.
     */
    public class ExchangeJPanel extends JPanel{

        /**
         *
         */
        public ExchangeJPanel(ListSelectionListener listSelectionListener){

            exchangeEventsTableModel = new ExchangeEventsTableModel();

            final ExchangeEventRenderer exchangeEventRenderer = new ExchangeEventRenderer(true);
            
            JTable table = new JTable() {
                public TableCellRenderer getCellRenderer(int row, int column) {
                    return exchangeEventRenderer;
                }
            };
            table.setModel(exchangeEventsTableModel);

            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setColumnSelectionAllowed(false);
            table.getTableHeader().setReorderingAllowed(false);
            
            table.getColumnModel().getColumn(0).setPreferredWidth(40);
            table.getColumnModel().getColumn(3).setPreferredWidth(200);
            table.getColumnModel().getColumn(4).setPreferredWidth(40);
            
            ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.addListSelectionListener(listSelectionListener);

            JScrollPane scrollpane = new JScrollPane(table);
            this.setLayout(new BorderLayout());
            this.add(scrollpane, BorderLayout.CENTER);
        }
    }

    ////////////////////////////////////////////

    /**
     *
     */
    public class ExchangeEventRenderer extends JLabel implements TableCellRenderer{

        public ExchangeEventRenderer(boolean isBordered) {
            //            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(JTable table, Object object,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Integer index = exchangeEventsTableModel.getEventIndexForRow(row);

            ExchangeEventWrapper wrapper = exchangeEventsData.getWrapper(index.intValue());
            
            logger.fine("RENDERING EXCHANGE:");
            logger.fine("EXCHANGE IS: " + wrapper);

            if(isSelected){
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else{
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            if (object != null) {
            	setText(object.toString());
            } else {
            	setText("");
            }

            //
            // Sets the individual exchange color
            //
            if(wrapper.isError()){
            	logger.info("isError - setting color dark red");
                setForeground(Color.red.darker());
            }
            else if (wrapper.isUnexpected()){
            	logger.info("isUnexpected - setting color red");
            	setForeground(Color.red);
            }
            else if(wrapper.isWarning()){
            	logger.info("isWarning - setting color orange");
                setForeground(Color.orange);
            }
            else if(wrapper.isInformationMessage() && column == 3){
            	logger.info("isInformationMessage - setting color blue");
                setForeground(Color.blue);            	
            }
            if (wrapper.isUnexpected())
            	setToolTipText("This message, '" + wrapper.getSendVariableTypeName()+ "' is invalid for this choreography.");
            else
            	setToolTipText(wrapper.getDescription()); //Discussed in the following section

            return this;
        }
    }

    ////////////////////////////////////////////

    /**
     *
     */
    public class ExchangeEventsTableModel extends AbstractTableModel implements TreeSelectionListener{

        String selectedChannelName = null;

        Vector filteredExchangeEvents = null;

        Vector emptyVector = new Vector();

        boolean filterByChannel = true;

        ////////////////////////////////////////////

        /**
         *
         */
        public ExchangeEventsTableModel(){
            this.filteredExchangeEvents = exchangeEventsData.getExchangeEvents();
        }

        ////////////////////////////////////////////

        public int getColumnCount() {
            return 5;
        }

        ////////////////////////////////////////////

        public int getRowCount() {
            return filteredExchangeEvents.size();
        }

        ////////////////////////////////////////////

        public String getColumnName(int col) {
            return getColumnNameSTP(col);
        }

        ////////////////////////////////////////////

        public String getColumnNameSTP(int col) {
            if(col == 0){ return "Session Id"; }
            else if(col == 1){ return "From"; }
            else if(col == 2){ return "To"; }
            else if(col == 3){ return "Msg"; }
            else return "Status";
            //            else return "Priority";
        }

        ////////////////////////////////////////////

        /**
         *
         */
        public Object getValueAt(int row, int col) {
            return getValueAtSTP(row, col);
        }

        ////////////////////////////////////////////

        /**
         *
         */
        public Class getColumnClass(int col) {
            return Object.class;
        }

        ////////////////////////////////////////////

        /**
         *
         */
        protected ExchangeEventWrapper getWrapperForRow(int row) {
            return (ExchangeEventWrapper) filteredExchangeEvents.elementAt(filteredExchangeEvents.size() - (row + 1));
        }

        ////////////////////////////////////////////


        /**
         *
         */
        public Integer getEventIndexForRow(int row) {
            ExchangeEventWrapper wrapper = getWrapperForRow(row);
            return wrapper.getIndex();
        }

        ////////////////////////////////////////////

        /**
         *
         */
        public Object getValueAtSTP(int row, int col) {
            ExchangeEventWrapper wrapper = getWrapperForRow(row);
            //            ExchangeEvent exchangeEvent = wrapper.getExchangeEvent();

            Object result = null;

            switch(col){
               case 0:
                   result = wrapper.getMessageIdentity();
                   break;
               case 1:
            	   result="";
      
            	   if (wrapper.getFromRoleTypeName() != null &&
            			   wrapper.getFromRoleTypeName().trim().length() > 0) {
            		   result = wrapper.getFromRoleTypeName();
            	   } else if (wrapper.getServiceName() != null) {
            		   result = org.pi4soa.common.xml.NameSpaceUtil.getLocalPart(
            				   		wrapper.getServiceName());
            	   }
                   break;
               case 2:
            	   result="";
            	      
            	   if (wrapper.getToRoleTypeName() != null) {
            		   result = wrapper.getToRoleTypeName();
            	   }
                   break;
               case 3:
                   result = wrapper.getMessageSummary();
                   break;
               case 4:
                   result = wrapper.getStatus();
                   break;
               default:
                   break;
            }

            return result;
        }

        ////////////////////////////////////////////


        /**
         *
         */
        public void cancelFiltering(){
            JTree tree = channelPanel.getTree();

            Vector exchangeEvents = exchangeEventsData.getExchangeEvents();
            if(this.filteredExchangeEvents != exchangeEvents){
                this.filteredExchangeEvents = exchangeEvents;
            }
            selectedChannelName = null;
            fireTableDataChanged();
            tree.clearSelection();
        }

        ////////////////////////////////////////////

        /**
         *
         */
        public void setFilterByChannel(boolean filterByChannel){
            this.filterByChannel = filterByChannel;
            JTree tree = channelPanel.getTree();

            if(filterByChannel == true){
                valueChangedForTree(tree);
            }
            else{
                Vector exchangeEvents = exchangeEventsData.getExchangeEvents();
                if(this.filteredExchangeEvents != exchangeEvents){
                    this.filteredExchangeEvents = exchangeEvents;
                }
                selectedChannelName = null;
                fireTableDataChanged();

                tree.clearSelection();
            }
        }

        ////////////////////////////////////////////////////////////

        /**
         *
         */
        public void setFilterByEventType(int index, ControlPanel controlPanel){

            Vector oldFilteredExchangeEvents = filteredExchangeEvents;
            Vector newFilteredExchangeEvents = null;

            switch(index){
               case 0:
                   newFilteredExchangeEvents = exchangeEventsData.getExchangeEvents();
                   break;
               case 1:
                   newFilteredExchangeEvents = exchangeEventsData.getWarningAndErrorExchangeEvents();
                   break;
               case 2:
                   newFilteredExchangeEvents = exchangeEventsData.getErrorExchangeEvents();
                   break;
               case 3:
                   newFilteredExchangeEvents = exchangeEventsData.getWarningExchangeEvents();
                   break;
              default:
                // this ain't gonna happen
                  return;
                  //break;
            }
            /*
            if(oldFilteredExchangeEvents != newFilteredExchangeEvents){
                // we should also reset the enablement of the channel filtering ...
                if(index == 0){
                    controlPanel.setFilterEventsByChannelIsEnabled(true);
                }
                else{
                    controlPanel.setFilterEventsByChannelIsEnabled(false);
                }

                this.filteredExchangeEvents = newFilteredExchangeEvents;
                fireTableDataChanged();
            }
            */
        }

        ////////////////////////////////////////////////////////////

        /**
         * When the channel panel selection changes, we (should) filter
         * the exchange event list.
         *
         * Right now, we simply print the selection name to stderr.
         */
        public void valueChanged(TreeSelectionEvent e) {

        	logger.fine("valueChanged");
            if(filterByChannel == false){
                return;
            }

            JTree tree = (JTree) e.getSource();

            valueChangedForTree(tree);
        }


        /**
         *
         */
        public void valueChangedForTree(JTree tree) {
        	logger.fine("valueChangedForTree");
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            if(node == null) {
                selectedChannelName = null;
                return;
            }

            Vector oldFilteredExchangeEvents = filteredExchangeEvents;

            if (node.isLeaf()) {
            	logger.fine("Selected is: " + node.getUserObject().toString());
            	selectedChannelName = (String) node.getUserObject().toString();
                logger.fine("SelectedChannelName is " + selectedChannelName);
                Vector newVector = exchangeEventsData.getExchangeEventsForChannel(selectedChannelName);

                // avoid npe's
                if(newVector == null) {
                	newVector = exchangeEventsData.getExchangeEventsForSession(selectedChannelName);
                    if (newVector == null) {
                    	this.filteredExchangeEvents = emptyVector;
                    	logger.fine("emptyVector");
                    } else {
                        this.filteredExchangeEvents = newVector;
                        logger.fine("newVector (" + newVector.size() + ")");
                    }
                } else {
                    this.filteredExchangeEvents = newVector;
                    logger.fine("newVector (" + newVector.size() + ")");
                }

                

            } else {
                this.filteredExchangeEvents = exchangeEventsData.getExchangeEvents();
                selectedChannelName = null;
            }

            if(oldFilteredExchangeEvents != filteredExchangeEvents) {
                fireTableDataChanged();
            }
        }

        ////////////////////////////////////////////////////////////

        /**
         *
         */
        public String getSelectedChannelName(){
            return selectedChannelName;
        }

        ////////////////////////////////////////////////////////////

        /**
         *
         */
        public int indexOfExchangeEventWrapperInFilteredList(Object o){
            return filteredExchangeEvents.indexOf(o);
        }
    }

    ////////////////////////////////////////////

    public class StatusPanel extends JPanel{
        JLabel statusJLabel = new JLabel();

        public StatusPanel(){
            this.setLayout(new BorderLayout());
            this.add(statusJLabel, BorderLayout.LINE_START);
            setStatus("No choreography loaded");
        }

        public void setStatus(String status){
            statusJLabel.setText(status);
        }
    }

    ////////////////////////////////////////////

    /**
     *
     */
    public class ControlPanel extends JPanel implements ItemListener, ActionListener{
        //JCheckBox filterEventsByChannelJCheckBox = new JCheckBox("Filter by channel", true);

        Object sessionItems[] = { "All sessions" };
        //JComboBox sessionPopupMenu = new JComboBox(sessionItems);

        // add the sessions to the jpopup menu ...
        //        JComboBox sessionPopupMenu = new JComboBox(sessionItems);

        //Object showItems[] = { "All Events", "Errors and Warnings", "Errors", "Warnings" };
        //JComboBox showPopupMenu = new JComboBox(showItems);

        //        JComboBox showPopupMenu = new JComboBox("Show: ");



        /**
         *
         */
        public ControlPanel(){
            this.setLayout(new BorderLayout());
            //this.add(filterEventsByChannelJCheckBox, BorderLayout.WEST);

            //filterEventsByChannelJCheckBox.addItemListener(this);

            //            this.add(sessionPopupMenu, BorderLayout.CENTER);
            //JPanel layoutPanel = new JPanel();
           // layoutPanel.setLayout(new FlowLayout());

            //layoutPanel.add(new JLabel("Show: "));
            //layoutPanel.add(showPopupMenu);
            //showPopupMenu.addActionListener(this);

            //this.add(layoutPanel, BorderLayout.EAST);
        }


        /**
         *
         *
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            if(source == filterEventsByChannelJCheckBox){
                boolean filterByChannel = filterEventsByChannelJCheckBox.isSelected();
                exchangeEventsTableModel.setFilterByChannel(filterByChannel);
            }
        }
	    */
        public void itemStateChanged(ItemEvent e) {}
        

        /**
         *
         *
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if(source == showPopupMenu){
                int index = showPopupMenu.getSelectedIndex();
                exchangeEventsTableModel.setFilterByEventType(index, this);
            }
        }
        */
        public void actionPerformed(ActionEvent e) {}
        


        /**
         *
         *
        public void setFilterEventsByChannelIsEnabled(boolean setFilterEventsByChannelIsEnabled){
            if(setFilterEventsByChannelIsEnabled == false){
                filterEventsByChannelJCheckBox.setSelected(false);
            }
            filterEventsByChannelJCheckBox.setEnabled(setFilterEventsByChannelIsEnabled);
        }
        */
        public void setFilterEventsByChannelIsEnabled(boolean setFilterEventsByChannelIsEnabled){}

    }

    ////////////////////////////////////////////

    /**
     *
     */
    //    public class MessageJPanel extends ExchangeDetailJPanel implements ListSelectionListener{
    public class MessageJPanel extends JPanel implements ListSelectionListener{

        JTextArea msgTextArea = null;

        /**
         *
         */
        public MessageJPanel(boolean dummy){
            msgTextArea = new JTextArea();
            msgTextArea.setEditable(false);
            JScrollPane scrollpane = new JScrollPane(msgTextArea);
            this.setLayout(new BorderLayout());
            this.add(scrollpane, BorderLayout.CENTER);
        }


        public void valueChanged(ListSelectionEvent e) {

            logger.fine(">>>> valueChanged - new message selected");

            //Ignore extra messages.
            if (e.getValueIsAdjusting()) {
               logger.fine(">>>> just adjusting - returning early");
               return;
            }

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();

            if (lsm.isSelectionEmpty()){
               logger.fine(">>>> no rows selected");
                // no rows are selected
            }
            else{
                // selectedRow is selected
                // harhar. grab the first value ...
                int selectedRowIndex = lsm.getMinSelectionIndex();

               logger.fine(">>>> selectedIndex = " + selectedRowIndex);

                Integer index = (Integer) exchangeEventsTableModel.getEventIndexForRow(selectedRowIndex);

                // ExchangeEventWrapper wrapper = (ExchangeEventWrapper) exchangeEvents.elementAt(index.intValue());
                ExchangeEventWrapper wrapper = exchangeEventsData.getWrapper(index.intValue());


                // avoid NPE's
                if(wrapper != null){
                    this.updateDetails(wrapper);
                }
                else{
                    logger.fine(">>>> null exception wrapper");
                }
            }
        }


        /**
         *
         */
        public void updateDetails(ExchangeEventWrapper exchangeEventWrapper){
            String text = exchangeEventWrapper.getMessageValue();
            logger.info("MESSAGE PAYLOAD: " + text);
            
            try {
            	org.w3c.dom.Node node=org.pi4soa.common.xml.XMLUtils.getNode(text);
            	
            	text = org.pi4soa.common.xml.XMLUtils.getText(node, true);
            	
            } catch (Exception e) {
            	//text = "Unable to display message\r\n\r\n"+e;
            }
            
            if (exchangeEventWrapper.getExceptionValue() != null) {
            	text += "\r\n\r\nException Trace:\r\n"+
            			exchangeEventWrapper.getExceptionValue();
            }
            
            msgTextArea.setText(text);
            msgTextArea.setBackground(Color.lightGray);
            msgTextArea.setCaretPosition(0);
        }
    }

}


// EOF
