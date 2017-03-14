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
package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.transformer.fileiterator.JsonFileRecordIterator;
import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manages the iteration over SHARE data contained in local file path. 
 * Will loop through all valid JSON files on the input path provided, splitting files 
 * that contain multiple records and handing them back each time "next()" is called.
 * @author khanson
 *
 */
public class ShareLocalTransformIterator implements Iterator<RecordDTO> {

	/**  Root element for JSON record - will split on this field where there are multiple records *. */
    protected static final String ROOT_ELEMENT = "results";
       
    /** Path of folder containing local JSON files. */
	private String inputpath;
	
	/** File extension of files containing JSON. */
	private String inputFileExt;
	
	/** Iterates over file. */
	private Iterator<String> fileIterator;
		
	/**
	 * Instantiates a new share local transform iterator.
	 *
	 * @param inputpath the inputpath
	 * @param inputFileExt the input file ext
	 */
	public ShareLocalTransformIterator(String inputpath, String inputFileExt){
		super();
		if (inputpath==null){
			throw new IllegalArgumentException("inputpath cannot be null");
		}
		if (inputFileExt==null){
			throw new IllegalArgumentException("inputFileExt cannot be null");
		}
		this.inputpath = inputpath;
		this.inputFileExt = inputFileExt;
		
		//initiate importer for iteration through files
		this.fileIterator = 
				new JsonFileRecordIterator<String>(this.inputpath, this.inputFileExt, ROOT_ELEMENT);	
	}	
	

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RecordDTO next() {
		RecordDTO recordDTO = null;
		try {
			String record = fileIterator.next();
			if (record!=null){
				// Convert JSON string to Object
				ObjectMapper mapper = new ObjectMapper();
				Record sharerec = (Record) mapper.readValue(record, Record.class);
				String id = sharerec.getShareProperties().getDocID();
				String source = sharerec.getShareProperties().getSource();
				if (source!=null && source.length()>0){
					id = source + "_" + id;
				}
				recordDTO = new RecordDTO(sharerec,id, RecordType.SHARE);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not generate SHARE record", ex);
		}
		return recordDTO;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return fileIterator.hasNext();
	}
	
		
}
