package info.rmapproject.transformer;

import info.rmapproject.cos.share.client.utils.Utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransformMgr {

    protected static final Logger log = LoggerFactory.getLogger(TransformMgr.class);
        
    /** For output DiSCO files, a simple naming convention embeds a counter number
     *  This is the starting value for this counter.     */
    protected static final Integer COUNTER_START= 10000001;
	
	/**
	* Template for disco filename.  outputFileExt will be added to the end, 
	* #### will be replaced with the current counter number.
	*/
	protected String discoFilenameTemplate = "DiSCO_####.rdf";
    
	/** maintains a list of records processed **/
	protected File registry;
	
	/**
	 * Output path for new DiSCOs
	 */
	protected String outputPath=".";
	protected String discoDescription="";
			
	/**
	 * Initialize transformer with outputpath
	 * @param outputPath
	 */
	protected TransformMgr(String outputPath, String discoDescription) {
		if (outputPath==null){
			throw new IllegalArgumentException("outputPath cannot be null");
		}
		this.outputPath = outputPath;
		discoDescription = Utils.setEmptyToNull(discoDescription);
		this.discoDescription = discoDescription;
	}
	
	public abstract Integer transform(Integer numrecords) throws Exception;	
	
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
	

	/**
	 * Convert URL params to Map<String,String>
	 * @param url 
	 * @param charset
	 * @return params
	 * @exception URISyntaxException
	 */
	public static HashMap<String, String> readParamsIntoMap(String filters, String charset) throws URISyntaxException {
		HashMap<String, String> params = new HashMap<>();
		
		String url = "http://fakeurl.fake";
		if (!filters.startsWith("?")){
			url = url + "?";
		}
		url = url + filters;
		
	    List<NameValuePair> result = URLEncodedUtils.parse(new URI(url), charset);

	    for (NameValuePair nvp : result) {
	        params.put(nvp.getName(), nvp.getValue());
	    }

	    return params;
	}
	
}
