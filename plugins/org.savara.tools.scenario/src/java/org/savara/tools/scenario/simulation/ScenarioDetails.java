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
 * Jan 5, 2006 : Initial version created by gary
 */
package org.savara.tools.scenario.simulation;

/**
 * This class represents details obtained from analysing the
 * scenario execution results.
 *
 */
public class ScenarioDetails {

	/**
	 * This is the default constructor.
	 *
	 */
	public ScenarioDetails() {
	}
	
	/**
	 * This method sets the start position for the scenario
	 * details.
	 * 
	 * @param startPos The start position
	 */
	public void setStartPosition(int startPos) {
		m_startPosition = startPos;
	}
	
	/**
	 * This method returns the start position for the scenario
	 * details.
	 * 
	 * @return The start position
	 */
	public int getStartPosition() {
		return(m_startPosition);
	}
	
	/**
	 * This method sets the end position for the scenario
	 * details.
	 * 
	 * @param endPos The end position
	 */
	public void setEndPosition(int endPos) {
		m_endPosition = endPos;
	}
	
	/**
	 * This method returns the end position for the scenario
	 * details.
	 * 
	 * @return The end position
	 */
	public int getEndPosition() {
		return(m_endPosition);
	}
	
	private int m_startPosition=0;
	private int m_endPosition=0;
}
