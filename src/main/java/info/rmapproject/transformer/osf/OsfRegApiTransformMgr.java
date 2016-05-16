package info.rmapproject.transformer.osf;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.DiscoConverter;
import info.rmapproject.transformer.TransformMgr;
import info.rmapproject.transformer.model.DiscoFile;
import info.rmapproject.transformer.share.ShareDiscoConverter;

import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

public class OsfRegApiTransformMgr extends TransformMgr {
	
    private String filters;
           
    public OsfRegApiTransformMgr(String outputpath, String filters, String discoCreator, String discoDescription){
    	super(outputpath, discoCreator, discoDescription);
    	this.filters = filters;
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
		
		// Split out params
		HashMap<String,String> params=null;
		try{
			params = readParamsIntoMap(this.filters, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		}

        ShareApiIterator shareApiIterator = new ShareApiIterator(params);
        
		do {
        	counter = counter + 1;
        	Record record = shareApiIterator.next();
          	DiscoConverter discoConverter = new ShareDiscoConverter(record, this.discoCreator, this.discoDescription);
			OutputStream rdf = discoConverter.generateDiscoRdf();
			String filename = getNewFilename(counter.toString());
			DiscoFile disco = new DiscoFile(rdf, this.outputPath, filename);
			disco.writeFile();
		} while((counter-COUNTER_START)<(numRecords));

        
		Integer totalTransformed = counter - COUNTER_START ;
		return totalTransformed;	
	}
	
	
	
}
