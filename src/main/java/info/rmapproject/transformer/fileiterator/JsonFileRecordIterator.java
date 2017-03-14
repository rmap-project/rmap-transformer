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
import java.util.Iterator;

/**
 * Supports iteration through files on a particular path to retrieve JSON records one at a time.
 *
 * @author khanson
 * @param <E> the element type
 */
public class JsonFileRecordIterator<E> implements Iterator<String> {
	
	/** Stores file list so that we don't need to rebuild the file list for each record request Maintains position in list for iteration. */
	private FileList inputFileList;

	/** Stores current file we are working on. */
	private File currentFile;
	
	/** Root for JSON record *. */
	private String recordRoot;
		
	/**
	 * Stores JSON array taken from current file. Maintains position in list for iteration
	 */
	private JsonRecordList records;

	
	/**
	 * Constructor loads file information using inputPath and inputFileExt provided by user.
	 *
	 * @param inputPath the input path
	 * @param inputFileExt the input file ext
	 * @param recordRoot the record root
	 */
	public JsonFileRecordIterator(String inputPath, String inputFileExt, String recordRoot){
		if (inputPath==null){
			throw new IllegalArgumentException("inputPath cannot be null");
		}
		if (inputFileExt==null){
			throw new IllegalArgumentException("inputFileExt cannot be null");
		}
		if (recordRoot==null){
			throw new IllegalArgumentException("recordRoot cannot be null");
		}		
		
		this.inputFileList = new FileList(inputPath, inputFileExt);
		this.recordRoot = recordRoot;
	}


	/**
	 * Get next JSON record in sequence - iterates through each file and extracts JSON records one at a time on request.
	 *
	 * @return the next JSON record as a String
	 */
    public String next() {
    	if (this.currentFile==null && !this.inputFileList.hasNext()){
    		throw new RuntimeException("There are no files in the file list. Need at least one file");
    	}
    	
    	
    	if (this.currentFile==null){
    		//first pass, load currentFile
			this.currentFile=this.inputFileList.next();
    	}

		if (this.records==null){
    		//haven't read in records yet, read them from current file...
    		this.records = new JsonRecordList(this.currentFile, recordRoot);
    	}
    	
    	
    	if (this.inputFileList.getCurrFileIndex()==-1 
    			|| !this.records.hasNext()) {
    		//either first time accessing, or done with previous file... open new file
    		this.currentFile = this.inputFileList.next();
    		//load corresponding records
        	this.records = new JsonRecordList(this.currentFile, recordRoot);
    	}
    	
    	String jsonRecord = records.next();
    	return jsonRecord;
    }
       
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext(){
	   	//are we on last record of last file?
		return (inputFileList.hasNext() || records.hasNext());
    }
	
}
