package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.TransformIterator;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.dataconservancy.cos.osf.client.model.NodeBase;

public abstract class OsfBaseApiTransformIterator extends TransformIterator {

	List<? extends NodeBase> records = null;
    NodeBase currRecord = null;
	protected int position = -1;

	public OsfBaseApiTransformIterator() {
		super();
	}

	public OsfBaseApiTransformIterator(String filters) {
		super(filters);
		if (!params.containsKey("filter[public]")){
			params.put("filter[public]", "true");
		}
		// this loads next record to be retrieved, each next() retrieves currReg and loads next one.
		loadNext(); 
	}

	@Override
	public Object next() {
		NodeBase node = null;
		if (hasNext()){
			node = currRecord;
			currId = node.getId();
			loadNext();
		} else {
			throw new RuntimeException("No more Node records available in this batch");
		}
		return node;
	}

	@Override
	public boolean hasNext() {
		return (currRecord!=null);	
	}

	/**
	 * Collect OSF data from API using parameters defined
	 */
	protected abstract void loadBatch();
	

	protected void loadNext(){
		currRecord = null;
		if (records == null) {
			loadBatch();
		}
		if (records.size()>0 && !isLastRow()){
			NodeBase nodebase = null;
			do {
				//load next
				position = position+1;
				nodebase = records.get(position);
				if (hasAccessibleParent(nodebase)){
					nodebase = null;
				}
			} while ((nodebase==null)&&!isLastRow());
			currRecord = nodebase;			
		}
	}

	protected boolean isLastRow() {
		return (position==(records.size()-1));
	}

    
    /**
     * Checks for any criteria that would exclude this record
     * @param reg
     * @return
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
	 * @param node
	 * @return
	 */
	protected boolean hasAccessibleParent(NodeBase node) {
		//check if we are at top level
		String parent = node.getParent();
		if (parent!=null){
			String parentId = OsfUtils.extractLastSubFolder(parent);
			
			if (!parentId.equals(node.getId())){
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