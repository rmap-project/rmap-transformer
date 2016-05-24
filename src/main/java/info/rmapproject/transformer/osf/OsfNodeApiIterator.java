package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeId;

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
		try {
			ids = osfClient.getNodeIdList(params);
		} catch(Exception e){
			log.error("Could not load list of records to iterate over.");
			throw new RuntimeException(e);
		}	
    }

	@Override
	public RecordDTO next() {
		RecordDTO nodeDTO = null;
		if (hasNext()){
			nodeDTO = new RecordDTO(currRecord, currRecord.getId(), RecordType.OSF_NODE);
			loadNext();
		} else {
			throw new RuntimeException("No more Node records available in this batch");
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
			NodeId id = null;
			Node node = null;
			do {
				//load next
				position = position+1;
				id = (NodeId) ids.get(position);
				node = osfClient.getNode(id.getId());
				if (hasAccessibleParent(node)){
					node = null;
				}
			} while ((node==null)&&!isLastRow());
			currRecord = node;			
		}
	}
}
