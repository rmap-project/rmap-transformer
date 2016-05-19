package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.transformer.DiscoFile;
import info.rmapproject.transformer.DiscoModel;
import info.rmapproject.transformer.TransformMgr;
import info.rmapproject.transformer.fileiterator.JsonFileRecordIterator;

import java.util.Iterator;

import org.openrdf.model.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manages the transform of SHARE data from a local file path. 
 * Will loop through all valid JSON files on the input path provided, splitting files 
 * that contain multiple records, and writing them to the output path.
 * @author khanson
 *
 */
public class ShareLocalTransformMgr extends TransformMgr {

	/** Root element for JSON record - will split on this field where there are multiple records **/
    protected static final String ROOT_ELEMENT = "results";
        
	private String inputpath;
	private String inputFileExt;
	
	public ShareLocalTransformMgr(String outputpath, String inputpath, String inputFileExt){
		this(outputpath, inputpath, inputFileExt, null);
	}	

	
	public ShareLocalTransformMgr(String outputpath, String inputpath, String inputFileExt, String discoDescription){
		super(outputpath, discoDescription);
		if (inputpath==null){
			throw new IllegalArgumentException("inputpath cannot be null");
		}
		if (inputFileExt==null){
			throw new IllegalArgumentException("inputFileExt cannot be null");
		}
		this.inputpath = inputpath;
		this.inputFileExt = inputFileExt;
	}	

	
	/** 
	 * Collect SHARE data from files on a particular path and transform them to DiSCOs.
	 * @param inputpath - path of files to be converted
	 * @param inputFileExt - file extension of files to be converted
	 * @param numRecords - limits many records should be processed.  Default is no limit
	 * @return Integer number of records converted
	 * @throws Exception
	 */
	public Integer transform(Integer numRecords) throws Exception{		
		if (numRecords==null){
			throw new IllegalArgumentException("numRecords cannot be null");
		}
		//Reset counter
		Integer counter = 0;
		
		//initiate importer for iteration through files
		Iterator<String> iterator = 
				new JsonFileRecordIterator<String>(this.inputpath, this.inputFileExt, ROOT_ELEMENT);			
				
		do {
        	String docID = null;
        	try {
        		String record = iterator.next();
        		if (record!=null){
					// Convert JSON string to Object
					ObjectMapper mapper = new ObjectMapper();
		        	Record sharerec = (Record) mapper.readValue(record, Record.class);
		        	
					docID = sharerec.getShareProperties().getDocID();
		
					//pass a JSON record to mapper class and get back RDF
					DiscoModel discoModel = new ShareDiscoModel(sharerec, discoDescription);
					Model rdf = discoModel.getModel();
		
					String filename = getNewFilename(counter+COUNTER_START);
					DiscoFile disco = new DiscoFile(rdf, this.outputPath, filename);
					disco.writeFile();
					
					counter = counter + 1;
					log.info("DiSCO created: " + docID + " -> " + filename);
        		}
    		} catch (Exception e) {
    			String logMsg = "Could not complete export for record " + counter + "\n Continuing to next record. Msg: " + e.getMessage();
    			if (docID!=null){
    				logMsg = "Could not complete export for docId: " + docID
        					+ "\n Continuing to next record. Msg: " + e.getMessage();
    			} 
    			log.error(logMsg,e);
    		}
		} while (iterator.hasNext() && counter < numRecords);

		return counter;
	}
	
}
