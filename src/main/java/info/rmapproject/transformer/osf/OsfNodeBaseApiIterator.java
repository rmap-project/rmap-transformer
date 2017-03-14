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

import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.rmapproject.cos.osf.client.model.NodeBase;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.model.RecordDTO;

/**
 * An abstract iterator class to iterate over a list of OSF Nodes. 
 * This can be used with both OSF Nodes and OSF Registrations which share a lot of fields
 */
public abstract class OsfNodeBaseApiIterator implements Iterator<RecordDTO>{
	
    /** The log. */
    protected static final Logger log = LoggerFactory.getLogger(OsfNodeBaseApiIterator.class);  
    
    /** The params. */
    protected Map<String, String> params = null;
	
	/** The next id. */
	protected String nextId = null;
	
	/** The position of the iterator. */
	protected int position = -1;
	
	/** The OSF client, used to retrieve data from API. */
	protected OsfClientService osfClient = null;
		
	/**
	 * Instantiates a new OSF Node base API iterator.
	 */
	protected OsfNodeBaseApiIterator() {}

	/**
	 * Instantiates a new OSF Node base API iterator.
	 *
	 * @param filters the filters for the iterator
	 */
	protected OsfNodeBaseApiIterator(String filters) {
		HashMap<String,String> params=null;
		try{
			params = TransformUtils.readParamsIntoMap(filters, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		} catch (Exception e){
			throw new RuntimeException("could not initiate OSF Api Iterator", e);    		
		}
		if (!params.containsKey("filter[public]")){
			params.put("filter[public]", "true");
		}
		this.params = params;
		this.osfClient = new OsfClientService();
		// this loads a batch of records to be retrieved, each next() retrieves current record and loads next id to see if there is a next
		loadNextId(); 
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (nextId!=null);	
	}

	/**
	 * Collect OSF data from API using parameters defined.
	 */
	protected abstract void loadBatch();

	/**
	 * Returns true if this is the last row in the current id list.
	 *
	 * @return true, if is last row
	 */
	protected abstract boolean isLastRow();

	/**
	 * load next Id to check using hasNext.
	 */
	protected abstract void loadNextId();

    /**
     * Checks for any criteria that would exclude this record.
     *
     * @param nodebase the nodebase
     * @return true, if successful
     */
	protected boolean hasExclusionCriteria(NodeBase nodebase){
		if (hasAccessibleParent(nodebase)){ //only one in this instance
			return true; // don't include these.
		}
		else {return false;}
	}
	
	
	/**
	 * Determines whether there is a parent node and if so whether it is 
	 * accessible through the API. If it is, we can skip over this child node,
	 * if not we can use this node.
	 *
	 * @param nodebase the nodebase
	 * @return true, if successful
	 */
	protected boolean hasAccessibleParent(NodeBase nodebase) {
		//check if we are at top level
		String parent = nodebase.getParent();
		if (parent!=null){
			String parentId = TransformUtils.extractLastSubFolder(parent);
			
			if (!parentId.equals(nodebase.getId())){
				try {
					URL url = new URL(parent); 
					HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
					connection.setRequestMethod("GET"); connection.connect(); 
					int code = connection.getResponseCode();
					if (code==401){// process this
						return false; //there is a parent node but it isn't accessible!
					} else {
						return true; //there is a parent node and it is accessible
					}
				} catch (Exception e){
					throw new RuntimeException("Could not validate Node accessibility");
				}
			}
		}
			
		return false; //this node is the parent node
	}

	
	
}