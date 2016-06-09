package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsfUserApiIterator implements Iterator<RecordDTO>{

    private static final Logger log = LoggerFactory.getLogger(OsfUserApiIterator.class);  
    
    private Map<String, String> params = null;
    private List<UserId> ids = null;
    private User currRecord = null;
    private int position = -1;
    private OsfClientService osfClient = null;
		
	public OsfUserApiIterator() {}

	public OsfUserApiIterator(String filters) {
		HashMap<String,String> params=null;
		try{
			params = TransformUtils.readParamsIntoMap(filters, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		} catch (Exception e){
			throw new RuntimeException("could not initiate OSF Api Iterator", e);    		
		}
		this.params = params;
		this.osfClient = new OsfClientService();
		// this loads next record to be retrieved, each next() retrieves currReg and loads next one.
		loadNext(); 
	}

	@Override
	public boolean hasNext() {
		return (currRecord!=null);	
	}

	@Override
	public RecordDTO next() {
		RecordDTO userDTO = null;
		if (hasNext()){
			userDTO = new RecordDTO(currRecord, currRecord.getId(), RecordType.OSF_USER);
			loadNext();
		} else {
			throw new RuntimeException("No more User records available in this batch");
		}
		return userDTO;
	}	

	
	/**
	 * Load batch of OSF data from API using parameters defined
	 */
	private void loadBatch() {
		position = -1;
    	try {
    		ids = osfClient.getUserIdList(params);
    	} catch(Exception e){
    		log.error("Could not load list of records to iterate over, exiting.");
    		throw new RuntimeException(e);
    	}	
	}


	/**
	 * Load next record into memory
	 */
	protected void loadNext() {
		currRecord = null;
		if (ids == null) {
			loadBatch();
		}
		if (ids.size()>0 && !isLastRow()){
			UserId id = null;
			User user = null;
			do {
				//load next
				position = position+1;
				id = (UserId) ids.get(position);
				user = osfClient.getUser(id.getId());
			} while ((user==null)&&!isLastRow());
			currRecord = user;			
		}
	}

	/**
	 * Returns true if this is the last row in the current id list
	 * @return
	 */
	protected boolean isLastRow() {
		return (position==(ids.size()-1));
	}

}