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
 * 5 Feb 2008 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor.properties;

/**
 * This is a default property section implementation used for
 * text based properties.
 */
public class DefaultTextPropertySection 
				extends AbstractTextPropertySection {

	public DefaultTextPropertySection(String property, 
			String displayName) {
		super(property, displayName, displayName);
	}

	public DefaultTextPropertySection(String property, 
			String displayName, String label) {
		super(property, displayName, label);
	}

	public DefaultTextPropertySection(String property, 
			String displayName, String label, int start, int end) {
		super(property, displayName, label, start, end);
	}

    public boolean isCreateForm() {
		return(false);
	}
}
