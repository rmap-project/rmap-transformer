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
package info.rmapproject.transformer.fileiterator;

import java.io.File;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.io.Files;

/**
 * Extracts the text from a File object and breaks it into a list of records.
 *
 * @author khanson
 */
public class JsonRecordList {

	/** JSONArray of records extracted from file. */
	private JSONArray records;
	
	/** Size of the JSON Records Array. */
	private Integer size;
		
	/** Stores current position in file list. */
	protected Integer currRecordIndex = -1;
	
    //private static final Logger log = LoggerFactory.getLogger(JsonRecordList.class);
	
    /**
     * Initiates file path based on path defined and filtered by file extension.
     * Note: does not currently iterate through sub-folders.
     *
     * @param file the file
     * @param recordRoot the record root element
     */
	public JsonRecordList(File file, String recordRoot){
		if (file==null){
			throw new IllegalArgumentException("file cannot be null");
		}
		if (recordRoot==null){
			throw new IllegalArgumentException("recordRoot cannot be null");
		}

		loadJsonRecordList(file, recordRoot);
		
    	if (this.size==0 || size==null){
    		//no records
    		throw new RuntimeException("No JSON records found in file " + file.getName());    		
    	}
	}	

	/**
	 * Gets the list size.
	 *
	 * @return the size
	 */
	public Integer getSize() {
		return this.size;
	}

	/**
	 * Gets the current record index.
	 *
	 * @return the current record index
	 */
	public Integer getCurrRecordIndex() {
		return this.currRecordIndex;
	}

	/**
	 * Loads JSON array of records to be processed.
	 *
	 * @param file the file
	 * @param recordRoot the record root
	 */
	protected void loadJsonRecordList(File file, String recordRoot) {
    	if (file == null){
    		throw new RuntimeException("File is null");
    	}
    	JSONArray jsonArray = null;
	    String jsonFileContents = null;
	    try {
	    	jsonFileContents = Files.toString(file, Charset.forName("UTF-8"));
	    	JSONObject jsonRecords = new JSONObject(jsonFileContents);
	    	jsonArray = jsonRecords.getJSONArray(recordRoot);
	    	this.size = jsonArray.length();
	    	this.currRecordIndex = -1;
	    }
	    catch (Exception e) {
	    	throw new RuntimeException("Error while retrieving contents from file " + file.getPath(), e);
	    }
		this.records = jsonArray;
      }
	
	/**
	 * retrieve the next record in the list and update the current recordIndex.
	 *
	 * @return the next JSON record as a String
	 */
	public String next(){
		if (!this.hasNext()){
			throw new RuntimeException("You have reached the end of the file list.");
		}
		this.currRecordIndex=this.currRecordIndex+1;
		return this.records.get(this.currRecordIndex).toString();		
	}
		
	/**
	 * Determines whether current file is last file.
	 *
	 * @return true, if successful
	 */
    public boolean hasNext() {
    	//do we have another file?
    	return (this.size!=(currRecordIndex+1));
    }
    
	
}
