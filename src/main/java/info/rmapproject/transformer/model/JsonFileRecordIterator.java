package info.rmapproject.transformer.model;

import java.io.File;

/**
 * Supports iteration through files on a particular path to retrieve JSON records one at a time
 * @author khanson
 */
public class JsonFileRecordIterator implements FileRecordIterator {
	
	/**
	 * Stores file list so that we don't need to rebuild the file list for each record request
	 * Maintains position in list for iteration
	 */
	private FileList inputFileList;

	/**
	 * Stores current file we are working on
	 */
	private File currentFile;
	
	/**
	 * Root for JSON record
	 * **/
	private String recordRoot;
		
	/**
	 * Stores JSON array taken from current file. Maintains position in list for iteration
	 */
	private JsonRecordList records;

	
	/**
	 * Constructor loads file information using inputPath and inputFileExt provided by user.
	 * @param inputPath
	 * @param inputFileExt
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
       
    public boolean hasNext(){
	   	//are we on last record of last file?
		return (!inputFileList.hasNext() && !records.hasNext());
    }
	
}
