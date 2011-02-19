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
import java.awt.event.*;
import java.io.*;

/**
 *
 */
public class MonitorMenuBar extends JMenuBar {

    MonitorMenuBar menuBar = this;

    static final int NEW_FRAME_WIDTH = 415;
    static final int NEW_FRAME_HEIGHT = 330;    

    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JMenu monitorMenu = new JMenu("Monitor");
    JMenu helpMenu = new JMenu("Help");
    
    JMenuItem openItem = new JMenuItem("Open Choreography...");
    JMenuItem importItem = new JMenuItem("Import Events...");
    JMenuItem exportItem = new JMenuItem("Export Events...");
    JMenuItem exitItem = new JMenuItem("Exit");
    
    JMenuItem undoItem = new JMenuItem("Undo");
    JMenuItem cutItem = new JMenuItem("Cut");
    JMenuItem copyItem = new JMenuItem("Copy");               
    JMenuItem pasteItem = new JMenuItem("Paste");               
    JMenuItem selectAllItem = new JMenuItem("Select All");               
    JMenuItem clearItem = new JMenuItem("Clear");
    JMenuItem preferencesItem = new JMenuItem("Preferences...");

    JMenuItem startMonitoringItem = new JMenuItem("Start Monitoring");
    JMenuItem stopMonitoringItem = new JMenuItem("Stop Monitoring");

    JMenuItem helpItem = new JMenuItem("About Choreography Monitor");
   
    Monitor monitor;

    /**
     * 
     */
    public MonitorMenuBar(boolean choreoProvided){
        setUpFileMenu(choreoProvided);
        setUpEditMenu(choreoProvided);
        setUpMonitorMenu(choreoProvided);
        setUpHelpMenu(choreoProvided);        
    }


    /**
     *
     */
    private void setUpFileMenu(boolean choreoProvided){
        openItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.META_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.META_MASK));
        
        openItem.setEnabled(true);
        importItem.setEnabled(true);
        exportItem.setEnabled(false);
        exitItem.setEnabled(true);

        openItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Open Choreography");

                // Note: source for ExampleFileFilter can be found in FileChooserDemo,
                // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
                MonitorFileFilter filter = new MonitorFileFilter();
                filter.addExtension("cdm");
                filter.setDescription("CDM Files");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(menuBar);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File directory = chooser.getCurrentDirectory();
                    String fileName = chooser.getSelectedFile().getName();
                    File path = new File(directory, fileName);
                    monitor.loadChoreography(path.getPath());

                    // now that we've opened, start monitoring should be enabled.
                }
            }
        });


        importItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Import Events");

                // Note: source for ExampleFileFilter can be found in FileChooserDemo,
                // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
                MonitorFileFilter filter = new MonitorFileFilter();
                filter.addExtension("data");
                filter.setDescription("Exchange Events");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(menuBar);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File directory = chooser.getCurrentDirectory();
                    String fileName = chooser.getSelectedFile().getName();
                    File path = new File(directory, fileName);
                    monitor.importEvents(path.getPath());
                }
            }
        });


        exportItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Export Events");

                // Note: source for ExampleFileFilter can be found in FileChooserDemo,
                // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
                //                ExampleFileFilter filter = new ExampleFileFilter();
                //                filter.addExtension("data");
                //                filter.setDescription("Exchange Events");
                //                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(menuBar);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File directory = chooser.getCurrentDirectory();
                    String fileName = chooser.getSelectedFile().getName();
                    File path = new File(directory, fileName);
                    monitor.exportEvents(path.getPath());
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
            	monitor.close();
            }
        });

        if (!choreoProvided) {
        	fileMenu.add(openItem);
        	fileMenu.add(new JSeparator());
        }
        
        // Disable import/export in this version
        // If re-enabled, need to sort out message summary
        // persistence, as it uses the exchange event which
        // is transient
        //fileMenu.add(importItem);                
        //fileMenu.add(exportItem);                
        //fileMenu.add(new JSeparator());                        
        
        fileMenu.add(exitItem);

        this.add(fileMenu);
    }


    /**
     *
     */
    private void setUpEditMenu(boolean choreoProvided){

        undoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.META_MASK));
        cutItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.META_MASK));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.META_MASK));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.Event.META_MASK));
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.META_MASK));
        preferencesItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P /* was VK_SEMICOLON */, 
                                                              java.awt.Event.META_MASK));

        undoItem.setEnabled(false);
        cutItem.setEnabled(false);
        copyItem.setEnabled(false);
        pasteItem.setEnabled(false);
        selectAllItem.setEnabled(false);
        clearItem.setEnabled(false);
        
        // preferences menu implementation
        preferencesItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                //                preferences.setVisible(true);
            }
        });
        
        editMenu.add(undoItem);
        editMenu.add(new JSeparator());                        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);                                                
        editMenu.add(selectAllItem);                                                        
        editMenu.add(clearItem);
        editMenu.add(new JSeparator());                        
        editMenu.add(preferencesItem);

        /*
        this.add(editMenu);
        */
    }


    /**
     *
     */
    private void setUpMonitorMenu(boolean choreoProvided){
        startMonitoringItem.setEnabled(false);
        stopMonitoringItem.setEnabled(false);

        startMonitoringItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                monitor.startMonitoring();
            }
        });

        stopMonitoringItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                monitor.stopMonitoring();
            }
        });

        monitorMenu.add(startMonitoringItem);
        monitorMenu.add(stopMonitoringItem);

        if (!choreoProvided) {
        	this.add(monitorMenu);
        }
    }        


    /**
     *
     */
    private void setUpHelpMenu(boolean choreoProvided){
        helpItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenu.add(helpItem);
        helpItem.setEnabled(true);

        helpItem.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                		"pi4soa Choreography Monitor\r\n\r\n" +
                		"Version 1\r\n\r\n" +
                		"(c) Pi4 Technologies Ltd, 2005-8",
                		"About ...", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        this.add(helpMenu);
    }
    

    /**
     * 
     */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }


    /**
     *
     */
    public void loadedChoreography(){
        startMonitoringItem.setEnabled(true);
    }


    /**
     *
     */
    public void startedMonitoring(){
        openItem.setEnabled(false);
        importItem.setEnabled(false);
        exportItem.setEnabled(true);
        startMonitoringItem.setEnabled(false);
        stopMonitoringItem.setEnabled(true);
    }


    /**
     *
     */
    public void stoppedMonitoring(){
        openItem.setEnabled(true);
        importItem.setEnabled(true);
        exportItem.setEnabled(true);
        startMonitoringItem.setEnabled(true);
        stopMonitoringItem.setEnabled(false);
    }

    /**
     *
     */
    public void importedEvents(){
        exportItem.setEnabled(true);
    }

} // End of class


// EOF
