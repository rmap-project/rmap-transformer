package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.DiscoFile;
import info.rmapproject.transformer.DiscoModel;
import info.rmapproject.transformer.TransformMgr;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.openrdf.model.Model;

/**
 * Manages the transform of SHARE data read directly from API
 * Will retrieve JSON records from API using path and params provided.
 * @author khanson
 *
 */
public class ShareApiTransformMgr extends TransformMgr {
        		
    private String requestpath;

	public ShareApiTransformMgr(String outputpath, String requestpath){
		this(outputpath, requestpath, null);
	}
	
    public ShareApiTransformMgr(String outputpath, String requestpath, String discoDescription){
    	super(outputpath, discoDescription);
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
		Integer counter = 0;
		
		// split out params
		HashMap<String,String> params=null;
		try{
			params = readParamsIntoMap(this.requestpath, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		}

		ShareApiIterator iterator = new ShareApiIterator(params);
        
        Record record = null;
		do {
	        String docID = null;
    		try {
    			record = iterator.next();
    			if (record!=null){
    				docID = record.getShareProperties().getDocID();
		          	
    				DiscoModel discoModel = new ShareDiscoModel(record, discoDescription);
					Model model = discoModel.getModel();
					
					String filename = getNewFilename(counter+COUNTER_START);
					DiscoFile disco = new DiscoFile(model, this.outputPath, filename);
					disco.writeFile();
		        	
					counter = counter + 1;
					log.info("DiSCO created: " + docID + " -> " + filename);
    			}
    		} catch (Exception e) {
    			String logMsg = "Could not complete export for record " + counter + "\n Continuing to next record. Msg: " + e.getMessage();
    			if (record!=null){
    				logMsg = "Could not complete export for docId: " + docID
        					+ "\n Continuing to next record. Msg: " + e.getMessage();
    			} 
    			log.error(logMsg,e);
    		}
		} while(iterator.hasNext() && counter<numRecords);

		return counter;		
	}
	    
}
