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

/**
 * OSF User Iterator class
 */
public class OsfUserApiIterator implements Iterator<RecordDTO>{

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(OsfUserApiIterator.class);  
    
    /** The params. */
    private Map<String, String> params = null;
    
    /** The list of User IDs. */
    private List<UserId> ids = null;
	
	/** The next id. */
	protected String nextId = null;
    
    /** The current iterator position. */
    private int position = -1;
    
    /** The OSF client. */
    private OsfClientService osfClient = null;
		
	/**
	 * Instantiates a new OSF User API iterator.
	 */
	public OsfUserApiIterator() {}

	/**
	 * Instantiates a new OSF User API iterator.
	 *
	 * @param filters the API filters
	 */
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
		loadNextId(); 
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (nextId!=null);	
	}


	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RecordDTO next() {
		RecordDTO userDTO = null;
		User user = null;
		
		try {			
			while(user==null && hasNext()) {
				//load next
				user = osfClient.getUser(nextId);
				loadNextId();
				//we only want users that have nodes assigned when doing an iteration.
				if (user.getNodes()==null || user.getNodes().size()==0){
					user=null;
				}
			} 
		} catch (Exception e){
			//load failed... though there may be another record... so let's load it for the next iteration
			loadNextId();
			throw new RuntimeException("Iterator failed to load Record for import",e);
		}

		if (user!=null){
			userDTO = new RecordDTO(user, user.getId(), RecordType.OSF_USER);
		} else {
			throw new RuntimeException("No more User records available in this batch");
		}
		return userDTO;
	}

	
	/**
	 * Load batch of OSF data from API using parameters defined.
	 */
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
    		ids = osfClient.getUserIdList(params);
			nextId = ids.get(0).getId();
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException(e);
		}	
    }
	
	/**
	 * load next Id to check using hasNext.
	 */
	protected void loadNextId(){
		if (ids==null || isLastRow()){
			loadBatch();
		}
		position = position+1;
		nextId = ids.get(position).getId();
	}

	/**
	 * Returns true if this is the last row in the current id list.
	 *
	 * @return true, if is last row
	 */
	protected boolean isLastRow() {
		return (position==(ids.size()-1));
	}

}