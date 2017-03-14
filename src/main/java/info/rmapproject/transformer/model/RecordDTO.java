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
 * DTO class to transfer record, it's type, and id in a consistent manner.
 * @author khanson
 */
public class RecordDTO {
	
	/** ID unique to this data set. */
	private String id;
	
	/** Single record from data source. */
	private Object record;
	
	/** Type of data record. */
	private RecordType recordType;
	
	/**
	 * Instantiates a new record DTO.
	 *
	 * @param record the record
	 * @param id the id
	 * @param recordType the record type
	 */
	public RecordDTO(Object record, String id, RecordType recordType){
		if (record==null){
			throw new IllegalArgumentException("record cannot be null");
		}
		if (id==null){
			throw new IllegalArgumentException("id cannot be null");
		}
		this.id = id;
		this.record = record;
		this.recordType = recordType;
	}
	
	/**
	 * Gets the Record ID.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Gets the record.
	 *
	 * @return the record
	 */
	public Object getRecord() {
		return record;
	}

	/**
	 * Gets the record type.
	 *
	 * @return the record type
	 */
	public RecordType getRecordType() {
		return recordType;
	}

	/**
	 * Sets the record type.
	 *
	 * @param recordType the new record type
	 */
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
}
