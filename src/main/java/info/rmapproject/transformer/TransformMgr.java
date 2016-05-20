package info.rmapproject.transformer;

import org.openrdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformMgr {

	private static final Logger log = LoggerFactory.getLogger(TransformMgr.class);

    /** For output DiSCO files, a simple naming convention embeds a counter number
     *  This is the starting value for this counter.     */
    private static final Integer COUNTER_START= 10000001;
    
	/**
	* Template for disco filename.  outputFileExt will be added to the end, 
	* #### will be replaced with the record id.
	*/
    private String discoFilenameTemplate = "DiSCO_####.rdf";

    /**
	 * Iterator corresponding to the transform to be performed.
	 */
	private TransformIterator iterator;
	
	/**
	 * Iterator corresponding to the transform to be performed.
	 */
	private DiscoModel discoModel;
	
	/**
	 * Output path for new DiSCOs
	 */
	protected String outputPath=".";
			
	/**
	 * Initialize transformer with outputpath
	 * @param outputPath
	 */
	protected TransformMgr(TransformIterator iterator, DiscoModel discoModel, String outputPath) {
		if (iterator==null){
			throw new IllegalArgumentException("iterator cannot be null");
		}
		if (discoModel==null){
			throw new IllegalArgumentException("discoModel cannot be null");
		}
		if (outputPath==null){
			throw new IllegalArgumentException("outputPath cannot be null");
		}
		this.iterator = iterator;
		this.discoModel = discoModel;
		this.outputPath = outputPath;
	}
	
	
	public Integer transform(Integer numRecords) throws Exception {
		if (numRecords==null){
			throw new IllegalArgumentException("numRecords cannot be null");
		}

		//Reset counter
		Integer counter = 0;
        
        Object record = null;
		do {
	        String id = null;
    		try {
    			record = iterator.next();
    			if (record!=null){
    				if (iterator.getCurrId()!=null) {
    					id = iterator.getCurrId();
    				} else {
    					Integer i = COUNTER_START+counter;
    					id = i.toString();
    				}
    				
    				discoModel.setRecord(record);
    				Model model = discoModel.getModel();
					
					String filename = getNewFilename(id);
					DiscoFile disco = new DiscoFile(model, this.outputPath, filename);
					disco.writeFile();
		        	
					counter = counter + 1;
					log.info("DiSCO created: " + id + " -> " + filename);
    			}
    		} catch (Exception e) {
    			String logMsg = "Could not complete export for record #" + counter + "\n Continuing to next record. Msg: " + e.getMessage();
    			if (record!=null){
    				logMsg = "Could not complete export for docId: " + id
        					+ "\n Continuing to next record. Msg: " + e.getMessage();
    			} 
    			log.error(logMsg,e);
    		}
		} while(iterator.hasNext() && counter<numRecords);

		return counter;		
	}
	
	/**
	* Set template for disco filename.  outputFileExt will be added to the end, 
	* #### should be included as this will be replaced with a unique ID.
	* by default this will be an incrementing counter number
	* If this is not set the default "DiSCO_####.[outputFileExt]" will be used to name files.
	* @param outputFilenameTemplate
	*/
	public void setDiscoFilenameTemplate(String discoFilenameTemplate) {
		if (discoFilenameTemplate==null){
			throw new IllegalArgumentException("discoFilenameTemplate cannot be null");
		}
		this.discoFilenameTemplate = discoFilenameTemplate;	
	}
	
	/**get template for disco filename**/
	public String getDiscoFilenameTemplate(){
		return this.discoFilenameTemplate;
	}

	/**
	 * Generates filename for new file using the discoFilenameTemplate and a value provided by the transform process
	 * - often this will be a counter if a simple uniqueid is unavailable.
	 * @return filename
	 */
	protected String getNewFilename(String uniqueval){
		uniqueval = uniqueval.replaceAll("[^a-zA-Z0-9.-]", "_"); //make filename safe
		String newFilename = this.discoFilenameTemplate.replace("####",uniqueval);
		return newFilename;
	}
	
	/**
	 * Generates filename for new file using the discoFilenameTemplate and a value provided by the transform process
	 * - often this will be a counter if a simple uniqueid is unavailable.
	 * @return filename
	 */
	protected String getNewFilename(Integer counter){
		return getNewFilename(counter.toString());
	}
	
	
}
