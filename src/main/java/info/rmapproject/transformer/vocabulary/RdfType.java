package info.rmapproject.transformer.vocabulary;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This is to support conversion of text-based type to a RDF:TYPE. allows you to look up the 
 * type path based on the term.
 * @author khanson
 *
 */
public enum RdfType {
	//TODO:add these to Terms.
	COLLECTION("collection", "http://purl.org/dc/dcmitype/Collection"),
	DATASET("dataset", "http://purl.org/dc/dcmitype/Dataset"),
	EVENT("event", "http://purl.org/dc/dcmitype/Event"),
	IMAGE("image", "http://purl.org/dc/dcmitype/Image"),
	INTERACTIVE_RESOURCE("interactive resource", "http://purl.org/dc/dcmitype/InteractiveResource"),
	MOVING_IMAGE("moving image", "http://purl.org/dc/dcmitype/MovingImage"),
	PHYSICAL_OBJECT("physical object", "http://purl.org/dc/dcmitype/PhysicalObject"),
	SERVICE("image", "http://purl.org/dc/dcmitype/Service"),
	SOFTWARE("image", "http://purl.org/dc/dcmitype/Software"),
	SOUND("image", "http://purl.org/dc/dcmitype/Sound"),
	STILL_IMAGE("image", "http://purl.org/dc/dcmitype/StillImage"),
	TEXT("text", "http://purl.org/dc/dcmitype/Text"),
	ARTICLE("article", "http://purl.org/spar/fabio/Article");
	
	private final String type;
	private final String typePath;

	private RdfType (String type, String typePath) {
		this.type = type;
		this.typePath = typePath;
	}

	public String getType()  {
		return type;
	}

	public String getTypePath()  {
		return typePath;
	}

	/** 
	 * pass in a type as text e.g. "dataset", and this will find the appropriate path or return null if not found.
	 * This is not case sensitive.
	 * @param type
	 * @return
	 */
    public static String get(String type) { 
    	if (type!=null){
	    	Map<String, String> lookup = new HashMap<String, String>();
	        for(RdfType rt : EnumSet.allOf(RdfType.class)) {
	            lookup.put(rt.getType().toLowerCase(), rt.getTypePath());
	        }
	        return lookup.get(type.toLowerCase()); 
    	} else {
    		return null;
    	}
    }
	
	
}
