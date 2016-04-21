package info.rmapproject.transformer;

import java.io.File;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.io.Files;

/**
 * Supports iteration through files on a particular path to retrieve JSON records one at a time
 * @author khanson
 */
public class JsonFileBasedRecordImporter extends FileBasedRecordImporter {
	
	private static final String DEFAULT_IMPORT_PATH = ".";
	private static final String DEFAULT_IMPORT_EXT = "json";
	private static final String DEFAULT_RECORD_ROOT = "results";
	
	private String recordRoot;
	
	/**
	 * Stores JSON array from current file
	 */
	private JSONArray currJsonArray;
		
	/**
	 * Constructor loads file information using default paths for inputPath and inputFileExt	
	 */
	public JsonFileBasedRecordImporter(){
		super(DEFAULT_IMPORT_PATH, DEFAULT_IMPORT_EXT);	
		this.recordRoot = DEFAULT_RECORD_ROOT;
	}
	
	/**
	 * Constructor loads file information using inputPath and inputFileExt provided by user.
	 * @param inputPath
	 * @param inputFileExt
	 */
	public JsonFileBasedRecordImporter(String inputPath, String inputFileExt, String recordRoot){
		super(inputPath, inputFileExt);	
		this.recordRoot = recordRoot;
	}


	/**
	 * Get next JSON record in sequence - iterates through each file and extracts JSON records one at a time on request.
	 */
    public String getNextRecord() {
    	if (isLastRecord()){
    		return null;
    	}

    	String jsonRecord = null;
    	File currFile = null;
    	
    	if (fileIndex==-1 || recordArrayIndex==recordArraySize) {
    		//either first time accessing, or done with previous file... open new file
        	fileIndex = fileIndex+1;
    		currFile = inputFileList.get(fileIndex);    
        	recordArrayIndex = 0;	
    	} else {
        	recordArrayIndex = recordArrayIndex+1;    		
    	}
    	
    	if (recordArrayIndex==0) {
    		//load the json
    		currJsonArray = getRecordsFromFile(currFile);    		
    	} 
    	
    	jsonRecord = currJsonArray.get(recordArrayIndex).toString();
    	
    	return jsonRecord;
    }
       
    /**
     * Gets JSON array of records from file provided. 
     * @param file
     * @return
     */
	private JSONArray getRecordsFromFile(File file) {
    	if (file == null){
    		throw new RuntimeException("File is null");
    	}
    	JSONArray jsonArray = null;
	    String jsonFileContents = null;
	    try {
	    	
	    	
	    	//FileInputStream fis = new FileInputStream(file);

	    	jsonFileContents = Files.toString(file, Charset.defaultCharset());
	    	JSONObject jsonRecords = new JSONObject(jsonFileContents);
	    	jsonArray = jsonRecords.getJSONArray(recordRoot);
	    }
	    catch (Exception e) {
	    	throw new RuntimeException("Error while retrieving contents from file " + file.getPath());
	    }
		return jsonArray;
    }
	
}
