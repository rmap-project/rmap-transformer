package info.rmapproject.transformer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransformIterator {
		
    protected String currId = null;
    protected Map<String, String> params = null;

    protected static final Logger log = LoggerFactory.getLogger(TransformIterator.class);    
    
    protected TransformIterator(){};
    
    protected TransformIterator(String filters) {
		// split out params
		HashMap<String,String> params=null;
		try{
			params = TransformIterator.readParamsIntoMap(filters, "UTF-8");
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		}
		this.params = params;
    }
    
    
	public String getCurrId() {
		return currId;
	}
	
	protected void setCurrId(String currId){
		this.currId = currId;
	}


	public abstract Object next() ;

	public abstract boolean hasNext();
	
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
