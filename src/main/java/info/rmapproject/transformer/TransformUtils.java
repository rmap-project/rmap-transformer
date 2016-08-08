package info.rmapproject.transformer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;


public class TransformUtils {
	
	private static final String PROP_FILE = "rmap-transform";
	
	private static final String DOI_HTTP_PREFIX = "http://dx.doi.org/";
	private static final String DOI_COLON_PREFIX = "doi:"; //going to normalize to http://dx.doi.org/ address
	private static final String DOI_HTTPS_PREFIX = "https://dx.doi.org/"; //incorrect! Need to replace https with http
	private static final String DOI_HTTP_NODX_PREFIX = "http://doi.org/"; //going to normalize to http://dx.doi.org/ address
	private static final String DOI_VALID_FIRST_CHARS = "10.";
	/**
	 * Convenience method for extracting a single property name/value pair from a property file
	 * @param propFileName base name for property file
	 * @param propKey propery name
	 * @return String containing proerty value, or null if not found
	 * @throws MissingResourceException
	 */
	public static String getPropertyValue(String propKey) throws NullPointerException, MissingResourceException {
		String propValue = null;
		ResourceBundle resources = ResourceBundle.getBundle(PROP_FILE, Locale.getDefault());
		for (String key:resources.keySet()){
			if (key.equals(propKey)){
				propValue = resources.getString(key);
				break;
			}
		}			
		return propValue;
	}
	
	/**
	 * Extract the last subfolder name from a path.
	 * e.g. for https://api.osf.io/v2/registrations/sdfkj/ sdfkj will be extracted
	 * @param linkUrl
	 * @return
	 */
	public static String extractLastSubFolder(String linkUrl){
		if (linkUrl!=null && linkUrl.length()>0 && linkUrl.contains("/")){
			if (linkUrl.endsWith("/")){
				linkUrl = linkUrl.substring(0,linkUrl.length()-1);
			}
			String id = linkUrl.substring(linkUrl.lastIndexOf('/') + 1);
			return id;
		} else {
			return null;
		}		
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
	

	/**
	 * Converts an issn to a URN format if possible
	 * @param issn
	 * @param prefix
	 * @return
	 */
	public static String issnFormatter(String issn){
		if (issn==null){
			return null;
		}
		String newIssn = issn.toUpperCase();
		newIssn = newIssn.replace(" ","-");
		newIssn = newIssn.replace("URN:","");
		newIssn = newIssn.replace("EISSN-","");
		newIssn = newIssn.replace("EISSN:","");
		newIssn = newIssn.replace("ISSN-","");
		newIssn = newIssn.replace("ISSN:","");
		
		String regex1 = "\\d{8}"; //12341234
		String regex2 = "\\d{4}-\\d{4}"; //1234-1234
		String regex3 = "\\d{4}-\\d{3}X"; //1234-123X
		
		if (newIssn.matches(regex1)||newIssn.matches(regex2)
				||newIssn.matches(regex3)){
			return "urn:issn:" + newIssn;
		} else {
			return issn;
		}
		
		
	}
	
	/**
	 * Converts an isbn to a URN format if possible
	 * @param isbn
	 * @param prefix - issn or eissn
	 * @return
	 */
	public static String isbnFormatter(String isbn){
		if (isbn==null){
			return null;
		}
		String newIsbn = isbn.toUpperCase();
		newIsbn = newIsbn.replace(" ","-");
		newIsbn = newIsbn.replace("URN:","");
		newIsbn = newIsbn.replace("ISBN-","");
		newIsbn = newIsbn.replace("ISBN:","");
		
		String regex = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";
		
		if (newIsbn.matches(regex)){
			return "urn:isbn:" + newIsbn;
		} else {
			return isbn;
		}
	}
	
	/** 
	 * Generates Turtle RDF given a Model
	 * @param model
	 * @return
	 */
	public static OutputStream generateTurtleRdf(Model model) {
		OutputStream bOut = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(bOut, "UTF-8");
			Rio.write(model, writer, RDFFormat.TURTLE);
		} catch (Exception e) {
			throw new RuntimeException("Exception thrown creating RDF from statement list", e);
		}
		return bOut;	
	}
	
	/**
	 * Very basic validation to check if a string looks like a doi. 
	 * @param doi
	 * @return
	 */
	public static boolean isDoi(String doi) {
		//TODO: make this better, add regex validation?
		doi = doi.toLowerCase(); 
		if (doi!=null 
				&& doi.contains(DOI_VALID_FIRST_CHARS)
				&& !doi.contains(" ") 
				&& !doi.equals(DOI_HTTPS_PREFIX) 
				&& !doi.equals(DOI_HTTP_PREFIX)
				&& !doi.equals(DOI_HTTP_NODX_PREFIX)
				&& !doi.equals(DOI_COLON_PREFIX)
				&& (doi.startsWith(DOI_COLON_PREFIX) || doi.startsWith(DOI_HTTP_PREFIX) || doi.startsWith(DOI_HTTPS_PREFIX)  
						|| doi.startsWith(DOI_VALID_FIRST_CHARS) || doi.startsWith(DOI_HTTP_NODX_PREFIX) )){
			return true;
		} 
		
		return false;
	}
	
	
	/** 
	 * Normalizes doi:10.xxx and https://dx.doi.org/10.xxx to http://dx.doi.org/10.xxx
	 * Will throw runtime exception if invalid doi provided
	 * @param doi
	 */
	public static String normalizeDoi(String doi){
		String normalizedDoi = "";
		if (doi == null){
			return normalizedDoi;
		}
		doi = doi.trim();

		if (!isDoi(doi)) {
			throw new RuntimeException("Invalid DOI provided: " + doi);
		}		
		
		if (doi.startsWith(DOI_VALID_FIRST_CHARS)) {
			doi = DOI_HTTP_PREFIX + doi;
		} else if (doi.startsWith(DOI_HTTPS_PREFIX)){
			doi = doi.replace(DOI_HTTPS_PREFIX, DOI_HTTP_PREFIX);
		} else if (doi.startsWith(DOI_COLON_PREFIX)){
			doi = doi.replace(DOI_COLON_PREFIX, DOI_HTTP_PREFIX);
		} else if (doi.startsWith(DOI_HTTP_NODX_PREFIX)){
			doi = doi.replace(DOI_HTTP_NODX_PREFIX, DOI_HTTP_PREFIX);
		}
		return doi;
	}
	
	
	
}
