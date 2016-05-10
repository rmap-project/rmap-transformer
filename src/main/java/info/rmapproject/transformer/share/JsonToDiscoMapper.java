package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.transformer.TextToDiscoMapper;

import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * Coordinates mapping from SHARE JSON to RDF.  
 * This mapping goes via a Java object model, so SHARE JSON -> Java Model -> RDF.
 * Other mappers could map directly without first mapping to a model.
 * @author khanson
 *
 */
public class JsonToDiscoMapper implements TextToDiscoMapper {

	/**
	 * Convert a single SHARE JSON record to an DiSCO.
	 * @param record - in this case a JSON record using the SHARE data model.
	 */
	public OutputStream toDiscoRdf(String record) throws Exception{
		if (record==null){
			throw new RuntimeException("Null record value - cannot convert null to RDF");
		}		

		// Convert JSON string to Object
		ObjectMapper mapper = new ObjectMapper();
		Record sharerec = (Record) mapper.readValue(record, Record.class);
		
		//Now map to RDF
		ModelToDiscoMapper rdfmapper = new ModelToDiscoMapper();
		OutputStream rdf = rdfmapper.toRDF(sharerec);
		
		return rdf;
	}
	
}
