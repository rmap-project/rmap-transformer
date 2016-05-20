package info.rmapproject.transformer.osf;


public class OsfUtils {

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
}
