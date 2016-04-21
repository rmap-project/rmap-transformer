package info.rmapproject.transformer.share;

import info.rmapproject.transformer.TextToDiscoMapper;
import info.rmapproject.transformer.share.model.SHARERecord;

import java.io.OutputStream;

/** 
 * Coordinates mapping from SHARE JSON to RDF.  
 * This mapping goes via a Java object model, so SHARE JSON -> Java Model -> RDF.
 * Other mappers could map directly without first mapping to a model.
 * @author khanson
 *
 */
public class SHAREJsonToDiscoMapper implements TextToDiscoMapper {

	/**
	 * Convert a single SHARE JSON record to an DiSCO.
	 * @param record - in this case a JSON record using the SHARE data model.
	 */
	public OutputStream toDiscoRdf(String record) throws Exception{
		if (record==null){
			throw new RuntimeException("Null record value - cannot convert null to RDF");
		}
		
		SHAREJsonToModelMapper modelmapper = new SHAREJsonToModelMapper();
		SHAREModelToDiscoMapper rdfmapper = new SHAREModelToDiscoMapper();
		SHARERecord shareRec = modelmapper.toModel(record);
		OutputStream rdf = rdfmapper.toRDF(shareRec);
		
		return rdf;
	}
	
}
