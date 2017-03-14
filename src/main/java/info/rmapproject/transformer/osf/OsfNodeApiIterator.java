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

import java.util.List;

import info.rmapproject.cos.osf.client.model.LightNode;
import info.rmapproject.cos.osf.client.model.Node;
import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

/**
 * Retrieves and iterates over OSF Node data.
 *
 * @author khanson
 */
public class OsfNodeApiIterator extends OsfNodeBaseApiIterator {
	   

	/** The list of IDs to iterate over. */
	protected List<LightNode> ids = null;
	
    /**
     * Instantiates a new OSF Node API iterator.
     *
     * @param filters the filters
     * @throws Exception the exception
     */
    public OsfNodeApiIterator(String filters) throws Exception{
    	super(filters);
    }    
    
    /* (non-Javadoc)
     * @see info.rmapproject.transformer.osf.OsfNodeBaseApiIterator#isLastRow()
     */
    @Override
	protected boolean isLastRow() {
		return (position==(ids.size()-1));
	}

    /* (non-Javadoc)
     * @see info.rmapproject.transformer.osf.OsfNodeBaseApiIterator#loadNextId()
     */
    @Override
	protected void loadNextId(){
		if (ids==null || isLastRow()){
			loadBatch();
		}
		position = position+1;
		nextId = ids.get(position).getId();
	}
	
    /* (non-Javadoc)
     * @see info.rmapproject.transformer.osf.OsfNodeBaseApiIterator#loadBatch()
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
			ids = osfClient.getNodeIds(params);
			nextId = ids.get(0).getId();
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException(e);
		}	
    }

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RecordDTO next() {
		RecordDTO nodeDTO = null;
		Node node = null;
		
		try {			
			while(node==null && hasNext()) {
				//load next
				node = osfClient.getNode(nextId);
				if (hasAccessibleParent(node)){
					node = null;
				}
				loadNextId();
			} 
		} catch (Exception e){
			//load failed... though there may be another record... so let's load it for the next iteration
			loadNextId();
			throw new RuntimeException("Iterator failed to load Record for import",e);
		}

		if (node!=null){
			nodeDTO = new RecordDTO(node, node.getId(), RecordType.OSF_NODE);
		} else {
			throw new RuntimeException("No more Node records available in this batch");
		}
		return nodeDTO;
	}

    	
}
