package info.rmapproject.transformer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;


public class Utils {

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
	
}
