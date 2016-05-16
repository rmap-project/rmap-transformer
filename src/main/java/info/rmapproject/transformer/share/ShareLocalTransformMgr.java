package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.transformer.DiscoConverter;
import info.rmapproject.transformer.TransformMgr;
import info.rmapproject.transformer.model.DiscoFile;
import info.rmapproject.transformer.model.FileRecordIterator;
import info.rmapproject.transformer.model.JsonFileRecordIterator;

import java.io.OutputStream;

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
		this(outputpath, inputpath, inputFileExt, null, null);
	}	

	
	public ShareLocalTransformMgr(String outputpath, String inputpath, String inputFileExt, String discoCreator, String discoDescription){
		super(outputpath, discoCreator, discoDescription);
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
	 * @param maxNumRecords - limits many records should be processed.  Default is no limit
	 * @return Integer number of records converted
	 * @throws Exception
	 */
	public Integer transform(Integer maxNumRecords) throws Exception{		
		//initiate importer for iteration through files
		FileRecordIterator importer = new JsonFileRecordIterator(this.inputpath, this.inputFileExt, ROOT_ELEMENT);			
				
		//Reset counter
		Integer counter = COUNTER_START;
		
		do {

        	if ((counter-COUNTER_START) == maxNumRecords){
        		//reached the maximum number of records
        		log.info("Processing stopped because you've reached your target number of records. More records are available.");
        		break;
        	}
			String record = importer.next();
			counter = counter + 1;

			// Convert JSON string to Object
			ObjectMapper mapper = new ObjectMapper();
			Record sharerec = (Record) mapper.readValue(record, Record.class);

			//pass a JSON record to mapper class and get back RDF
			DiscoConverter discoConverter = new ShareDiscoConverter(sharerec);
			OutputStream rdf = discoConverter.generateDiscoRdf();

			String filename = getNewFilename(counter.toString());
			DiscoFile disco = new DiscoFile(rdf, this.outputPath, filename);
			disco.writeFile();
			
		} while (!importer.hasNext());

		Integer totalTransformed = counter - COUNTER_START ;
		return totalTransformed;
	}
	
}
