/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
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
	/** Collection type. */
	//TODO:add these to Terms.
	COLLECTION("collection", "http://purl.org/dc/dcmitype/Collection"),
	
	/** Dataset type. */
	DATASET("dataset", "http://purl.org/dc/dcmitype/Dataset"),
	
	/** Event type. */
	EVENT("event", "http://purl.org/dc/dcmitype/Event"),
	
	/** Image type. */
	IMAGE("image", "http://purl.org/dc/dcmitype/Image"),
	
	/** Interactive resource type. */
	INTERACTIVE_RESOURCE("interactive resource", "http://purl.org/dc/dcmitype/InteractiveResource"),
	
	/** Moving image type. */
	MOVING_IMAGE("moving image", "http://purl.org/dc/dcmitype/MovingImage"),
	
	/** Physical object type. */
	PHYSICAL_OBJECT("physical object", "http://purl.org/dc/dcmitype/PhysicalObject"),
	
	/** Service type. */
	SERVICE("service", "http://purl.org/dc/dcmitype/Service"),
	
	/** Software type. */
	SOFTWARE("software", "http://purl.org/dc/dcmitype/Software"),
	
	/** Sound type. */
	SOUND("sound", "http://purl.org/dc/dcmitype/Sound"),
	
	/** Still image type. */
	STILL_IMAGE("still image", "http://purl.org/dc/dcmitype/StillImage"),
	
	/** Text type. */
	TEXT("text", "http://purl.org/dc/dcmitype/Text"),
	
	/** Article type. */
	ARTICLE("article", "http://purl.org/spar/fabio/Article");
	
	/** The type. */
	private final String type;
	
	/** The type path. */
	private final String typePath;

	/**
	 * Instantiates a new rdf type.
	 *
	 * @param type the type
	 * @param typePath the type path
	 */
	private RdfType (String type, String typePath) {
		this.type = type;
		this.typePath = typePath;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType()  {
		return type;
	}

	/**
	 * Gets the type path.
	 *
	 * @return the type path
	 */
	public String getTypePath()  {
		return typePath;
	}

	/**
	 *  
	 * pass in a type as text e.g. "dataset", and this will find the appropriate path or return null if not found.
	 * This is not case sensitive.
	 *
	 * @param type the type
	 * @return the string
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
