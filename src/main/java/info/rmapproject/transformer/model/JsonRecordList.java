package info.rmapproject.transformer.model;

import java.io.File;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.io.Files;

/**
 * Extracts the text from a File object and breaks it into a list of records
 * @author khanson
 *
 */
public class JsonRecordList {

	/**
	 * JSONArray of records extracted from file
	 */
	private JSONArray records;
	
	/**
	 * Size of the JSON Records Array
	 */
	private Integer size;
		
	/**
	 * Stores current position in file list
	 */
	protected Integer currRecordIndex = -1;
	
    //private static final Logger log = LoggerFactory.getLogger(JsonRecordList.class);
	
    /**
     * Initiates file path based on path defined and filtered by file extension.
     * Note: does not currently iterate through sub-folders.
     * @param inputfilepath
     * @param inputfileext
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

	public Integer getSize() {
		return this.size;
	}

	public Integer getCurrRecordIndex() {
		return this.currRecordIndex;
	}

	/**
	 * Loads JSON array of records to be processed
	 * @param inputPath
	 * @param inputFileExt
	 */
	protected void loadJsonRecordList(File file, String recordRoot) {
    	if (file == null){
    		throw new RuntimeException("File is null");
    	}
    	JSONArray jsonArray = null;
	    String jsonFileContents = null;
	    try {
	    	jsonFileContents = Files.toString(file, Charset.defaultCharset());
	    	JSONObject jsonRecords = new JSONObject(jsonFileContents);
	    	jsonArray = jsonRecords.getJSONArray(recordRoot);
	    	this.size = jsonArray.length();
	    	this.currRecordIndex = -1;
	    }
	    catch (Exception e) {
	    	throw new RuntimeException("Error while retrieving contents from file " + file.getPath());
	    }
		this.records = jsonArray;
      }
	
	/**
	 * retrieve the next record in the list and update the current recordIndex.
	 * @return
	 */
	public String next(){
		if (!this.hasNext()){
			throw new RuntimeException("You have reached the end of the file list.");
		}
		this.currRecordIndex=this.currRecordIndex+1;
		return this.records.get(this.currRecordIndex).toString();		
	}
		
	/**
	 * Determines whether current file is last file
	 * @return
	 */
    public boolean hasNext() {
    	//do we have another file?
    	return (this.size!=(currRecordIndex+1));
    }
    
	
}
