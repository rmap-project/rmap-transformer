package info.rmapproject.transformer;

import info.rmapproject.transformer.model.DiscoFile;
import info.rmapproject.transformer.model.RecordDTO;

import java.io.OutputStream;
import java.util.Iterator;

import org.openrdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transformer {

	private static final Logger log = LoggerFactory.getLogger(Transformer.class);
    
	/**
	* Template for disco filename.  outputFileExt will be added to the end, 
	* #### will be replaced with the record id.
	*/
    private String discoFilenameTemplate = "DiSCO_####.rdf";

    /**
	 * Output path for new DiSCOs
	 */
	protected String outputPath=".";
	
	/**
	 * Description to be assigned to new discos
	 */
	protected String discoDescription=".";
			
		
	/**
	* Initialize transformer with outputpath
	* @param outputPath
	*/
	protected Transformer(String outputPath) {
		if (outputPath==null){
			throw new IllegalArgumentException("outputPath cannot be null");
		}
		this.outputPath = outputPath;
	}
	
	/**
	 * Initialize transformer with outputpath and discoDescription to be assigned to each transformed DiSCO
	 * @param outputPath
	 * @param discoDescription
	 */
	protected Transformer(String outputPath, String discoDescription) {
	//protected TransformMgr(TransformIterator iterator, DiscoModel discoModel, String outputPath) {
		if (discoDescription==null){
			throw new IllegalArgumentException("discoDescription cannot be null");
		}
		if (outputPath==null){
			throw new IllegalArgumentException("outputPath cannot be null");
		}
		this.outputPath = outputPath;
		this.discoDescription = discoDescription;
	}
	
	/**
	 * Transforms the source data to DiSCOs
	 * @param transformType 
	 * @param iterator - iterator to loop through for record processing.
	 * @param maxNumberRecords - maximum number of records to process. 
	 * @return
	 * @throws Exception
	 */
	public Integer transform(Iterator<RecordDTO> iterator, Integer maxNumberRecords)  {
		if (iterator==null){
			throw new IllegalArgumentException("iterator cannot be null");
		}
		if (maxNumberRecords==null){
			throw new IllegalArgumentException("maxNumberRecords cannot be null");
		}

		//Reset counter
		Integer counter = 0;
        
        RecordDTO recordDTO = null;                                                                  
		do {
	        String id = null;
    		try {
    			recordDTO = iterator.next();
    			id = transform(recordDTO);
    			counter = counter + 1;
    		} catch (Exception e) {
    			String logMsg = "Could not complete export for record #" + (counter+1) + "\n Continuing to next record. Msg: " + e.getMessage();
    			if (id!=null){
    				logMsg = "Could not complete export for docId: " + id
        					+ "\n Continuing to next record. Msg: " + e.getMessage();
    			} 
    			log.error(logMsg,e);
    			//iterator.next();
    		}
		} while(iterator.hasNext() && counter<maxNumberRecords);

		return counter;		
	}
	
	/**
	 * Take a RecordDTO, which contains the record, ID and a type and writes a DiSCO to the output path.
	 * @param recordDTO
	 * @return
	 */
	public String transform(RecordDTO recordDTO) {
		String id = null;
		if (recordDTO!=null){
			if (recordDTO.getId()!=null) {
				id = recordDTO.getId();
			} 
			DiscoBuilder discoModel = 
					DiscoBuilderFactory.createDiscoBuilder(recordDTO.getRecordType(), discoDescription);
			discoModel.setRecord(recordDTO.getRecord());
			Model model = discoModel.getModel();
			String filename = getNewFilename(id);
			OutputStream rdf = TransformUtils.generateTurtleRdf(model);
			DiscoFile disco = new DiscoFile(rdf, this.outputPath, filename);
			disco.writeFile();
			log.info("File written: id " + id + " -> " + filename);
		}
		return id;
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
