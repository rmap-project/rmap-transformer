package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;

/**
 * Retrieves and iterates over OSF Registration data
 * @author khanson
 */
public class OsfRegistrationApiIterator extends OsfNodeBaseApiIterator {
	
    public OsfRegistrationApiIterator(String filters){
    	super(filters);
    }
    	
	/**
	 * Load batch of OSF data from API using parameters defined
	 */
    @Override
	protected void loadBatch() {
		position = -1;
		nextId = null;
		try {
			Integer page = 0;
			String pageval = params.get("page");
			if (pageval!=null && !pageval.isEmpty()){
				page = Integer.parseInt(pageval);
			}
			page=page+1;
			params.put("page", page.toString());
    		ids = osfClient.getRegIdList(params);
			nextId = ids.get(0).getId();
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException("Could not load list of records to iterate over.", e);
		}	
    }
    
	@Override
	public RecordDTO next() {
		RecordDTO registrationDTO = null;
		Registration registration = null;
		
		try {			
			while(registration==null && hasNext()) {
				//load next
				registration = osfClient.getRegistration(nextId);
				if (hasAccessibleParent(registration)){
					registration = null;
				}
				loadNextId();
			} 
		} catch (Exception e){
			//load failed... though there may be another record... so let's load it for the next iteration
			loadNextId();
			throw new RuntimeException("Iterator failed to load Record for import",e);
		}

		if (registration!=null){
			registrationDTO = new RecordDTO(registration, registration.getId(), RecordType.OSF_REGISTRATION);
		} else {
			throw new RuntimeException("No more Registration records available in this batch");
		}
		return registrationDTO;
	}
	
    
    /**
     * Checks for any criteria that would exclude this record
     * for registrations, records not yet approved, under embargo, 
     * or in the process of withdrawal are excluded as well as those that
     * have an accessible parent Registration
     * @param nodeBase
     * @return
     */
    @Override
	protected boolean hasExclusionCriteria(NodeBase nodeBase){
    	Registration reg = (Registration) nodeBase;
		if (hasAccessibleParent(nodeBase)
				|| reg.isPending_embargo_approval() 
				|| reg.isPending_registration_approval()
				|| reg.isPending_withdrawal() 
				|| reg.isWithdrawn()){
			return true; // don't include these.
		}
		else {return false;}
	}
	
	
}
