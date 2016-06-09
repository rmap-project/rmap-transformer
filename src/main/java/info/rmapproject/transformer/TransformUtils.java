package info.rmapproject.transformer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;


public class TransformUtils {

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
	
	
	
}
