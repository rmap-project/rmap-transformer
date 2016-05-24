package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;

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
    	try {
    		ids = osfClient.getRegIdList(params);
    	} catch(Exception e){
    		log.error("Could not load list of records to iterate over, exiting.");
    		throw new RuntimeException(e);
    	}	
	}

	@Override
	public RecordDTO next() {
		RecordDTO nodeDTO = null;
		if (hasNext()){
			nodeDTO = new RecordDTO(currRecord, currRecord.getId(), RecordType.OSF_REGISTRATION);
			loadNext();
		} else {
			throw new RuntimeException("No more Registration records available in this batch");
		}
		return nodeDTO;
	}
	

	@Override
	protected void loadNext() {
		currRecord = null;
		if (ids == null) {
			loadBatch();
		}
		if (ids.size()>0 && !isLastRow()){
			RegistrationId id = null;
			Registration registration = null;
			do {
				//load next
				position = position+1;
				id = (RegistrationId) ids.get(position);
				registration = osfClient.getRegistration(id.getId());
				if (hasAccessibleParent(registration)){
					registration = null;
				}
			} while ((registration==null)&&!isLastRow());
			currRecord = registration;			
		}
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
