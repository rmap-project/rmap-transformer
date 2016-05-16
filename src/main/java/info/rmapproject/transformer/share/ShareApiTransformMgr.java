package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.DiscoConverter;
import info.rmapproject.transformer.TransformMgr;
import info.rmapproject.transformer.model.DiscoFile;

import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Manages the transform of SHARE data read directly from API
 * Will retrieve JSON records from API using path and params provided.
 * @author khanson
 *
 */
public class ShareApiTransformMgr extends TransformMgr {
        		
    private String requestpath;

	public ShareApiTransformMgr(String outputpath, String requestpath){
		this(outputpath, requestpath, null, null);
	}
	
    public ShareApiTransformMgr(String outputpath, String requestpath, String discoCreator, String discoDescription){
    	super(outputpath, discoCreator, discoDescription);
    	if (requestpath==null){
    		throw new IllegalArgumentException("requestpath cannot be null");
    	}
    	this.requestpath = requestpath;
    }
    

	/**
	 * Collect SHARE data from API and transform to DiSCOs
	 * @param inputUrl - API url 
	 * @param numRecords - number of records to retrieve. Will paginate if necessary.
	 */
	public Integer transform(Integer numRecords) throws Exception {
		if (numRecords==null){
			throw new IllegalArgumentException("numRecords cannot be null");
		}

		//Reset counter
		Integer counter = COUNTER_START;
		
		// split out params
		HashMap<String,String> params=null;
		try{
			params = readParamsIntoMap(this.requestpath, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		}

        ShareApiIterator shareApiIterator = new ShareApiIterator(params);
        Record record = null;
		do {
        	record = shareApiIterator.next();
        	if (record!=null){
	          	DiscoConverter discoConverter = new ShareDiscoConverter(record);
				OutputStream rdf = discoConverter.generateDiscoRdf();
				String filename = getNewFilename(counter.toString());
				DiscoFile disco = new DiscoFile(rdf, this.outputPath, filename);
				disco.writeFile();
	        	counter = counter + 1;
        	}
		} while((counter-COUNTER_START)<(numRecords) && record != null);

        
		Integer totalTransformed = counter - COUNTER_START ;
		return totalTransformed;		
	}
	    
}
