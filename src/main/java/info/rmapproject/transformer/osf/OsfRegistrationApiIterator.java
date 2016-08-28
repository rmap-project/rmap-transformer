/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;

/**
 * Retrieves and iterates over OSF Registration data.
 *
 * @author khanson
 */
public class OsfRegistrationApiIterator extends OsfNodeBaseApiIterator {
	
    /**
     * Instantiates a new osf registration api iterator.
     *
     * @param filters the filters
     */
    public OsfRegistrationApiIterator(String filters){
    	super(filters);
    }
    	
	/**
	 * Load batch of OSF data from API using parameters defined.
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
			log.info("Loading page " + page);
    		ids = osfClient.getRegIdList(params);
			nextId = ids.get(0).getId();
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException("Could not load list of records to iterate over.", e);
		}	
    }
    
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
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
     * have an accessible parent Registration.
     *
     * @param nodeBase the node base
     * @return true, if successful
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
