/*
 * Copyright 2005-7 Pi4 Technologies Ltd
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
 * Feb 14, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.savara.scenario.model.Group;
import org.savara.scenario.model.Import;
import org.savara.scenario.model.ReceiveEvent;
import org.savara.scenario.model.Role;
import org.savara.scenario.model.SendEvent;
import org.savara.scenario.model.TimeElapsedEvent;
import org.savara.scenario.model.Link;
import org.savara.tools.scenario.designer.DesignerImages;
import org.savara.tools.scenario.designer.model.ModelCreationFactory;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;

/**
 * This class implements the palette root for the designer.
 */
public class ScenarioPaletteRoot extends PaletteRoot {

	/**
     * Default constructor.
     * 
     */
    public ScenarioPaletteRoot() {
        // create root
        super();

        // a group of default control tools
        PaletteGroup controls = new PaletteGroup("Controls");
        add(controls);

        // the selection tool
        ToolEntry tool = new SelectionToolEntry();
        controls.add(tool);

        // use selection tool as default entry
        setDefaultEntry(tool);

    	PaletteDrawer drawer=new PaletteDrawer("Scenario");
    	
    	controls.add(drawer);
    	
        CombinedTemplateCreationEntry entry =
            new CombinedTemplateCreationEntry(
                "Role",
                "Creates a role",
                new Role(),
                new ModelCreationFactory(Role.class),
                DesignerImages.getImageDescriptor("Role.png"),
                null);
        drawer.add(entry);

        entry = new CombinedTemplateCreationEntry(
                "Event Group",
                "Creates an event group",
                new Group(),
                new ModelCreationFactory(Group.class),
                DesignerImages.getImageDescriptor("Group.png"),
                null);
        drawer.add(entry);

        drawer.add(new PaletteSeparator());
        
        // conection creation
        drawer.add(
            new ConnectionCreationToolEntry(
                "Message Link",
                "Creates a link between two message events",
                new ModelCreationFactory(Link.class),
                DesignerImages.getImageDescriptor("Link.png"),
				null));
        
        drawer.add(new PaletteSeparator());

        entry = new CombinedTemplateCreationEntry(
                "Send",
                "Creates a send",
                new SendEvent(),
                new ModelCreationFactory(SendEvent.class),
                DesignerImages.getImageDescriptor("Send.png"),
                null);
        drawer.add(entry);

        // Using the interface MessageEvent, instead of the MessageEvent
        // (EMF) EClass, to distinguish the receive from the send
        // until potentially we explicitly model these as separate
        // events.
        entry = new CombinedTemplateCreationEntry(
                "Receive",
                "Creates a receive",
                new ReceiveEvent(),
                new ModelCreationFactory(ReceiveEvent.class),
                DesignerImages.getImageDescriptor("Receive.png"),
                null);
        drawer.add(entry);

        entry = new CombinedTemplateCreationEntry(
                "Elapsed Time",
                "Creates an elapsed time event",
                new TimeElapsedEvent(),
                new ModelCreationFactory(TimeElapsedEvent.class),
                DesignerImages.getImageDescriptor("TimeElapsed.png"),
                null);
        drawer.add(entry);

        drawer.add(new PaletteSeparator());

        entry = new CombinedTemplateCreationEntry(
                "Import Scenario",
                "Import another scenario",
                new Import(),
                new ModelCreationFactory(Import.class),
                DesignerImages.getImageDescriptor("Import.png"),
                null);
        drawer.add(entry);
    }
}
