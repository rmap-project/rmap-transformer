package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Iterates over SHARE API data - can next() over records.
 * Retrieve JSON records from API using path and params provided.
 * @author khanson
 *
 */
public class ShareApiTransformIterator implements Iterator<RecordDTO>{
        		
    private ShareApiIterator shareApiIterator = null;

    /**
     * Initiate iterator using filters provided
     * @param filters
     */
	public ShareApiTransformIterator(String filters){
		HashMap<String,String> params=null;
		try{
			params = TransformUtils.readParamsIntoMap(filters, "UTF-8");
    		shareApiIterator = new ShareApiIterator(params);
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		} catch (Exception e){
			throw new RuntimeException("could not initiate SHARE Api Iterator", e);    		
		}
	}
	
	@Override
	public RecordDTO next() {
		RecordDTO shareDTO = null;
		try {
			Record sharerec = shareApiIterator.next();
			String id = sharerec.getShareProperties().getDocID();
			String source = sharerec.getShareProperties().getSource();
			if (source!=null && source.length()>0){
				id = source + "_" + id;
			}
			shareDTO = new RecordDTO(sharerec, id, RecordType.SHARE);
		} catch (Exception ex) {
			throw new RuntimeException("Could not generate SHARE record", ex);
		}
		return shareDTO;
	}

	@Override
	public boolean hasNext() {
		return shareApiIterator.hasNext();
	}
			 
}
