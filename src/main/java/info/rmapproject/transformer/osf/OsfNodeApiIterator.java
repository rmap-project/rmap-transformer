package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import org.dataconservancy.cos.osf.client.model.Node;

/**
 * Retrieves and iterates over OSF Node data
 * @author khanson
 */
public class OsfNodeApiIterator extends OsfNodeBaseApiIterator {
	        
    public OsfNodeApiIterator(String filters) throws Exception{
    	super(filters);
    }    

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
			ids = osfClient.getNodeIdList(params);
			nextId = ids.get(0).getId();
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException(e);
		}	
    }

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
