package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.Utils;
import info.rmapproject.transformer.model.RecordDTO;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.NodeBaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OsfNodeBaseApiIterator implements Iterator<RecordDTO>{

    protected static final Logger log = LoggerFactory.getLogger(OsfNodeBaseApiIterator.class);  
    
    protected Map<String, String> params = null;
	protected List<? extends NodeBaseId> ids = null;
    protected NodeBase currRecord = null;
	protected int position = -1;
	protected OsfClientService osfClient = null;
		
	protected OsfNodeBaseApiIterator() {}

	protected OsfNodeBaseApiIterator(String filters) {
		HashMap<String,String> params=null;
		try{
			params = Utils.readParamsIntoMap(filters, "UTF-8");
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
		// this loads next record to be retrieved, each next() retrieves currReg and loads next one.
		loadNext(); 
	}

	@Override
	public boolean hasNext() {
		return (currRecord!=null);	
	}

	/**
	 * Collect OSF data from API using parameters defined
	 */
	protected abstract void loadBatch();

	/**
	 * load next record
	 */
	protected abstract void loadNext();

	/**
	 * Returns true if this is the last row in the current id list
	 * @return
	 */
	protected boolean isLastRow() {
		return (position==(ids.size()-1));
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
	 * @param nodebase
	 * @return
	 */
	protected boolean hasAccessibleParent(NodeBase nodebase) {
		//check if we are at top level
		String parent = nodebase.getParent();
		if (parent!=null){
			String parentId = Utils.extractLastSubFolder(parent);
			
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