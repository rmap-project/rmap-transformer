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

	/** Root element for JSON record - will split on this field where there are multiple records **/
    protected static final String ROOT_ELEMENT = "results";
       
    /**
     * Path of folder containing local JSON files
     */
	private String inputpath;
	
	/**
	 * File extension of files containing JSON
	 */
	private String inputFileExt;
	
	/**
	 * Iterates over file
	 */
	private Iterator<String> fileIterator;
		
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
	

	@Override
	public RecordDTO next() {
		RecordDTO recordDTO = null;
		try {
			String record = fileIterator.next();
			if (record!=null){
				// Convert JSON string to Object
				ObjectMapper mapper = new ObjectMapper();
				Record sharerec = (Record) mapper.readValue(record, Record.class);
				recordDTO = new RecordDTO(sharerec,sharerec.getShareProperties().getDocID(), RecordType.SHARE);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not generate SHARE record", ex);
		}
		return recordDTO;
	}

	@Override
	public boolean hasNext() {
		return fileIterator.hasNext();
	}
	
		
}
