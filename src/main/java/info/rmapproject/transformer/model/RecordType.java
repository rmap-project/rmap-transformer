/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformer.model;

/**
 * RecordType enum for listing the types of records available to transform
 */
public enum RecordType {
	
	/** SHARE. */
	SHARE ("share"), 
	
	/** OSF Registration. */
	OSF_REGISTRATION ("osf_registration"),
	
	/** OSF user. */
	OSF_USER ("osf_user"),
	
	/** OSF node. */
	OSF_NODE ("osf_node");
	
	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new record type.
	 *
	 * @param value the type as string
	 */
	private RecordType(String value){
		this.value = value;
	}
	
	/**
	 * Get current record type as string
	 *
	 * @return the string
	 */
	public String value(){
		return this.value;
	}
	
	/**
	 * Get record type enum based on string value
	 *
	 * @param value the value
	 * @return the record type
	 */
	public static RecordType forValue(String value) {
        if (value != null) {
        	value = value.toLowerCase();
            for (RecordType type : RecordType.values()) {
                if (value.equals(type.value())) {                	
                    return type;
                }
            }
        }
        return null;
    }
	
}
