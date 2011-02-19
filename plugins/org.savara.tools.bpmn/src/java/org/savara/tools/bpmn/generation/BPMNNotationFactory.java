/*
 * Copyright 2005-6 Pi4 Technologies Ltd
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
 * 30 Jan 2007 : Initial version created by gary
 */
package org.savara.tools.bpmn.generation;


public interface BPMNNotationFactory {

	public String getFileExtension();
	
	public void saveNotation(String modelFileName, Object diagramModel,
			String notationFileName, Object diagramNotation)
							throws BPMNGenerationException;

	public Object createDiagram(BPMNModelFactory factory,
			Object diagramModel, int x, int y, int width, int height);
	
	public Object createPool(BPMNModelFactory factory,
				Object poolModel, Object diagramNotation,
				int x, int y, int width, int height);
	
	public Object createTask(BPMNModelFactory factory,
			Object taskModel, Object parentNotation,
					int x, int y, int width, int height);
	
	public Object createJunction(BPMNModelFactory factory,
			Object junctionModel, Object parentNotation,
					int x, int y, int width, int height);
		
	public Object createMessageLink(BPMNModelFactory factory,
			Object linkModel, Object diagramNotation);
	
}
