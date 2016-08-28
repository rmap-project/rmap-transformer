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
 * TransformType enum lists all sources for the RecordTypes 
 * @author khanson
 */
public enum TransformType {
	
	/** SHARE API. */
	SHARE_API (RecordType.SHARE, "api"), 
	
	/** SHARE local file. */
	SHARE_LOCAL (RecordType.SHARE, "local"), 
	
	/** OSF Registrations API. */
	OSF_REGISTRATIONS_API (RecordType.OSF_REGISTRATION, "api"),
	
	/** OSF Users API. */
	OSF_USERS_API (RecordType.OSF_USER, "api"),
	
	/** OSF Nodes API. */
	OSF_NODES_API (RecordType.OSF_NODE, "api");
	
	/** The record type. */
	private RecordType recordType;
	
	/** The source - api or local */
	private String source;
	
	/**
	 * Instantiates a new transform type.
	 *
	 * @param value the Record Type 
	 * @param source the source (api or local)
	 */
	private TransformType(RecordType value, String source){
		this.recordType = value;
		this.source = source;
	}
	
	/**
	 * Record type.
	 *
	 * @return the record type
	 */
	public RecordType recordType(){
		return this.recordType;
	}
	
	/**
	 * Get the current source
	 *
	 * @return the string
	 */
	public String source(){
		return this.source;
	}
	
	/**
	 * Gets the Transform type based on the record type and source
	 *
	 * @param value the transform type as string
	 * @param source the source (api or local)
	 * @return the transform type
	 */
	public static TransformType getVal(String value, String source) {
        if (value != null) {
        	RecordType recordType = RecordType.forValue(value);
            for (TransformType type : TransformType.values()) {
                if (recordType.equals(type.recordType()) && source.equals(type.source())) {                	
                    return type;
                }
            }
        }
        return null;
    }
	
}
