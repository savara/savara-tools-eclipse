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

import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.pi4soa.service.correlator.CorrelationSession;


/**
 * The left hand pane contains a tree whose root is the
 * choreography, and whose leaves are channels.
 *
 * Selecting a channel filters the exchange events list (or should
 * do).
 */
class MonitorTreeModelListener implements TreeModelListener {
	
	private static Logger logger = Logger.getLogger("org.savara.tools.monitor.ui");
	  
    public void treeNodesChanged(TreeModelEvent e) {
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode)
                 (e.getTreePath().getLastPathComponent());

        /*
         * If the event lists children, then the changed
         * node is the child of the node we've already
         * gotten.  Otherwise, the changed node and the
         * specified node are the same.
         */
        try {
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)
                   (node.getChildAt(index));
        } catch (NullPointerException exc) {}

        logger.fine(">>> CHANGED NODE");
        logger.fine("The user has finished editing the node.");
        logger.fine("New value: " + node.getUserObject());
    }
    public void treeNodesInserted(TreeModelEvent e) {
    	logger.fine(">>> INSERTED NODE");
    }
    public void treeNodesRemoved(TreeModelEvent e) {
    	logger.fine(">>> REMOVED NODE");
    }
    public void treeStructureChanged(TreeModelEvent e) {
    	logger.fine(">>> TREE STRUCTURE CHANGED");
    }
}