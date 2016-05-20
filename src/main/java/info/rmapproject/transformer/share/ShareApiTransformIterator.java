package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.TransformIterator;

/**
 * Iterates over SHARE API data - can next() over records.
 * Retrieve JSON records from API using path and params provided.
 * @author khanson
 *
 */
public class ShareApiTransformIterator extends TransformIterator{
        		
    private ShareApiIterator shareApiIterator = null;

	public ShareApiTransformIterator(String filters){
    	super(filters);
    	try {
    		shareApiIterator = new ShareApiIterator(params);
    	} catch (Exception e){
    		throw new RuntimeException("could not initiate SHARE Api Iterator", e);    		
    	}
	}
	
	@Override
	public Object next() {
		Record sharerec = null;
		try {
			sharerec = shareApiIterator.next();
        	setCurrId(sharerec.getShareProperties().getDocID());
		} catch (Exception ex) {
			throw new RuntimeException("Could not generate SHARE record", ex);
		}
		return sharerec;
	}

	@Override
	public String getCurrId() {
		return this.currId;
	}

	@Override
	public boolean hasNext() {
		return shareApiIterator.hasNext();
	}
			 
}
