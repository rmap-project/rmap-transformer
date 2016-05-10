package info.rmapproject.transformer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util procedures for Transformer
 * @author khanson
 *
 */
public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Trims a String and if it is empty converts it to nulls 
	 * @param str
	 * @return
	 */
	public static String setEmptyToNull(String str){
		if (str!=null){
			str = str.trim();
			if (str.length()==0){
				str=null;
			}
		}	
		return str;
	}

	/**
	 * Converts a string to a URI
	 * @param str
	 * @return
	 */
	public static URI convertStringToUri(String str) {
		return convertStringToUri(str, "");
	}
	
	/**
	 * Converts a string to a URI, adds prefix if provided and not already present
	 * @param str
	 * @param prefix
	 * @return
	 */
	public static URI convertStringToUri(String str, String prefix) {
		if (prefix==null){prefix="";}
		prefix = prefix.trim();
		
		str = setEmptyToNull(str);
		if (str==null){return null;}
		
		if (prefix.length()>0 && !str.startsWith(prefix)) {
			str = prefix + str;
		}
		
		try {
			return new URI(str);
		} catch (URISyntaxException e) {
			log.error("String '" + str + "' is not a valid URI.");
			throw new RuntimeException("String '" + str + "' is not a valid URI.", e);
		}
	}
	
	
	/**
	 * Converts a List of strings to a list of URIs
	 * @param strList
	 * @return
	 */
	public static List<URI> convertStringListToUris(List<String> strList){
		return convertStringListToUris(strList, "");
	}
	
	
	/**
	 * Converts a list of strings to a list of URIs, adds a prefix to each string where that prefix 
	 * is not already there.
	 * @param strList
	 * @param prefix
	 * @return
	 */
	public static List<URI> convertStringListToUris(List<String> strList, String prefix){
		if (prefix==null){prefix="";}
		if (strList==null){return null;}
		
		List<URI> uriList = new ArrayList<URI>();
		for (String str : strList){
			URI uri = convertStringToUri(str, prefix);
			if (uri!=null){
				uriList.add(uri);
			}
		}
		return uriList;
	}
	
	
	
}
