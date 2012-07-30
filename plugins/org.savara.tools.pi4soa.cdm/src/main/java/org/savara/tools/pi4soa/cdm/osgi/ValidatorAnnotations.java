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
 * 21 Oct 2008 : Initial version created by gary
 */
package org.savara.tools.pi4soa.cdm.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.pi4soa.common.annotations.eclipse.EclipseAnnotationsManager;

/**
 * This class implements the AnnotationsManager extension point.
 */
public class ValidatorAnnotations extends EclipseAnnotationsManager {

	public ValidatorAnnotations() {
		super(Activator.PLUGIN_ID);
	}
	
	/**
	 * This method returns an input stream associated with the
	 * supplied URL. The URL can also reference a local relative
	 * file path.
	 * 
	 * @param url The URL, or relative file path
	 * @return The input stream
	 */
	protected java.io.InputStream getInputStream(String url) {
		java.io.InputStream ret=null;
		
		try {
			String path=ANNOTATIONS_FOLDER +
							PATH_SEPARATOR + url;

			logger.fine("Load from path: "+path);

			ret = ValidatorAnnotations.class.getResourceAsStream(path);

		} catch(Exception e) {
			logger.severe("Failed to get input stream for URL '"+
					url+"': "+e);
		}

		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Ret="+ret);
		}
		
		return(ret);
	}
	
    private static Logger logger = Logger.getLogger("org.savara.tools.validator.osgi");

	private static final String	ANNOTATIONS_FOLDER="annotations";
    private static final String PATH_SEPARATOR = "/";
}
